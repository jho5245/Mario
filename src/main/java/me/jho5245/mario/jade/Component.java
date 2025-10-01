package me.jho5245.mario.jade;

public abstract class Component
{
	protected GameObject gameObject = null;

	public void start()
	{

	}

	public abstract void update(float dt);

	public GameObject getGameObject()
	{
		return this.gameObject;
	}
}
