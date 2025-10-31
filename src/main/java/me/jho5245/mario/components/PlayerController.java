package me.jho5245.mario.components;

import me.jho5245.mario.animations.StateMachine;
import me.jho5245.mario.jade.GameObject;
import me.jho5245.mario.jade.KeyListener;
import me.jho5245.mario.jade.Window;
import me.jho5245.mario.physics2d.RaycastInfo;
import me.jho5245.mario.physics2d.components.PillboxCollider;
import me.jho5245.mario.physics2d.components.Rigidbody2D;
import me.jho5245.mario.sounds.Sound;
import me.jho5245.mario.util.AssetPool;
import org.jbox2d.dynamics.contacts.Contact;
import org.joml.Vector2f;
import org.joml.Vector4f;

import java.util.List;

import static org.lwjgl.glfw.GLFW.*;

public class PlayerController extends Component
{
	public enum PlayerState
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
	public transient Rigidbody2D rb;
	private transient StateMachine stateMachine;
	private transient float bigJumpBoostFactor = 1.2f;
	private transient float playerWidth;
	private transient float playerHeight;
	private transient float maxJumpTime = 50;
	private transient float maxSprintingJumpTime = 80;
	private transient float jumpTime;
	private transient Vector2f acceleration = new Vector2f();
	public transient Vector2f velocity = new Vector2f();
	private transient boolean isDead;
	private transient int enemyBounce = 0;

	private transient Sound backgroundMusic, starMusic;

	private transient final float starTimeColorFlickerTimeStart = 0.1f;
	private transient float starTimeColorFlickerTime;
	private transient final float starTimeStart = 10f;
	private transient float starTime;
	private transient PlayerState previousState;

	private final transient List<String> runningTitle = List.of("Run", "BigRun", "FireRun");

	private transient int coinAmount;

	private final transient float powerUpSimulationTimeStart = 0.5f;
	private transient float powerUpSimulationTime;

	@Override
	public void start()
	{
		this.playerWidth = gameObject.transform.scale.x;
		this.playerHeight = gameObject.transform.scale.y;
		this.rb = gameObject.getComponent(Rigidbody2D.class);
		this.stateMachine = gameObject.getComponent(StateMachine.class);
		this.rb.setGravityScale(0f);
		this.backgroundMusic = AssetPool.getSound("assets/sounds/main-theme-overworld.ogg");
		this.starMusic = AssetPool.getSound("assets/sounds/invincible.ogg");
	}

	@Override
	public void update(float dt)
	{
		if (starTime == 0 && !backgroundMusic.isPlaying())
		{
			backgroundMusic.play();
		}
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
		jumpUpdate(dt);

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

		starUpdate(dt);

		powerUpSimulationUpdate(dt);

		setAnimationSpeed();
	}

