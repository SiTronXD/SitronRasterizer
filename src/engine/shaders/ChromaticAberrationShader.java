package engine.shaders;

import engine.Matrix;
import engine.Shader;
import engine.Texture;
import engine.Vector;
import engine.Vertex;
import static engine.SMath.*;

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
		offset.x *= Sin(-GetTime() * 3.0f);
		offset.y *= Cos(-GetTime() * 3.0f);
		
		Vector tempSampleCol = new Vector();

		// Sample and add from textures with different offsets to the uvs
		texture.SampleColor(texCoords.x, texCoords.y, tempSampleCol);
		out_col.Add(tempSampleCol.x, 0.0f, 0.0f, 0.0f); // Add red
		
		texture.SampleColor(texCoords.x + offset.x, texCoords.y + offset.y, tempSampleCol);
		out_col.Add(0.0f, tempSampleCol.y, 0.0f, 0.0f); // Add green
		
		texture.SampleColor(texCoords.x - offset.x, texCoords.y - offset.y, tempSampleCol);
		out_col.Add(0.0f, 0.0f, tempSampleCol.z, 0.0f); // Add blue
		
		// Set byte valus to float values
		out_col.SetByteValsToFloatVals();
	}
}
