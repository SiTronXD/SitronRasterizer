package engine;

import java.util.ArrayList;
import java.util.Collections;

import engine.shaders.DefaultShader;

public class Renderer {
	public static final int RENDER_FLAGS_NO_FLAGS = 0;
	public static final int RENDER_FLAGS_WIREFRAME = 1;
	public static final int RENDER_FLAGS_NON_PERSPECTIVE_CORRECT_INTERPOLATION = 2;
	public static final int RENDER_FLAGS_DISABLE_BACK_FACE_CULLING = 4;
	
	int m_width;
	int m_height;
	
	double[] m_depthBuffer;
	
	Shader m_shader;
	
	Texture m_renderTexture;
	
	Vertex[] xMinVert;// = new Vertex[m_height];
	Vertex[] xMaxVert;// = new Vertex[m_height];
	int[] xMin;// = new int[m_height];
	int[] xMax;// = new int[m_height];
	int yMin;// = 0;
	int yMax;// = 0;
	
	public Renderer(int width, int height)
	{
		m_width = width;
		m_height = height;
		
		m_depthBuffer = new double[width * height];
		ClearDepthBuffer();
		
		m_shader = new DefaultShader();
		
		m_renderTexture = new Texture(width, height);
		
		
		// Setup min and max boundaries for the horizontal lines in the triangle
		xMinVert = new Vertex[m_height];
		xMaxVert = new Vertex[m_height];
		xMin = new int[m_height];
		xMax = new int[m_height];
		yMin = 0;
		yMax = 0;
		
		for(int i = 0; i < xMinVert.length; i++)
		{
			xMinVert[i] = new Vertex();
			xMaxVert[i] = new Vertex();
		}
	}
	
	public void DrawTriangle(Vertex v1, Vertex v2, Vertex v3, int renderFlags)
	{
		Vertex transformedV1 = new Vertex(v1);
		Vertex transformedV2 = new Vertex(v2);
		Vertex transformedV3 = new Vertex(v3);

		// Local space --> Clip space
		m_shader.VertexShader(v1, transformedV1.m_position);
		m_shader.VertexShader(v2, transformedV2.m_position);
		m_shader.VertexShader(v3, transformedV3.m_position);

		// Frustum culling
		if(!(Vertex.IsInsideViewFrustum(transformedV1.GetPosition()) && 
			 Vertex.IsInsideViewFrustum(transformedV2.GetPosition()) && 
			 Vertex.IsInsideViewFrustum(transformedV3.GetPosition())))
			return;
		
		// Perspective divide
		transformedV1.GetPosition().Div(transformedV1.GetPosition().w, transformedV1.GetPosition().w, transformedV1.GetPosition().w, 1.0f);
		transformedV2.GetPosition().Div(transformedV2.GetPosition().w, transformedV2.GetPosition().w, transformedV2.GetPosition().w, 1.0f);
		transformedV3.GetPosition().Div(transformedV3.GetPosition().w, transformedV3.GetPosition().w, transformedV3.GetPosition().w, 1.0f);

		// Back-face culling
		if((renderFlags & RENDER_FLAGS_DISABLE_BACK_FACE_CULLING) != RENDER_FLAGS_DISABLE_BACK_FACE_CULLING)
		{
			Vector edge0 = new Vector(transformedV2.GetPosition()); edge0.Sub(transformedV1.GetPosition());
			Vector edge1 = new Vector(transformedV3.GetPosition()); edge1.Sub(transformedV1.GetPosition());
			Vector normal = Vector.Cross(edge0, edge1);
			if(normal.z >= 0.0) { return; }
		}
		
		// NDC space --> screen space
		transformedV1.TransformToScreenSpace(m_width, m_height, transformedV1);
		transformedV2.TransformToScreenSpace(m_width, m_height, transformedV2);
		transformedV3.TransformToScreenSpace(m_width, m_height, transformedV3);

		// Draw wireframe
		if((renderFlags & RENDER_FLAGS_WIREFRAME) == RENDER_FLAGS_WIREFRAME)
			DrawNonFilledTriangleOnScreen(transformedV1, transformedV2, transformedV3);
		else
			FillTriangleOnScreen(transformedV1, transformedV2, transformedV3, renderFlags);
	}
	
