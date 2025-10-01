package me.jho5245.mario.jade.components;

import me.jho5245.mario.jade.Component;
import org.joml.Vector4f;

public class SpriteRenderer extends Component
{
	private Vector4f color;

	public SpriteRenderer()
	{
		this(new Vector4f());
	}

	public SpriteRenderer(Vector4f color)
	{
		this.color = color;
	}

	@Override
	public void start()
	{

	}

	@Override
	public void update(float dt)
	{

	}

	public Vector4f getColor()
	{
		return color;
	}
}
