package me.jho5245.mario.components;

import me.jho5245.mario.animations.StateMachine;
import me.jho5245.mario.jade.Camera;
import me.jho5245.mario.jade.GameObject;
import me.jho5245.mario.jade.KeyListener;
import me.jho5245.mario.jade.Window;
import me.jho5245.mario.observers.ObserverHandler;
import me.jho5245.mario.observers.events.Event;
import me.jho5245.mario.observers.events.EventType;
import me.jho5245.mario.physics2d.Physics2D;
import me.jho5245.mario.physics2d.components.PillboxCollider;
import me.jho5245.mario.physics2d.components.Rigidbody2D;
import me.jho5245.mario.physics2d.enums.BodyType;
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
	private transient final float groundDebounceTime = 0.1f;
	public transient Rigidbody2D rb;
	private transient StateMachine stateMachine;
	private transient final float bigJumpBoostFactor = 1.2f;
	private transient float playerWidth;
	private transient float playerHeight;
	private transient float maxJumpTime = 50;
	private transient float maxSprintingJumpTime = 80;
	private transient float jumpTime;
	private transient final Vector2f acceleration = new Vector2f();
	public transient Vector2f velocity = new Vector2f();

	private transient boolean isDead;
	private transient int enemyBounce = 0;

	private transient Sound backgroundMusic, starMusic;
	private transient Sound powerUpSound, dieSound, hurtSound, oneUpSound, smallJumpSound, superJumpSound;

	private transient final float starTimeColorFlickerTime = 0.1f;
	private transient float starTimeColorFlickerTimeLeft;
	private transient final float starTime = 10f;

	private transient float starTimeLeft;
	private transient PlayerState previousState;

	private final transient List<String> runningTitle = List.of("Run", "BigRun", "FireRun");

	private transient int coinAmount;

	private final transient float powerUpSimulationTime = 0.5f;
	private transient float powerUpSimulationTimeLeft;
	// 파워업 아이템을 먹었을 당시 마리오의 Y축 좌표
	private transient float powerUpStartY;

	private transient final float hurtSimulationTime = 0.5f;
	private transient float hurtSimulationTimeLeft;
	// 피해를 입었을 당시 마리오의 Y축 좌표
	private transient float hurtStartY;

	private transient final float hurtInvincibleTime = 2f;
	private transient float hurtInvincibleTimeLeft;
	private transient final float hurtTimeColorFlickerTime = 0.1f;
	private transient float hurtTimeColorFlickerTimeLeft;

	private transient float dieAnimationTime;
	// 죽었을 당시 마리오의 Y축 좌표
	private transient Float diePositionY;

	private transient boolean isSitting;
	private transient boolean upCeiling;

	private transient float stopSittingTimeLeft;

	// 파이프 이동 등 특정 행동을 할 때 플레이어 zIndex에 변화를 주고 다시 원래 값으로 복구할 값
	private transient int startZIndex;

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
		this.powerUpSound = AssetPool.getSound("assets/sounds/powerup.ogg");
		this.dieSound = AssetPool.getSound("assets/sounds/mario_die.ogg");
		this.hurtSound = AssetPool.getSound("assets/sounds/pipe.ogg");
		this.oneUpSound = AssetPool.getSound("assets/sounds/1-up.ogg");
		this.smallJumpSound = AssetPool.getSound("assets/sounds/jump-small.ogg");
		this.superJumpSound = AssetPool.getSound("assets/sounds/jump-super.ogg");

		this.startZIndex = gameObject.transform.zIndex;
	}

	@Override
	public void update(float dt)
	{
		Camera camera = Window.getCurrentScene().getCamera();

		// 화면 아래로 떨어지면 상태에 관계없이 즉시 사망
		if (!isDead && gameObject.transform.position.y <= camera.getPosition().y - 3)
		{
			kill();
		}
		if (isDead)
		{
			if (diePositionY == null)
			{
				diePositionY = gameObject.transform.position.y;
			}
			starMusic.stop();
			backgroundMusic.stop();
			Window.getPhysics().setPlaying(false);
			dieAnimationTime += dt;
			if (dieAnimationTime >= 0.5f)
			{
				gameObject.transform.position.y = (float) (-16 * Math.pow(dieAnimationTime - 0.83f, 2)) + diePositionY + 2;
			}
			if (dieAnimationTime > 3f)
			{
				ObserverHandler.notify(null, new Event(EventType.GAME_ENGINE_STOP_PLAY));
			}
			return;
		}
		if (starTimeLeft <= 0 && !backgroundMusic.isPlaying())
		{
			backgroundMusic.play();
		}
		isSprinting = !isSitting && KeyListener.isKeyPressed(GLFW_KEY_X);
		if (KeyListener.isKeyPressed(GLFW_KEY_RIGHT))
		{
			this.gameObject.transform.scale.x = playerWidth;
			if (!isSitting)
			{
				this.acceleration.x = isSprinting ? sprintSpeed : walkSpeed;
			}

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
			if (!isSitting)
			{
				this.acceleration.x = isSprinting ? -sprintSpeed : -walkSpeed;
			}

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
				if (isSitting)
				{
					stateMachine.trigger("sit");
				}
			}
		}

		checkOnGround();
		jumpUpdate(dt);

		// 앉아있지 않을땐 방향키 조작 중이 아닐 때만
		if (isSitting || (!KeyListener.isKeyPressed(GLFW_KEY_LEFT) && !KeyListener.isKeyPressed(GLFW_KEY_RIGHT)))
		{
			sitUpdate(dt);
		}

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

		hurtUpdate(dt);

		setAnimationSpeed();
	}

	private void jumpUpdate(float dt)
	{
		if ((KeyListener.isKeyPressed(GLFW_KEY_Z)) && (jumpTime > 0 || onGround || groundDebounce > 0))
		{
			if ((onGround || groundDebounce > 0) && jumpTime == 0)
			{
				if (previousState == PlayerState.SMALL || playerState == PlayerState.SMALL)
				{
					smallJumpSound.play();
				}
				else
				{
					superJumpSound.play();
				}
				jumpTime = isSprinting && Math.abs(velocity.x) > walkSpeed ? maxSprintingJumpTime : maxJumpTime;
				if (isSitting)
					jumpTime *= 0.7f;
				this.velocity.y = jumpImpulse;
				if (isSitting)
				{
					if (KeyListener.isKeyPressed(GLFW_KEY_LEFT))
					{
						this.velocity.x -= jumpImpulse;
					}
					else if (KeyListener.isKeyPressed(GLFW_KEY_RIGHT))
					{
						this.velocity.x = jumpImpulse;
					}
				}
			}
			else if (jumpTime > 0)
			{
				jumpTime--;
				if (jumpTime <= 0)
					jumpTime = 0;
				this.velocity.y = ((jumpTime / 2.2f) * jumpBoost);
				if (isSitting)
				{
					if (KeyListener.isKeyPressed(GLFW_KEY_LEFT))
					{
						this.velocity.x = -((jumpTime / 2.2f) * jumpBoost);
					}
					else if (KeyListener.isKeyPressed(GLFW_KEY_RIGHT))
					{
						this.velocity.x = ((jumpTime / 2.2f) * jumpBoost);
					}
				}
			}
			else
			{
				this.velocity.y = 0;
			}
			groundDebounce = 0;
		}
		else if (enemyBounce > 0)
		{
			enemyBounce--;
			this.velocity.y = ((enemyBounce / 2.2f) * jumpBoost);
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
			if (isSitting)
			{
				if (KeyListener.isKeyPressed(GLFW_KEY_LEFT) || KeyListener.isKeyPressed(GLFW_KEY_RIGHT))
				{
					this.velocity.x *= 0.9f;
					this.acceleration.x *= 0.9f;
				}
			}
			groundDebounce = groundDebounceTime;
		}
	}

	float test;

	private void sitUpdate(float dt)
	{
		// 아래로 들어가는 파이프 사용 시 일정 시간 동안 앉기 동작 불가능
		stopSittingTimeLeft -= dt;
		if (stopSittingTimeLeft < 0)
		{
			stopSittingTimeLeft = 0;
		}

		float innerPlayerWidth = this.playerWidth * 0.6f;
		float yValue = playerState == PlayerState.SMALL || previousState == PlayerState.SMALL ? -0.54f : -1.04f;
		upCeiling = Physics2D.checkCeling(gameObject, innerPlayerWidth, yValue);
		if (playerState == PlayerState.BIG || playerState == PlayerState.FIRE || previousState == PlayerState.BIG || previousState == PlayerState.FIRE)
		{
			// 앉기 키를 누르거나/천장에 닿여있는 상태
			isSitting = stopSittingTimeLeft <= 0 && (KeyListener.isKeyPressed(GLFW_KEY_DOWN) || upCeiling);
			PillboxCollider pb = gameObject.getComponent(PillboxCollider.class);
			if (isSitting)
			{
				stateMachine.trigger("sit");
				pb.setHeight(gameObject.transform.scale.y / 1.8f);
				pb.setOffset(new Vector2f(0, -0.5f));

			}
			else
			{
				stateMachine.trigger("stopSitting");
				pb.setHeight(gameObject.transform.scale.y * 1.5f);
				pb.setOffset(new Vector2f());
			}
		}
	}

	private void powerUpSimulationUpdate(float dt)
	{
		if (powerUpSimulationTimeLeft > 0)
		{
			powerUpSimulationTimeLeft -= dt;
			if (playerState == PlayerState.BIG || previousState == PlayerState.BIG)
			{
				gameObject.transform.scale.y = playerHeight * (2f - powerUpSimulationTimeLeft * 2);
				setPosition(new Vector2f(gameObject.transform.position.x, powerUpStartY + (0.5f - powerUpSimulationTimeLeft)));
			}
			boolean isFire = false;
			if (playerState == PlayerState.FIRE || previousState == PlayerState.FIRE)
			{
				isFire = true;
				if ((int) (powerUpSimulationTimeLeft * 20) % 2 == 0)
				{
					String title = stateMachine.getCurrentTitle();
					stateMachine.trigger(title.contains("Big") ? "powerup" : "damage");
				}
			}
			Window.getPhysics().setPlaying(false);
			if (powerUpSimulationTimeLeft <= 0)
			{
				if (isFire)
				{
					stateMachine.trigger("powerup");
				}
				powerUpSimulationTimeLeft = 0;
				Window.getPhysics().setPlaying(true);
			}
		}
	}

	private void hurtUpdate(float dt)
	{
		if (hurtSimulationTimeLeft > 0)
		{
			hurtSimulationTimeLeft -= dt;
			if (playerState == PlayerState.SMALL || previousState == PlayerState.SMALL)
			{
				gameObject.transform.scale.y = playerHeight * (hurtSimulationTimeLeft + 0.5f) * 2;
				setPosition(new Vector2f(gameObject.transform.position.x, hurtStartY - (0.5f - hurtSimulationTimeLeft)));
			}
			Window.getPhysics().setPlaying(false);
			if (hurtSimulationTimeLeft <= 0)
			{
				hurtSimulationTimeLeft = 0;
				Window.getPhysics().setPlaying(true);
				if (isSitting && playerState == PlayerState.SMALL)
				{
					gameObject.getComponent(PillboxCollider.class).setHeight(gameObject.transform.scale.y / 1.5f);
					isSitting = false;
				}
			}
		}

		if (hurtInvincibleTimeLeft > 0)
		{
			hurtInvincibleTimeLeft -= dt;
			hurtTimeColorFlickerTimeLeft -= dt;
			if (hurtTimeColorFlickerTimeLeft <= 0)
			{
				hurtTimeColorFlickerTimeLeft = hurtTimeColorFlickerTime * hurtInvincibleTimeLeft / hurtInvincibleTime;
				float alpha = gameObject.getComponent(SpriteRenderer.class).getColor().w;
				gameObject.getComponent(SpriteRenderer.class).setColor(new Vector4f(1f, 1f, 1f, alpha == 0.3f ? 1f : 0.3f));
			}
			if (hurtInvincibleTimeLeft <= 0)
			{
				gameObject.getComponent(SpriteRenderer.class).setColor(new Vector4f(1f));
				hurtInvincibleTimeLeft = 0;
			}
		}
	}

	private void starUpdate(float dt)
	{
		if (starTimeLeft > 0)
		{
			starTimeLeft -= dt;
			starTimeColorFlickerTimeLeft -= dt;
			if (starTimeColorFlickerTimeLeft <= 0)
			{
				if (starTimeLeft <= 3f)
				{
					starTimeColorFlickerTimeLeft = starTimeColorFlickerTime * starTimeLeft / 3;
					float alpha = gameObject.getComponent(SpriteRenderer.class).getColor().w;
					gameObject.getComponent(SpriteRenderer.class).setColor(new Vector4f(1f, 1f, 1f, alpha == 0.3f ? 1f : 0.3f));
				}
				else
				{
					starTimeColorFlickerTimeLeft = starTimeColorFlickerTime;
					gameObject.getComponent(SpriteRenderer.class).setColor(new Vector4f((float) Math.random(), (float) Math.random(), (float) Math.random(), 1f));
				}
			}
			if (starTimeLeft <= 0)
			{
				gameObject.getComponent(SpriteRenderer.class).setColor(new Vector4f(1f));
				starTimeLeft = 0;
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
		float innerPlayerWidth = this.playerWidth * 0.6f;
		float yValue = playerState == PlayerState.SMALL || previousState == PlayerState.SMALL ? -0.54f : -1.04f;
		onGround = Physics2D.checkOnGround(gameObject, innerPlayerWidth, yValue);
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
		powerUpSound.play();
		powerUpStartY = gameObject.transform.position.y;
		if (!(playerState == PlayerState.FIRE || previousState == PlayerState.FIRE))
		{
			stateMachine.trigger("powerup");
			powerUpSimulationTimeLeft = powerUpSimulationTime;
		}

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
	}

	public void useStar()
	{
		if (playerState != PlayerState.INVINCIBLE)
		{
			previousState = this.playerState;
			playerState = PlayerState.INVINCIBLE;
		}
		this.backgroundMusic.stop();
		if (!this.starMusic.isPlaying())
		{
			this.starMusic.play();
		}
		powerUpSound.play();
		starTimeLeft = starTime;
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
			oneUpSound.play();
		}
	}

	public boolean isDead()
	{
		return isDead;
	}

	public void setDead(boolean dead)
	{
		isDead = dead;
	}

	public boolean isHurtInvincible()
	{
		return hurtInvincibleTimeLeft > 0;
	}

	/**
	 * 적을 밟았을 때 점프
	 */
	public void enemyBounce()
	{
		this.enemyBounce = 16;
	}

	public void kill()
	{
		playerState = PlayerState.SMALL;
		hurt();
	}

	public void hurt()
	{
		hurtStartY = gameObject.transform.position.y;
		switch (playerState)
		{
			case SMALL ->
			{
				this.stateMachine.trigger("die");
				isDead = true;
				dieSound.play();
				this.velocity.set(0, 0);
				this.acceleration.set(0, 0);
				this.rb.setVelocity(new Vector2f());
				this.rb.setBodyType(BodyType.STATIC);
			}
			case BIG ->
			{
				hurtSound.play();
				playerState = PlayerState.SMALL;
				previousState = PlayerState.SMALL;
				gameObject.transform.scale.y = playerHeight;
				PillboxCollider pb = gameObject.getComponent(PillboxCollider.class);
				if (pb != null)
				{
					jumpBoost /= bigJumpBoostFactor;
					walkSpeed /= bigJumpBoostFactor;
					maxJumpTime /= bigJumpBoostFactor;
					maxSprintingJumpTime /= bigJumpBoostFactor;
					pb.setHeight(gameObject.transform.scale.y / 1.5f);
					pb.setOffset(new Vector2f());
				}
				hurtSimulationTimeLeft = hurtSimulationTime;
				hurtInvincibleTimeLeft = hurtInvincibleTime;
				stateMachine.trigger("damage");
			}
			case FIRE ->
			{
				hurtSound.play();
				this.playerState = PlayerState.BIG;
				this.previousState = PlayerState.BIG;
				hurtSimulationTimeLeft = hurtSimulationTime;
				hurtInvincibleTimeLeft = hurtInvincibleTime;
				stateMachine.trigger("damage");
			}
		}
	}

	public float getStarTimeLeft()
	{
		return starTimeLeft;
	}

	public void setPosition(Vector2f position)
	{
		this.gameObject.transform.position.set(position);
		this.rb.setPosition(position);
	}

	public void preventSittingFor(float stopSittingTime)
	{
		this.isSitting = false;
		this.stopSittingTimeLeft = stopSittingTime;
	}

	public float getStopSittingTimeLeft()
	{
		return stopSittingTimeLeft;
	}

	public void setStopSittingTimeLeft(float stopSittingTimeLeft)
	{
		this.stopSittingTimeLeft = stopSittingTimeLeft;
	}

	public StateMachine getStateMachine()
	{
		return stateMachine;
	}

	public int getStartZIndex()
	{
		return startZIndex;
	}
}
