package engine;

public class Matrix {
	public float[][] m;
	
	public Matrix()
	{
		m = new float[4][4];
	}
	
	public static Matrix Identity() 
	{
		Matrix ssm = new Matrix();
		
		ssm.m[0][0] = 1.0f;		ssm.m[0][1] = 0.0f;		ssm.m[0][2] = 0.0f;		ssm.m[0][3] = 0.0f;
		ssm.m[1][0] = 0.0f;		ssm.m[1][1] = 1.0f;		ssm.m[1][2] = 0.0f;		ssm.m[1][3] = 0.0f;
		ssm.m[2][0] = 0.0f;		ssm.m[2][1] = 0.0f;		ssm.m[2][2] = 1.0f;		ssm.m[2][3] = 0.0f;
		ssm.m[3][0] = 0.0f;		ssm.m[3][1] = 0.0f;		ssm.m[3][2] = 0.0f;		ssm.m[3][3] = 1.0f;
		
		return ssm;
	}
	
	public static Matrix ScreenSpace(float screenWidth, float screenHeight)
	{
		Matrix ssm = new Matrix();
		
		ssm.m[0][0] = screenWidth / 2.0f;	ssm.m[0][1] = 0.0f;					ssm.m[0][2] = 0.0f;		ssm.m[0][3] = screenWidth/2.0f;
		ssm.m[1][0] = 0.0f;					ssm.m[1][1] = screenHeight/2.0f;	ssm.m[1][2] = 0.0f;		ssm.m[1][3] = screenHeight/2.0f;
		ssm.m[2][0] = 0.0f;					ssm.m[2][1] = 0.0f;					ssm.m[2][2] = 1.0f;		ssm.m[2][3] = 0.0f;
		ssm.m[3][0] = 0.0f;					ssm.m[3][1] = 0.0f;					ssm.m[3][2] = 0.0f;		ssm.m[3][3] = 1.0f;
		
		return ssm;
	}
	
	public static Vector MatVecMul(Matrix mat, Vector columnVector)
	{
		Vector newVec = new Vector(
			mat.m[0][0] * columnVector.x +	mat.m[0][1] * columnVector.y + 	mat.m[0][2] * columnVector.z + 	mat.m[0][3] * columnVector.w,
			mat.m[1][0] * columnVector.x + 	mat.m[1][1] * columnVector.y + 	mat.m[1][2] * columnVector.z + 	mat.m[1][3] * columnVector.w,
			mat.m[2][0] * columnVector.x + 	mat.m[2][1] * columnVector.y + 	mat.m[2][2] * columnVector.z + 	mat.m[2][3] * columnVector.w,
			mat.m[3][0] * columnVector.x + 	mat.m[3][1] * columnVector.y +	mat.m[3][2] * columnVector.z + 	mat.m[3][3] * columnVector.w
		);
		
		return newVec;
	}
}