package me.jho5245.mario.jade;

public class LevelScene extends Scene
{
	public LevelScene()
	{
		System.out.println("Inside Level Scene");
		Window.getInstance().r = 1;
		Window.getInstance().g = 1;
		Window.getInstance().b = 1;
	}

	@Override
	public void update(float dt)
	{

	}
}
