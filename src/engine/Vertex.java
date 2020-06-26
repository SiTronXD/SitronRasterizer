package engine;

public class Vertex {
	Vector m_position;
	Vector m_color;
	Vector m_texCoord;
	
	public Vertex(Vector position, Vector color, Vector texCoord)
	{
		m_position = position;
		m_color = color;
		m_texCoord = texCoord;
	}
	
	public Vertex Transform(Matrix transform)
	{
		Vector newPos = Matrix.MatVecMul(transform, m_position);
		
		return new Vertex(
			newPos,
			m_color,
			m_texCoord
		);
	}
	
	public Vertex TransformToScreenSpace(float width, float height)
	{
		Vector newPos = new Vector(
			(m_position.x * 0.5f + 0.5f) * width,
			(-m_position.y * 0.5f + 0.5f) * height, // Invert Y
			m_position.z,
			m_position.w
		);
		
		return new Vertex(
			newPos,
			m_color,
			m_texCoord
		);
	}
	
	public static boolean IsInsideViewFrustum(Vector transformedVertexPosition)
	{
		return 	Math.abs(transformedVertexPosition.x) <= Math.abs(transformedVertexPosition.w) && 
				Math.abs(transformedVertexPosition.y) <= Math.abs(transformedVertexPosition.w) &&
				Math.abs(transformedVertexPosition.z) <= Math.abs(transformedVertexPosition.w);
	}
	
	public static Vertex Lerp(Vertex v1, Vertex v2, float t)
	{
		return new Vertex(
				Vector.Lerp(v1.GetPosition(), v2.GetPosition(), t),
				Vector.Lerp(v1.GetColor(), v2.GetColor(), t),
				Vector.Lerp(v1.GetTexCoord(), v2.GetTexCoord(), t)
			);
	}
	
	public static Vertex PerspectiveCorrectLerp(Vertex v1, Vertex v2, float t)
	{
		float depth1 = v1.GetPosition().w;
		float depth2 = v2.GetPosition().w;
		
		return new Vertex(
				Vector.PerspectiveCorrectLerp(v1.GetPosition(), v2.GetPosition(), depth1, depth2, t),
				Vector.PerspectiveCorrectLerp(v1.GetColor(), v2.GetColor(), depth1, depth2, t),
				Vector.PerspectiveCorrectLerp(v1.GetTexCoord(), v2.GetTexCoord(), depth1, depth2, t)
			);
	}
	
	public Vector GetPosition() { return m_position; }
	public Vector GetColor() { return m_color; }
	public Vector GetTexCoord() { return m_texCoord; }
}
