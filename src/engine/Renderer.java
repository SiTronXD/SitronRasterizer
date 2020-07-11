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
	
	ArrayList<Vertex> vertices;
	ArrayList<Vertex> verticesToCheck;
	ArrayList<Vertex> currentSetOfVertices;
	
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
		
		vertices = new ArrayList<Vertex>();
		verticesToCheck = new ArrayList<Vertex>();
		currentSetOfVertices = new ArrayList<Vertex>();
	}
	
	public void DrawTriangle(Vertex v1, Vertex v2, Vertex v3, int renderFlags)
	{
		Vertex[] oldVertices = new Vertex[]{ v1, v2, v3 };
		
		vertices.clear();
		vertices.add(new Vertex(oldVertices[0]));
		vertices.add(new Vertex(oldVertices[1]));
		vertices.add(new Vertex(oldVertices[2]));
		
		// Local space --> Clip space
		for(int i = 0; i < vertices.size(); i++)
			m_shader.VertexShader(oldVertices[i], vertices.get(i).m_position);

		// Clipping
		GetVerticesAfterClipping();
		if(vertices.size() <= 0)
			return;
		
		// Perspective divide
		for(int i = 0; i < vertices.size(); i++)
		{
			float w = vertices.get(i).GetPosition().w;
			vertices.get(i).GetPosition().Div(w, w, w, 1.0f);
		}
		
		// Back-face culling
		if((renderFlags & RENDER_FLAGS_DISABLE_BACK_FACE_CULLING) != RENDER_FLAGS_DISABLE_BACK_FACE_CULLING)
		{
			// Since all triangles after clipping lay on the same plane, we only need to check one single normal
			Vector edge0 = new Vector(vertices.get(1).GetPosition()); edge0.Sub(vertices.get(0).GetPosition());
			Vector edge1 = new Vector(vertices.get(2).GetPosition()); edge1.Sub(vertices.get(0).GetPosition());
			Vector normal = Vector.Cross(edge0, edge1);
			if(normal.z >= 0.0) { return; }
		}
		
		// NDC space --> screen space
		for(int i = 0; i < vertices.size(); i++)
		{
			vertices.get(i).TransformToScreenSpace(m_width, m_height, vertices.get(i));
		}

		for(int i = 0; i < vertices.size()-2; i++)
		{
			// Draw wireframe
			if((renderFlags & RENDER_FLAGS_WIREFRAME) == RENDER_FLAGS_WIREFRAME)
				DrawNonFilledTriangleOnScreen(vertices.get(0), vertices.get(i+1), vertices.get(i+2));
			else
				FillTriangleOnScreen(vertices.get(0), vertices.get(i+1), vertices.get(i+2), renderFlags);
		}
	}
	
	// Clipping algorithm was heavily inspired by Fabien Sanglard's article on homogeneous clipping
	void GetVerticesAfterClipping()
	{
		boolean v1InVF = Vertex.IsInsideViewFrustum(vertices.get(0).m_position);
		boolean v2InVF = Vertex.IsInsideViewFrustum(vertices.get(1).m_position);
		boolean v3InVF = Vertex.IsInsideViewFrustum(vertices.get(2).m_position); 
		
		// Ignore if all vertices are inside view frustum
		if(v1InVF && v2InVF && v3InVF)
		{
			return;
		}
		// I choose to not return here, since the triangle could potentially cover the screen even when the 
		// vertices are outside the window's opposite sides. The triangle should still be rendered in that case.
		/*else if(!v1InVF && !v2InVF && !v3InVF)
		{
			vertices.clear();
			return;
		}*/
			
		verticesToCheck.clear();
		verticesToCheck.add(vertices.get(0));
		verticesToCheck.add(vertices.get(1));
		verticesToCheck.add(vertices.get(2));
		
		
		for(int checkAxis = 0; checkAxis < 6; checkAxis++)
		{
			if(verticesToCheck.size() <= 0)
				break;
			
			currentSetOfVertices.clear();
			
			// Last vertex
			Vertex lastVertex = verticesToCheck.get(verticesToCheck.size()-1);
			boolean lastInVF = Vertex.IsInsideViewAxis(lastVertex.GetPosition(), checkAxis);
			
			// Go through each vertex in each "line"
			for(int i = 0; i < verticesToCheck.size(); i++)
			{
				Vertex currentVertex = verticesToCheck.get(i);
				boolean currentInVF = Vertex.IsInsideViewAxis(currentVertex.GetPosition(), checkAxis);
				
				// One of the vertices are outside
				if(currentInVF ^ lastInVF)
				{
					int componentIndex = (int)(checkAxis/2.0f);
					float wScalar = (checkAxis == 5) ? 0.0f : 1.0f;
					Vertex insideVertex = currentInVF ? currentVertex : lastVertex;
					Vertex outsideVertex = !currentInVF ? currentVertex : lastVertex;
					Vertex createdVert = GetNewClippedVertex(insideVertex, outsideVertex, componentIndex, wScalar);
					
					currentSetOfVertices.add(createdVert);
				}
				// This vertex is inside
				if(currentInVF)
				{
					currentSetOfVertices.add(currentVertex);
				}
				
				lastVertex = currentVertex;
				lastInVF = currentInVF;
			}
			
			// Start over with current vertices
			verticesToCheck.clear();
			for(int i = 0; i < currentSetOfVertices.size(); i++)
				verticesToCheck.add(currentSetOfVertices.get(i));
		}
		
		// Done
		vertices.clear();
		for(int i = 0; i < verticesToCheck.size(); i++)
		{
			vertices.add(verticesToCheck.get(i));
		}
	}
	
	Vertex GetNewClippedVertex(Vertex vertA, Vertex vertB, int componentIndex, float wScalar)
	{
		float differenceA = (vertA.m_position.w * Math.signum(vertB.m_position.GetComponent(componentIndex)) * wScalar) - vertA.m_position.GetComponent(componentIndex); 
		float differenceB = (vertB.m_position.w * Math.signum(vertB.m_position.GetComponent(componentIndex)) * wScalar) - vertB.m_position.GetComponent(componentIndex);
		
		// Find t-value on line
		float t = differenceA / (differenceA - differenceB);
		
		// Create vertex
		Vertex newVert = new Vertex();
		Vertex.Lerp(vertA, vertB, t, newVert);
		
		return newVert;
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
	
	// Resets bounds before drawing a new triangle
	void ResetMinMaxBounds()
	{
		// Set extreme initial boundaries for min and max
		for(int i = 0; i < xMin.length; i++)
			xMin[i] = m_width+1;
		for(int i = 0; i < xMax.length; i++)
			xMax[i] = -1;
	}

	// Sets bounds for one triangle edge
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
	
	// Clear render texture using a color
	public void ClearRenderTexture(byte red, byte green, byte blue)
	{
		m_renderTexture.SetToColor(red, green, blue);
	}
	
	// Clear depth buffer to 1
	public void ClearDepthBuffer()
	{
		for(int i = 0; i < m_depthBuffer.length; i++)
		{
			m_depthBuffer[i] = 1.0;
		}
	}
	
	// Updates shader
	public void Update(float dt)
	{
		m_shader.Update(dt);
	}
	
	// Sets current shader
	public void SetShader(Shader newShader)
	{
		m_shader = newShader;
	}
	
	public int GetWidth() { return m_width; }
	public int GetHeight() { return m_height; }
	
	public Texture GetRenderTexture() { return m_renderTexture; }
	
	public Shader GetShader() { return m_shader; }
}
