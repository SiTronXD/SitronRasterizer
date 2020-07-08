package engine;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class Texture {
	static final int NUM_COLOR_CHANNELS = 4;
	byte[] m_colorChannels; // Red, Green, Blue, Alpha, Red, Green....
	
	int m_width;
	int m_height;
	
	public Texture() { }
	
	public Texture(int width, int height)
	{
		m_width = width;
		m_height = height;
		
		m_colorChannels = new byte[m_width * m_height * NUM_COLOR_CHANNELS];
	}
	
	public Texture(String filePath)
	{
		// Try to load image
		BufferedImage bImage = null;
		try {
			bImage = ImageIO.read(new File(filePath));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		// Set data
		m_width = bImage.getWidth();
		m_height = bImage.getHeight();
		
		m_colorChannels = new byte[m_width * m_height * NUM_COLOR_CHANNELS];
		
		// Fill temporary variable with texture data
		int[] imgPixels = new int[m_width * m_height];
		bImage.getRGB(0, 0, m_width, m_height, imgPixels, 0, m_width);
		
		for(int i = 0; i < m_width * m_height; i++)
		{
			int currentPixel = imgPixels[i];
			
			// Shift until the 8 bits are at the end, and then just keep those 8 bits
			m_colorChannels[i * NUM_COLOR_CHANNELS + 0] = (byte) ((currentPixel >> 16 ) & 255);	// R
			m_colorChannels[i * NUM_COLOR_CHANNELS + 1] = (byte) ((currentPixel >> 8 ) 	& 255);	// G
			m_colorChannels[i * NUM_COLOR_CHANNELS + 2] = (byte) ((currentPixel >> 0) 	& 255); // B
			m_colorChannels[i * NUM_COLOR_CHANNELS + 3] = (byte) ((currentPixel >> 24) 	& 255); // A
		}
	}
	
	public void SetPixel(int x, int y, int red, int green, int blue, int alpha)
	{
		int index = (x + y * m_width) * NUM_COLOR_CHANNELS;
		
		m_colorChannels[index + 0] = (byte) (red);
		m_colorChannels[index + 1] = (byte) (green);
		m_colorChannels[index + 2] = (byte) (blue);
		m_colorChannels[index + 3] = (byte) (alpha);
	}
	
	// Copy from texture colors, to a color format recognizable by the JFrame.
	// RGBA --> BGR
	public void CopyToWindowColors(byte[] destination) 
	{
		for(int i = 0; i < m_width * m_height; i++)
		{
			destination[i*3 + 0] = m_colorChannels[i*NUM_COLOR_CHANNELS + 2];	// Blue
			destination[i*3 + 1] = m_colorChannels[i*NUM_COLOR_CHANNELS + 1];	// Green
			destination[i*3 + 2] = m_colorChannels[i*NUM_COLOR_CHANNELS + 0];	// Red
		}
	}
	
	public void SetToColor(byte red, byte green, byte blue)
	{
		for(int i = 0; i < m_width * m_height; i++)
		{
			m_colorChannels[i*NUM_COLOR_CHANNELS + 0] = red;
			m_colorChannels[i*NUM_COLOR_CHANNELS + 1] = green;
			m_colorChannels[i*NUM_COLOR_CHANNELS + 2] = blue;
		}
	}
	
	public void SetToColor(byte red, byte green, byte blue, byte alpha)
	{
		for(int i = 0; i < m_width * m_height; i++)
		{
			m_colorChannels[i*NUM_COLOR_CHANNELS + 0] = red;
			m_colorChannels[i*NUM_COLOR_CHANNELS + 1] = green;
			m_colorChannels[i*NUM_COLOR_CHANNELS + 2] = blue;
			m_colorChannels[i*NUM_COLOR_CHANNELS + 3] = alpha;
		}
	}
	
	public void SampleColor(float nx, float ny, Vector newInfoVector)
	{
		// Clamp coordinates within texture
		nx = SMath.Clamp(nx, 0.0f, 1.0f);
		ny = SMath.Clamp(ny, 0.0f, 1.0f);
		
		// Use coordinates to find index in m_colorChannels
		int realX = (int)(nx * m_width);
		int realY = (int)(ny * m_height);
		int imageIndex = realX + realY * m_width;
		imageIndex = Math.min(m_width * m_height - 1, imageIndex);
		
		// Create vector with colors
		newInfoVector.x = m_colorChannels[imageIndex * NUM_COLOR_CHANNELS + 0] & 0xFF;
		newInfoVector.y = m_colorChannels[imageIndex * NUM_COLOR_CHANNELS + 1] & 0xFF;
		newInfoVector.z = m_colorChannels[imageIndex * NUM_COLOR_CHANNELS + 2] & 0xFF;
		newInfoVector.w = m_colorChannels[imageIndex * NUM_COLOR_CHANNELS + 3] & 0xFF;
	}
	
	public void SampleColorByte(float nx, float ny, Vector newInfoVector)
	{
		// Clamp coordinates within texture
		nx = SMath.Clamp(nx, 0.0f, 1.0f);
		ny = SMath.Clamp(ny, 0.0f, 1.0f);
		
		// Use coordinates to find index in m_colorChannels
		int realX = (int)(nx * m_width);
		int realY = (int)(ny * m_height);
		int imageIndex = realX + realY * m_width;
		imageIndex = Math.min(m_width * m_height - 1, imageIndex);
		
		// Create vector with colors
		newInfoVector.byte_x = m_colorChannels[imageIndex * NUM_COLOR_CHANNELS + 0];
		newInfoVector.byte_y = m_colorChannels[imageIndex * NUM_COLOR_CHANNELS + 1];
		newInfoVector.byte_z = m_colorChannels[imageIndex * NUM_COLOR_CHANNELS + 2];
		newInfoVector.byte_w = m_colorChannels[imageIndex * NUM_COLOR_CHANNELS + 3];
	}
	
	public int GetWidth() { return m_width; }
	public int GetHeight() { return m_height; }
}
