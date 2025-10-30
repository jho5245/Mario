package me.jho5245.mario.components.block;

import me.jho5245.mario.animations.StateMachine;
import me.jho5245.mario.components.PlayerController;
import me.jho5245.mario.components.SpriteRenderer;
import me.jho5245.mario.jade.GameObject;
import me.jho5245.mario.jade.Prefabs;
import me.jho5245.mario.jade.Window;
import me.jho5245.mario.util.Settings;
import org.joml.Vector4f;

public class QuestionBlock extends Block
{
	private enum BlockType
	{
		COIN,
		POWER_UP,
		STAR,
	}

	public BlockType blockType = BlockType.COIN;

	@Override
	void playerHit(PlayerController playerController)
	{
		switch (blockType)
		{
			case COIN -> doCoin(playerController);
			case POWER_UP -> doPowerUp(playerController);
			case STAR -> doStar(playerController);
		}

		StateMachine stateMachine = gameObject.getComponent(StateMachine.class);
		if (stateMachine != null)
		{
			stateMachine.trigger("setInactive");
			this.setInactive();
		}
	}

	private void doCoin(PlayerController playerController)
	{
		GameObject coin = Prefabs.generateBlockCoin();
		coin.transform.position.set(this.gameObject.transform.position);
		coin.transform.position.y += Settings.GRID_HEIGHT;
		Window.getCurrentScene().addGameObject(coin);
	}

	private void doPowerUp(PlayerController playerController)
	{
		switch (playerController.getPlayerState())
		{
			case SMALL -> spawnMushroom();
			case BIG -> spawnFlower();
		}
	}

	private void doStar(PlayerController playerController)
	{
	}

	private void spawnMushroom()
	{
		GameObject mushroom = Prefabs.generateMushroom();
		mushroom.transform.position.set(this.gameObject.transform.position);
		mushroom.transform.position.y += Settings.GRID_HEIGHT;
		mushroom.getComponent(SpriteRenderer.class).setColor(new Vector4f(1, 1, 1, 0f));
		Window.getCurrentScene().addGameObject(mushroom);
	}

	private void spawnFlower()
	{
//		GameObject flower = Prefabs.generateFlower();
//		flower.transform.position.set(this.gameObject.transform.position);
//		flower.transform.position.y += Settings.GRID_HEIGHT;
//		Window.getCurrentScene().addGameObject(flower);
	}
}
