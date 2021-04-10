package engine;

import java.util.ArrayList;
import java.util.Collections;

import engine.shaders.DefaultShader;

public class Renderer {
	public static final int RENDER_FLAGS_NO_FLAGS = 0;
	public static final int RENDER_FLAGS_WIREFRAME = 1;
	public static final int RENDER_FLAGS_NON_PERSPECTIVE_CORRECT_INTERPOLATION = 2;
	public static final int RENDER_FLAGS_DISABLE_BACK_FACE_CULLING = 4;

	private static final float EPSILON = 0.001f;
	
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
		if((renderFlags & RENDER_FLAGS_DISABLE_BACK_FACE_CULLING) == 0)
		{
			// Since all triangles after clipping lay on the same plane, we only need to check one single normal
			Vector edge0 = new Vector(vertices.get(1).GetPosition()); edge0.Sub(vertices.get(0).GetPosition());
			Vector edge1 = new Vector(vertices.get(2).GetPosition()); edge1.Sub(vertices.get(0).GetPosition());
			Vector normal = Vector.Cross(edge0, edge1);
			if(normal.z >= EPSILON) { return; }
		}
		
		/*System.out.println("NDC: ");
		System.out.println(vertices.get(0).m_position.GetString());
		System.out.println(vertices.get(1).m_position.GetString());
		System.out.println(vertices.get(2).m_position.GetString());
		System.out.println(" ");*/
		
		// NDC space --> screen space
		for(int i = 0; i < vertices.size(); i++)
		{
			vertices.get(i).TransformToScreenSpace(m_width, m_height, vertices.get(i));
		}

