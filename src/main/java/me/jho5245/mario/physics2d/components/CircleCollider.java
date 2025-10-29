package me.jho5245.mario.physics2d.components;

import me.jho5245.mario.components.Component;
import me.jho5245.mario.renderer.DebugDraw;
import org.joml.Vector2f;
import org.joml.Vector3f;

public class CircleCollider extends Component
{
	private float radius = 1f;
	protected Vector2f offset = new Vector2f();

	@Override
	public void editorUpdate(float dt)
	{
		Vector2f center = new Vector2f(this.gameObject.transform.position).add(this.offset);
		DebugDraw.addCircle(center, this.radius, new Vector3f(0, 1, 0),  1);
	}

	public float getRadius()
	{
		return radius;
	}

	public void setRadius(float radius)
	{
		this.radius = radius;
	}

	public Vector2f getOffset()
	{
		return this.offset;
	}

	public void setOffset(Vector2f offset)
	{
		this.offset.set(offset);
	}
}
