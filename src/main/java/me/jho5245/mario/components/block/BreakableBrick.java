package me.jho5245.mario.components.block;

import me.jho5245.mario.components.PlayerController;
import me.jho5245.mario.util.AssetPool;

public class BreakableBrick extends Block
{

	@Override
	void playerHit(PlayerController playerController)
	{
		if (!playerController.isSmall())
		{
			AssetPool.getSound("assets/sounds/break_block.ogg").play();
			gameObject.destroy();
			// TODO: Play break animation
		}
		else
		{

		}
	}
}
