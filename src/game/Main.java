package game;

import com.sun.glass.events.KeyEvent;

import engine.GameState;
import engine.Input;
import engine.Renderer;
import engine.Window;
import gamestates.DefaultQuadShowcaseState;
import gamestates.OBJModelShaderShowcaseState;
import gamestates.OBJModelShowcaseState;
import gamestates.ShaderShowcaseState;
import gamestates.TempleOSShowcase;

public class Main {
	// 0: Default quad mesh
	// 1: Default quad mesh with "custom shader"
	// 2: Model of monkey head
	// 3: Model of monkey head with "custom shader"
	// 4: Epicly!!! :^)
	public static final int CURRENT_STATE = 4;
	
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
		switch(CURRENT_STATE)
		{
		case 0:
			currentGameState = new DefaultQuadShowcaseState(window, renderer, input);
			break;
		case 1:
			currentGameState = new ShaderShowcaseState(window, renderer, input);
			break;
		case 2:
			currentGameState = new OBJModelShowcaseState(window, renderer, input);
			break;
		case 3:
			currentGameState = new OBJModelShaderShowcaseState(window, renderer, input);
			break;
		case 4:
			currentGameState = new TempleOSShowcase(window, renderer, input);
			break;
		
		default:
			currentGameState = new DefaultQuadShowcaseState(window, renderer, input);
			break;
		}
		
		
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
			
			// Update and render game state
			currentGameState.Update((float) deltaTime);
			currentGameState.Render(renderer);
			
			renderer.Update((float) deltaTime);
			
			window.ShowBuffer(renderer.GetRenderTexture());
		}
		
		// Exit
		System.exit(0);
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
