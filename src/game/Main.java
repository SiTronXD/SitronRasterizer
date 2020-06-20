package game;

import engine.Renderer;
import engine.Vector;
import engine.Vertex;
import engine.Window;

public class Main {
	public static void main(String[] args) {
		int width = 1280;
		int height = 720;
		
		Window window = new Window(width, height, "Sitron Rasterizer!!!");
		
		Renderer renderer = new Renderer(width, height);
		
		float timer = 0.0f;
		
		Vertex v1 = new Vertex(new Vector(50, 20), new Vector(255, 0, 0));
		Vertex v2 = new Vertex(new Vector(30, 50), new Vector(0, 255, 0));
		Vertex v3 = new Vertex(new Vector(70, 50), new Vector(0, 0, 255));
		
		// Main loop
		boolean running = true;
		while(running) // JFrame is running on a different thread
		{
			renderer.ClearRenderTexture(50, 50, 50);
			
			/*
			timer += 0.001f;
			renderer.DrawLine(
				50, 
				50, 
				(int) (50 + 20 * Math.cos(timer)), 
				(int) (50 + 20 * Math.sin(timer))
			);
			*/
			
			renderer.FillTriangle(v1, v2, v3);
			
			window.ShowBuffer(renderer.GetRenderTexture());
		}
	}
}
