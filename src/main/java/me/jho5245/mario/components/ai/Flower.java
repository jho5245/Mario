package me.jho5245.mario.components.ai;

import me.jho5245.mario.components.Component;
import me.jho5245.mario.components.PlayerController;
import me.jho5245.mario.components.SpriteRenderer;
import me.jho5245.mario.jade.GameObject;
import me.jho5245.mario.physics2d.components.Rigidbody2D;
import me.jho5245.mario.util.AssetPool;
import me.jho5245.mario.util.Settings;
import org.jbox2d.dynamics.contacts.Contact;
import org.joml.Vector2f;
import org.joml.Vector4f;

public class Flower extends Component
{
	private transient float spawnTime = 0.5f;
	private transient int originZIndex;
	private transient float originY;

	@Override
	public void start()
	{
		originZIndex = gameObject.transform.zIndex;
		originY = gameObject.transform.position.y;
		gameObject.getComponent(Rigidbody2D.class).setIsSensor();
		AssetPool.getSound("assets/sounds/powerup_appears.ogg").play();
	}

	@Override
	public void update(float dt)
	{
		if (spawnTime > 0)
		{
			gameObject.transform.zIndex = - 100;
			spawnTime -= dt;
			gameObject.transform.position.y = originY - spawnTime * 2f;
			gameObject.getComponent(SpriteRenderer.class).setColor(new Vector4f(1, 1, 1, 1f));
		}
		else
		{
			gameObject.transform.zIndex = originZIndex;
		}
	}

	@Override
	public void beginCollision(GameObject gameObject, Contact contact, Vector2f contactNormal)
	{
		PlayerController playerController = gameObject.getComponent(PlayerController.class);
		if (playerController != null)
		{
				playerController.powerUp();
				this.gameObject.destroy();
		}
	}
}
