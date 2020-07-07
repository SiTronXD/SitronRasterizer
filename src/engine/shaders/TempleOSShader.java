package engine.shaders;

import engine.Matrix;
import engine.SMath;
import engine.Shader;
import engine.Texture;
import engine.Vector;
import engine.Vertex;

public class TempleOSShader extends Shader 
{
	@Override
	public void VertexShader(Vertex in_vert, Vector out_vec) 
	{
		// World position
		Vector newWorldPos = Matrix.MatVecMul(GetMatrix("ModelMatrix"), in_vert.m_position); 
		in_vert.m_worldPosition.Set(newWorldPos); 

		// Real position
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
			
			Vector normal = in_vert.m_normal;
			normal.w = 0.0f;
			normal = Matrix.MatVecMul(GetMatrix("ModelMatrix"), normal);
			
			Vector tempCol = new Vector(255, 255, 255, 255);
			Vector lightDir = new Vector(-1, -1, -1);
			lightDir.Scale(-1);
			Vector.Normalize(lightDir);
			tempCol.Scale(Vector.Dot(normal, lightDir));
			
			tempCol.x = SMath.Clamp(tempCol.x, 0, 255);
			tempCol.y = SMath.Clamp(tempCol.y, 0, 255);
			tempCol.z = SMath.Clamp(tempCol.z, 0, 255);
			tempCol.w = SMath.Clamp(tempCol.w, 0, 255);
			
			out_col.Set(tempCol);
			out_col.SetByteValsToFloatVals();
		}
	}
}
