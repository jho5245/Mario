package me.jho5245.mario.physics2d;

import me.jho5245.mario.jade.GameObject;
import org.jbox2d.callbacks.RayCastCallback;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Fixture;
import org.joml.Vector2f;

public class RaycastInfo implements RayCastCallback
{
	public Fixture fixture;
	public Vector2f point;
	public Vector2f normal;
	public float fraction;
	public boolean hit;
	public GameObject hitObject;
	private GameObject requestingObject;

	public RaycastInfo(GameObject requestingObject)
	{
		point = new Vector2f();
		normal = new Vector2f();
		this.requestingObject = requestingObject;
	}


	@Override
	public float reportFixture(Fixture fixture, Vec2 point, Vec2 normal, float fraction)
	{
		if (fixture.m_userData == requestingObject)
		{
			return 1;
		}
		this.fixture = fixture;
		this.point = new Vector2f(point.x, point.y);
		this.normal = new Vector2f(normal.x, normal.y);
		this.fraction = fraction;
		this.hit = fraction != 0;
		this.hitObject = (GameObject) fixture.m_userData;
		return fraction;
	}
}
