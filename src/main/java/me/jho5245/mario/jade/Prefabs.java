package me.jho5245.mario.jade;

import me.jho5245.mario.animations.AnimationState;
import me.jho5245.mario.animations.StateMachine;
import me.jho5245.mario.components.*;
import me.jho5245.mario.components.ai.MushroomAI;
import me.jho5245.mario.components.block.BlockCoin;
import me.jho5245.mario.components.block.QuestionBlock;
import me.jho5245.mario.physics2d.components.Box2DCollider;
import me.jho5245.mario.physics2d.components.CircleCollider;
import me.jho5245.mario.physics2d.components.PillboxCollider;
import me.jho5245.mario.physics2d.components.Rigidbody2D;
import me.jho5245.mario.physics2d.enums.BodyType;
import me.jho5245.mario.util.AssetPool;
import me.jho5245.mario.util.Settings;
import org.joml.Vector2f;

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
		SpriteSheet bigPlayerSprites = AssetPool.getSpriteSheet("assets/images/bigSpritesheet.png");
		GameObject mario = generateSpriteObject(playerSprites.getSprite(0), Settings.GRID_WIDTH, Settings.GRID_HEIGHT);

		// Little mario animations
		AnimationState run = new AnimationState();
		run.title = "Run";
		float defaultFrameTime = 0.2f;
		run.addFrame(playerSprites.getSprite(0), defaultFrameTime);
		run.addFrame(playerSprites.getSprite(2), defaultFrameTime);
		run.addFrame(playerSprites.getSprite(3), defaultFrameTime);
		run.addFrame(playerSprites.getSprite(2), defaultFrameTime);
		run.setDoesLoop(true);

		AnimationState switchDirection = new AnimationState();
		switchDirection.title = "Switch Direction";
		switchDirection.addFrame(playerSprites.getSprite(4), 0.1f);
		switchDirection.setDoesLoop(false);

		AnimationState idle = new AnimationState();
		idle.title = "Idle";
		idle.addFrame(playerSprites.getSprite(0), 0.1f);
		idle.setDoesLoop(false);

		AnimationState jump = new AnimationState();
		jump.title = "Jump";
		jump.addFrame(playerSprites.getSprite(5), 0.1f);
		jump.setDoesLoop(false);

		// Big mario animations
		AnimationState bigRun = new AnimationState();
		bigRun.title = "BigRun";
		bigRun.addFrame(bigPlayerSprites.getSprite(0), defaultFrameTime);
		bigRun.addFrame(bigPlayerSprites.getSprite(1), defaultFrameTime);
		bigRun.addFrame(bigPlayerSprites.getSprite(2), defaultFrameTime);
		bigRun.addFrame(bigPlayerSprites.getSprite(3), defaultFrameTime);
		bigRun.addFrame(bigPlayerSprites.getSprite(2), defaultFrameTime);
		bigRun.addFrame(bigPlayerSprites.getSprite(1), defaultFrameTime);
		bigRun.setDoesLoop(true);

		AnimationState bigSwitchDirection = new AnimationState();
		bigSwitchDirection.title = "Big Switch Direction";
		bigSwitchDirection.addFrame(bigPlayerSprites.getSprite(4), 0.1f);
		bigSwitchDirection.setDoesLoop(false);

		AnimationState bigIdle = new AnimationState();
		bigIdle.title = "BigIdle";
		bigIdle.addFrame(bigPlayerSprites.getSprite(0), 0.1f);
		bigIdle.setDoesLoop(false);

		AnimationState bigJump = new AnimationState();
		bigJump.title = "BigJump";
		bigJump.addFrame(bigPlayerSprites.getSprite(5), 0.1f);
		bigJump.setDoesLoop(false);

		// Fire mario animations
		int fireOffset = 21;
		AnimationState fireRun = new AnimationState();
		fireRun.title = "FireRun";
		fireRun.addFrame(bigPlayerSprites.getSprite(fireOffset + 0), defaultFrameTime);
		fireRun.addFrame(bigPlayerSprites.getSprite(fireOffset + 1), defaultFrameTime);
		fireRun.addFrame(bigPlayerSprites.getSprite(fireOffset + 2), defaultFrameTime);
		fireRun.addFrame(bigPlayerSprites.getSprite(fireOffset + 3), defaultFrameTime);
		fireRun.addFrame(bigPlayerSprites.getSprite(fireOffset + 2), defaultFrameTime);
		fireRun.addFrame(bigPlayerSprites.getSprite(fireOffset + 1), defaultFrameTime);
		fireRun.setDoesLoop(true);

		AnimationState fireSwitchDirection = new AnimationState();
		fireSwitchDirection.title = "Fire Switch Direction";
		fireSwitchDirection.addFrame(bigPlayerSprites.getSprite(fireOffset + 4), 0.1f);
		fireSwitchDirection.setDoesLoop(false);

		AnimationState fireIdle = new AnimationState();
		fireIdle.title = "FireIdle";
		fireIdle.addFrame(bigPlayerSprites.getSprite(fireOffset + 0), 0.1f);
		fireIdle.setDoesLoop(false);

		AnimationState fireJump = new AnimationState();
		fireJump.title = "FireJump";
		fireJump.addFrame(bigPlayerSprites.getSprite(fireOffset + 5), 0.1f);
		fireJump.setDoesLoop(false);

		AnimationState die = new AnimationState();
		die.title = "Die";
		die.addFrame(playerSprites.getSprite(6), 0.1f);
		die.setDoesLoop(false);

		StateMachine stateMachine = new StateMachine();
		stateMachine.addState(run);
		stateMachine.addState(idle);
		stateMachine.addState(switchDirection);
		stateMachine.addState(jump);
		stateMachine.addState(die);

		stateMachine.addState(bigRun);
		stateMachine.addState(bigIdle);
		stateMachine.addState(bigSwitchDirection);
		stateMachine.addState(bigJump);

		stateMachine.addState(fireRun);
		stateMachine.addState(fireIdle);
		stateMachine.addState(fireSwitchDirection);
		stateMachine.addState(fireJump);

		stateMachine.setDefaultState(idle.title);
		stateMachine.addState(run.title, switchDirection.title, "switchDirection");
		stateMachine.addState(run.title, idle.title, "stopRunning");
		stateMachine.addState(run.title, jump.title, "jump");
		stateMachine.addState(switchDirection.title, idle.title, "stopRunning");
		stateMachine.addState(switchDirection.title, run.title, "startRunning");
		stateMachine.addState(switchDirection.title, jump.title, "jump");
		stateMachine.addState(idle.title, run.title, "startRunning");
		stateMachine.addState(idle.title, jump.title, "jump");
		stateMachine.addState(jump.title, idle.title, "stopJumping");

		stateMachine.addState(bigRun.title, bigSwitchDirection.title, "switchDirection");
		stateMachine.addState(bigRun.title, bigIdle.title, "stopRunning");
		stateMachine.addState(bigRun.title, bigJump.title, "jump");
		stateMachine.addState(bigSwitchDirection.title, bigIdle.title, "stopRunning");
		stateMachine.addState(bigSwitchDirection.title, bigRun.title, "startRunning");
		stateMachine.addState(bigSwitchDirection.title, bigJump.title, "jump");
		stateMachine.addState(bigIdle.title, bigRun.title, "startRunning");
		stateMachine.addState(bigIdle.title, bigJump.title, "jump");
		stateMachine.addState(bigJump.title, bigIdle.title, "stopJumping");

		stateMachine.addState(fireRun.title, fireSwitchDirection.title, "switchDirection");
		stateMachine.addState(fireRun.title, fireIdle.title, "stopRunning");
		stateMachine.addState(fireRun.title, fireJump.title, "jump");
		stateMachine.addState(fireSwitchDirection.title, fireIdle.title, "stopRunning");
		stateMachine.addState(fireSwitchDirection.title, fireRun.title, "startRunning");
		stateMachine.addState(fireSwitchDirection.title, fireJump.title, "jump");
		stateMachine.addState(fireIdle.title, fireRun.title, "startRunning");
		stateMachine.addState(fireIdle.title, fireJump.title, "jump");
		stateMachine.addState(fireJump.title, fireIdle.title, "stopJumping");

		stateMachine.addState(run.title, bigRun.title, "powerup");
		stateMachine.addState(idle.title, bigIdle.title, "powerup");
		stateMachine.addState(switchDirection.title, bigSwitchDirection.title, "powerup");
		stateMachine.addState(jump.title, bigJump.title, "powerup");
		stateMachine.addState(bigRun.title, fireRun.title, "powerup");
		stateMachine.addState(bigIdle.title, fireIdle.title, "powerup");
		stateMachine.addState(bigSwitchDirection.title, fireSwitchDirection.title, "powerup");
		stateMachine.addState(bigJump.title, fireJump.title, "powerup");

		stateMachine.addState(bigRun.title, run.title, "damage");
		stateMachine.addState(bigIdle.title, idle.title, "damage");
		stateMachine.addState(bigSwitchDirection.title, switchDirection.title, "damage");
		stateMachine.addState(bigJump.title, jump.title, "damage");
		stateMachine.addState(fireRun.title, bigRun.title, "damage");
		stateMachine.addState(fireIdle.title, bigIdle.title, "damage");
		stateMachine.addState(fireSwitchDirection.title, bigSwitchDirection.title, "damage");
		stateMachine.addState(fireJump.title, bigJump.title, "damage");

		stateMachine.addState(run.title, die.title, "die");
		stateMachine.addState(switchDirection.title, die.title, "die");
		stateMachine.addState(idle.title, die.title, "die");
		stateMachine.addState(jump.title, die.title, "die");
		stateMachine.addState(bigRun.title, run.title, "die");
		stateMachine.addState(bigSwitchDirection.title, switchDirection.title, "die");
		stateMachine.addState(bigIdle.title, idle.title, "die");
		stateMachine.addState(bigJump.title, jump.title, "die");
		stateMachine.addState(fireRun.title, bigRun.title, "die");
		stateMachine.addState(fireSwitchDirection.title, bigSwitchDirection.title, "die");
		stateMachine.addState(fireIdle.title, bigIdle.title, "die");
		stateMachine.addState(fireJump.title, bigJump.title, "die");
		mario.addComponent(stateMachine);

		PillboxCollider pillboxCollider = new PillboxCollider();
		pillboxCollider.setWidth(1.56f);
		pillboxCollider.setHeight(1.04f);
		Rigidbody2D rigidbody2D = new Rigidbody2D();
		rigidbody2D.setBodyType(BodyType.DYNAMIC);
		rigidbody2D.setContinuousCollision(false);
		rigidbody2D.setFixedRotation(true);
		rigidbody2D.setMass(25.0f);

		mario.addComponent(rigidbody2D);
		mario.addComponent(pillboxCollider);
		mario.addComponent(new PlayerController());

		return mario;
	}

	public static GameObject generateQuestionBlock()
	{
		SpriteSheet items = AssetPool.getSpriteSheet("assets/images/items.png");
		GameObject questionBlock = generateSpriteObject(items.getSprite(0), Settings.GRID_WIDTH, Settings.GRID_HEIGHT);

		AnimationState flicker = new AnimationState();
		flicker.title = "Question";
		float defaultFrameTime = 0.23f;
		flicker.addFrame(items.getSprite(0), 0.57f);
		flicker.addFrame(items.getSprite(1), defaultFrameTime);
		flicker.addFrame(items.getSprite(2), defaultFrameTime);
		flicker.setDoesLoop(true);

		AnimationState inactive = new AnimationState();
		inactive.title = "Inactive";
		inactive.addFrame(items.getSprite(3), 0.1f);
		inactive.setDoesLoop(false);

		StateMachine stateMachine = new StateMachine();
		stateMachine.addState(flicker);
		stateMachine.addState(inactive);
		stateMachine.setDefaultState(flicker.title);
		stateMachine.addState(flicker.title, inactive.title, "setInactive");
		questionBlock.addComponent(stateMachine);
		questionBlock.addComponent(new QuestionBlock());

		Rigidbody2D rb = new Rigidbody2D();
		rb.setBodyType(BodyType.STATIC);
		questionBlock.addComponent(rb);
		Box2DCollider box2dCollider = new Box2DCollider();
		box2dCollider.setHalfSize(new Vector2f(Settings.GRID_WIDTH, Settings.GRID_HEIGHT));
		questionBlock.addComponent(box2dCollider);
		questionBlock.addComponent(new Ground());

		return questionBlock;
	}

	public static GameObject generateBlockCoin()
	{
		SpriteSheet items = AssetPool.getSpriteSheet("assets/images/items.png");
		GameObject coin = generateSpriteObject(items.getSprite(7), Settings.GRID_WIDTH, Settings.GRID_HEIGHT);

		AnimationState coinFlip = new AnimationState();
		coinFlip.title = "CoinFlip";
		float defaultFrameTime = 0.23f;
		coinFlip.addFrame(items.getSprite(7), defaultFrameTime);
		coinFlip.addFrame(items.getSprite(8), defaultFrameTime);
		coinFlip.addFrame(items.getSprite(9), defaultFrameTime);
		coinFlip.setDoesLoop(true);

		StateMachine stateMachine = new StateMachine();
		stateMachine.addState(coinFlip);
		stateMachine.setDefaultState(coinFlip.title);
		coin.addComponent(stateMachine);
		coin.addComponent(new QuestionBlock());

		coin.addComponent(new BlockCoin());

		return coin;
	}

	public static GameObject generateMushroom()
	{
		SpriteSheet items = AssetPool.getSpriteSheet("assets/images/items.png");
		GameObject mushroom = generateSpriteObject(items.getSprite(10), Settings.GRID_WIDTH, Settings.GRID_HEIGHT);

		Rigidbody2D rb = new Rigidbody2D();
		rb.setBodyType(BodyType.DYNAMIC);
		rb.setFixedRotation(true);
		rb.setContinuousCollision(false);
		mushroom.addComponent(rb);

		CircleCollider circleCollider = new CircleCollider();
		circleCollider.setRadius(0.49f);
		mushroom.addComponent(circleCollider);
		mushroom.addComponent(new MushroomAI());

		return mushroom;
	}
}
