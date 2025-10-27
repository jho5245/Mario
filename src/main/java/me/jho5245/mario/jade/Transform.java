package me.jho5245.mario.jade;

import org.joml.Vector2f;

public class Transform
{
	protected Vector2f position;
	protected Vector2f scale;
	protected float rotation;

	public Transform()
	{
		this(new Vector2f());
	}

	public Transform(Vector2f position)
	{
		this(position, 0f);
	}

	public Transform(Vector2f position, float rotation)
	{
		this(position, new Vector2f(1, 1), rotation);
	}

	public Transform(Vector2f position, Vector2f scale, float rotation)
	{
		init(position, scale, rotation);
	}

	public void init(Vector2f position, Vector2f scale, float rotation)
	{
		this.position = position;
		this.rotation = rotation;
		this.scale = scale;
	}

	public Vector2f getPosition()
	{
		return this.position;
	}

	public float getRotation()
	{
		return this.rotation;
	}

	public void setRotation(float rotation)
	{
		this.rotation = rotation;
	}

	public Vector2f getScale()
	{
		return this.scale;
	}

	public Transform copy()
	{
		return new Transform(new Vector2f(this.position), new Vector2f(this.scale), this.rotation);
	}

	public void copy(Transform to)
	{
		to.position.set(this.position);
		to.scale.set(this.scale);
		to.rotation = this.rotation;
	}

	@Override
	public boolean equals(Object o)
	{
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Transform transform = (Transform) o;
		if (!position.equals(transform.position)) return false;
		if (rotation != transform.rotation) return false;
		return scale.equals(transform.scale);
	}
}
