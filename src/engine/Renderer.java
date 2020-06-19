package engine;

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
		//if(v1.GetPosition().y > )
	}
	
	// Bresenham's line algorithm
	public void DrawLine(int x0, int y0, int x1, int y1)
	{
		if(Math.abs(y1 - y0) < Math.abs(x1 - x0))
		{
			if(x0 > x1)
				DrawLineLow(x1, y1, x0, y0);
			else
				DrawLineLow(x0, y0, x1, y1);
		}
		else
		{
			if(y0 > y1)
				DrawLineHigh(x1, y1, x0, y0);
			else
				DrawLineHigh(x0, y0, x1, y1);
		}
	}
	
	void DrawLineLow(int x0, int y0, int x1, int y1)
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
		
		for(int x = x0; x <= x1; x++)
		{
			renderTexture.SetPixel(x, y, 255, 0, 0, 255);
			
			if(D > 0)
			{
				y += yi;
				D -= 2*dx;
			}
			D += 2*dy;
		}
	}
	
	void DrawLineHigh(int x0, int y0, int x1, int y1)
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
		
		for(int y = y0; y <= y1; y++)
		{
			renderTexture.SetPixel(x, y, 255, 0, 0, 255);
			
			if(D > 0)
			{
				x += xi;
				D -= 2*dy;
			}
			D += 2*dx;
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
