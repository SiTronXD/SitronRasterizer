package game;

import engine.Matrix;
import engine.SMath;
import engine.Vector;

public class Camera {
	Vector cameraPosition;
	
	Vector forward;
	Vector up;
	
	float yaw;
	float pitch;
	
	public Camera()
	{
		cameraPosition = new Vector(0.0f, 0.0f, 0.0f);
		
		forward = new Vector(0.0f, 0.0f, 1.0f);
		up = new Vector(0.0f, 1.0f, 0.0f);
	}
	
	public Camera(float startYaw)
	{
		cameraPosition = new Vector(0.0f, 0.0f, 0.0f);
		
		forward = new Vector(0.0f, 0.0f, 1.0f);
		up = new Vector(0.0f, 1.0f, 0.0f);
		
		this.yaw = startYaw;
	}
	
	public Camera(Vector startPos)
	{
		cameraPosition = new Vector(startPos);
		
		forward = new Vector(0.0f, 0.0f, 1.0f);
		up = new Vector(0.0f, 1.0f, 0.0f);
	}
	
	public void Move(float r, float u, float f)
	{
		// Forward
		Vector delta = new Vector(forward);
		delta.Scale(f);
		
		// Right
		Vector rightDir = Vector.Cross(forward, up);
		rightDir = Vector.Normalize(rightDir);
		rightDir.Scale(r);
		delta.Add(rightDir);
		
		// Up
		Vector upDir = new Vector(up);
		upDir.Scale(u);
		delta.Add(upDir);
		
		cameraPosition.Add(delta);
	}
	
	public void Rotate(float h, float v)
	{
		yaw += h;
		pitch += v;
		
		pitch = SMath.Clamp(pitch, (float)(-Math.PI/2.0*0.7), (float)(Math.PI/2.0*0.7));
		
		/*
		forward = new Vector(
			(float) (Math.cos(pitch) * Math.sin(yaw)),
			(float) (Math.sin(pitch) * Math.cos(yaw)),
			(float) (Math.cos(yaw))
		);
		*/
		
		forward = new Vector(
			(float) (Math.sin(yaw) * Math.cos(pitch)),
			(float) (Math.sin(pitch)),
			(float) (Math.cos(yaw) * Math.cos(pitch))
		);
	}
	
	public Matrix GetViewMat()
	{
		return Matrix.View(cameraPosition, forward, up);
	}
}
