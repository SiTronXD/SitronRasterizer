package engine;

import java.util.ArrayList;
import java.util.Collections;

public class Renderer {
	int m_width;
	int m_height;
	
	Texture renderTexture;
	
	public Renderer(int width, int height)
	{
		m_width = width;
		m_height = height;
		
		renderTexture = new Texture(width, height);
	}
	
	public void FillTriangle(Vertex v1, Vertex v2, Vertex v3)
	{
		SortVertices(v1, v2, v3);
		int[] xMin = new int[m_height];
		int[] xMax = new int[m_height];
		int yMin = 0;
		int yMax = 0;
		
		// Set extreme boundaries for min and max
		for(int i = 0; i < xMin.length; i++)
			xMin[i] = m_width+1;
		for(int i = 0; i < xMax.length; i++)
			xMax[i] = -1;
		
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
		
		if(v2.GetPosition().x < v3.GetPosition().x)
		{
			// Set xMin
			for(int i = 0; i < firstLine.size(); i++)
			{
				int curr_x = (int) firstLine.get(i).x;
				int curr_y = (int) firstLine.get(i).y;
				
				if(curr_x < xMin[curr_y])
					xMin[curr_y] = curr_x;
			}
			for(int i = 0; i < thirdLine.size(); i++)
			{
				int curr_x = (int) thirdLine.get(i).x;
				int curr_y = (int) thirdLine.get(i).y;
				
				if(curr_x < xMin[curr_y])
					xMin[curr_y] = curr_x;
			}
			

			// Set xMax
			for(int i = 0; i < secondLine.size(); i++)
			{
				int curr_x = (int) secondLine.get(i).x;
				int curr_y = (int) secondLine.get(i).y;
				
				if(curr_x > xMax[curr_y])
					xMax[curr_y] = curr_x;
			}
		}
		else
		{
			// Set xMin
			for(int i = 0; i < secondLine.size(); i++)
			{
				int curr_x = (int) secondLine.get(i).x;
				int curr_y = (int) secondLine.get(i).y;
				
				if(curr_x < xMin[curr_y])
					xMin[curr_y] = curr_x;
			}
			
			// Set xMax
			for(int i = 0; i < firstLine.size(); i++)
			{
				int curr_x = (int) firstLine.get(i).x;
				int curr_y = (int) firstLine.get(i).y;
				
				if(curr_x > xMax[curr_y])
					xMax[curr_y] = curr_x;
			}
			for(int i = 0; i < thirdLine.size(); i++)
			{
				int curr_x = (int) thirdLine.get(i).x;
				int curr_y = (int) thirdLine.get(i).y;
				
				if(curr_x > xMax[curr_y])
					xMax[curr_y] = curr_x;
			}
		}
		
		for(int y = yMin; y <= yMax; y++)
		{
			for(int x = xMin[y]; x <= xMax[y]; x++)
			{
				renderTexture.SetPixel(x, y, 255, 0, 0, 255);
			}
		}
		
		
		/*
		for(int i = 0; i < firstLine.size(); i++)
			renderTexture.SetPixel(
					(int) firstLine.get(i).x, 
					(int) firstLine.get(i).y, 
					(int)(255*((float)i / (float)firstLine.size())), 
					0, 
					0, 
					255
				);
		for(int i = 0; i < secondLine.size(); i++)
			renderTexture.SetPixel(
					(int) secondLine.get(i).x, 
					(int) secondLine.get(i).y, 
					0, 
					(int)(255*((float)i / (float)secondLine.size())), 
					0, 
					255
				);
		for(int i = 0; i < thirdLine.size(); i++)
			renderTexture.SetPixel(
					(int) thirdLine.get(i).x, 
					(int) thirdLine.get(i).y, 
					0, 
					0, 
					(int)(255*((float)i / (float)thirdLine.size())), 
					255
				);
				*/
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
	
	void SortVertices(Vertex v1, Vertex v2, Vertex v3)
	{
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
	}
	
	public void ClearRenderTexture(int red, int green, int blue)
	{
		renderTexture.SetToColor(red, green, blue, 255);
	}
	
	public int GetWidth() { return m_width; }
	public int GetHeight() { return m_height; }
	
	public Texture GetRenderTexture() { return renderTexture; }
}
