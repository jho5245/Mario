package me.jho5245.mario.jade;

import java.awt.event.KeyEvent;

public class LevelEditorScene extends Scene
{
	private boolean changingScene = false;
	private float timeToChangeScene = 2f;

	public LevelEditorScene()
	{
		System.out.println("Inside Level Editor Scene");
	}

	@Override
	public void update(float dt)
	{
		if (!changingScene && KeyListener.isKeyPressed(KeyEvent.VK_SPACE))
		{
			changingScene = true;
		}

		if (changingScene && timeToChangeScene > 0)
		{
			timeToChangeScene -= dt;
			Window.getInstance().r -= dt * 5f;
			Window.getInstance().g -= dt * 5f;
			Window.getInstance().b -= dt * 5f;
		}
		else if (changingScene)
		{
			Window.changeScene(1);
		}
	}
}
