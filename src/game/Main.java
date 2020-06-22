package game;

import java.awt.Canvas;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;

import javax.swing.JFrame;

import engine.Matrix;
import engine.Renderer;
import engine.Texture;
import engine.Vector;
import engine.Vertex;
import engine.Window;

public class Main {
	static int screenWidth = 1280;
	static int screenHeight = 720;
	
	float timer;
	
	boolean running;
	
	Window window;
	Renderer renderer;
	
	Matrix perspectiveTransform;
	Matrix transform;
	
	Vertex v1;
	Vertex v2;
	Vertex v3;
	
	public void Init() 
	{
		window = new Window(screenWidth, screenHeight, "Sitron Rasterizer!!!");
		renderer = new Renderer(screenWidth, screenHeight);
		
		perspectiveTransform = Matrix.Perspective(
			(float)screenWidth / (float)screenHeight, 
			(float) Math.toRadians(90.0f), 
			0.1f, 
			100.0f
		);
		
		v1 = new Vertex(new Vector( 0.0f,  0.5f, 0.0f), new Vector(255, 0, 0));
		v2 = new Vertex(new Vector(-0.5f, -0.5f, 0.0f), new Vector(0, 255, 0));
		v3 = new Vertex(new Vector( 0.5f, -0.5f, 0.0f), new Vector(0, 0, 255));
		
		
		// Main loop
		double lastTime = System.nanoTime();
		running = true;
		while(running) // JFrame is running on a different thread
		{
			double timeNow = System.nanoTime();
			double deltaTime = (timeNow - lastTime) / 1000000000.0;
			lastTime = System.nanoTime();
			
			double timeInMilliseconds = deltaTime * 1000.0;
			double fps = 1.0 / deltaTime; // 	1 / (deltaTime / 1000000000.0)
			
			timeInMilliseconds = (double) Math.round(timeInMilliseconds * 100.0) / 100.0;
			fps = (double) Math.round(fps);
			
			System.out.println("ms: " + timeInMilliseconds + "  (fps: " + fps + ")");
			
			
			// Eclipse's hot code replace doesn't work if the code is in this while loop :/
			Update();
			Render();
		}
	}
	
	void Update()
	{
		timer += 0.003f;
		
		transform = Matrix.Identity();
		transform = Matrix.MatMatMul(Matrix.Translate(0.0f, 0.0f, 1.0f), transform);
		transform = Matrix.MatMatMul(Matrix.RotateY(timer), transform);
		transform = Matrix.MatMatMul(Matrix.Translate(0.0f, 0.0f, 2.0f), transform);
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
	}
	
	void Render()
	{
		renderer.ClearRenderTexture(50, 50, 50);
		
		renderer.DrawTriangle(transform, v1, v2, v3);

		window.ShowBuffer(renderer.GetRenderTexture());
	}
	
	public static void main(String[] args) {  Main m = new Main(); m.Init(); }
}
