package engine;

// Sitron math :^)
public class SMath {
	
	// Returns a value clamped between a minimum and a maximum value
	public static float Clamp(float val, float min, float max)
	{
		if(val < min)
			return min;
		if(val > max)
			return max;
		
		return val;
	}
	
	// Sine as float
	public static float Sin(float angle)
	{
		return (float)Math.sin(angle);
	}
	
	// Cosine as float
	public static float Cos(float angle)
	{
		return (float)Math.cos(angle);
	}
}
