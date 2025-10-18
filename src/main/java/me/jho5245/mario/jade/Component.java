package me.jho5245.mario.jade;

public abstract class Component
{
	protected transient GameObject gameObject = null;

	public void start()
	{

	}

	public void update(float dt)
	{

	}

	public GameObject getGameObject()
	{
		return this.gameObject;
	}

	public void imgui()
	{

	}
}
