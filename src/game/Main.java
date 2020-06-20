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
		
		float timer = (float)(Math.PI/2.0f);
		
		Vertex v1 = new Vertex(new Vector(50, 20), new Vector(255, 0, 0));
		Vertex v2 = new Vertex(new Vector(30, 50), new Vector(0, 255, 0));
		Vertex v3 = new Vertex(new Vector(70, 50), new Vector(0, 0, 255));
		
		int x = 100; // 1280/2; 
		int y = 50; // 720/2;
		int r = 20; // 200;
		
		// Main loop
		boolean running = true;
		while(running) // JFrame is running on a different thread
		{
			renderer.ClearRenderTexture(50, 50, 50);
			
			timer += 0.001f;
			v1.GetPosition().Set(
				(float) (x + r*Math.cos(timer)), 
				(float) (y + r*Math.sin(timer)), 
				0.0f, 
				0.0f
			);
			v2.GetPosition().Set(
					(float) (x + r*Math.cos(timer + Math.PI*2.0f/3.0f)), 
					(float) (y + r*Math.sin(timer + Math.PI*2.0f/3.0f)), 
					0.0f, 
					0.0f
				);
			v3.GetPosition().Set(
					(float) (x + r*Math.cos(timer + Math.PI*4.0f/3.0f)), 
					(float) (y + r*Math.sin(timer + Math.PI*4.0f/3.0f)), 
					0.0f, 
					0.0f
				);
			
			renderer.FillTriangle(v1, v2, v3);
			
			System.out.println(timer);
			
			window.ShowBuffer(renderer.GetRenderTexture());
		}
	}
}
