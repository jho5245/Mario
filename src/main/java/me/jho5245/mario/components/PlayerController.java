package me.jho5245.mario.components;

import me.jho5245.mario.animations.StateMachine;
import me.jho5245.mario.jade.KeyListener;
import me.jho5245.mario.jade.Window;
import me.jho5245.mario.physics2d.components.Rigidbody2D;
import org.joml.Vector2f;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_LEFT;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_RIGHT;

public class PlayerController extends Component
{
	public transient float walkSpeed = 7.6f;
	public transient float jumpBoost = 1f;
	public transient float jumpImpulse = 3f;
	public transient float slowDownForce = 0.1f;
	public transient Vector2f terminalVelocity = new Vector2f(8.4f, 12.4f);

	public transient boolean onGround = false;
	private transient float groundDebounce;
	private transient float groundDebounceTime = 0.1f;
	private transient Rigidbody2D rb;
	private transient StateMachine stateMachine;
	private transient float bigJumpBoostFactor = 1.05f;
	private transient float playerWidth;
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
		if (KeyListener.isKeyPressed(GLFW_KEY_RIGHT))
		{
			this.gameObject.transform.scale.x = playerWidth;
			this.acceleration.x = walkSpeed;

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
			this.acceleration.x = -walkSpeed;

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

		this.acceleration.y = Window.getPhysics().getGravity().y * 0.8f;

		this.velocity.x += this.acceleration.x * dt;
		this.velocity.y += this.acceleration.y * dt;
		this.velocity.x = Math.max(Math.min(this.velocity.x, this.terminalVelocity.x), -this.terminalVelocity.x);
		this.velocity.y = Math.max(Math.min(this.velocity.y, this.terminalVelocity.y), -this.terminalVelocity.y);
		this.rb.setVelocity(this.velocity);
		this.rb.setAngularVelocity(0);
	}
}
