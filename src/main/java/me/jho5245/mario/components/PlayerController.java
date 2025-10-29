package me.jho5245.mario.components;

import me.jho5245.mario.animations.StateMachine;
import me.jho5245.mario.jade.KeyListener;
import me.jho5245.mario.jade.Window;
import me.jho5245.mario.physics2d.RaycastInfo;
import me.jho5245.mario.physics2d.components.Rigidbody2D;
import me.jho5245.mario.util.AssetPool;
import org.joml.Vector2f;

import static org.lwjgl.glfw.GLFW.*;

public class PlayerController extends Component
{
	private enum PlayerState
	{
		SMALL,
		BIG,
		FIRE,
		INVINCIBLE
	}

	public transient float walkSpeed = 3.6f;
	public transient float sprintSpeed = 6.6f;
	public transient float jumpBoost = 1f;
	public transient float jumpImpulse = 8f;
	public transient float slowDownForce = 0.1f;
	public transient Vector2f terminalVelocity = new Vector2f(8.4f, 12.4f);
	public transient Vector2f terminalSprintingVelocity = new Vector2f(12.4f, 12.4f);
	private transient PlayerState playerState = PlayerState.SMALL;

	private transient boolean isSprinting;
	public transient boolean onGround = false;
	private transient float groundDebounce;
	private transient float groundDebounceTime = 0.1f;
	private transient Rigidbody2D rb;
	private transient StateMachine stateMachine;
	private transient float bigJumpBoostFactor = 1.05f;
	private transient float playerWidth;
	private transient final float maxJumpTime = 50;
	private transient final float maxSprintingJumpTime = 80;
	private transient float jumpTime;
	private transient Vector2f acceleration = new Vector2f();
	private transient Vector2f velocity = new Vector2f();
	private transient boolean isDead;
	private transient int enemyBounce = 0;

	@Override
	public void start()
	{
		this.playerWidth = gameObject.transform.scale.x;
		this.rb = gameObject.getComponent(Rigidbody2D.class);
		this.stateMachine = gameObject.getComponent(StateMachine.class);
		this.rb.setGravityScale(0f);
	}

	@Override
	public void update(float dt)
	{
		isSprinting = KeyListener.isKeyPressed(GLFW_KEY_X);
		if (KeyListener.isKeyPressed(GLFW_KEY_RIGHT))
		{
			this.gameObject.transform.scale.x = playerWidth;
			this.acceleration.x = isSprinting ? sprintSpeed : walkSpeed;

			if (this.velocity.x < 0)
			{
				this.stateMachine.trigger("switchDirection");
				this.velocity.x += slowDownForce;
			}
			else
			{
				this.stateMachine.trigger("startRunning");
			}
		}
		else if (KeyListener.isKeyPressed(GLFW_KEY_LEFT))
		{
			this.gameObject.transform.scale.x = -playerWidth;
			this.acceleration.x = isSprinting ? -sprintSpeed : -walkSpeed;

			if (this.velocity.x > 0)
			{
				this.stateMachine.trigger("switchDirection");
				this.velocity.x -= slowDownForce;
			}
			else
			{
				this.stateMachine.trigger("startRunning");
			}
		}
		else
		{
			this.acceleration.x = 0;
			if (this.velocity.x > 0)
			{
				this.velocity.x = Math.max(0, this.velocity.x - slowDownForce);
			}
			else if (this.velocity.x < 0)
			{
				this.velocity.x = Math.min(0, this.velocity.x + slowDownForce);
			}
			else
			{
				this.stateMachine.trigger("stopRunning");
			}
		}

		checkOnGround();
		if (KeyListener.isKeyPressed(GLFW_KEY_Z) && (jumpTime > 0 || onGround || groundDebounce > 0))
		{
			if ((onGround || groundDebounce > 0) && jumpTime == 0)
			{
				AssetPool.getSound("assets/sounds/jump-small.ogg").play();
				jumpTime = isSprinting ? maxSprintingJumpTime : maxJumpTime;
				this.velocity.y = jumpImpulse;
			}
			else if (jumpTime > 0)
			{
				jumpTime--;
				this.velocity.y = ((jumpTime / 2.2f) * jumpBoost);
			}
			else
			{
				this.velocity.y = 0;
			}
			groundDebounce = 0;
		}
		else if (!onGround)
		{
			if (this.jumpTime > 0)
			{
				this.velocity.y *= 0.35f;
				this.jumpTime = 0;
			}
			groundDebounce -= dt;
			this.acceleration.y = Window.getPhysics().getGravity().y * 0.8f;
		}
		else
		{
			this.velocity.y = 0;
			this.acceleration.y = 0;
			groundDebounce = groundDebounceTime;
		}
		//		else if (enemyBounce > 0)
		//		{
		//
		//		}

		this.acceleration.y = Window.getPhysics().getGravity().y * 0.8f;

		this.velocity.x += this.acceleration.x * dt;
		this.velocity.y += this.acceleration.y * dt;
		if (isSprinting)
		{
			this.velocity.x = Math.max(Math.min(this.velocity.x, this.terminalSprintingVelocity.x), -this.terminalSprintingVelocity.x);
			this.velocity.y = Math.max(Math.min(this.velocity.y, this.terminalSprintingVelocity.y), -this.terminalSprintingVelocity.y);
		}
		else
		{
			this.velocity.x = Math.max(Math.min(this.velocity.x, this.terminalVelocity.x), -this.terminalVelocity.x);
			this.velocity.y = Math.max(Math.min(this.velocity.y, this.terminalVelocity.y), -this.terminalVelocity.y);
		}
		if (this.stateMachine.getCurrentTitle().equals("Run"))
		{
			this.stateMachine.setSpeed(Math.abs(this.velocity.x / 4));
		}
		else
		{
			this.stateMachine.setSpeed(1f);
		}
		this.rb.setVelocity(this.velocity);
		this.rb.setAngularVelocity(0);

		if (!onGround)
		{
			stateMachine.trigger("jump");
		}
		else
		{
			stateMachine.trigger("stopJumping");
		}
	}

	public void checkOnGround()
	{
		Vector2f raycastBegin = new Vector2f(this.gameObject.transform.position);
		float innerPlayerWidth = this.playerWidth * 0.6f;
		raycastBegin.sub(innerPlayerWidth / 2f, 0f);
		float yValue = playerState == PlayerState.SMALL ? -0.54f : -0.84f;
		Vector2f raycastEnd = new Vector2f(raycastBegin).add(0f, yValue);
		RaycastInfo info = Window.getPhysics().rayCast(this.gameObject, raycastBegin, raycastEnd);
		Vector2f raycast2Begin = new Vector2f(raycastBegin).add(innerPlayerWidth, 0f);
		Vector2f raycast2End = new Vector2f(raycastEnd).add(innerPlayerWidth, 0f);
		RaycastInfo info2 = Window.getPhysics().rayCast(this.gameObject, raycast2Begin, raycast2End);
		onGround = (info.hit && info.hitObject != null && info.hitObject.getComponent(Ground.class) != null) || (info2.hit && info2.hitObject != null
				&& info2.hitObject.getComponent(Ground.class) != null);
	}
}
