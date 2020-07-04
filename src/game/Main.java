package game;

import com.sun.glass.events.KeyEvent;

import engine.Input;
import engine.Matrix;
import engine.Mesh;
import engine.OBJLoader;
import engine.Renderer;
import engine.Texture;
import engine.Vector;
import engine.Vertex;
import engine.Window;
import engine.shaders.ChromaticAberrationShader;
import engine.shaders.DefaultShader;

public class Main {
	static int screenWidth = 1280;
	static int screenHeight = 720;
	
	float timer;
	
	boolean running;
	
	Window window;
	Input input;
	Renderer renderer;
	
	Matrix perspectiveTransform;
	Matrix transform;
	
	Camera camera;
	
	Texture testTexture;
	
	Vertex v1;
	Vertex v2;
	Vertex v3;
	Vertex v4;
	Mesh testTriangleMesh;
	Mesh objModelMesh;
	
	double lastTime;
	double timeInMilliseconds;
	double deltaTime;
	double fps;
	
	boolean renderWireframe = false;
	boolean renderPerspectiveIncorrect = false;
	int renderFlags = 0;
	
	public void Init() 
	{
		window = new Window(screenWidth, screenHeight, "Sitron Rasterizer!!!");
		input = new Input();
		renderer = new Renderer(screenWidth, screenHeight);
		//renderer.SetShader(new ChromaticAberrationShader());
		
		window.SetKeyListener(input);
		
		perspectiveTransform = Matrix.Perspective(
			(float)screenWidth / (float)screenHeight, 
			(float) Math.toRadians(90.0f), 
			0.1f, 
			100.0f
		);
		
		v1 = new Vertex(new Vector(-0.5f,  0.5f, 0.0f), new Vector(255, 0, 0), new Vector(0.0f, 0.0f));
		v2 = new Vertex(new Vector(-0.5f, -0.5f, 0.0f), new Vector(0, 255, 0), new Vector(0.0f, 1.0f));
		v3 = new Vertex(new Vector( 0.5f, -0.5f, 0.0f), new Vector(0, 0, 255), new Vector(1.0f, 1.0f));
		v4 = new Vertex(new Vector( 0.5f,  0.5f, 0.0f), new Vector(255, 255, 0), new Vector(1.0f, 0.0f));
		testTriangleMesh = new Mesh(new Vertex[]{ v1, v2, v3, v4 }, new int[]{ 0, 1, 2, 0, 2, 3 });
		
		OBJLoader objLoader = new OBJLoader("./res/gfx/monkey2.obj");
		//OBJLoader objLoader = new OBJLoader("./res/gfx/sphere.obj");
		//OBJLoader objLoader = new OBJLoader("./res/gfx/TempleOSLeftSide.obj");
		objModelMesh = new Mesh(objLoader);
		
		/*
		v1 = new Vertex(new Vector( 0.0f,  0.5f, 0.0f), new Vector(255, 0, 0), new Vector(0.5f, 0.0f));
		v2 = new Vertex(new Vector(-0.5f, -0.5f, 0.0f), new Vector(0, 255, 0), new Vector(0.0f, 1.0f));
		v3 = new Vertex(new Vector( 0.5f, -0.5f, 0.0f), new Vector(0, 0, 255), new Vector(1.0f, 1.0f));
		//v4 = new Vertex(new Vector( 0.5f,  0.5f, 0.0f), new Vector(255, 255, 0), new Vector(1.0f, 0.0f));
		testTriangleMesh = new Mesh(new Vertex[]{ v1, v2, v3, v4 }, new int[]{ 0, 1, 2 });*/
		
		camera = new Camera();
		
		testTexture = new Texture("./res/gfx/howBoutYallFellas.png");
		//testTexture = new Texture("./res/gfx/WhiteBlackTexture.png");
		
		// Main loop
		lastTime = System.nanoTime();
		running = true;
		while(running) // JFrame is running on a different thread
		{
			// Update and print stats
			UpdateTimeStats();
			System.out.println("ms: " + timeInMilliseconds + "  (fps: " + fps + ")");
			
			// Eclipse's hot code replace doesn't work if the code is in this while loop.
			// This might be since Eclipse tries to replace the whole function while the function is not active
			Update((float) deltaTime);
			Render();
			renderer.Update((float) deltaTime);
			//input.UpdatePreviousKeys();
		}
		
		// Exit
		System.exit(0);
	}
	
	void Update(float dt)
	{
		// Exit
		if(input.GetKeyDown(KeyEvent.VK_ESCAPE))
			running = false;
		
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
		
		transform = Matrix.Identity();
		//transform = Matrix.MatMatMul(Matrix.RotateY((float)Math.PI), transform);
		transform = Matrix.MatMatMul(Matrix.Translate(0.0f, 0.0f, 1.6f), transform);
		transform = Matrix.MatMatMul(camera.GetViewMat(), transform);
		transform = Matrix.MatMatMul(perspectiveTransform, transform);
		
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
	
	void Render()
	{
		renderer.ClearRenderTexture((byte)0x32, (byte)0x32, (byte)0x32);
		renderer.ClearDepthBuffer();
		
		// Update shader
		renderer.GetShader().SetMatrix("MVP", transform);
		renderer.GetShader().SetTexture("DiffuseTexture", testTexture);
		
		// Render mesh
		//testTriangleMesh.Draw(renderer, renderFlags);
		
		objModelMesh.Draw(renderer, renderFlags);
		
		window.ShowBuffer(renderer.GetRenderTexture());
	}
	
	void UpdateTimeStats()
	{
		double timeNow = System.nanoTime();
		deltaTime = (timeNow - lastTime) / 1000000000.0;
		lastTime = System.nanoTime();
		
		timeInMilliseconds = deltaTime * 1000.0;
		fps = 1.0 / deltaTime; // 	1 / (deltaTime / 1000000000.0)
		
		timeInMilliseconds = (double) Math.round(timeInMilliseconds * 100.0) / 100.0;
		fps = (double) Math.round(fps);
	}
	
	public static void main(String[] args) {  Main m = new Main(); m.Init(); }
}
