package gamestates;

import java.awt.event.KeyEvent;
import engine.GameState;
import engine.Input;
import engine.Matrix;
import engine.Mesh;
import engine.Renderer;
import engine.Texture;
import engine.Vector;
import engine.Vertex;
import engine.Window;
import engine.shaders.GerstnerWaveShader;
import game.Camera;

public class GerstnerWavesShowcaseState extends GameState 
{
	Camera camera;

	Matrix perspectiveTransform;
	Matrix meshTransform;
	
	Vertex v1, v2, v3, v4;
	
	Mesh planeMesh;
	
	Texture testTexture;
	
	boolean renderWireframe = true;
	boolean renderPerspectiveIncorrect = false;
	int renderFlags = 0;
	
	@Override
	public void Init() {
		camera = new Camera();
		
		// Create plane mesh
		int planeRes = 50;
		
		Vertex[] planeVertices = new Vertex[planeRes*planeRes];
		int[] planeIndices = new int[(planeRes-1)*(planeRes-1)*6];
		
		// Plane vertices
		for(int i = 0; i < planeVertices.length; i++)
		{
			float x = (float) (i % planeRes);
			float z = (float) Math.floor(i / planeRes);
			x /= planeRes; // From 0 to 1
			z /= planeRes; // From 0 to 1
			
			x -= 0.5f;	// From -0.5 to 0.5
			z -= 0.5f;  // From -0.5 to 0.5
			
			planeVertices[i] = new Vertex(new Vector(x,  -1.0f, z), new Vector(150, 150, 230), new Vector(0.0f, 0.0f), new Vector(0.0f, 1.0f, 0.0f));
		}
		
		// Plane indices
		for(int z = 0; z < planeRes-1; z++)
		{
			for(int x = 0; x < planeRes-1; x++)
			{
				int index = z * (planeRes-1) + x;
				
				int verticalOffset = (int) Math.floor(index / (planeRes-1));
				
				planeIndices[index*6 + 0] = index + 0 				+ verticalOffset;
				planeIndices[index*6 + 1] = index + 1 				+ verticalOffset;
				planeIndices[index*6 + 2] = index + 1 + planeRes 	+ verticalOffset;
				
				planeIndices[index*6 + 3] = index + 0				+ verticalOffset;
				planeIndices[index*6 + 4] = index + 1 + planeRes	+ verticalOffset;
				planeIndices[index*6 + 5] = index + 0 + planeRes	+ verticalOffset;
			}
		}
		planeMesh = new Mesh(planeVertices, planeIndices);
		
		
		perspectiveTransform = Matrix.Perspective(
				(float)window.getWidth() / (float)window.getHeight(), 
				(float) Math.toRadians(90.0f), 
				0.1f, 
				100.0f
			);

		renderer.SetShader(new GerstnerWaveShader());
	}

	@Override
	public void Update(float dt) 
	{
		float rotSpeed = 1.7f;
		float movementSpeed = 3.5f;
		
		float r = 0;
		float u = 0;
		float f = 0;
		if(input.GetKeyDown(KeyEvent.VK_D))
			r += movementSpeed * dt;
		if(input.GetKeyDown(KeyEvent.VK_A))
			r -= movementSpeed * dt;
		if(input.GetKeyDown(KeyEvent.VK_W))
			f += movementSpeed * dt;
		if(input.GetKeyDown(KeyEvent.VK_S))
			f -= movementSpeed * dt;
		if(input.GetKeyDown(KeyEvent.VK_Q))
			u -= movementSpeed * dt;
		if(input.GetKeyDown(KeyEvent.VK_E))
			u += movementSpeed * dt;
		
		float h = 0;
		float v = 0;
		if(input.GetKeyDown(KeyEvent.VK_RIGHT))
			h += rotSpeed * dt;
		if(input.GetKeyDown(KeyEvent.VK_LEFT))
			h -= rotSpeed * dt;
		if(input.GetKeyDown(KeyEvent.VK_UP))
			v += rotSpeed * dt;
		if(input.GetKeyDown(KeyEvent.VK_DOWN))
			v -= rotSpeed * dt;

		camera.Rotate(h, v);
		camera.Move(r, u, f);
		
		meshTransform = Matrix.Identity();
		meshTransform = Matrix.MatMatMul(Matrix.Translate(0.0f, 0.0f, 1.6f), meshTransform);
		meshTransform = Matrix.MatMatMul(camera.GetViewMat(), meshTransform);
		meshTransform = Matrix.MatMatMul(perspectiveTransform, meshTransform);
		
		// Rot Y from above
		/*
			float x = 20.0f;
			float y = 20.0f;
			renderer.GetRenderTexture().SetPixel(
				100 + (int) (Math.cos(timer)*x + Math.sin(timer)*y), 
				100 + (int) (Math.cos(timer)*y - Math.sin(timer)*x), 
				255, 0, 0, 255);
		*/
		
		// Render flags
		if(input.GetKeyJustPressed(KeyEvent.VK_R))
			renderWireframe = !renderWireframe;
		if(input.GetKeyJustPressed(KeyEvent.VK_T))
			renderPerspectiveIncorrect = !renderPerspectiveIncorrect;
		
		renderFlags = 0;
		if(renderWireframe)
			renderFlags |= Renderer.RENDER_FLAGS_WIREFRAME;
		if(renderPerspectiveIncorrect)
			renderFlags |= Renderer.RENDER_FLAGS_NON_PERSPECTIVE_CORRECT_INTERPOLATION;
	}

	@Override
	public void Render(Renderer renderer) 
	{
		renderer.ClearRenderTexture((byte)0x32, (byte)0x32, (byte)0x32);
		renderer.ClearDepthBuffer();
		
		// Update shader
		renderer.GetShader().SetMatrix("MVP", meshTransform);
		
		// Render mesh
		planeMesh.Draw(renderer, renderFlags);
	}

	public GerstnerWavesShowcaseState(Window window, Renderer renderer, Input input) { super(window, renderer, input); }
}
