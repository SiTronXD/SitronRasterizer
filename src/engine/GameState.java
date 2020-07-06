package engine;

public abstract class GameState {
	protected Window window;
	protected Renderer renderer;
	protected Input input;
	
	public GameState(Window window, Renderer renderer, Input input) 
	{
		this.window = window;
		this.renderer = renderer;
		this.input = input; 
		
		Init();
	}
	
	public abstract void Init();
	public abstract void Update(float dt);
	public abstract void Render(Renderer renderer);
}
