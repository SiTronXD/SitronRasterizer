package engine;

public class Renderer {
	int m_width;
	int m_height;
	
	public Renderer(int width, int height)
	{
		m_width = width;
		m_height = height;
	}
	
	public int GetWidth() { return m_width; }
	public int GetHeight() { return m_height; }
}
