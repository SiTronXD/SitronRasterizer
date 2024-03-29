package engine;

public class Vector {
	public float x, y, z, w;
	public byte byte_x, byte_y, byte_z, byte_w;
	
	public Vector()
	{
		
	}
	
	public Vector(Vector v)
	{
		x = v.x;
		y = v.y;
		z = v.z;
		w = v.w;
	}
	
	public Vector(float x_pos, float y_pos)
	{
		x = x_pos;
		y = y_pos;
		z = 0.0f;
		w = 1.0f;
	}
	
	public Vector(float x_pos, float y_pos, float z_pos)
	{
		x = x_pos;
		y = y_pos;
		z = z_pos;
		w = 1.0f;
	}
	
	public Vector(float x_pos, float y_pos, float z_pos, float w_pos)
	{
		x = x_pos;
		y = y_pos;
		z = z_pos;
		w = w_pos;
	}
	
	public Vector(byte byte_x_pos, byte byte_y_pos, byte byte_z_pos, byte byte_w_pos)
	{
		byte_x = byte_x_pos;
		byte_y = byte_y_pos;
		byte_z = byte_z_pos;
		byte_w = byte_w_pos;
	}
	
	public void Add(float a)
	{
		x += a;
		y += a;
		z += a;
		w += a;
	}
	
	public void Add(float xa, float ya, float za, float wa)
	{
		x += xa;
		y += ya;
		z += za;
		w += wa;
	}
	
	public void Add(Vector v)
	{
		x += v.x;
		y += v.y;
		z += v.z;
		w += v.w;
	}
	
	public void Sub(Vector v)
	{
		x -= v.x;
		y -= v.y;
		z -= v.z;
		w -= v.w;
	}
	
	public void Scale(float s)
	{
		x *= s;
		y *= s;
		z *= s;
		w *= s;
	}
	
	public void Scale(Vector v)
	{
		x *= v.x;
		y *= v.y;
		z *= v.z;
		w *= v.w;
	}
	
	public void Div(float xd, float yd, float zd, float wd)
	{
		x /= xd;
		y /= yd;
		z /= zd;
		w /= wd;
	}
	
	public void Set(float x_pos, float y_pos, float z_pos)
	{
		x = x_pos;
		y = y_pos;
		z = z_pos;
	}
	
	public void Set(float x_pos, float y_pos, float z_pos, float w_pos)
	{
		x = x_pos;
		y = y_pos;
		z = z_pos;
		w = w_pos;
	}
	
	public void Set(Vector v)
	{
		x = v.x;
		y = v.y;
		z = v.z;
		w = v.w;
	}
	
	// Normalizing the vector into a unit vector
	public static Vector Normalize(Vector v)
	{
		float l = (float) Math.sqrt(v.x * v.x + v.y * v.y + v.z * v.z + v.w * v.w);
		
		return new Vector(
				v.x / l,
				v.y / l,
				v.z / l,
				v.w / l
			);
	}
	
	// Returns v1 X v2
	public static Vector Cross(Vector v1, Vector v2)
	{
		return new Vector(
				-(v1.y * v2.z - v1.z * v2.y),
				-(v1.z * v2.x - v1.x * v2.z),
				-(v1.x * v2.y - v1.y * v2.x)
			);
	}
	
	// Linearly interpolate between two vectors, based on a scalar t
	public static Vector Lerp(Vector v1, Vector v2, float t)
	{
		return new Vector(
				v1.x + (v2.x - v1.x) * t,
				v1.y + (v2.y - v1.y) * t,
				v1.z + (v2.z - v1.z) * t,
				v1.w + (v2.w - v1.w) * t
			);
	}
	
	// Interpolate between two vectors, based on a scalar t, with respect to the vertices non-linear depth
	public static Vector PerspectiveCorrectLerp(Vector v1, Vector v2, float depth1, float depth2, float t)
	{
		float leftSideScale = (1.0f - t) / depth1;
		float rightSideScale = t / depth2;
		float denominator = leftSideScale + rightSideScale; 

		float x = (leftSideScale * v1.x + rightSideScale * v2.x) / denominator;
		float y = (leftSideScale * v1.y + rightSideScale * v2.y) / denominator;
		float z = (leftSideScale * v1.z + rightSideScale * v2.z) / denominator;
		float w = (leftSideScale * v1.w + rightSideScale * v2.w) / denominator;
		
		return new Vector(x, y, z, w);
	}
	
	// Linearly interpolate between two vectors, based on a scalar t
	public static void Lerp(Vector v1, Vector v2, float t, Vector newInfoVector)
	{
		newInfoVector.x = v1.x + (v2.x - v1.x) * t;
		newInfoVector.y = v1.y + (v2.y - v1.y) * t;
		newInfoVector.z = v1.z + (v2.z - v1.z) * t;
		newInfoVector.w = v1.w + (v2.w - v1.w) * t;
	}
	
	// Interpolate between two vectors, based on a scalar t, with respect to the vertices non-linear depth
	public static void PerspectiveCorrectLerp(Vector v1, Vector v2, float depth1, float depth2, float t, Vector newInfoVector)
	{
		float leftSideScale = (1.0f - t) / depth1;
		float rightSideScale = t / depth2;
		float denominator = leftSideScale + rightSideScale; 
		
		newInfoVector.x = (leftSideScale * v1.x + rightSideScale * v2.x) / denominator;
		newInfoVector.y = (leftSideScale * v1.y + rightSideScale * v2.y) / denominator;
		newInfoVector.z = (leftSideScale * v1.z + rightSideScale * v2.z) / denominator;
		newInfoVector.w = (leftSideScale * v1.w + rightSideScale * v2.w) / denominator;
	}
	
	// Dot product
	public static float Dot(Vector v1, Vector v2)
	{
		return v1.x * v2.x + v1.y * v2.y + v1.z * v2.z + v1.w * v2.w;
	}
	
	public void SetByteValsToFloatVals()
	{
		byte_x = (byte) x;
		byte_y = (byte) y;
		byte_z = (byte) z;
		byte_w = (byte) w; 
	}
	
	public float GetComponent(int componentIndex)
	{
		switch(componentIndex)
		{
		case 0:
			return x;
		case 1:
			return y;
		case 2:
			return z;
		case 3:
			return w;
		default:
			System.out.println("INCORRECT COMPONENT INDEX IN GetComponent(): " + componentIndex);
			return 0.0f;
		}
	}
	
	public String GetString() { return "x: " + x + "  y: " + y + "  z: " + z + "  w: " + w; }
}