	public void FillTriangleOnScreen(Vertex v1, Vertex v2, Vertex v3, int renderFlags)
	{
		boolean lerpPerspCorrect = (renderFlags & RENDER_FLAGS_NON_PERSPECTIVE_CORRECT_INTERPOLATION) != RENDER_FLAGS_NON_PERSPECTIVE_CORRECT_INTERPOLATION;
		
		// Sort vertices
		Vertex[] sortedVertices = SortVertices(v1, v2, v3);
		v1 = sortedVertices[0];
		v2 = sortedVertices[1];
		v3 = sortedVertices[2];

		ResetMinMaxBounds();
		
		// Find lines and add the points to arraylists
		ArrayList<Vector> firstLine = DrawLine(
			(int) v1.GetPosition().x, 
			(int) v1.GetPosition().y, 
			(int) v2.GetPosition().x, 
			(int) v2.GetPosition().y
		);
		ArrayList<Vector> secondLine = DrawLine(
			(int) v1.GetPosition().x,
			(int) v1.GetPosition().y,
			(int) v3.GetPosition().x,
			(int) v3.GetPosition().y
		);
		ArrayList<Vector> thirdLine = DrawLine(
			(int) v2.GetPosition().x,
			(int) v2.GetPosition().y,
			(int) v3.GetPosition().x,
			(int) v3.GetPosition().y
		);
		yMin = (int) firstLine.get(0).y;
		yMax = (int) thirdLine.get(thirdLine.size()-1).y;
		
		SetMinMaxLineBounds(firstLine, v1, v2, lerpPerspCorrect);
		SetMinMaxLineBounds(secondLine, v1, v3, lerpPerspCorrect);
		SetMinMaxLineBounds(thirdLine, v2, v3, lerpPerspCorrect);
		
		// Go from top to bottom in the triangle
		Vertex tempLerpVertex = new Vertex();
		Vector pixelColor = new Vector();
		for(int y = yMin; y <= yMax; y++)
		{
			Vertex minVert = xMinVert[y];
			Vertex maxVert = xMaxVert[y];
			
			// Go through each pixel in the horizontal line
			for(int x = xMin[y]; x <= xMax[y]; x++)
			{
				float t = 0.0f;
				
				// Make sure there is more than 1 pixel in the line
				if(xMax[y] - xMin[y] != 0)
					t = (float)(x - xMin[y]) / (float) (xMax[y] - xMin[y]);
				else
					t = 1.0f;
				
				// Interpolate across the line
				if(lerpPerspCorrect)
					Vertex.PerspectiveCorrectLerp(minVert, maxVert, t, tempLerpVertex);
				else
					Vertex.Lerp(minVert, maxVert, t, tempLerpVertex);
				
				// Depth buffer
				int depthBufferIndex = x + y*m_width;
				if(tempLerpVertex.GetPosition().z < m_depthBuffer[depthBufferIndex])	
				{
					m_depthBuffer[depthBufferIndex] = tempLerpVertex.GetPosition().z;
				}
				else
					continue;
				
				// Find a pixel color
				pixelColor.Set(0.0f, 0.0f, 0.0f);
				m_shader.FragmentShader(tempLerpVertex, pixelColor);
				
				// Render!
				m_renderTexture.SetPixel(
					x, 
					y, 
					pixelColor.byte_x, 
					pixelColor.byte_y, 
					pixelColor.byte_z, 
					255
				);
			}
		}
	}
	
	public void DrawNonFilledTriangleOnScreen(Vertex v1, Vertex v2, Vertex v3)
	{
		// Sort vertices
		Vertex[] sortedVertices = SortVertices(v1, v2, v3);
		v1 = sortedVertices[0];
		v2 = sortedVertices[1];
		v3 = sortedVertices[2];

		// Find lines and add the points to arraylists
		ArrayList<Vector> firstLine = DrawLine(
			(int) v1.GetPosition().x, 
			(int) v1.GetPosition().y, 
			(int) v2.GetPosition().x, 
			(int) v2.GetPosition().y
		);
		ArrayList<Vector> secondLine = DrawLine(
			(int) v1.GetPosition().x,
			(int) v1.GetPosition().y,
			(int) v3.GetPosition().x,
			(int) v3.GetPosition().y
		);
		ArrayList<Vector> thirdLine = DrawLine(
			(int) v2.GetPosition().x,
			(int) v2.GetPosition().y,
			(int) v3.GetPosition().x,
			(int) v3.GetPosition().y
		);
		
		// Add all line points in one single arraylist
		ArrayList<Vector> allLinePoints = new ArrayList<Vector>();
		allLinePoints.addAll(firstLine);
		allLinePoints.addAll(secondLine);
		allLinePoints.addAll(thirdLine);
		
		// Draw the points
		for(int i = 0; i < allLinePoints.size(); i++)
		{
			m_renderTexture.SetPixel(
				(int) allLinePoints.get(i).x, 
				(int) allLinePoints.get(i).y, 
				255, 
				255, 
				255, 
				255
			);
		}
	}
	
	// Bresenham's line algorithm
	public ArrayList<Vector> DrawLine(int x0, int y0, int x1, int y1)
	{
		if(Math.abs(y1 - y0) < Math.abs(x1 - x0))
		{
			if(x0 > x1)
				return DrawLineLow(x1, y1, x0, y0);		// 135 <= v <= 225
			else
				return DrawLineLow(x0, y0, x1, y1);		// -45 <= v <= 45
		}
		else
		{
			if(y0 > y1)
				return DrawLineHigh(x1, y1, x0, y0);	// 45 <= v <= 135
			else
				return DrawLineHigh(x0, y0, x1, y1);	// 225 <= v <= 315 (-45)
		}
	}
	
