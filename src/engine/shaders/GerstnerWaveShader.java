package engine.shaders;

import engine.Matrix;
import engine.Shader;
import engine.Vector;
import engine.Vertex;
import static engine.SMath.*;

public class GerstnerWaveShader extends Shader 
{
	Vector GerstnerWave(Vector wave, Vector p)
	{
		float steepness = wave.z;
		float waveLength = wave.w;

		float k = (float) (2.0 * Math.PI / waveLength);
		float c = (float) Sqrt(9.81f / k); // Phase speed 
		Vector d = new Vector(wave.x, wave.y, 0.0f, 0.0f);
		float f = k * (Vector.Dot(new Vector(p.x, p.z, 0.0f, 0.0f), d) - c * GetTime());
		float a = steepness / k;	// Amplitude

		return new Vector(
			d.x * a * Cos(f),
			a * Sin(f),
			d.y * a * Cos(f),
			0.0f
		);
	}
	
	// Keep only the decimals of each component in the vector
	void Fract(Vector fVec)
	{
		fVec.x = (float)(fVec.x - Math.floor(fVec.x));
		fVec.y = (float)(fVec.y - Math.floor(fVec.y));
		fVec.z = (float)(fVec.z - Math.floor(fVec.z));
		fVec.w = (float)(fVec.w - Math.floor(fVec.w));
	}
	
	// Got this method from Youtuber "The Art of Code"
	Vector GetPseudoRandomColor(float n)
	{
		Vector newCol = new Vector(n, n, n);
		Vector newColCopy = new Vector(n, n, n);
		newColCopy.Add(64.853f);
		
		newCol.Scale(new Vector(573.721f, 853.712f, 653.325f, 543.234f));
		newCol.Add(new Vector(123.521f, 342.324f, 234.213f, 642.523f));
		Fract(newCol);
		
	    newCol.Add(Vector.Dot(newCol, newColCopy));
		Fract(newCol);
	    
	    return newCol;
	}
	
	@Override
	public void VertexShader(Vertex in_vert, Vector out_vec) 
	{
		Vector newPos = new Vector(in_vert.m_position);
		newPos.Scale(4.0f);
		
		// Gerstner waves
		int numIterations = 6;
		for (int i = 0; i < numIterations; i++)
		{
			Vector wave = new Vector(
				GetPseudoRandomColor(i*4).x,
				GetPseudoRandomColor((i+1)*4).x,
				GetPseudoRandomColor((i+2)*4).x / numIterations * 2.0f,
				GetPseudoRandomColor((i+3)*4).x
			);

			newPos.Add(GerstnerWave(wave, newPos));
		}
		
		// Local space to clip space
		newPos = Matrix.MatVecMul(GetMatrix("MVP"), newPos);
		
		out_vec.Set(newPos);
	}
	
	@Override
	public void FragmentShader(Vertex in_vert, Vector out_col) 
	{
		out_col.Set(in_vert.m_color);
		out_col.SetByteValsToFloatVals();
	}
}
