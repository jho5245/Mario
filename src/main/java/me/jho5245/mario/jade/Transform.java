package me.jho5245.mario.jade;

import org.joml.Vector2f;

public class Transform
{
	protected Vector2f position;
	protected Vector2f rotation;
	protected Vector2f scale;

	public Transform()
	{
		this(new Vector2f());
	}

	public Transform(Vector2f position)
	{
		this(position, new Vector2f());
	}

	public Transform(Vector2f position, Vector2f rotation)
	{
		this(position, rotation, new Vector2f(1, 1));
	}

	public Transform(Vector2f position, Vector2f rotation, Vector2f scale)
	{
		init(position, rotation, scale);
	}

	public void init(Vector2f position, Vector2f rotation, Vector2f scale)
	{
		this.position = position;
		this.rotation = rotation;
		this.scale = scale;
	}

	public Vector2f getPosition()
	{
		return this.position;
	}

	public Vector2f getRotation()
	{
		return this.rotation;
	}

	public Vector2f getScale()
	{
		return this.scale;
	}
}
