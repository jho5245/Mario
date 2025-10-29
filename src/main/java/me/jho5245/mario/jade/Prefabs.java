package me.jho5245.mario.jade;

import me.jho5245.mario.animations.AnimationState;
import me.jho5245.mario.animations.StateMachine;
import me.jho5245.mario.components.Sprite;
import me.jho5245.mario.components.SpriteRenderer;
import me.jho5245.mario.components.SpriteSheet;
import me.jho5245.mario.util.AssetPool;
import me.jho5245.mario.util.Settings;

import java.util.Set;

public class Prefabs
{
	public static GameObject generateSpriteObject(Sprite sprite, float sizeX, float sizeY)
	{
		GameObject gameObject = Window.getCurrentScene().createGameObject("Sprite_Object_Gen");
		gameObject.transform.scale.x = sizeX;
		gameObject.transform.scale.y = sizeY;
		SpriteRenderer renderer = new SpriteRenderer(sprite);
		gameObject.addComponent(renderer);
		return gameObject;
	}

	public static GameObject generateMario()
	{
		SpriteSheet playerSprites = AssetPool.getSpriteSheet("assets/images/spritesheet.png");
		GameObject mario = generateSpriteObject(playerSprites.getSprite(0), Settings.GRID_WIDTH, Settings.GRID_HEIGHT);

		AnimationState animationState = new AnimationState();
		animationState.title = "Mario";
		float defaultFrameTime = 0.23f;
		animationState.addFrame(playerSprites.getSprite(0), defaultFrameTime);
		animationState.addFrame(playerSprites.getSprite(2), defaultFrameTime);
		animationState.addFrame(playerSprites.getSprite(3), defaultFrameTime);
		animationState.addFrame(playerSprites.getSprite(2), defaultFrameTime);
		animationState.doesLoop = true;

		StateMachine stateMachine = new StateMachine();
		stateMachine.addState(animationState);
		stateMachine.setDefaultStateTitle(animationState.title);
		mario.addComponent(stateMachine);
		return mario;
	}

	public static GameObject generateQuestionBlock()
	{
		SpriteSheet items = AssetPool.getSpriteSheet("assets/images/items.png");
		GameObject questionBlock = generateSpriteObject(items.getSprite(0), Settings.GRID_WIDTH, Settings.GRID_HEIGHT);

		AnimationState animationState = new AnimationState();
		animationState.title = "Flicker";
		float defaultFrameTime = 0.23f;
		animationState.addFrame(items.getSprite(0), 0.57f);
		animationState.addFrame(items.getSprite(1), defaultFrameTime);
		animationState.addFrame(items.getSprite(2), defaultFrameTime);
		animationState.doesLoop = true;

		StateMachine stateMachine = new StateMachine();
		stateMachine.addState(animationState);
		stateMachine.setDefaultStateTitle(animationState.title);
		questionBlock.addComponent(stateMachine);
		return questionBlock;
	}
}
