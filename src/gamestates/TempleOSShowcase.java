package gamestates;

import com.sun.glass.events.KeyEvent;

import engine.GameState;
import engine.Input;
import engine.Matrix;
import engine.Mesh;
import engine.OBJLoader;
import engine.Renderer;
import engine.Texture;
import engine.Vertex;
import engine.Window;
import engine.shaders.TempleOSShader;
import game.Camera;

public class TempleOSShowcase extends GameState 
{
	Camera camera;

	Matrix perspectiveTransform;
	Matrix meshTransform;
	
	Vertex v1, v2, v3, v4;
	
	Mesh swordMesh;
	Mesh topMesh;
	Mesh rightSideMesh;
	Mesh leftSideMesh;
	
	Texture maskTexture;
	
	boolean renderWireframe = false;
	boolean renderPerspectiveIncorrect = false;
	int renderFlags = 0;
	
	@Override
	public void Init() {
		renderer.SetShader(new TempleOSShader());
		
		camera = new Camera();
		
		// Load everything
		OBJLoader objLoader = new OBJLoader("./res/gfx/TempleOSSword.obj");
		swordMesh = new Mesh(objLoader);
		
		objLoader = new OBJLoader("./res/gfx/TempleOSTop.obj");
		topMesh = new Mesh(objLoader);

		objLoader = new OBJLoader("./res/gfx/TempleOSRightSide.obj");
		rightSideMesh = new Mesh(objLoader);

		objLoader = new OBJLoader("./res/gfx/TempleOSLeftSide.obj");
		leftSideMesh = new Mesh(objLoader);
		
		perspectiveTransform = Matrix.Perspective(
				(float)window.getWidth() / (float)window.getHeight(), 
				(float) Math.toRadians(90.0f), 
				0.1f, 
				100.0f
			);

		maskTexture = new Texture("./res/gfx/WhiteBlackTexture.png");
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
		meshTransform = Matrix.MatMatMul(Matrix.RotateX((float) Math.PI/2.0f), meshTransform);
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
		renderer.ClearRenderTexture((byte)0x55, (byte)0xFF, (byte)0xFF);
		renderer.ClearDepthBuffer();
		
		// Update shader
		renderer.GetShader().SetMatrix("MVP", meshTransform);
		renderer.GetShader().SetTexture("DiffuseTexture", maskTexture);
		
		// Render meshes
		swordMesh.Draw(renderer, renderFlags);
		topMesh.Draw(renderer, renderFlags);
		rightSideMesh.Draw(renderer, renderFlags);
		leftSideMesh.Draw(renderer, renderFlags);
	}

	public TempleOSShowcase(Window window, Renderer renderer, Input input) { super(window, renderer, input); }
}
