package me.jho5245.mario.components;

import me.jho5245.mario.jade.GameObject;
import me.jho5245.mario.util.AssetPool;
import org.jbox2d.dynamics.contacts.Contact;
import org.joml.Vector2f;

public class Coin extends Component
{
	@Override
	public void beginCollision(GameObject gameObject, Contact contact, Vector2f contactNormal)
	{
		PlayerController playerController = gameObject.getComponent(PlayerController.class);
		if (playerController != null)
		{
			AssetPool.getSound("assets/sounds/coin.ogg").play();
			playerController.addCoinAmount(1);
			this.gameObject.destroy();
		}
	}
}
