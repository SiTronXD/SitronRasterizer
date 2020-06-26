package engine;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class Input implements KeyListener {
	boolean[] keysDown = new boolean[128];
	boolean[] previousKeysDown = new boolean[128];
	
	@Override
	public void keyPressed(KeyEvent arg0) {
		keysDown[arg0.getKeyCode()] = true;
	}

	@Override
	public void keyReleased(KeyEvent arg0) {
		keysDown[arg0.getKeyCode()] = false;
	}

	@Override
	public void keyTyped(KeyEvent arg0) {
		
	}
	
	public boolean GetKeyDown(int keyEvent)
	{
		return keysDown[keyEvent];
	}
	
	public boolean GetKeyJustPressed(int keyEvent)
	{
		// Bad approach, but I can't get it to work any other way... :/
		boolean lastPrevious = previousKeysDown[keyEvent];
		previousKeysDown[keyEvent] = keysDown[keyEvent];
		
		return keysDown[keyEvent] && !lastPrevious;
	}
	
	public boolean GetKeyJustReleased(int keyEvent)
	{
		return !keysDown[keyEvent] && previousKeysDown[keyEvent];
	}
}
