package me.jho5245.mario.components.ai;

import me.jho5245.mario.components.Component;
import me.jho5245.mario.components.Ground;
import me.jho5245.mario.components.PlayerController;
import me.jho5245.mario.components.PlayerController.PlayerState;
import me.jho5245.mario.components.SpriteRenderer;
import me.jho5245.mario.jade.GameObject;
import me.jho5245.mario.jade.Window;
import me.jho5245.mario.physics2d.Physics2D;
import me.jho5245.mario.physics2d.RaycastInfo;
import me.jho5245.mario.physics2d.components.Rigidbody2D;
import me.jho5245.mario.util.AssetPool;
import org.jbox2d.dynamics.contacts.Contact;
import org.joml.Vector2f;
import org.joml.Vector4f;

public class StarAI extends Component
{
	private transient boolean goingRight = true;
	private transient Rigidbody2D rb;
	private transient Vector2f speed = new Vector2f(10f, 0f);
	private transient float maxSpeed = 3.2f;
	private transient boolean hitPlayer = false;
	private transient float spawnTime = 0.5f;
	private transient int originZIndex;
	private transient float originY;

	private transient boolean onGround;

	@Override
	public void start()
	{
		originZIndex = gameObject.transform.zIndex;
		originY = gameObject.transform.position.y;
		this.rb = gameObject.getComponent(Rigidbody2D.class);
		AssetPool.getSound("assets/sounds/powerup_appears.ogg").play();
	}

	@Override
	public void update(float dt)
	{
		if (!Window.getPhysics().isPlaying())
			return;

		if (spawnTime > 0)
		{
			gameObject.transform.zIndex = -100;
			spawnTime -= dt;
			gameObject.transform.position.y = originY - spawnTime * 2f;
			gameObject.getComponent(SpriteRenderer.class).setColor(new Vector4f(1, 1, 1, 1f));
			return;
		}
		else
		{
			gameObject.transform.zIndex = originZIndex;
		}

		if (Math.abs(rb.getVelocity().x) < maxSpeed)
		{
			rb.addVelocity(goingRight ? speed : new Vector2f(-speed.x, speed.y));
		}

		checkOnGround();
		if (onGround)
		{
			rb.setVelocity(new Vector2f(rb.getVelocity().x, 15f));
		}
	}

	@Override
	public void preSolve(GameObject gameObject, Contact contact, Vector2f contactNormal)
	{
		// 플레이어는 충돌해도 밀리지 않는다.
		PlayerController playerController = gameObject.getComponent(PlayerController.class);
		if (playerController != null)
		{
			if (!hitPlayer)
			{
				playerController.useStar();
				this.gameObject.destroy();
				hitPlayer = true;
			}
			return;
		}

		// 블록이 아닌 다른 대상과는 충돌하지 않는다.
		if (gameObject.getComponent(Ground.class) == null)
		{
			contact.setEnabled(false);
			return;
		}

		// 블록에 충돌하면 이동 방향 전환
		if (Math.abs(contactNormal.y) < 0.1f)
		{
			goingRight = contactNormal.x < 0;
		}
	}

	public void checkOnGround()
	{
		float innerWidth = this.gameObject.transform.scale.y * 0.6f;
		float yValue = -0.54f;
		onGround = Physics2D.checkOnGround(gameObject, innerWidth, yValue);
	}
}
