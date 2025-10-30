package me.jho5245.mario.components.block;

import me.jho5245.mario.components.Component;
import me.jho5245.mario.components.PlayerController;
import me.jho5245.mario.jade.GameObject;
import me.jho5245.mario.util.AssetPool;
import org.jbox2d.dynamics.contacts.Contact;
import org.joml.Vector2f;

public abstract class Block extends Component
{
	private transient boolean bopGoingUp = true;
	private transient boolean doBopAnimation;
	private transient Vector2f bobStart;
	private transient Vector2f topBopLocation;
	private transient boolean active = true;

	public float bobSpeed = 1.6f;

	@Override
	public void start()
	{
		this.bobStart = new Vector2f(this.gameObject.transform.position);
		this.topBopLocation = new Vector2f(bobStart).add(0f, 0.08f);
	}

	@Override
	public void update(float dt)
	{
		if (doBopAnimation)
		{
			if (bopGoingUp)
			{
				if (this.gameObject.transform.position.y < this.topBopLocation.y)
				{
					this.gameObject.transform.position.y += bobSpeed * dt;
				}
				else
				{
					bopGoingUp = false;
				}
			}
			// not bob Going Up
			else
			{
				if (this.gameObject.transform.position.y > this.bobStart.y)
				{
					this.gameObject.transform.position.y -= bobSpeed * dt;
				}
				else
				{
					this.gameObject.transform.position.y = bobStart.y;
					bopGoingUp = true;
					doBopAnimation = false;
				}
			}
		}
	}

	@Override
	public void beginCollision(GameObject gameObject, Contact contact, Vector2f contactNormal)
	{
		PlayerController playerController = gameObject.getComponent(PlayerController.class);
		if (active && playerController != null && contactNormal.y < -0.8f)
		{
			doBopAnimation = true;
			AssetPool.getSound("assets/sounds/bump.ogg").play();
			playerHit(playerController);
		}
	}

	public void setInactive()
	{
		this.active = false;
	}

	abstract void playerHit(PlayerController playerController);
}
