package me.jho5245.mario.components.block;

import me.jho5245.mario.components.PlayerController;
import me.jho5245.mario.components.PlayerController.PlayerState;
import me.jho5245.mario.jade.GameObject;
import me.jho5245.mario.jade.Prefabs;
import me.jho5245.mario.jade.Window;
import me.jho5245.mario.util.AssetPool;

public class BreakableBrick extends Block
{

	@Override
	void playerHit(PlayerController playerController)
	{
		// 별을 먹은 상태에서는 작은 마리오도 벽돌을 부술 수 있다.
		if (playerController.getPlayerState() != PlayerState.SMALL)
		{
			AssetPool.getSound("assets/sounds/break_block.ogg").play();
			gameObject.destroy();
			for (int i = 0; i < 4; i++)
			{
				GameObject blockBreakFragment = Prefabs.generateBlockBreakFragment(i, gameObject.transform.position);
				Window.getCurrentScene().addGameObject(blockBreakFragment);
			}
		}
//		else
		{

		}
	}
}