	private void jumpUpdate(float dt)
	{
		if (KeyListener.isKeyPressed(GLFW_KEY_Z) && (jumpTime > 0 || onGround || groundDebounce > 0))
		{
			if ((onGround || groundDebounce > 0) && jumpTime == 0)
			{
				AssetPool.getSound("assets/sounds/jump-small.ogg").play();
				jumpTime = isSprinting && Math.abs(velocity.x) > walkSpeed ? maxSprintingJumpTime : maxJumpTime;
				this.velocity.y = jumpImpulse;
			}
			else if (jumpTime > 0)
			{
				jumpTime--;
				if (jumpTime <= 0)
					jumpTime = 0;
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
	}

	private void powerUpSimulationUpdate(float dt)
	{
		if (powerUpSimulationTime > 0)
		{
			powerUpSimulationTime -= dt;
			if (playerState == PlayerState.BIG || previousState == PlayerState.BIG)
			{
				gameObject.transform.scale.y = playerHeight * (2f - powerUpSimulationTime * 2);
				gameObject.transform.position.y += (0.5f - powerUpSimulationTime);
			}
			Window.getPhysics().setPlaying(false);
			if (powerUpSimulationTime <= 0)
			{
				powerUpSimulationTime = 0;
				Window.getPhysics().setPlaying(true);
			}
		}
	}

	private void starUpdate(float dt)
	{
		if (starTime > 0)
		{
			starTime -= dt;
			starTimeColorFlickerTime -= dt;
			if (starTimeColorFlickerTime <= 0)
			{
				if (starTime <= 3f)
				{
					starTimeColorFlickerTime = starTimeColorFlickerTimeStart * starTime / 3;
					float alpha = gameObject.getComponent(SpriteRenderer.class).getColor().w;
					gameObject.getComponent(SpriteRenderer.class).setColor(new Vector4f(1f, 1f, 1f, alpha == 0.3f ? 1f : 0.3f));
				}
				else
				{
					starTimeColorFlickerTime = starTimeColorFlickerTimeStart;
					gameObject.getComponent(SpriteRenderer.class).setColor(new Vector4f((float) Math.random(), (float) Math.random(), (float) Math.random(), 1f));
				}
			}
			if (starTime <= 0)
			{
				gameObject.getComponent(SpriteRenderer.class).setColor(new Vector4f(1f));
				starTime = 0;
				playerState = previousState;
				starMusic.stop();
				backgroundMusic.play();
			}
		}
	}

	private void setAnimationSpeed()
	{
		if (runningTitle.contains(this.stateMachine.getCurrentTitle()))
		{
			this.stateMachine.setSpeed(Math.abs(this.velocity.x / 4));
		}
		else
		{
			this.stateMachine.setSpeed(1f);
		}
	}

	public void checkOnGround()
	{
		Vector2f raycastBegin = new Vector2f(this.gameObject.transform.position);
		float innerPlayerWidth = this.playerWidth * 0.6f;
		raycastBegin.sub(innerPlayerWidth / 2f, 0f);
		float yValue = playerState == PlayerState.SMALL || previousState == PlayerState.SMALL ? -0.54f : -1.04f;
		Vector2f raycastEnd = new Vector2f(raycastBegin).add(0f, yValue);
		RaycastInfo info = Window.getPhysics().rayCast(this.gameObject, raycastBegin, raycastEnd);
		Vector2f raycast2Begin = new Vector2f(raycastBegin).add(innerPlayerWidth, 0f);
		Vector2f raycast2End = new Vector2f(raycastEnd).add(innerPlayerWidth, 0f);
		RaycastInfo info2 = Window.getPhysics().rayCast(this.gameObject, raycast2Begin, raycast2End);
		onGround = (info.hit && info.hitObject != null && info.hitObject.getComponent(Ground.class) != null) || (info2.hit && info2.hitObject != null
				&& info2.hitObject.getComponent(Ground.class) != null);
	}

	@Override
	public void beginCollision(GameObject collidingObject, Contact contact, Vector2f contactNormal)
	{
		if (isDead)
			return;
		if (collidingObject.getComponent(Ground.class) != null)
		{
			// hit horizontally
			if (Math.abs(contactNormal.x) > 0.8f)
			{
				this.velocity.x = 0;
			}
			// hit bottom of block
			else if (contactNormal.y > 0.8f)
			{
				this.velocity.y = 0;
				this.acceleration.y = 0;
				this.jumpTime = 0;
			}
		}
	}

	public PlayerState getPlayerState()
	{
		return this.playerState;
	}

	public PlayerState getPreviousState()
	{
		return this.previousState;
	}

	/**
	 * 마리오가 버섯/꽃을 먹음
	 */
	public void powerUp()
	{
		AssetPool.getSound("assets/sounds/powerup.ogg").play();
		if (playerState == PlayerState.SMALL || previousState == PlayerState.SMALL)
		{
			playerState = PlayerState.BIG;
			previousState = PlayerState.BIG;
			gameObject.transform.scale.y = playerHeight * 2f;
			PillboxCollider pb = gameObject.getComponent(PillboxCollider.class);
			if (pb != null)
			{
				jumpBoost *= bigJumpBoostFactor;
				walkSpeed *= bigJumpBoostFactor;
				maxJumpTime *= bigJumpBoostFactor;
				maxSprintingJumpTime *= bigJumpBoostFactor;
				pb.setHeight(gameObject.transform.scale.y * 1.5f);
			}
		}
		else if (playerState == PlayerState.BIG || previousState == PlayerState.BIG)
		{
			playerState = PlayerState.FIRE;
			previousState = PlayerState.FIRE;
		}
		stateMachine.trigger("powerup");
		powerUpSimulationTime = powerUpSimulationTimeStart;
	}

	public void useStar()
	{
		previousState = this.playerState;
		playerState = PlayerState.INVINCIBLE;
		this.backgroundMusic.stop();
		this.starMusic.play();
		starTime = starTimeStart;
	}

	public int getCoinAmount()
	{
		return coinAmount;
	}

	public void setCoinAmount(int coinAmount)
	{
		this.coinAmount = coinAmount;
	}

	public void addCoinAmount(int coinAmount)
	{
		this.coinAmount += coinAmount;
		if (this.coinAmount >= 100)
		{
			this.coinAmount -= 100;
			AssetPool.getSound("assets/sounds/1-up.ogg").play();
		}
	}
}
