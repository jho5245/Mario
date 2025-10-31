package me.jho5245.mario.components.ai;

import me.jho5245.mario.animations.StateMachine;
import me.jho5245.mario.components.Component;
import me.jho5245.mario.components.PlayerController;
import me.jho5245.mario.jade.Camera;
import me.jho5245.mario.jade.GameObject;
import me.jho5245.mario.jade.Window;
import me.jho5245.mario.physics2d.components.Rigidbody2D;
import me.jho5245.mario.physics2d.enums.BodyType;
import me.jho5245.mario.util.AssetPool;
import org.jbox2d.dynamics.contacts.Contact;
import org.joml.Vector2f;

public class GoombaAI extends Component
{
	private transient boolean goingRight;
	private transient Rigidbody2D rb;
	private transient Vector2f speed = new Vector2f(3f, 0f);
	private transient float maxSpeed = 3.2f;
	private transient boolean isDead;
	private transient boolean isStompByStar;
	/**
	 * 별을 먹은 마리오한테 닿아 죽었을 때 마리오의 x축 속력(굼바 죽는 모션 표현에 사용)
 	 */
	private transient float starForce;
	/**
	 * 죽었을 때 순간의 y
	 */
	private transient float deadY;
	private transient float timeToKill = 0.5f;
	private transient StateMachine stateMachine;

	@Override
	public void start()
	{
		this.rb = gameObject.getComponent(Rigidbody2D.class);
		this.stateMachine = gameObject.getComponent(StateMachine.class);
	}

	@Override
	public void update(float dt)
	{
		if (!Window.getPhysics().isPlaying())
			return;


		if (isDead)
		{
			if (isStompByStar)
			{
				gameObject.transform.position.x += starForce / 200f;
				gameObject.transform.position.y = (float) (2 + deadY - 16 * Math.pow(3.666 - timeToKill, 2));
			}
			timeToKill -= dt;
			if (timeToKill <= 0)
			{
				gameObject.destroy();
				return;
			}
		}

		// 아직 이동하지 않은 곳(카메라 오른쪽 밖)의 굼바는 움직이지 않는다.
		Camera camera = Window.getCurrentScene().getCamera();
		if (gameObject.transform.position.x > camera.getPosition().x + camera.getProjectionSize().x * camera.getZoom())
		{
			return;
		}

		if (Math.abs(rb.getVelocity().x) < maxSpeed)
		{
			rb.addVelocity(goingRight ? speed : new Vector2f(-speed.x, speed.y));
		}
	}

	@Override
	public void preSolve(GameObject gameObject, Contact contact, Vector2f contactNormal)
	{
		if (isDead)
		{
			return;
		}

		// 플레이어는 충돌해도 밀리지 않는다.
		PlayerController playerController = gameObject.getComponent(PlayerController.class);
		if (playerController != null)
		{
			if (!playerController.isDead())
			{
				// 플레이어가 굼바를 밟음
				if (!playerController.isHurtInvincible() && contactNormal.y > 0.58f)
				{
					playerController.enemyBounce();
					stomp();
				}
				// 별을 먹은 상태로 닿음
				else if (playerController.getStarTimeLeft() > 0)
				{
					stompByStar(playerController);
				}
				// 플레이어가 굼바에게 닿음
				else if (!playerController.isHurtInvincible())
				{
					playerController.hurt();
				}
			}

			contact.setEnabled(false);
			return;
		}

		// 블록에 충돌하면 이동 방향 전환
		if (Math.abs(contactNormal.y) < 0.1f)
		{
			goingRight = contactNormal.x < 0;
		}
	}

	private void stomp()
	{
		stomp(true);
	}

	private void stomp(boolean playSound)
	{
		this.isDead = true;
		this.speed = new Vector2f(0f, 0f);
		this.rb.setVelocity(new Vector2f(0f, 0f));
		this.rb.setGravityScale(0f);
		this.stateMachine.trigger("squashMe");
		this.rb.setIsSensor();
		this.rb.setBodyType(BodyType.STATIC);
		if (playSound)
		{
			AssetPool.getSound("assets/sounds/stomp.ogg").play();
		}
	}

	private void stompByStar(PlayerController playerController)
	{
		this.isDead = true;
		this.isStompByStar = true;
		this.timeToKill = 4f;
		this.starForce = playerController.rb.getVelocity().x;
		this.deadY = gameObject.transform.position.y;
		this.speed = new Vector2f(0f, 0f);
		this.rb.setVelocity(new Vector2f(0f, 0f));
		this.rb.setGravityScale(0f);
		this.rb.setIsSensor();
		this.rb.setBodyType(BodyType.STATIC);
		this.gameObject.transform.scale.y *= -1;
		AssetPool.getSound("assets/sounds/stomp.ogg").play();
	}
}
