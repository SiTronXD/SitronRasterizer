package engine;

public class Vector {
	public float x, y, z, w;
	
	public Vector(float x_pos, float y_pos, float z_pos)
	{
		x = x_pos;
		y = y_pos;
		z = z_pos;
		w = 0.0f;
	}
	
	public Vector(float x_pos, float y_pos, float z_pos, float w_pos)
	{
		x = x_pos;
		y = y_pos;
		z = z_pos;
		w = w_pos;
	}
}