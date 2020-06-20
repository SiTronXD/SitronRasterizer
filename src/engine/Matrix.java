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
	
	public static Matrix Translate(float x, float y, float z)
	{
		Matrix tm = new Matrix();
		
		tm.m[0][0] = 1.0f;		tm.m[0][1] = 0.0f;		tm.m[0][2] = 0.0f;		tm.m[0][3] = x;
		tm.m[1][0] = 0.0f;		tm.m[1][1] = 1.0f;		tm.m[1][2] = 0.0f;		tm.m[1][3] = y;
		tm.m[2][0] = 0.0f;		tm.m[2][1] = 0.0f;		tm.m[2][2] = 1.0f;		tm.m[2][3] = z;
		tm.m[3][0] = 0.0f;		tm.m[3][1] = 0.0f;		tm.m[3][2] = 0.0f;		tm.m[3][3] = 1.0f;
		
		return tm;
	}
	
	public static Matrix Perspective(float aspectRatio, float fov, float near, float far)
	{
		Matrix pm = new Matrix();
		
		float tanHalfFov = (float) Math.tan(Math.toRadians(fov) / 2.0f);
		float FMinusN = far - near;
		
		pm.m[0][0] = 1.0f / (aspectRatio * tanHalfFov);		pm.m[0][1] = 0.0f;					pm.m[0][2] = 0.0f;						pm.m[0][3] = 0.0f;
		pm.m[1][0] = 0.0f;									pm.m[1][1] = 1.0f / tanHalfFov;		pm.m[1][2] = 0.0f;						pm.m[1][3] = 0.0f;
		pm.m[2][0] = 0.0f;									pm.m[2][1] = 0.0f;					pm.m[2][2] = -(far + near) / FMinusN;	pm.m[2][3] = -(2.0f * far * near) / FMinusN;
		pm.m[3][0] = 0.0f;									pm.m[3][1] = 0.0f;					pm.m[3][2] = -1.0f;						pm.m[3][3] = 0.0f;
		
		return pm;
	}
	
	// Matrix to transform NDC coordinates to Screen space coordinates (requires w-component to be 1)
	public static Matrix ScreenSpace(float screenWidth, float screenHeight)
	{
		Matrix ssm = new Matrix();
		
		ssm.m[0][0] = screenWidth / 2.0f;	ssm.m[0][1] = 0.0f;					ssm.m[0][2] = 0.0f;		ssm.m[0][3] = screenWidth/2.0f;
		ssm.m[1][0] = 0.0f;					ssm.m[1][1] = screenHeight/2.0f;	ssm.m[1][2] = 0.0f;		ssm.m[1][3] = screenHeight/2.0f;
		ssm.m[2][0] = 0.0f;					ssm.m[2][1] = 0.0f;					ssm.m[2][2] = 1.0f;		ssm.m[2][3] = 0.0f;
		ssm.m[3][0] = 0.0f;					ssm.m[3][1] = 0.0f;					ssm.m[3][2] = 0.0f;		ssm.m[3][3] = 1.0f;
		
		return ssm;
	}
	
	public static Matrix MatMatMul(Matrix mat1, Matrix mat2)
	{
		int maxCols = mat2.m[0].length;
		int maxRows = mat1.m.length;
		
		Matrix newMat = new Matrix();
		newMat.m = new float[maxRows][maxCols];
		
		for(int col = 0; col < maxCols; col++)
		{
			for(int row = 0; row < maxRows; row++)
			{
				for(int k = 0; k < maxRows; k++)
					newMat.m[row][col] += mat1.m[row][k] * mat2.m[k][col];
			}
		}
		
		return newMat;
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