package engine.shaders;

import engine.Matrix;
import engine.Shader;
import engine.Texture;
import engine.Vector;
import engine.Vertex;

public class ChromaticAberrationShader extends Shader 
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
		
		// Calculate offset
		Vector offset = new Vector(0.02f, 0.02f);
		offset.x *= Math.sin(-GetTime() * 3.0f);
		offset.y *= Math.cos(-GetTime() * 3.0f);
		
		Vector col0 = new Vector();
		Vector col1 = new Vector();
		Vector col2 = new Vector();

		// Sample from different offsets
		texture.SampleColor(texCoords.x, texCoords.y, col0);
		texture.SampleColor(texCoords.x + offset.x, texCoords.y + offset.y, col1);
		texture.SampleColor(texCoords.x - offset.x, texCoords.y - offset.y, col2);
		
		// Cancel out all except one channel from each sampled color
		col0.y = 0.0f; col0.z = 0.0f;
		col1.x = 0.0f; col1.z = 0.0f;
		col2.x = 0.0f; col2.y = 0.0f;
		
		// Add them to the final color
		out_col.Add(col0);
		out_col.Add(col1);
		out_col.Add(col2);
		
		// Set byte valus to float values
		out_col.SetByteValsToFloatVals();
	}
}
