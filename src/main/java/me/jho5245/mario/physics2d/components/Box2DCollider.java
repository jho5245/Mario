package me.jho5245.mario.physics2d.components;

import me.jho5245.mario.components.Component;
import org.joml.Vector2f;

public class Box2DCollider extends Component
{
	private Vector2f halfSize = new Vector2f(1f);

	public Vector2f getHalfSize()
	{
		return halfSize;
	}

	public void setHalfSize(Vector2f halfSize)
	{
		this.halfSize = halfSize;
	}
}
