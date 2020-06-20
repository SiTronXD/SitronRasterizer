package engine;

public class Vertex {
	Vector m_position;
	Vector m_color;
	
	public Vertex(Vector position, Vector color)
	{
		m_position = position;
		m_color = color;
	}
	
	public Vertex Transform(Matrix transform)
	{
		Vector newPos = Matrix.MatVecMul(transform, m_position);
		
		return new Vertex(
			newPos,
			m_color
		);
	}
	
	public static Vertex Lerp(Vertex v1, Vertex v2, float t)
	{
		return new Vertex(
				Vector.Lerp(v1.GetPosition(), v2.GetPosition(), t),
				Vector.Lerp(v1.GetColor(), v2.GetColor(), t)
			);
	}
	
	public Vector GetPosition() { return m_position; }
	public Vector GetColor() { return m_color; }
}
