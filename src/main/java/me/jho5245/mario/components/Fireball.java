package me.jho5245.mario.components;

import me.jho5245.mario.animations.AnimationState;
import me.jho5245.mario.components.ai.GoombaAI;
import me.jho5245.mario.components.ai.TurtleAI;
import me.jho5245.mario.jade.Camera;
import me.jho5245.mario.jade.GameObject;
import me.jho5245.mario.jade.Window;
import me.jho5245.mario.physics2d.Physics2D;
import me.jho5245.mario.physics2d.components.Rigidbody2D;
import me.jho5245.mario.util.AssetPool;
import org.jbox2d.dynamics.contacts.Contact;
import org.joml.Vector2f;

public class Fireball extends Component
{
	private static int fireballCount;
	private transient Rigidbody2D rb;
	private transient float fireballSpeed = 9f;
	private transient Vector2f velocity = new Vector2f();
	private transient Vector2f acceleration = new Vector2f();
	private transient Vector2f terminalVelocity = new Vector2f(8.4f, 6.3f);
	private transient boolean onGround = false;
	private transient float lifeTime = 4f;

	public transient boolean goingRight = true;

	public static boolean canSpawn()
	{
		return fireballCount < 2;
	}

	@Override
	public void start()
	{
		this.rb = gameObject.getComponent(Rigidbody2D.class);
		fireballCount++;
	}

	@Override
	public void update(float dt)
	{
		if (!Window.getPhysics().isPlaying())
			return;

		Vector2f cameraPos = Window.getCurrentScene().getCamera().getPosition();
		Vector2f cameraSize = Window.getCurrentScene().getCamera().getProjectionSize();

		if (gameObject.transform.position.x < cameraPos.x ||
				gameObject.transform.position.y < cameraPos.y ||
				gameObject.transform.position.x > cameraPos.x + cameraSize.x ||
				gameObject.transform.position.y > cameraPos.y + cameraSize.y)
		{
			disappear();
			return;
		}

		lifeTime -= dt;
		if (lifeTime <= 0f)
		{
			disappear();
			return;
		}

		if (goingRight)
		{
			gameObject.transform.scale.x = -Math.abs(gameObject.transform.scale.x);
			velocity.x = fireballSpeed;
		}
		else
		{
			gameObject.transform.scale.x = Math.abs(gameObject.transform.scale.x);
			velocity.x = -fireballSpeed;
		}

		checkOnGround();
		if (onGround)
		{
			this.acceleration.y = 10f;
			this.velocity.y = 10f;
		}
		else
		{
			this.acceleration.y = Window.getPhysics().getGravity().y;
		}
		this.velocity.y += this.acceleration.y * dt;
		this.velocity.y = Math.max(Math.min(this.velocity.y, this.terminalVelocity.y), -terminalVelocity.y);
		this.rb.setVelocity(velocity);
	}

	public void checkOnGround()
	{
		float innerPlayerWidth = 0.3f;
		float yVal = -0.25f;
		onGround = Physics2D.checkOnGround(this.gameObject, innerPlayerWidth, yVal);
	}

	@Override
	public void preSolve(GameObject object, Contact contact, Vector2f contactNormal)
	{
		if (object.getComponent(PlayerController.class) != null || object.getComponent(Fireball.class) != null || object.isDead())
		{
			contact.setEnabled(false);
		}
	}

	@Override
	public void beginCollision(GameObject object, Contact contact, Vector2f contactNormal)
	{
		// hit horizontally
		if (Math.abs(contactNormal.x) > 0.8f)
		{
			if (object.getComponent(PlayerController.class) == null)
			{
				disappear();
			}
		}

		if (object.getComponent(PlayerController.class) == null)
		{
			GoombaAI goombaAI = object.getComponent(GoombaAI.class);
			if (goombaAI != null)
			{
				disappear();
				goombaAI.stompByForce(rb.getVelocity().x, AssetPool.getSound("assets/sounds/kick.ogg"));
			}
			TurtleAI turtleAI = object.getComponent(TurtleAI.class);
			if (turtleAI != null)
			{
				disappear();
				turtleAI.stompByForce(rb.getVelocity().x, AssetPool.getSound("assets/sounds/kick.ogg"));
			}
		}
	}

	private void disappear()
	{
		fireballCount--;
		gameObject.destroy();
	}
}
