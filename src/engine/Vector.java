package engine;

public class Vector {
	public float x, y, z, w;
	
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
	
	public String GetString() { return "x: " + x + "  y: " + y + "  z: " + z + "  w: " + w; }
}