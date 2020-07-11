package engine;

public class Vertex {
	public Vector m_position;
	public Vector m_color;
	public Vector m_texCoord;
	public Vector m_normal;
	
	public Vector m_worldPosition;
	
	public Vertex()
	{
		m_position = new Vector(0.0f, 0.0f, 0.0f);
		m_color = new Vector(0.0f, 0.0f, 0.0f);
		m_texCoord = new Vector(0.0f, 0.0f);
		m_normal = new Vector(0.0f, 1.0f, 0.0f);
		
		m_worldPosition = new Vector(0.0f, 0.0f, 0.0f);
	}
	
	public Vertex(Vector position, Vector color, Vector texCoord, Vector normal)
	{
		m_position = position;
		m_color = color;
		m_texCoord = texCoord;
		m_normal = normal;

		m_worldPosition = new Vector(0.0f, 0.0f, 0.0f);
	}
	
	public Vertex(Vertex oldVertex)
	{
		m_position = new Vector(oldVertex.m_position);
		m_color = new Vector(oldVertex.m_color);
		m_texCoord = new Vector(oldVertex.m_texCoord);
		m_normal = new Vector(oldVertex.m_normal);

		m_worldPosition = new Vector(oldVertex.m_worldPosition);
	}
	
	public void TransformToScreenSpace(float width, float height, Vertex oldVertex)
	{
		Vector newPos = new Vector(
			(m_position.x * 0.5f + 0.5f) * (width-1),
			(-m_position.y * 0.5f + 0.5f) * (height-1), // Invert Y
			m_position.z,
			m_position.w
		);
		
		oldVertex.m_position = newPos;
	}
	
	public static boolean IsInsideViewFrustum(Vector transformedVertexPosition)
	{
		return 	Math.abs(transformedVertexPosition.x) <= Math.abs(transformedVertexPosition.w) && 
				Math.abs(transformedVertexPosition.y) <= Math.abs(transformedVertexPosition.w) &&
				transformedVertexPosition.z <= transformedVertexPosition.w && transformedVertexPosition.z >= 0.0f;
	}
	
	public static boolean IsInsideViewAxis(Vector transformedVertexPosition, int checkAxis)
	{
		int component = (int)(checkAxis/2.0f);
		boolean checkPositive = checkAxis % 2 == 0;
		
		// Compare component to w-value as you would expect
		if(checkAxis < 2*2+1) // component * side 
		{
			if(checkPositive)
				return transformedVertexPosition.GetComponent(component) <= transformedVertexPosition.w;
			else
				return transformedVertexPosition.GetComponent(component) >= -transformedVertexPosition.w;
		}
		// Compare z-value to 0
		else if(checkAxis == 5)
		{
			return transformedVertexPosition.GetComponent(component) >= 0.0f;
		}
		
		return true;
	}
	
	public static void Lerp(Vertex v1, Vertex v2, float t, Vertex newInfoVertex)
	{
		Vector.Lerp(v1.GetPosition(), v2.GetPosition(), t, newInfoVertex.m_position);
		Vector.Lerp(v1.GetColor(), v2.GetColor(), t, newInfoVertex.m_color);
		Vector.Lerp(v1.GetTexCoord(), v2.GetTexCoord(), t, newInfoVertex.m_texCoord);
		Vector.Lerp(v1.GetNormal(), v2.GetNormal(), t, newInfoVertex.m_normal);
		
		Vector.Lerp(v1.GetWorldPosition(), v2.GetWorldPosition(), t, newInfoVertex.m_worldPosition);
	}
	
	public static void PerspectiveCorrectLerp(Vertex v1, Vertex v2, float t, Vertex newInfoVertex)
	{
		float depth1 = v1.GetPosition().w;
		float depth2 = v2.GetPosition().w;
		
		// Don't perspective correct lerp position, 
		// since the z-position is already correct
		Vector.Lerp(v1.GetPosition(), v2.GetPosition(), t, newInfoVertex.m_position); 
		
		Vector.PerspectiveCorrectLerp(v1.GetColor(), v2.GetColor(), depth1, depth2, t, newInfoVertex.m_color);
		Vector.PerspectiveCorrectLerp(v1.GetTexCoord(), v2.GetTexCoord(), depth1, depth2, t, newInfoVertex.m_texCoord);
		Vector.PerspectiveCorrectLerp(v1.GetNormal(), v2.GetNormal(), depth1, depth2, t, newInfoVertex.m_normal);
		
		Vector.PerspectiveCorrectLerp(v1.GetWorldPosition(), v2.GetWorldPosition(), depth1, depth2, t, newInfoVertex.m_worldPosition);
	}

	public Vector GetPosition() { return m_position; }
	public Vector GetColor() { return m_color; }
	public Vector GetTexCoord() { return m_texCoord; }
	public Vector GetNormal() { return m_normal; }
	
	public Vector GetWorldPosition() { return m_worldPosition; };
}
