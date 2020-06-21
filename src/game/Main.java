package game;

import engine.Matrix;
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
		
		Matrix screenSpaceTransform = Matrix.ScreenSpace(width, height);
		Matrix perspectiveTransform = Matrix.Perspective(
			(float)width / (float)height, 
			(float) Math.toRadians(90.0f), 
			0.1f, 
			100.0f
		);
		
		float timer = 0.0f;
		
		Vertex v1 = new Vertex(new Vector( 0.0f,  0.5f, 0.0f), new Vector(255, 0, 0));
		Vertex v2 = new Vertex(new Vector(-0.5f, -0.5f, 0.0f), new Vector(0, 255, 0));
		Vertex v3 = new Vertex(new Vector( 0.5f, -0.5f, 0.0f), new Vector(0, 0, 255));
		
		
		// Main loop
		double lastTime = System.nanoTime();
		boolean running = true;
		while(running) // JFrame is running on a different thread
		{
			double timeNow = System.nanoTime();
			double deltaTime = timeNow - lastTime;
			lastTime = System.nanoTime();
			
			double timeInMilliseconds = deltaTime / 1000000.0;
			double fps = 1000000000.0 / deltaTime; // 1 / (deltaTime / 1000000000.0)
			
			timeInMilliseconds = (double) Math.round(timeInMilliseconds * 100.0) / 100.0;
			fps = (double) Math.round(fps);
			
			System.out.println("ms: " + timeInMilliseconds + "  (fps: " + fps + ")");
			
			
			renderer.ClearRenderTexture(50, 50, 50);
			
			timer += 0.003f;
			
			Matrix t = Matrix.Identity();
			t = Matrix.MatMatMul(Matrix.Translate(0.0f, 0.0f, 1.0f), t);
			t = Matrix.MatMatMul(Matrix.RotateY(timer), t);
			t = Matrix.MatMatMul(Matrix.Translate(0.0f, 0.0f, 2.0f), t);
			t = Matrix.MatMatMul(perspectiveTransform, t);
			
			renderer.DrawTriangle(t, v1, v2, v3);
			
			// Rot Y from above
			/*
			float x = 20.0f;
			float y = 20.0f;
			renderer.GetRenderTexture().SetPixel(
				100 + (int) (Math.cos(timer)*x + Math.sin(timer)*y), 
				100 + (int) (Math.cos(timer)*y - Math.sin(timer)*x), 
				255, 0, 0, 255);
				*/
			
			window.ShowBuffer(renderer.GetRenderTexture());
		}
	}
}
