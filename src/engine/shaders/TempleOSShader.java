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
	
	void Fract(Vector fVec)
	{
		fVec.x = (float)(fVec.x - Math.floor(fVec.x));
		fVec.y = (float)(fVec.y - Math.floor(fVec.y));
		fVec.z = (float)(fVec.z - Math.floor(fVec.z));
		fVec.w = (float)(fVec.w - Math.floor(fVec.w));
	}
	
	Vector GetPseudoRandomColor(Vector n)
	{
		Vector newCol = new Vector(n);
		Vector newColCopy = new Vector(n);
		newColCopy.Add(64.853f);
		
		newCol.Scale(new Vector(573.721f, 853.712f, 653.325f, 543.234f));
		newCol.Add(new Vector(123.521f, 342.324f, 234.213f, 642.523f));
		Fract(newCol);
		
	    newCol.Add(Vector.Dot(newCol, newColCopy));
		Fract(newCol);
	    
	    return newCol;
	}
	
	@Override
	public void FragmentShader(Vertex in_vert, Vector out_col) 
	{
		Texture diffuseTexture = GetTexture("DiffuseTexture");
		Texture shadowTexture = GetTexture("ShadowTexture");
		Vector texCoords = in_vert.m_texCoord;
		
		Vector regularColor = new Vector();
		Vector shadowColor = new Vector();
		
		diffuseTexture.SampleColor(texCoords.x, texCoords.y, regularColor);
		shadowTexture.SampleColor(texCoords.x, texCoords.y, shadowColor);
		
		//out_col.byte_x = (byte)0xFD;
		//out_col.byte_y = (byte)0xF6;
		//out_col.byte_z = (byte)0x5A;
		
		Vector screenPos = new Vector(in_vert.m_position.x, in_vert.m_position.y, 1.0f, 1.0f);
		Vector r = GetPseudoRandomColor(screenPos);
		
		// Transform normal to world space
		Vector normal = in_vert.m_normal;
		normal.w = 0.0f;
		normal = Matrix.MatVecMul(GetMatrix("ModelMatrix"), normal);
		Vector.Normalize(normal);
		
		// Light
		Vector lightDir = new Vector(-3, 0.5f, 0);
		lightDir.Sub(in_vert.m_worldPosition);
		//lightDir.Scale(-1);
		Vector.Normalize(lightDir);
		float lambertLight = Vector.Dot(normal, lightDir);
		lambertLight = SMath.Clamp(lambertLight, 0.0f, 1.0f);
		float squish = 0.60f;
		lambertLight *= squish;
		lambertLight += (1.0f - squish)/2.0f;
		
		if(lambertLight*lambertLight < r.x)
			out_col.Set(shadowColor);
		else
			out_col.Set(regularColor);
		
		// Final color
		out_col.SetByteValsToFloatVals();
	}
}
