package me.jho5245.mario.components.block;

import me.jho5245.mario.components.Component;
import me.jho5245.mario.components.PlayerController;
import me.jho5245.mario.jade.GameObject;
import org.jbox2d.dynamics.contacts.Contact;
import org.joml.Vector2f;

public class GoalFlag extends Component
{
	private boolean isFlag;

	public GoalFlag(boolean isFlag)
	{
		this.isFlag = isFlag;
	}

	@Override
	public void beginCollision(GameObject object, Contact contact, Vector2f contactNormal)
	{
		PlayerController playerController = object.getComponent(PlayerController.class);
		if (playerController != null)
		{
			playerController.playWinAnimation(this.gameObject);
		}
	}

	@Override
	public void preSolve(GameObject object, Contact contact, Vector2f contactNormal)
	{
		if (object.getComponent(PlayerController.class) == null)
		{
			contact.setEnabled(false);
		}
	}
}
