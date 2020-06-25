package engine;

public class Mesh {
	Vertex[] m_vertices;
	
	int[] m_indices;
	
	public Mesh(Vertex[] vertices, int[] indices)
	{
		m_vertices = vertices;
		m_indices = indices;
	}
	
	public void Draw(Renderer renderer, Matrix transform, Texture texture)
	{
		for(int i = 0; i < m_indices.length; i += 3)
		{
			renderer.DrawTriangle(
				transform, 
				m_vertices[i + 0], 
				m_vertices[i + 1], 
				m_vertices[i + 2],
				texture
			);

			/*
			renderer.DrawTriangleWireframe(
				transform, 
				m_vertices[i + 0], 
				m_vertices[i + 1], 
				m_vertices[i + 2]
			);*/
		}
	}
}
