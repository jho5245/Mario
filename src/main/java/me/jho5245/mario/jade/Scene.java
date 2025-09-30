package me.jho5245.mario.jade;

import java.util.ArrayList;
import java.util.List;

public abstract class Scene
{
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
		gameObjects.forEach(GameObject::start);
		running = true;
	}

	public void addGameObject(GameObject gameObject)
	{
		gameObjects.add(gameObject);
		if (running)
		{
			gameObject.start();
		}
	}

	public abstract void update(float dt);
}