	ArrayList<Vector> DrawLineLow(int x0, int y0, int x1, int y1)
	{
		float dx = x1 - x0;
		float dy = y1 - y0;
		int yi = 1;
		
		if(dy < 0)
		{
			yi = -1;
			dy = -dy;
		}
		
		float D = 2*dy - dx;
		int y = y0;
		
		ArrayList<Vector> storedPoints = new ArrayList<Vector>();
		for(int x = x0; x <= x1; x++)
		{
			//renderTexture.SetPixel(x, y, 255, 0, 0, 255);
			storedPoints.add(new Vector(x, y));
			
			if(D > 0)
			{
				y += yi;
				D -= 2*dx;
			}
			D += 2*dy;
		}
		
		// Reverse so the list goes from lowest Y to highest Y
		if(yi < 0)
			Collections.reverse(storedPoints);
			
		return storedPoints;
	}
	
	ArrayList<Vector> DrawLineHigh(int x0, int y0, int x1, int y1)
	{
		float dx = x1 - x0;
		float dy = y1 - y0;
		int xi = 1;
		
		if(dx < 0)
		{
			xi = -1;
			dx = -dx;
		}
		
		float D = 2*dx - dy;
		int x = x0;
		
		ArrayList<Vector> storedPoints = new ArrayList<Vector>();
		for(int y = y0; y <= y1; y++)
		{
			//renderTexture.SetPixel(x, y, 255, 0, 0, 255);
			storedPoints.add(new Vector(x, y));
			
			if(D > 0)
			{
				x += xi;
				D -= 2*dy;
			}
			D += 2*dx;
		}
		
		return storedPoints;
	}
	
	void ResetMinMaxBounds()
	{
		// Set extreme initial boundaries for min and max
		for(int i = 0; i < xMin.length; i++)
			xMin[i] = m_width+1;
		for(int i = 0; i < xMax.length; i++)
			xMax[i] = -1;
	}
	
	void SetMinMaxLineBounds(ArrayList<Vector> line, Vertex v1, Vertex v2, boolean lerpPerspCorrect)
	{
		for(int i = 0; i < line.size(); i++)
		{
			int curr_x = (int) line.get(i).x;
			int curr_y = (int) line.get(i).y;
			
			if(curr_x < xMin[curr_y])
			{
				float t = (float)i / (float) line.size();
				
				xMin[curr_y] = curr_x;
				
				if(lerpPerspCorrect)
					Vertex.PerspectiveCorrectLerp(v1, v2, t, xMinVert[curr_y]);
				else
					Vertex.Lerp(v1, v2, t, xMinVert[curr_y]);
			}
			if(curr_x > xMax[curr_y])
			{
				float t = (float)i / (float) line.size();
				
				xMax[curr_y] = curr_x;
				
				if(lerpPerspCorrect)
					Vertex.PerspectiveCorrectLerp(v1, v2, t, xMaxVert[curr_y]);
				else
					Vertex.Lerp(v1, v2, t, xMaxVert[curr_y]);
			}
		}
	}
	
	// Brute force sort through the 3 vertices
	Vertex[] SortVertices(Vertex v1, Vertex v2, Vertex v3)
	{
		// Sort based on y-position
		if(v1.GetPosition().y > v2.GetPosition().y)
		{
			Vertex temp = v1;
			v1 = v2;
			v2 = temp;
		}
		
		if(v1.GetPosition().y > v3.GetPosition().y) 
		{
			Vertex temp = v1;
			v1 = v3;
			v3 = temp;
		}
		
		if(v2.GetPosition().y > v3.GetPosition().y)
		{
			Vertex temp = v2;
			v2 = v3;
			v3 = temp;
		}
		
		// v1.y == v2.y and v1.x > v2.x, then switch so the minX and maxX can be correctly calculated
		if((int) v1.GetPosition().y == (int) v2.GetPosition().y && v1.GetPosition().x > v2.GetPosition().x)
		{
			Vertex temp = v1;
			v1 = v2;
			v2 = temp;
		}
		
		return new Vertex[] { v1, v2, v3 };
	}
	
	public void ClearRenderTexture(byte red, byte green, byte blue)
	{
		m_renderTexture.SetToColor(red, green, blue);
	}
	
	public void ClearDepthBuffer()
	{
		for(int i = 0; i < m_depthBuffer.length; i++)
		{
			m_depthBuffer[i] = 1.0;
		}
	}
	
	public void Update(float dt)
	{
		m_shader.Update(dt);
	}
	
	public void SetShader(Shader newShader)
	{
		m_shader = newShader;
	}
	
	public int GetWidth() { return m_width; }
	public int GetHeight() { return m_height; }
	
	public Texture GetRenderTexture() { return m_renderTexture; }
	
	public Shader GetShader() { return m_shader; }
}
