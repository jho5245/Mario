package me.jho5245.mario.components.ai;

import me.jho5245.mario.animations.StateMachine;
import me.jho5245.mario.components.Component;
import me.jho5245.mario.components.PlayerController;
import me.jho5245.mario.jade.Camera;
import me.jho5245.mario.jade.GameObject;
import me.jho5245.mario.jade.Window;
import me.jho5245.mario.physics2d.Physics2D;
import me.jho5245.mario.physics2d.components.Rigidbody2D;
import me.jho5245.mario.physics2d.enums.BodyType;
import me.jho5245.mario.sounds.Sound;
import me.jho5245.mario.util.AssetPool;
import org.jbox2d.dynamics.contacts.Contact;
import org.joml.Vector2f;

public class TurtleAI extends Component
{
	private transient boolean goingRight = false;
	transient Rigidbody2D rb;
	private transient float walkSpeed = 2.4f;
	private transient Vector2f velocity = new Vector2f();
	private transient Vector2f acceleration = new Vector2f();
	private transient Vector2f terminalVelocity = new Vector2f(8.4f, 12.4f);
	private transient final float shellTime = 5f;
	private transient float shellTimeLeft;
	private transient boolean isStompByStar;
	private transient boolean isStompByShell;
	/**
	 * 별을 먹은 마리오한테 닿아 죽었을 때 마리오의 x축 속력(엉금엉금 죽는 모션 표현에 사용)
	 */
	private transient float starForce;
	/**
	 * 죽었을 때 순간의 y
	 */
	private transient float deadY;
	private transient float timeToKill = 4f;
	private transient boolean onGround = false;
	private transient boolean isDead = false;
	private transient boolean isMoving = false;
	private transient StateMachine stateMachine;
	private float movingDebounce = 0.32f;

	@Override
	public void start()
	{
		this.stateMachine = this.gameObject.getComponent(StateMachine.class);
		this.rb = gameObject.getComponent(Rigidbody2D.class);
		this.acceleration.y = Window.getPhysics().getGravity().y * 0.7f;
	}

	@Override
	public void update(float dt)
	{
		if (!Window.getPhysics().isPlaying())
			return;

		movingDebounce -= dt;
		Camera camera = Window.getCurrentScene().getCamera();
		if (this.gameObject.transform.position.x > camera.position.x + camera.getProjectionSize().x * camera.getZoom())
		{
			return;
		}

		if (isStompByStar || isStompByShell)
		{
			gameObject.transform.position.x += starForce / 200f;
			gameObject.transform.position.y = (float) (2 + deadY - 16 * Math.pow(3.666 - timeToKill, 2));
			timeToKill -= dt;
			if (timeToKill <= 0)
			{
				gameObject.destroy();
			}
			return;
		}

		if (!isDead || isMoving)
		{
			if (goingRight)
			{
				gameObject.transform.scale.x = -1f;
				velocity.x = walkSpeed;
			}
			else
			{
				gameObject.transform.scale.x = 1f;
				velocity.x = -walkSpeed;
			}
		}
		else
		{
			velocity.x = 0;
		}

		if (isDead && !isMoving)
		{
			if (isStompByStar || isStompByShell)
				return;
			shellTimeLeft -= dt;

			if (shellTimeLeft <= 2 && shellTimeLeft > 0)
			{
				stateMachine.trigger("wake");
				stateMachine.setSpeed(2 / shellTimeLeft);
			}
			else if (shellTimeLeft <= 0)
			{
				isDead = false;
				stateMachine.trigger("walk");
				stateMachine.setSpeed(1);
				walkSpeed /= 3;
			}
		}

		checkOnGround();
		if (onGround)
		{
			this.acceleration.y = 0;
			this.velocity.y = 0;
		}
		else
		{
			this.acceleration.y = Window.getPhysics().getGravity().y * 0.7f;
		}
		this.velocity.y += this.acceleration.y * dt;
		this.velocity.y = Math.max(Math.min(this.velocity.y, this.terminalVelocity.y), -terminalVelocity.y);
		this.rb.setVelocity(velocity);

		if (this.gameObject.transform.position.x < Window.getCurrentScene().getCamera().position.x - 0.5f)
		{// ||
			//this.gameObject.transform.position.y < 0.0f) {
			this.gameObject.destroy();
		}
	}

