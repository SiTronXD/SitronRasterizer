package engine;

public class Vector {
	public float x, y, z, w;
	
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
	
	public void Add(Vector v)
	{
		x += v.x;
		y += v.y;
		z += v.z;
		w += v.w;
	}
	
	public void Scale(float s)
	{
		x *= s;
		y *= s;
		z *= s;
		w *= s;
	}
	
	public void Scale(float xs, float ys, float zs, float ws)
	{
		x *= xs;
		y *= ys;
		z *= zs;
		w *= ws;
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
	
	public String GetString() { return "x: " + x + "  y: " + y + "  z: " + z + "  w: " + w; }
}