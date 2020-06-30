package engine;

import java.util.HashMap;

public class Shader {
	HashMap<String, Matrix> m_matrices;
	HashMap<String, Texture> m_textures;
	HashMap<String, Vector> m_vectors;
	
	float m_passedTime;
	
	// Vertex shader that expects the final vector coordinates to be in Clip space
	public void VertexShader(Vertex in_vert, Vector out_pos) {}
	
	// Fragment shader that expects the final vector to be the color of the fragment
	public void FragmentShader(Vertex in_vert, Vector out_col) {}
	
	public void Update(float dt)
	{
		m_passedTime += dt;
	}
	
	public Shader()
	{
		m_matrices = new HashMap<String, Matrix>();
		m_textures = new HashMap<String, Texture>();
		m_vectors = new HashMap<String, Vector>();
	}
	
	public void SetMatrix(String name, Matrix mat) { m_matrices.put(name, mat); }
	public void SetTexture(String name, Texture tex) { m_textures.put(name, tex); }
	public void SetVector(String name, Vector vec) { m_vectors.put(name, vec); }
	
	protected Matrix GetMatrix(String name) { return m_matrices.get(name); }
	protected Texture GetTexture(String name) { return m_textures.get(name); }
	protected Vector GetVector(String name) { return m_vectors.get(name); }
	protected float GetTime() { return m_passedTime; }
}
