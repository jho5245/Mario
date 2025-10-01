package me.jho5245.mario.jade;

import me.jho5245.mario.renderer.Renderer;

import java.util.ArrayList;
import java.util.List;

public abstract class Scene
{
	protected Renderer renderer = new Renderer();

	protected Camera camera;

	protected boolean running = false;

	protected final List<GameObject> gameObjects = new ArrayList<>();

	public Scene()
	{

	}

	public void init()
	{

	}

	public void start()
	{
		gameObjects.forEach(gameObject ->
		{
			gameObject.start();
			renderer.add(gameObject);
		});
		running = true;
	}

	public void addGameObject(GameObject gameObject)
	{
		gameObjects.add(gameObject);
		if (running)
		{
			gameObject.start();
			renderer.add(gameObject);
		}
	}

	public abstract void update(float dt);

	public Camera getCamera()
	{
		return camera;
	}
}
