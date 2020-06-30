package engine.shaders;

import engine.Matrix;
import engine.Shader;
import engine.Texture;
import engine.Vector;
import engine.Vertex;

public class DefaultShader extends Shader 
{
	@Override
	public void VertexShader(Vertex in_vert, Vector out_vec) 
	{
		Vector newPos = Matrix.MatVecMul(GetMatrix("MVP"), in_vert.m_position);
		
		out_vec.Set(newPos);
	}
	
	@Override
	public void FragmentShader(Vertex in_vert, Vector out_col) 
	{
		Texture texture = GetTexture("DiffuseTexture");
		Vector texCoords = in_vert.m_texCoord;
		
		texture.SampleColorByte(texCoords.x, texCoords.y, out_col);
	}
}
