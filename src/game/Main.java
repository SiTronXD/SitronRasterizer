package game;

import java.util.Random;

import engine.Renderer;
import engine.Window;

public class Main {
	public static void main(String[] args) {
		int width = 1280;
		int height = 720;
		
		Window window = new Window(width, height, "Sitron Rasterizer!!!");
		
		Renderer renderer = new Renderer(width, height);
		
		float timer = 0.0f;
		
		// Main loop
		boolean running = true;
		while(running) // JFrame is running on a different thread
		{
			renderer.ClearRenderTexture(50, 50, 50);
			
			timer += 0.01f;
			renderer.DrawLine(
				50, 
				50, 
				(int) (50 + 20 * Math.cos(timer)), 
				(int) (50 + 20 * Math.sin(timer))
			);
			
			window.ShowBuffer(renderer.GetRenderTexture());
		}
	}
}
