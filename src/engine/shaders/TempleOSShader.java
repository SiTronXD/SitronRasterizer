package engine.shaders;

import engine.Matrix;
import engine.Shader;
import engine.Texture;
import engine.Vector;
import engine.Vertex;

public class TempleOSShader extends Shader 
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
		
		// Color is not black, and should go through the shader as usual
		if((out_col.byte_x & 0xFF) > 0)	// (java doesn't have unsigned bytes)
		{
			out_col.byte_x = (byte)0xFD;
			out_col.byte_y = (byte)0xF6;
			out_col.byte_z = (byte)0x5A;
		}
	}
}
