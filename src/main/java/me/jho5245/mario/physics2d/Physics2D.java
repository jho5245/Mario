package me.jho5245.mario.physics2d;

import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.World;

public class Physics2D
{
	private Vec2 gravity = new Vec2(0f, -10f);
	private World world = new World(gravity);

	private float physicsTime = 0f;
	private float physicsTimeStep = 1f / 60f;
	private int velocityIterations = 8;
	private int positionIterations = 3;

	public void update(float dt)
	{
		// 0 + 16ms
		// 16ms - 16ms


		// 14ms
		// 14ms - 16ms = -2ms
		// -2ms
		// -2ms + 16ms = 14ms
		// 14ms - 16ms = -2ms
		physicsTime += dt;
		if (physicsTime >= 0f)
		{
			physicsTime -= physicsTimeStep;
			world.step(physicsTimeStep, velocityIterations, positionIterations);
		}
	}
}
