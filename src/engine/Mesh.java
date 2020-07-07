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
	
	public void RecalculateNormals()
	{
		// Recalculate the normal for all vertices
		for(int i = 0; i < m_indices.length; i += 3)
		{
			Vertex v1 = m_vertices[m_indices[i + 0]];
			Vertex v2 = m_vertices[m_indices[i + 1]];
			Vertex v3 = m_vertices[m_indices[i + 2]];
			
			Vector edge0 = new Vector(v2.GetPosition()); edge0.Sub(v1.GetPosition());
			Vector edge1 = new Vector(v3.GetPosition()); edge1.Sub(v1.GetPosition());
			Vector normal = Vector.Cross(edge0, edge1);
			Vector.Normalize(normal);
			
			v1.m_normal.Add(normal);
			v2.m_normal.Add(normal);
			v3.m_normal.Add(normal);
		}
		
		// Normalize the final normal (average)
		for(int i = 0; i < m_vertices.length; i++)
			Vector.Normalize(m_vertices[i].m_normal);
	}
	
	public Vertex[] GetVertices() { return m_vertices; }
}
