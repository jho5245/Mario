package me.jho5245.mario.physics2d;

import me.jho5245.mario.components.Ground;
import me.jho5245.mario.components.Transform;
import me.jho5245.mario.jade.GameObject;
import me.jho5245.mario.jade.Window;
import me.jho5245.mario.physics2d.components.Box2DCollider;
import me.jho5245.mario.physics2d.components.CircleCollider;
import me.jho5245.mario.physics2d.components.PillboxCollider;
import me.jho5245.mario.physics2d.components.Rigidbody2D;
import me.jho5245.mario.util.Settings;
import org.jbox2d.collision.shapes.CircleShape;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.*;
import org.joml.Vector2f;

public class Physics2D
{
	private Vec2 gravity;
	private World world;
	private float physicsTime = 0.0f;

	private boolean isPlaying;

	public Physics2D(boolean isPlaying)
	{
		this.isPlaying = isPlaying;
		this.gravity = new Vec2(0, -30f);
		this.world = new World(gravity);
		world.setContactListener(new JadeContactListener());
		world.setDebugDraw(new DebugDrawJ2D());
	}

	public Vector2f getGravity()
	{
		return new Vector2f(this.world.getGravity().x, this.world.getGravity().y);
	}

	public void add(GameObject gameObject)
	{
		Rigidbody2D rb = gameObject.getComponent(Rigidbody2D.class);
		if (rb != null && rb.getRawBody() == null)
		{
			Transform transform = gameObject.transform;

			BodyDef bodyDef = new BodyDef();
			bodyDef.position.set(transform.position.x, transform.position.y);
			bodyDef.angle = (float) Math.toRadians(transform.rotation);
			bodyDef.angularDamping = rb.getAngularDamping();
			bodyDef.linearDamping = rb.getLinearDamping();
			bodyDef.fixedRotation = rb.isFixedRotation();
			bodyDef.bullet = rb.isContinuousCollision();
			bodyDef.gravityScale = rb.gravityScale;
			bodyDef.angularVelocity = rb.angularVelocity;
			bodyDef.userData = rb.gameObject;

			switch (rb.getBodyType())
			{
				case KINEMATIC -> bodyDef.type = BodyType.KINEMATIC;
				case STATIC -> bodyDef.type = BodyType.STATIC;
				case DYNAMIC -> bodyDef.type = BodyType.DYNAMIC;
			}

			Body body = this.world.createBody(bodyDef);
			body.m_mass = rb.getMass();
			rb.setRawBody(body);
			CircleCollider circleCollider;
			Box2DCollider box2DCollider;
			PillboxCollider pillboxCollider;

			if ((circleCollider = gameObject.getComponent(CircleCollider.class)) != null)
			{
				addCircleCollider(rb, circleCollider);
			}
			if ((box2DCollider = gameObject.getComponent(Box2DCollider.class)) != null)
			{
				addBox2DCollider(rb, box2DCollider);
			}
			if ((pillboxCollider = gameObject.getComponent(PillboxCollider.class)) != null)
			{
				addPillboxCollider(rb, pillboxCollider);
			}

			if (isPlaying)
			{
				rb.setIsPlaying(true);
			}
		}
	}

	public void destroyGameObject(GameObject go)
	{
		Rigidbody2D rb = go.getComponent(Rigidbody2D.class);
		if (rb != null)
		{
			if (rb.getRawBody() != null)
			{
				world.destroyBody(rb.getRawBody());
				rb.setRawBody(null);
			}
		}
	}

	public void update(float dt)
	{
		if (!isPlaying)
			return;

		physicsTime += dt;
		if (physicsTime >= 0f)
		{
			physicsTime -= Settings.PHYSICS_TIMESTEP;
			world.step(Settings.PHYSICS_TIMESTEP, Settings.PHYSICS_VELOCITY_ITERATIONS, Settings.PHYSICS_POSITION_ITERATIONS);
		}
	}

	public boolean isPlaying()
	{
		return isPlaying;
	}

	public void setPlaying(boolean playing)
	{
		isPlaying = playing;
	}

	public void debugDraw()
	{
		world.drawDebugData();
	}

	public void setIsSensor(Rigidbody2D rb)
	{
		Body body = rb.getRawBody();
		if (body == null) return;

		Fixture fixture = body.getFixtureList();
		while (fixture != null)
		{
			fixture.m_isSensor = true;
			fixture = fixture.m_next;
		}
	}

	public void setNotSensor(Rigidbody2D rb)
	{
		Body body = rb.getRawBody();
		if (body == null) return;

		Fixture fixture = body.getFixtureList();
		while (fixture != null)
		{
			fixture.m_isSensor = false;
			fixture = fixture.m_next;
		}
	}

	public void addBox2DCollider(Rigidbody2D rb, Box2DCollider box2DCollider)
	{
		Body body = rb.getRawBody();
		assert body != null: "Raw body must not be null";
		PolygonShape shape = new PolygonShape();
		Vector2f halfSize = new Vector2f(box2DCollider.getHalfSize()).mul(0.5f);
		Vector2f offset = box2DCollider.getOffset();
		Vector2f origin = new Vector2f(box2DCollider.getOrigin());
		shape.setAsBox(halfSize.x, halfSize.y, new Vec2(offset.x, offset.y), 0);

		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.shape = shape;
		fixtureDef.density = 1f;
		fixtureDef.friction = rb.getFriction();
		fixtureDef.userData = box2DCollider.gameObject;
		fixtureDef.isSensor = rb.isSensor();
		body.createFixture(fixtureDef);
	}

