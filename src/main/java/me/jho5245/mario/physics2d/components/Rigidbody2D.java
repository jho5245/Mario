package me.jho5245.mario.physics2d.components;

import me.jho5245.mario.components.Component;
import me.jho5245.mario.jade.Window;
import me.jho5245.mario.physics2d.enums.BodyType;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.joml.Vector2f;

public class Rigidbody2D extends Component
{
	private Vector2f velocity = new Vector2f();
	private float angularDamping = 0.8f;
	private float linearDamping = 0.9f;
	private float mass = 0;
	private BodyType bodyType = BodyType.DYNAMIC;
	private float friction = 0.1f;
	public float angularVelocity;
	public float gravityScale = 1f;
	// 충돌 감지는 하나, 길막을 하지 않는다.
	private boolean isSensor;

	private boolean fixedRotation = false;
	private boolean continuousCollision = true;

	private transient Body rawBody = null;
	private transient boolean isPlaying = false;

	public Rigidbody2D()
	{

	}

	@Override
	public void update(float dt)
	{
		if (rawBody != null)
		{
			if (this.bodyType == BodyType.DYNAMIC || this.bodyType == BodyType.KINEMATIC)
			{
				this.gameObject.transform.position.set(rawBody.getPosition().x, rawBody.getPosition().y);
				this.gameObject.transform.rotation = (float) Math.toDegrees(rawBody.getAngle());
				Vec2 vel = rawBody.getLinearVelocity();
				this.velocity.set(vel.x, vel.y);
			}
			else if (this.bodyType == BodyType.STATIC)
			{
				this.rawBody.setTransform(new Vec2(this.gameObject.transform.position.x, this.gameObject.transform.position.y), this.gameObject.transform.rotation);
			}
		}
	}

	public void addVelocity(Vector2f forceToAdd)
	{
		if (rawBody != null)
		{
			rawBody.applyForceToCenter(new Vec2(velocity.x, velocity.y));
		}
	}

	public void addImpulse(Vector2f impulse)
	{
		if (rawBody != null)
		{
			// TODO: should we wake it?
			rawBody.applyLinearImpulse(new Vec2(velocity.x, velocity.y), rawBody.getWorldCenter(), false);
		}
	}

	public Vector2f getVelocity()
	{
		return velocity;
	}

	public void setVelocity(Vector2f velocity)
	{
		this.velocity.set(velocity);
		if (rawBody != null)
		{
			this.rawBody.setLinearVelocity(new Vec2(velocity.x, velocity.y));
		}
	}

	public void setAngularVelocity(float angularVelocity)
	{
		this.angularVelocity = angularVelocity;
		if (rawBody != null)
		{
			this.rawBody.setAngularVelocity(angularVelocity);
		}
	}

	public void setGravityScale(float gravityScale)
	{
		this.gravityScale = gravityScale;
		if (rawBody != null)
		{
			this.rawBody.setGravityScale(gravityScale);
		}
	}

	public void setIsSensor()
	{
		this.isSensor = true;
		if (rawBody != null)
		{
			Window.getPhysics().setIsSensor(this);
		}
	}

	public float getFriction()
	{
		return this.friction;
	}

	public boolean isSensor()
	{
		return this.isSensor;
	}

	public void setNotSensor()
	{
		this.isSensor = false;
		if (rawBody != null)
		{
			Window.getPhysics().setNotSensor(this);
		}
	}

	public float getAngularDamping()
	{
		return angularDamping;
	}

	public void setAngularDamping(float angularDamping)
	{
		this.angularDamping = angularDamping;
	}

	public float getLinearDamping()
	{
		return linearDamping;
	}

	public void setLinearDamping(float linearDamping)
	{
		this.linearDamping = linearDamping;
	}

	public float getMass()
	{
		return mass;
	}

	public void setMass(float mass)
	{
		this.mass = mass;
	}

	public BodyType getBodyType()
	{
		return bodyType;
	}

	public void setBodyType(BodyType bodyType)
	{
		this.bodyType = bodyType;
	}

	public boolean isFixedRotation()
	{
		return fixedRotation;
	}

	public void setFixedRotation(boolean fixedRotation)
	{
		this.fixedRotation = fixedRotation;
	}

	public boolean isContinuousCollision()
	{
		return continuousCollision;
	}

	public void setContinuousCollision(boolean continuousCollision)
	{
		this.continuousCollision = continuousCollision;
	}

	public Body getRawBody()
	{
		return rawBody;
	}

	public void setRawBody(Body rawBody)
	{
		this.rawBody = rawBody;
	}

	public void setIsPlaying(boolean val)
	{
		this.isPlaying = val;
	}
}