		for(int i = 0; i < vertices.size()-2; i++)
		{
			// Draw wireframe
			if((renderFlags & RENDER_FLAGS_WIREFRAME) != 0)
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
			boolean lastInVF = Vertex.IsInsideViewPlaneAxis(lastVertex.GetPosition(), checkAxis);
			
			// Go through each vertex in each "line"
			for(int i = 0; i < verticesToCheck.size(); i++)
			{
				Vertex currentVertex = verticesToCheck.get(i);
				boolean currentInVF = Vertex.IsInsideViewPlaneAxis(currentVertex.GetPosition(), checkAxis);
				
				// One of the vertices are outside
				if(currentInVF ^ lastInVF)
				{
					int componentIndex = (int)(checkAxis * 0.5f);
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
			
			// Debug new vertex positions after perspective division
			/*ArrayList<Vertex> debuggingVertices = new ArrayList<Vertex>();
			for(int i = 0; i < currentSetOfVertices.size(); i++)
			{
				debuggingVertices.add(new Vertex(currentSetOfVertices.get(i)));
				debuggingVertices.get(i).m_position.Div(
						currentSetOfVertices.get(i).m_position.w,
						currentSetOfVertices.get(i).m_position.w,
						currentSetOfVertices.get(i).m_position.w,
						1.0f
				);
			}*/
			
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
		t = SMath.Clamp(t, 0.0f, 1.0f);
		
		// Create vertex
		Vertex newVert = new Vertex();
		Vertex.Lerp(vertA, vertB, t, newVert);
		
		return newVert;
	}
	
	/*boolean IsTriangleAABBIntersectingViewFrustum(Vertex in_v0, Vertex in_v1, Vertex in_v2)
	{
		// Avoid division by 0
		if(in_v0.m_position.w == 0 || in_v1.m_position.w == 0 || in_v2.m_position.w == 0)
			return false;
		
		Vector v0 = new Vertex(in_v0).m_position;
		Vector v1 = new Vertex(in_v1).m_position;
		Vector v2 = new Vertex(in_v2).m_position;
		
		float v0w = Math.abs(v0.w);
		v0.Div(v0w, v0w, v0w, 1.0f);

		float v1w = Math.abs(v1.w);
		v1.Div(v1w, v1w, v1w, 1.0f);
		
		float v2w = Math.abs(v2.w);
		v2.Div(v2w, v2w, v2w, 1.0f);
		
		// System.out.println(v0.GetString());
		// System.out.println(v1.GetString());
		// System.out.println(v2.GetString());
		
		Vector frustumCenter = new Vector(0.0f, 0.0f, 0.5f);
		Vector frustumHalfSize = new Vector(1.0f, 1.0f, 0.5f);

		v0.Sub(frustumCenter);
		v1.Sub(frustumCenter);
		v2.Sub(frustumCenter);
		
		Vector triMax = new Vector(
			Math.max(Math.max(v0.x, v1.x), v2.x),
			Math.max(Math.max(v0.y, v1.y), v2.y),
			Math.max(Math.max(v0.z, v1.z), v2.z)
		);
		Vector triMin = new Vector(
			Math.min(Math.min(v0.x, v1.x), v2.x),
			Math.min(Math.min(v0.y, v1.y), v2.y),
			Math.min(Math.min(v0.z, v1.z), v2.z)
		);
		Vector fruMax = new Vector(frustumCenter); fruMax.Add(frustumHalfSize);
		Vector fruMin = new Vector(frustumCenter); fruMin.Sub(frustumHalfSize);
		
		
		return 	triMax.x >= fruMin.x && triMin.x <= fruMax.x && 
				triMax.y >= fruMin.y && triMin.y <= fruMax.y &&
				triMax.z >= fruMin.z && triMin.z <= fruMax.z;
	}*/
	
	/*boolean IsTriangleIntersectingViewFrustum(Vertex in_v0, Vertex in_v1, Vertex in_v2)
	{
		// Avoid division by 0
		if(in_v0.m_position.w == 0 || in_v1.m_position.w == 0 || in_v2.m_position.w == 0)
			return false;
		
		Vector v0 = new Vertex(in_v0).m_position;
		Vector v1 = new Vertex(in_v1).m_position;
		Vector v2 = new Vertex(in_v2).m_position;
		
		float v0w = Math.abs(v0.w);
		v0.Div(v0w, v0w, v0w, 1.0f);

		float v1w = Math.abs(v1.w);
		v1.Div(v1w, v1w, v1w, 1.0f);
		
		float v2w = Math.abs(v2.w);
		v2.Div(v2w, v2w, v2w, 1.0f);
		
		// System.out.println(v0.GetString());
		// System.out.println(v1.GetString());
		// System.out.println(v2.GetString());
		
		Vector frustumCenter = new Vector(0.0f, 0.0f, 0.5f);
		Vector frustumHalfSize = new Vector(1.0f, 1.0f, 0.5f);

		v0.Sub(frustumCenter);
		v1.Sub(frustumCenter);
		v2.Sub(frustumCenter);
		
		// Edges
		Vector f0 = new Vector(v1); f0.Sub(v0);
		Vector f1 = new Vector(v2); f1.Sub(v1);
		Vector f2 = new Vector(v0); f2.Sub(v2);
		
		// Face normals
		Vector u0 = new Vector(1.0f, 0.0f, 0.0f, 0.0f);
		Vector u1 = new Vector(0.0f, 1.0f, 0.0f, 0.0f);
		Vector u2 = new Vector(0.0f, 0.0f, 1.0f, 0.0f);
		
		// Axis
		Vector axis_u0_f0 = Vector.Cross(u0, f0);//	axis_u0_f0.Scale(-1.0f);
	    Vector axis_u0_f1 = Vector.Cross(u0, f1);//	axis_u0_f1.Scale(-1.0f);
	    Vector axis_u0_f2 = Vector.Cross(u0, f2);//	axis_u0_f2.Scale(-1.0f);

	    Vector axis_u1_f0 = Vector.Cross(u1, f0);//	axis_u1_f0.Scale(-1.0f);
	    Vector axis_u1_f1 = Vector.Cross(u1, f1);//	axis_u1_f1.Scale(-1.0f);
	    Vector axis_u1_f2 = Vector.Cross(u2, f2);//	axis_u1_f2.Scale(-1.0f);

	    Vector axis_u2_f0 = Vector.Cross(u2, f0);//	axis_u2_f0.Scale(-1.0f);
	    Vector axis_u2_f1 = Vector.Cross(u2, f1);//	axis_u2_f1.Scale(-1.0f);
	    Vector axis_u2_f2 = Vector.Cross(u2, f2);//	axis_u2_f2.Scale(-1.0f);
	    
	    // Testing axis
	    boolean axisAreCorrect = 	TestAxis(v0, v1, v2, u0, u1, u2, axis_u0_f0, frustumHalfSize) && 
	    							TestAxis(v0, v1, v2, u0, u1, u2, axis_u0_f1, frustumHalfSize) && 
	    							TestAxis(v0, v1, v2, u0, u1, u2, axis_u0_f2, frustumHalfSize) && 
	    						
	    							TestAxis(v0, v1, v2, u0, u1, u2, axis_u1_f0, frustumHalfSize) && 
	    							TestAxis(v0, v1, v2, u0, u1, u2, axis_u1_f1, frustumHalfSize) && 
	    							TestAxis(v0, v1, v2, u0, u1, u2, axis_u1_f2, frustumHalfSize) &&
	    						
	    							TestAxis(v0, v1, v2, u0, u1, u2, axis_u2_f0, frustumHalfSize) && 
	    							TestAxis(v0, v1, v2, u0, u1, u2, axis_u2_f1, frustumHalfSize) && 
	    							TestAxis(v0, v1, v2, u0, u1, u2, axis_u2_f2, frustumHalfSize);
	    
	    if(!axisAreCorrect)
	    	return false;
	    
	    //System.out.println("axisAreCorrect");
	    
	    // Testing normal
	    boolean normalsAreCorrect = TestAxis(v0, v1, v2, u0, u1, u2, u0, frustumHalfSize) && 
	    							TestAxis(v0, v1, v2, u0, u1, u2, u1, frustumHalfSize) && 
	    							TestAxis(v0, v1, v2, u0, u1, u2, u2, frustumHalfSize);
	    
	    if(!normalsAreCorrect)
	    	return false;

	    //System.out.println("normalsAreCorrect");
	    
	    // Testing triangle normal
		Vector axis_f0_f1 = Vector.Cross(f0, f1);
		return TestAxis(v0, v1, v2, u0, u1, u2, axis_f0_f1, frustumHalfSize);
	}*/
	
	boolean TestAxis(Vector v0, Vector v1, Vector v2, Vector u0, Vector u1, Vector u2, Vector axis, Vector boxHalfSize)
	{
		float p0 = Vector.Dot(v0, axis);
		float p1 = Vector.Dot(v1, axis);
		float p2 = Vector.Dot(v2, axis);
		
		float r = 	boxHalfSize.x * Math.abs(Vector.Dot(u0, axis)) + 
					boxHalfSize.y * Math.abs(Vector.Dot(u1, axis)) +
					boxHalfSize.z * Math.abs(Vector.Dot(u2, axis));
		
		if (Math.max(-Math.max(Math.max(p0, p1), p2), Math.min(Math.min(p0, p1), p2)) > r)
	        return false;
		
		return true;
	}
	
	public void FillTriangleOnScreen(Vertex v1, Vertex v2, Vertex v3, int renderFlags)
	{
		boolean lerpPerspCorrect = (renderFlags & RENDER_FLAGS_NON_PERSPECTIVE_CORRECT_INTERPOLATION) == 0;
		
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
		
		// Clamp just to make sure the values are valid
		yMin = (int) SMath.Clamp(yMin, 0.0f, m_height-1);
		yMax = (int) SMath.Clamp(yMax, 0.0f, m_height-1);
		
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

	// Sets horizontal bounds for one triangle edge
	void SetMinMaxLineBounds(ArrayList<Vector> line, Vertex v1, Vertex v2, boolean lerpPerspCorrect)
	{
		for(int i = 0; i < line.size(); i++)
		{
			int curr_x = (int) line.get(i).x;
			int curr_y = (int) line.get(i).y;

			// Clamp just to make sure the values are valid
			curr_x = (int) SMath.Clamp(curr_x, 0.0f, m_width-1);
			curr_y = (int) SMath.Clamp(curr_y, 0.0f, m_height-1);
			
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
