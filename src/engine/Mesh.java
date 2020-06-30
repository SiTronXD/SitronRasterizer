package engine;

public class Mesh {
	Vertex[] m_vertices;
	
	int[] m_indices;
	
	public Mesh(OBJLoader objLoader)
	{
		m_vertices = objLoader.GetVertices();
		m_indices = objLoader.GetIndices();
	}
	
	public Mesh(Vertex[] vertices, int[] indices)
	{
		m_vertices = vertices;
		m_indices = indices;
	}
	
	public void Draw(Renderer renderer, int renderFlags)
	{
		for(int i = 0; i < m_indices.length; i += 3)
		{
			renderer.DrawTriangle(
				m_vertices[m_indices[i + 0]], 
				m_vertices[m_indices[i + 1]], 
				m_vertices[m_indices[i + 2]],
				renderFlags
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
