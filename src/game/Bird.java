package game;

import java.util.Random;

import engine.Matrix;
import engine.Mesh;
import engine.Renderer;
import engine.Vector;
import engine.Vertex;

public class Bird {
	Mesh mesh;
	
	Matrix transform;
	
	Vertex leftWingVert;
	Vertex rightWingVert;
	
	float wingsTimer;
	float movementTimer;
	
	float currentY;
	float movementRadius = 1.0f;
	
	float yLimit = 1.5f;
	
	public Bird(float offset)
	{
		Vertex forwardVert = 	new Vertex(new Vector(    0, 0,  0.5f), new Vector(255, 255, 255), new Vector(1.0f, 0.0f), new Vector(0.0f, 1.0f, 0.0f));
		Vertex backVert = 		new Vertex(new Vector(    0, 0, -0.5f), new Vector(255, 255, 255), new Vector(1.0f, 1.0f), new Vector(0.0f, 1.0f, 0.0f));
		leftWingVert = 			new Vertex(new Vector(-0.5f, 0,  0.0f), new Vector(255, 255, 255), new Vector(0.0f, 1.0f), new Vector(0.0f, 1.0f, 0.0f));
		rightWingVert = 		new Vertex(new Vector( 0.5f, 0,  0.0f), new Vector(255, 255, 255), new Vector(0.0f, 0.0f), new Vector(0.0f, 1.0f, 0.0f));
		mesh = new Mesh(new Vertex[]{ forwardVert, backVert, leftWingVert, rightWingVert }, new int[]{ 0, 2, 1, 0, 1, 3 });
	
		currentY -= offset;
		movementTimer -= offset;
		
		// Bird should independently randomize it's own wing timer and movement radius
		Random random = new Random();
		wingsTimer += random.nextFloat() * 10.0f;
		movementRadius = random.nextFloat()*0.5f + 1.0f;
	}
	
	public void Update(float dt)
	{
		wingsTimer += dt * 20.0f;
		movementTimer += dt;
		currentY += dt * 0.1f;
		
		if(currentY >= yLimit)
		{
			currentY -= yLimit*2.0f;
		}
		
		leftWingVert.m_position.y = (float) Math.sin(wingsTimer) * 0.5f;
		rightWingVert.m_position.y = (float) Math.sin(wingsTimer) * 0.5f;
	}
	
	public void Draw(Matrix vpMatrixTransform, Renderer renderer, int renderFlags)
	{
		float x = (float) Math.sin(movementTimer) * movementRadius;
		float z = (float) Math.cos(movementTimer) * movementRadius * 0.7f;
		transform = Matrix.Scale(0.1f, 0.1f, 0.1f);
		transform = Matrix.MatMatMul(Matrix.RotateY((float) (Math.atan2(x, z) + Math.PI/2.0f)), transform);
		transform = Matrix.MatMatMul(Matrix.Translate(x, currentY, z), transform);
		
		renderer.GetShader().SetMatrix("MVP", Matrix.MatMatMul(vpMatrixTransform, transform));

		renderFlags |= Renderer.RENDER_FLAGS_DISABLE_BACK_FACE_CULLING;
		mesh.Draw(renderer, renderFlags);
	}
}
