package me.jho5245.mario.physics2d.components;

import me.jho5245.mario.components.Component;
import me.jho5245.mario.jade.Window;
import org.joml.Vector2f;

public class PillboxCollider extends Component
{
	private transient CircleCollider topCircle = new CircleCollider();
	private transient CircleCollider bottomCircle = new CircleCollider();
	private transient Box2DCollider centerBox = new Box2DCollider();
	// change size e.g; mario eats mushroom then grows up
	private transient boolean resetFixtureNextFrame;

	private float width = 0.1f;
	private float height = 0.2f;
	public Vector2f offset = new Vector2f();

	@Override
	public void start()
	{
		this.topCircle.gameObject = this.gameObject;
		this.bottomCircle.gameObject = this.gameObject;
		this.centerBox.gameObject = this.gameObject;
		recalculateColliders();
	}

	@Override
	public void editorUpdate(float dt)
	{
		topCircle.editorUpdate(dt);
		bottomCircle.editorUpdate(dt);
		centerBox.editorUpdate(dt);

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
				Window.getPhysics().resetPillboxCollider(rb, this);
			}
		}
	}

	public float getWidth()
	{
		return width;
	}

	public void setWidth(float width)
	{
		this.width = width;
		recalculateColliders();
		resetFixture();
	}

	public float getHeight()
	{
		return height;
	}

	public void setHeight(float height)
	{
		this.height = height;
		recalculateColliders();
		resetFixture();
	}

	public Vector2f getOffset()
	{
		return offset;
	}

	public void setOffset(Vector2f offset)
	{
		this.offset = new Vector2f(offset);
	}

	public void recalculateColliders()
	{
		float circleRadius = width / 4f;
		float boxHeight = height - 2 * circleRadius;
		topCircle.setRadius(circleRadius);
		bottomCircle.setRadius(circleRadius);
		topCircle.setOffset(new Vector2f(offset).add(0, boxHeight / 4f));
		bottomCircle.setOffset(new Vector2f(offset).sub(0, boxHeight / 4f));
		centerBox.setHalfSize(new Vector2f(width / 2f, boxHeight / 2f));
		centerBox.setOffset(offset);
	}

	public CircleCollider getTopCircle()
	{
		return topCircle;
	}

	public CircleCollider getBottomCircle()
	{
		return bottomCircle;
	}

	public Box2DCollider getCenterBox()
	{
		return centerBox;
	}
}
