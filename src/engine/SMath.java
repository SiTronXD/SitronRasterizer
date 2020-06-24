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
}
