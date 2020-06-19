package engine;

public class Texture {
	static final int NUM_COLOR_CHANNELS = 4;
	byte[] m_colorChannels; // Red, Green, Blue, Alpha, Red, Green....
	
	int m_width;
	int m_height;
	
	public Texture(int width, int height)
	{
		m_width = width;
		m_height = height;
		
		m_colorChannels = new byte[m_width * m_height * NUM_COLOR_CHANNELS];
	}
	
	public void SetPixel(int x, int y, int red, int green, int blue, int alpha)
	{
		int index = (x + y * m_width) * NUM_COLOR_CHANNELS;
		
		m_colorChannels[index + 0] = (byte) red;
		m_colorChannels[index + 1] = (byte) green;
		m_colorChannels[index + 2] = (byte) blue;
		m_colorChannels[index + 3] = (byte) alpha;
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
	
	public int GetWidth() { return m_width; }
	public int GetHeight() { return m_height; }
}
