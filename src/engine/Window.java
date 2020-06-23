package engine;

import java.awt.Canvas;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.KeyListener;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;

import javax.swing.JFrame;

public class Window extends Canvas 
{
	final BufferStrategy m_bufferStrategy;
	final Graphics m_graphics;
	
	final BufferedImage m_windowTexture;
	final byte[] m_windowColorChannels;
	
	Dimension m_windowSize;
	
	public Window(int width, int height, String title)
	{
		// Window size
		m_windowSize = new Dimension(width, height);
		setPreferredSize(m_windowSize);
		setMinimumSize(m_windowSize);
		setMaximumSize(m_windowSize);
		
		// Create window
		JFrame jframe = new JFrame();
		jframe.add(this);
		jframe.pack();
		jframe.setResizable(false);
		jframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		jframe.setLocationRelativeTo(null);
		jframe.setTitle(title);
		jframe.setVisible(true);
		
		// Request focus so the KeyListener will work when the window pops up
		requestFocus();
		
		// Create strategy for buffering
		createBufferStrategy(1);
		m_bufferStrategy = getBufferStrategy();
		m_graphics = m_bufferStrategy.getDrawGraphics();
		
		m_windowTexture = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
		m_windowColorChannels = ((DataBufferByte) m_windowTexture.getRaster().getDataBuffer()).getData();
	}
	
	// Draw and show buffer using a texture
	public void ShowBuffer(Texture textureToDraw)
	{
		// Copy colors from texture to window color channels
		textureToDraw.CopyToWindowColors(m_windowColorChannels);
		
		// Draw image
		m_graphics.drawImage(
			m_windowTexture, 
			0, 
			0, 
			m_windowSize.width + 10, 	// Weird borders? Simple fix
			m_windowSize.height + 10, 
			null
		);
		
		// Show and switch buffer
		m_bufferStrategy.show(); 
	}
	
	public void SetKeyListener(KeyListener kl)
	{
		addKeyListener(kl);
	}
}