	public void resetBox2DCollider(Rigidbody2D rb, Box2DCollider box2DCollider)
	{
		Body body = rb.getRawBody();
		if (body == null) return;

		int size = fixtureListSize(body);
		for (int i = 0; i < size; i++)
		{
			body.destroyFixture(body.getFixtureList());
		}

		addBox2DCollider(rb, box2DCollider);
		body.resetMassData();
	}

	public void addCircleCollider(Rigidbody2D rb, CircleCollider circleCollider)
	{
		Body body = rb.getRawBody();
		assert body != null: "Raw body must not be null";
		CircleShape shape = new CircleShape();
		shape.setRadius(circleCollider.getRadius());
		shape.m_p.set(circleCollider.getOffset().x, circleCollider.getOffset().y);

		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.shape = shape;
		fixtureDef.density = 1f;
				fixtureDef.friction = rb.getFriction();
		fixtureDef.userData = circleCollider.gameObject;
				fixtureDef.isSensor = rb.isSensor();
		body.createFixture(fixtureDef);
	}

	public void resetCircleCollider(Rigidbody2D rb, CircleCollider circleCollider)
	{
		Body body = rb.getRawBody();
		if (body == null) return;

		int size = fixtureListSize(body);
		for (int i = 0; i < size; i++)
		{
			body.destroyFixture(body.getFixtureList());
		}

		addCircleCollider(rb, circleCollider);
		body.resetMassData();
	}

	public void addPillboxCollider(Rigidbody2D rb, PillboxCollider pillboxCollider)
	{
		Body body = rb.getRawBody();
		assert body != null: "Raw body must not be null";

		addBox2DCollider(rb, pillboxCollider.getBox());
//		addCircleCollider(rb, pillboxCollider.getTopCircle());
		addCircleCollider(rb, pillboxCollider.getBottomCircle());
	}

	public void resetPillboxCollider(Rigidbody2D rb, PillboxCollider pillboxCollider)
	{
		Body body = rb.getRawBody();
		if (body == null) return;

		int size = fixtureListSize(body);
		for (int i = 0; i < size; i++)
		{
			body.destroyFixture(body.getFixtureList());
		}

		addPillboxCollider(rb, pillboxCollider);
		body.resetMassData();
	}

	private int fixtureListSize(Body body)
	{
		int size = 0;
		Fixture fixture = body.getFixtureList();
		while (fixture != null)
		{
			size++;
			fixture = fixture.m_next;
		}
		return size;
	}

	public boolean isLocked()
	{
		return world.isLocked();
	}

	public RaycastInfo rayCast(GameObject requestingObject, Vector2f point1, Vector2f point2)
	{
		RaycastInfo callback = new RaycastInfo(requestingObject);
		world.raycast(callback, new Vec2(point1.x, point1.y), new Vec2(point2.x, point2.y));
		return callback;
	}

	public static boolean checkOnGround(GameObject gameObject, float innerPlayerWidth, float height)
	{
		Vector2f raycastBegin = new Vector2f(gameObject.transform.position);
		raycastBegin.sub(innerPlayerWidth / 2f, 0f);
		Vector2f raycastEnd = new Vector2f(raycastBegin).add(0f, height);
		RaycastInfo info = Window.getPhysics().rayCast(gameObject, raycastBegin, raycastEnd);
		Vector2f raycast2Begin = new Vector2f(raycastBegin).add(innerPlayerWidth, 0f);
		Vector2f raycast2End = new Vector2f(raycastEnd).add(innerPlayerWidth, 0f);
		RaycastInfo info2 = Window.getPhysics().rayCast(gameObject, raycast2Begin, raycast2End);

//		DebugDraw.addLine2D(raycastBegin, raycastEnd, new Vector3f(1,0,0));
//		DebugDraw.addLine2D(raycast2Begin, raycast2End, new Vector3f(1,0,0));

		return (info.hit && info.hitObject != null && info.hitObject.getComponent(Ground.class) != null) || (info2.hit && info2.hitObject != null
				&& info2.hitObject.getComponent(Ground.class) != null);
	}

	public static boolean checkCeling(GameObject gameObject, float innerPlayerWidth, float height)
	{
		Vector2f raycastEnd = new Vector2f(gameObject.transform.position).add(innerPlayerWidth / 2f, 0.1f);
		Vector2f raycastBegin = new Vector2f(raycastEnd).add(0f, height);
		RaycastInfo info = Window.getPhysics().rayCast(gameObject, raycastBegin, raycastEnd);

		Vector2f raycast2Begin = new Vector2f(raycastBegin).add(-innerPlayerWidth, 0f);
		Vector2f raycast2End = new Vector2f(raycastEnd).add(-innerPlayerWidth, 0f);
		RaycastInfo info2 = Window.getPhysics().rayCast(gameObject, raycast2Begin, raycast2End);

//		DebugDraw.addLine2D(raycastBegin, raycastEnd, new Vector3f(1,0,0));
//		DebugDraw.addLine2D(raycast2Begin, raycast2End, new Vector3f(1,0,0));

		return (info.hit && info.hitObject != null && info.hitObject.getComponent(Ground.class) != null) || (info2.hit && info2.hitObject != null
				&& info2.hitObject.getComponent(Ground.class) != null);
	}
}
