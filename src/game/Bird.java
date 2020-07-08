package game;

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
	
	float timer;
	
	public Bird()
	{
		Vertex forwardVert = 	new Vertex(new Vector(    0, 0,  0.5f), new Vector(0, 0, 0), new Vector(1.0f, 0.0f), new Vector(0.0f, 1.0f, 0.0f));
		Vertex backVert = 		new Vertex(new Vector(    0, 0, -0.5f), new Vector(0, 0, 0), new Vector(1.0f, 1.0f), new Vector(0.0f, 1.0f, 0.0f));
		leftWingVert = 			new Vertex(new Vector(-0.5f, 0,  0.0f), new Vector(0, 0, 0), new Vector(0.0f, 1.0f), new Vector(0.0f, 1.0f, 0.0f));
		rightWingVert = 		new Vertex(new Vector( 0.5f, 0,  0.0f), new Vector(0, 0, 0), new Vector(0.0f, 0.0f), new Vector(0.0f, 1.0f, 0.0f));
		mesh = new Mesh(new Vertex[]{ forwardVert, backVert, leftWingVert, rightWingVert }, new int[]{ 0, 2, 1, 0, 1, 3 });
	
		transform = Matrix.Identity();
	}
	
	public void Update(float dt)
	{
		timer += dt;
		
		leftWingVert.m_position.y = (float) Math.sin(timer) * 0.5f;
		rightWingVert.m_position.y = (float) Math.sin(timer) * 0.5f;
	}
	
	public void Draw(Matrix vpMatrixTransform, Renderer renderer, int renderFlags)
	{
		transform = Matrix.Translate(0.0f, 0.0f, (float)Math.sin(timer));
		renderer.GetShader().SetMatrix("MVP", Matrix.MatMatMul(vpMatrixTransform, transform));

		renderFlags |= Renderer.RENDER_FLAGS_DISABLE_BACK_FACE_CULLING;
		mesh.Draw(renderer, renderFlags);
	}
}
