package gamestates;

import java.util.Random;

import java.awt.event.KeyEvent;

import engine.GameState;
import engine.Input;
import engine.Matrix;
import engine.Mesh;
import engine.OBJLoader;
import engine.Renderer;
import engine.Shader;
import engine.Texture;
import engine.Vector;
import engine.Vertex;
import engine.Window;
import engine.shaders.TempleOSShader;
import engine.shaders.VertexColorShader;
import game.Bird;
import game.Camera;

public class TempleOSShowcase extends GameState 
{
	Camera camera;

	Matrix perspectiveTransform;
	Matrix vpMatrixTransform;
	Matrix swordMatrixTransform;
	Matrix topMatrixTransform;
	Matrix leftSideTransform;
	Matrix rightSideTransform;
	
	Vertex v1, v2, v3, v4;
	
	Mesh swordMesh;
	Mesh topMesh;
	Mesh rightSideMesh;
	Mesh leftSideMesh;
	Mesh wholeModelMeshTest;

	Shader templeOSShader;
	Shader birdShader;
	
	Texture maskTexture;
	Texture shadowTexture;
	
	Bird[] birds;
	
	boolean renderWireframe = false;
	boolean renderPerspectiveIncorrect = false;
	int renderFlags = 0;
	
	float timer;
	
	
	@Override
	public void Init() {
		templeOSShader = new TempleOSShader();
		birdShader = new VertexColorShader();
		
		camera = new Camera(new Vector(0.0f, 0.0f, -1.6f));
		
		// Load everything
		OBJLoader objLoader = new OBJLoader("./res/gfx/TempleOSSword.obj");
		swordMesh = new Mesh(objLoader);
		swordMesh.RecalculateNormals();
		
		objLoader = new OBJLoader("./res/gfx/TempleOSTop.obj");
		topMesh = new Mesh(objLoader);
		topMesh.RecalculateNormals();

		objLoader = new OBJLoader("./res/gfx/TempleOSRightSide.obj");
		rightSideMesh = new Mesh(objLoader);
		rightSideMesh.RecalculateNormals();

		objLoader = new OBJLoader("./res/gfx/TempleOSLeftSide.obj");
		leftSideMesh = new Mesh(objLoader);
		leftSideMesh.RecalculateNormals();
		
		objLoader = new OBJLoader("./res/gfx/TempleOSWholeModel.obj");
		wholeModelMeshTest = new Mesh(objLoader);
		wholeModelMeshTest.RecalculateNormals();
		
		perspectiveTransform = Matrix.Perspective(
				(float)window.getWidth() / (float)window.getHeight(), 
				(float) Math.toRadians(90.0f), 
				0.1f, 
				100.0f
			);

		maskTexture = new Texture("./res/gfx/WhiteBlackTexture.png");
		shadowTexture = new Texture("./res/gfx/ShadowTexture.png");
		
		// Set initial textures
		renderer.SetShader(templeOSShader);
		renderer.GetShader().SetTexture("DiffuseTexture", maskTexture);
		renderer.GetShader().SetTexture("ShadowTexture", shadowTexture);
		
		Random random = new Random();
		
		birds = new Bird[7];
		for(int i = 0; i < birds.length; i++)
		{
			birds[i] = new Bird(i*(random.nextFloat()*0.3f + 0.2f));
		}
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
		
		timer += dt * 1.7f;
		
		// Local space --> World space
		Matrix startRotMat = Matrix.RotateX((float) Math.PI/2.0f);
		swordMatrixTransform = Matrix.MatMatMul(Matrix.RotateZ((float) Math.sin(timer)*0.1f), startRotMat);
		swordMatrixTransform = Matrix.MatMatMul(Matrix.Translate(0.0f, 0.0f, 0.1f), swordMatrixTransform);
		
		topMatrixTransform = Matrix.MatMatMul(Matrix.Translate(0.0f, -0.596905f, 0.0f), startRotMat);
		topMatrixTransform = Matrix.MatMatMul(Matrix.RotateZ((float) Math.sin(timer)*-0.065f), topMatrixTransform);
		topMatrixTransform = Matrix.MatMatMul(Matrix.Translate(0.0f, 0.596905f, 0.0f), topMatrixTransform);
		
		leftSideTransform = Matrix.MatMatMul(Matrix.Translate(0.0f, (float) Math.sin(timer)*0.04f, 0.0f), startRotMat);
		rightSideTransform = Matrix.MatMatMul(Matrix.Translate(0.0f, (float) Math.sin(timer)*-0.04f, 0.0f), startRotMat);;
		
		// World space --> Clip space
		vpMatrixTransform = camera.GetViewMat();
		vpMatrixTransform = Matrix.MatMatMul(perspectiveTransform, vpMatrixTransform);
		
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
		
		// Update all birds
		for(int i = 0; i < birds.length; i++)
			birds[i].Update(dt);
	}

	@Override
	public void Render(Renderer renderer) 
	{
		renderer.ClearRenderTexture((byte)0x55, (byte)0xFF, (byte)0xFF);
		renderer.ClearDepthBuffer();
		
		// Update shader
		renderer.SetShader(templeOSShader);
		
		// Render meshes
		renderer.GetShader().SetMatrix("ModelMatrix", swordMatrixTransform);
		renderer.GetShader().SetMatrix("MVP", Matrix.MatMatMul(vpMatrixTransform, swordMatrixTransform));
		swordMesh.Draw(renderer, renderFlags);

		renderer.GetShader().SetMatrix("ModelMatrix", topMatrixTransform);
		renderer.GetShader().SetMatrix("MVP", Matrix.MatMatMul(vpMatrixTransform, topMatrixTransform));
		topMesh.Draw(renderer, renderFlags);

		renderer.GetShader().SetMatrix("ModelMatrix", leftSideTransform);
		renderer.GetShader().SetMatrix("MVP", Matrix.MatMatMul(vpMatrixTransform, leftSideTransform));
		leftSideMesh.Draw(renderer, renderFlags);

		renderer.GetShader().SetMatrix("ModelMatrix", rightSideTransform);
		renderer.GetShader().SetMatrix("MVP", Matrix.MatMatMul(vpMatrixTransform, rightSideTransform));		
		rightSideMesh.Draw(renderer, renderFlags);
		
		
		// Update shader and render birds
		renderer.SetShader(birdShader);
		
		// Render all birds
		for(int i = 0; i < birds.length; i++)
			birds[i].Draw(vpMatrixTransform, renderer, renderFlags);
	}

	public TempleOSShowcase(Window window, Renderer renderer, Input input) { super(window, renderer, input); }
}
