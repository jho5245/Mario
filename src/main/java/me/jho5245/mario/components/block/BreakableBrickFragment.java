package me.jho5245.mario.components.block;

import me.jho5245.mario.components.Component;
import me.jho5245.mario.components.Ground;
import org.joml.Vector2f;

public class BreakableBrickFragment extends Component
{
	private transient float fragmentRemoveTime = 2f;
	private transient Vector2f startPosition;

	private transient final float fragmentRotationTime = 0.05f;
	private transient float fragmentRotationTimeLeft = fragmentRotationTime;

	@Override
	public void start()
	{
		this.startPosition = new Vector2f(gameObject.transform.position);
	}

	@Override
	public void update(float dt)
	{
		fragmentRemoveTime -= dt;
		fragmentRotationTimeLeft -= dt;
		if (fragmentRotationTimeLeft <= 0)
		{
			fragmentRotationTimeLeft = fragmentRotationTime;
			gameObject.transform.rotation += gameObject.transform.rotation < 0 ? -45 : 45;
		}

		gameObject.transform.position.x += gameObject.transform.rotation <= 0 ? dt * 2 : -dt * 2;

		float functionX = 2f - fragmentRemoveTime;
		gameObject.transform.position.y = (float) (-16 * Math.pow(functionX - 0.25, 2) + 1 + startPosition.y);
		if (fragmentRemoveTime <= 0)
		{
			gameObject.destroy();
		}
	}
}
