package me.jho5245.mario.physics2d.components;

import me.jho5245.mario.components.Component;
import me.jho5245.mario.jade.Window;
import me.jho5245.mario.renderer.DebugDraw;
import org.joml.Vector2f;
import org.joml.Vector3f;

public class CircleCollider extends Component
{
	private float radius = 1f;
	private transient boolean resetFixtureNextFrame;
	protected Vector2f offset = new Vector2f();

	@Override
	public void editorUpdate(float dt)
	{
		Vector2f center = new Vector2f(this.gameObject.transform.position).add(this.offset);
		DebugDraw.addCircle(center, this.radius, new Vector3f(0, 1, 0),  1);

		if (resetFixtureNextFrame)
		{
			resetFixture();
		}
	}

	@Override
	public void update(float dt)
	{
		if (resetFixtureNextFrame)
		{
			resetFixture();
		}
	}

	public void resetFixture()
	{
		if (Window.getPhysics().isLocked())
		{
			resetFixtureNextFrame = true;
			return;
		}
		resetFixtureNextFrame = false;
		if (gameObject != null)
		{
			Rigidbody2D rb = gameObject.getComponent(Rigidbody2D.class);
			if (rb != null)
			{
				Window.getPhysics().resetCircleCollider(rb, this);
			}
		}
	}

	public float getRadius()
	{
		return radius;
	}

	public void setRadius(float radius)
	{
		resetFixtureNextFrame = true;
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
