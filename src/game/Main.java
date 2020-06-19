package game;

import java.util.Random;

import engine.Texture;
import engine.Window;

public class Main {
	public static void main(String[] args) {
		int width = 1280;
		int height = 720;
		
		Window window = new Window(width, height, "Sitron Rasterizer!!!");
		
		Texture tex = new Texture(width, height);

		// Main loop
		boolean running = true;
		while(running) // JFrame is running on a different thread
		{
			Random r = new Random();
			for(int x = 0; x < width; x++)
			{
				for(int y = 0; y < height; y++)
				{
					tex.SetPixel(x, y, r.nextInt() % 255, 0, 0, 255);
				}
			}
			
			window.ShowBuffer(tex);
		}
	}
}
