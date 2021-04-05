package game;

import java.awt.event.KeyEvent;

import engine.GameState;
import engine.Input;
import engine.Renderer;
import engine.Window;
import gamestates.DefaultQuadShowcaseState;
import gamestates.GerstnerWavesShowcaseState;
import gamestates.OBJModelShaderShowcaseState;
import gamestates.OBJModelShowcaseState;
import gamestates.ShaderShowcaseState;
import gamestates.TempleOSShowcase;

public class Main {
	// 1: Default textured quad mesh
	// 2: Default textured quad mesh with (fragment "shader" example)
	// 3: Model of monkey head
	// 4: Model of monkey head with (fragment "shader" example)
	// 5: Gerstner waves (vertex "shader" example)
	// 6: TempleOS example scene
	public static final int INITIAL_STATE = 1;
	
	static int screenWidth = 1280;
	static int screenHeight = 720;
	
	float timer;
	
	boolean running;
	
	Window window;
	Input input;
	Renderer renderer;
	
	GameState currentGameState;
	
	double lastTime;
	double timeInMilliseconds;
	double deltaTime;
	double fps;
	
	public void Init() 
	{
		window = new Window(screenWidth, screenHeight, "Sitron Rasterizer!!!");
		input = new Input();
		renderer = new Renderer(screenWidth, screenHeight);
		
		window.SetKeyListener(input);
		
		// Change state
		SetState(INITIAL_STATE);
		
		// Main loop
		lastTime = System.nanoTime();
		running = true;
		while(running) // JFrame is running on a different thread
		{
			// Update and print stats
			UpdateTimeStats();
			System.out.println("ms: " + timeInMilliseconds + "  (fps: " + fps + ")");
			
			// Exit
			if(input.GetKeyDown(KeyEvent.VK_ESCAPE))
				running = false;
			
			// Switch state
			if(input.GetKeyDown(KeyEvent.VK_1))
				SetState(1);
			else if(input.GetKeyDown(KeyEvent.VK_2))
				SetState(2);
			else if(input.GetKeyDown(KeyEvent.VK_3))
				SetState(3);
			else if(input.GetKeyDown(KeyEvent.VK_4))
				SetState(4);
			else if(input.GetKeyDown(KeyEvent.VK_5))
				SetState(5);
			else if(input.GetKeyDown(KeyEvent.VK_6))
				SetState(6);
			
			// Update and render game state
			currentGameState.Update((float) deltaTime);
			currentGameState.Render(renderer);
			
			renderer.Update((float) deltaTime);
			
			window.ShowBuffer(renderer.GetRenderTexture());
		}
		
		// Exit
		System.exit(0);
	}
	
	// Sets the current state
	void SetState(int newState)
	{
		switch(newState)
		{
		case 1:
			currentGameState = new DefaultQuadShowcaseState(window, renderer, input);
			break;
		case 2:
			currentGameState = new ShaderShowcaseState(window, renderer, input);
			break;
		case 3:
			currentGameState = new OBJModelShowcaseState(window, renderer, input);
			break;
		case 4:
			currentGameState = new OBJModelShaderShowcaseState(window, renderer, input);
			break;
		case 5:
			currentGameState = new GerstnerWavesShowcaseState(window, renderer, input);
			break;
		case 6:
			currentGameState = new TempleOSShowcase(window, renderer, input);
			break;
		
		default:
			currentGameState = new DefaultQuadShowcaseState(window, renderer, input);
			break;
		}
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
