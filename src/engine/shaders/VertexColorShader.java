package engine.shaders;

import engine.Matrix;
import engine.Shader;
import engine.Vector;
import engine.Vertex;

public class VertexColorShader extends Shader 
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
		out_col.Set(in_vert.m_color);
		out_col.SetByteValsToFloatVals();
	}
}