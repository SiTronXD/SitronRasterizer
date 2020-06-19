package engine;

public class Vertex {
	Vector m_position;
	Vector m_color;
	
	public Vertex(Vector position, Vector color)
	{
		m_position = position;
		m_color = color;
	}
	
	public Vector GetPosition() { return m_position; }
	public Vector GetColor() { return m_color; }
}