	public void checkOnGround()
	{
		float innerPlayerWidth = 0.7f;
		float yVal = -0.9f;
		onGround = Physics2D.checkOnGround(this.gameObject, innerPlayerWidth, yVal);
	}

	public void stomp()
	{
		this.isDead = true;
		this.isMoving = false;
		this.velocity.zero();
		this.rb.setVelocity(this.velocity);
		this.rb.setAngularVelocity(0.0f);
		this.rb.setGravityScale(0.0f);
		this.stateMachine.trigger("squashMe");
		AssetPool.getSound("assets/sounds/stomp.ogg").play();
	}

	public void stompByForce(float force, Sound forceSound)
	{
		this.isDead = true;
		this.isStompByShell = true;
		this.starForce = force;
		this.deadY = gameObject.transform.position.y;
		this.rb.setVelocity(new Vector2f(0f, 0f));
		this.rb.setGravityScale(0f);
		this.rb.setIsSensor();
		this.rb.setBodyType(BodyType.STATIC);
		this.gameObject.transform.scale.y *= -1;
		this.stateMachine.trigger("squashMe");
		forceSound.play();
	}

	@Override
	public void beginCollision(GameObject obj, Contact contact, Vector2f contactNormal)
	{
		if (isStompByShell || isStompByStar)
			return;

		PlayerController playerController = obj.getComponent(PlayerController.class);
		if (playerController != null)
		{
			if (!isDead && !playerController.isDead() && !playerController.isHurtInvincible() && contactNormal.y > 0.58f)
			{
				shellTimeLeft = shellTime;
				playerController.enemyBounce();
				stomp();
				walkSpeed *= 3.0f;
				AssetPool.getSound("assets/sounds/stomp.ogg").play();
				this.stateMachine.trigger("squashMe");
			}
			else if (movingDebounce < 0 && !playerController.isDead() && !playerController.isHurtInvincible() && (isMoving || !isDead) && contactNormal.y < 0.58f)
			{
				if (playerController.getStarTimeLeft() > 0)
				{
					stompByForce(playerController.rb.getVelocity().x, AssetPool.getSound("assets/sounds/kick.ogg"));
				}
				else
				{
					playerController.hurt();
				}
			}
			else if (!playerController.isDead() && !playerController.isHurtInvincible())
			{
				if (isDead && contactNormal.y > 0.58f)
				{
					shellTimeLeft = shellTime;
					playerController.enemyBounce();
					isMoving = !isMoving;
					goingRight = contactNormal.x < 0;
					AssetPool.getSound("assets/sounds/stomp.ogg").play();
					this.stateMachine.trigger("squashMe");
				}
				else if (isDead && !isMoving)
				{
					isMoving = true;
					goingRight = contactNormal.x < 0;
					movingDebounce = 0.32f;
					AssetPool.getSound("assets/sounds/kick.ogg").play();
					this.stateMachine.trigger("squashMe");
				}
			}
		}
		else if (Math.abs(contactNormal.y) < 0.1f && !obj.isDead())
		{
			goingRight = contactNormal.x < 0;
			if (isMoving && isDead)
			{
				AssetPool.getSound("assets/sounds/bump.ogg").play();
			}
		}
	}

	@Override
	public void preSolve(GameObject obj, Contact contact, Vector2f contactNormal)
	{
		if (isStompByShell || isStompByStar)
			return;

		GoombaAI goomba = obj.getComponent(GoombaAI.class);
		if (isDead && isMoving && goomba != null)
		{
			goomba.stompByForce(rb.getVelocity().x, AssetPool.getSound("assets/sounds/kick.ogg"));
			contact.setEnabled(false);
		}

		TurtleAI turtle = obj.getComponent(TurtleAI.class);
		if (isDead && isMoving && turtle != null)
		{
			turtle.stompByForce(rb.getVelocity().x, AssetPool.getSound("assets/sounds/kick.ogg"));
			contact.setEnabled(false);
		}

		PlayerController playerController = obj.getComponent(PlayerController.class);
		if (playerController != null)
		{
			if (playerController.isHurtInvincible())
			{
				contact.setEnabled(false);
			}
			else if (playerController.getStarTimeLeft() > 0)
			{
				stompByForce(playerController.rb.getVelocity().x, AssetPool.getSound("assets/sounds/kick.ogg"));
			}
		}
	}
}