package me.jho5245.mario.jade;

import me.jho5245.mario.animations.AnimationState;
import me.jho5245.mario.animations.StateMachine;
import me.jho5245.mario.components.*;
import me.jho5245.mario.components.ai.*;
import me.jho5245.mario.components.block.*;
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
		
		AnimationState goalDown = new AnimationState();
		goalDown.title = "Goal Down";
		goalDown.addFrame(playerSprites.getSprite(7), 0.1f);
		
		AnimationState goalSit = new AnimationState();
		goalSit.title = "Goal Sit";
		goalSit.addFrame(playerSprites.getSprite(8), 0.1f);

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

		AnimationState bigSit = new AnimationState();
		bigSit.title = "BigSit";
		bigSit.addFrame(bigPlayerSprites.getSprite(6), 0.1f);
		bigSit.setDoesLoop(false);

		AnimationState bigGoalDown = new AnimationState();
		bigGoalDown.title = "Big Goal Down";
		bigGoalDown.addFrame(bigPlayerSprites.getSprite(7), 0.1f);

		AnimationState bigGoalSit = new AnimationState();
		bigGoalSit.title = "Big Goal Sit";
		bigGoalSit.addFrame(bigPlayerSprites.getSprite(8), 0.1f);

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

		AnimationState fireSit = new AnimationState();
		fireSit.title = "FireSit";
		fireSit.addFrame(bigPlayerSprites.getSprite(fireOffset + 6), 0.1f);
		fireSit.setDoesLoop(false);

		AnimationState fireGoalDown = new AnimationState();
		fireGoalDown.title = "Fire Goal Down";
		fireGoalDown.addFrame(bigPlayerSprites.getSprite(fireOffset + 7), 0.1f);

		AnimationState fireGoalSit = new AnimationState();
		fireGoalSit.title = "Fire Goal Sit";
		fireGoalSit.addFrame(bigPlayerSprites.getSprite(fireOffset + 8), 0.1f);

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
		stateMachine.addState(goalDown);
		stateMachine.addState(goalSit);

		stateMachine.addState(bigRun);
		stateMachine.addState(bigIdle);
		stateMachine.addState(bigSit);
		stateMachine.addState(bigSwitchDirection);
		stateMachine.addState(bigJump);
		stateMachine.addState(bigGoalDown);
		stateMachine.addState(bigGoalSit);

		stateMachine.addState(fireRun);
		stateMachine.addState(fireIdle);
		stateMachine.addState(fireSit);
		stateMachine.addState(fireSwitchDirection);
		stateMachine.addState(fireJump);
		stateMachine.addState(fireGoalDown);
		stateMachine.addState(fireGoalSit);

		stateMachine.setDefaultState(idle.title);
		stateMachine.addState(run.title, switchDirection.title, "switchDirection");
		stateMachine.addState(run.title, idle.title, "stopRunning");
		stateMachine.addState(run.title, jump.title, "jump");
		stateMachine.addState(switchDirection.title, idle.title, "stopRunning");
		stateMachine.addState(switchDirection.title, run.title, "startRunning");
		stateMachine.addState(goalSit.title, run.title, "startRunning");
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
		stateMachine.addState(bigGoalSit.title, bigRun.title, "startRunning");
		stateMachine.addState(bigIdle.title, bigJump.title, "jump");
		stateMachine.addState(bigJump.title, bigIdle.title, "stopJumping");
		stateMachine.addState(bigIdle.title, bigSit.title, "sit");
		stateMachine.addState(bigRun.title, bigSit.title, "sit");
		stateMachine.addState(bigSit.title, bigIdle.title, "stopSitting");

		stateMachine.addState(fireRun.title, fireSwitchDirection.title, "switchDirection");
		stateMachine.addState(fireRun.title, fireIdle.title, "stopRunning");
		stateMachine.addState(fireRun.title, fireJump.title, "jump");
		stateMachine.addState(fireSwitchDirection.title, fireIdle.title, "stopRunning");
		stateMachine.addState(fireSwitchDirection.title, fireRun.title, "startRunning");
		stateMachine.addState(fireSwitchDirection.title, fireJump.title, "jump");
		stateMachine.addState(fireIdle.title, fireRun.title, "startRunning");
		stateMachine.addState(fireGoalSit.title, fireRun.title, "startRunning");
		stateMachine.addState(fireIdle.title, fireJump.title, "jump");
		stateMachine.addState(fireJump.title, fireIdle.title, "stopJumping");
		stateMachine.addState(fireIdle.title, fireSit.title, "sit");
		stateMachine.addState(fireRun.title, fireSit.title, "sit");
		stateMachine.addState(fireSit.title, fireIdle.title, "stopSitting");

		stateMachine.addState(run.title, bigRun.title, "powerup");
		stateMachine.addState(idle.title, bigIdle.title, "powerup");
		stateMachine.addState(switchDirection.title, bigSwitchDirection.title, "powerup");
		stateMachine.addState(jump.title, bigJump.title, "powerup");
		stateMachine.addState(bigRun.title, fireRun.title, "powerup");
		stateMachine.addState(bigIdle.title, fireIdle.title, "powerup");
		stateMachine.addState(bigSwitchDirection.title, fireSwitchDirection.title, "powerup");
		stateMachine.addState(bigJump.title, fireJump.title, "powerup");
		stateMachine.addState(bigSit.title, fireSit.title, "powerup");

		stateMachine.addState(bigRun.title, run.title, "damage");
		stateMachine.addState(bigIdle.title, idle.title, "damage");
		stateMachine.addState(bigSwitchDirection.title, switchDirection.title, "damage");
		stateMachine.addState(bigJump.title, jump.title, "damage");
		stateMachine.addState(fireRun.title, bigRun.title, "damage");
		stateMachine.addState(fireIdle.title, bigIdle.title, "damage");
		stateMachine.addState(fireSwitchDirection.title, bigSwitchDirection.title, "damage");
		stateMachine.addState(fireJump.title, bigJump.title, "damage");
		stateMachine.addState(bigSit.title, idle.title, "damage");
		stateMachine.addState(fireSit.title, bigSit.title, "damage");

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

		stateMachine.addState(run.title, goalDown.title, "goalDown");
		stateMachine.addState(switchDirection.title, goalDown.title, "goalDown");
		stateMachine.addState(idle.title, goalDown.title, "goalDown");
		stateMachine.addState(jump.title, goalDown.title, "goalDown");
		stateMachine.addState(bigRun.title, bigGoalDown.title, "goalDown");
		stateMachine.addState(bigSwitchDirection.title, bigGoalDown.title, "goalDown");
		stateMachine.addState(bigIdle.title, bigGoalDown.title, "goalDown");
		stateMachine.addState(bigJump.title, bigGoalDown.title, "goalDown");
		stateMachine.addState(bigSit.title, bigGoalDown.title, "goalDown");
		stateMachine.addState(fireRun.title, fireGoalDown.title, "goalDown");
		stateMachine.addState(fireSwitchDirection.title, fireGoalDown.title, "goalDown");
		stateMachine.addState(fireIdle.title, fireGoalDown.title, "goalDown");
		stateMachine.addState(fireJump.title, fireGoalDown.title, "goalDown");
		stateMachine.addState(fireSit.title, fireGoalDown.title, "goalDown");

		stateMachine.addState(goalDown.title, goalSit.title, "goalSit");
		stateMachine.addState(bigGoalDown.title, bigGoalSit.title, "goalSit");
		stateMachine.addState(fireGoalDown.title, fireGoalSit.title, "goalSit");
		mario.addComponent(stateMachine);

		PillboxCollider pillboxCollider = new PillboxCollider();
		pillboxCollider.setWidth(1.56f);
		pillboxCollider.setHeight(0.98f);
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

	public static GameObject generateQuestionBlock(boolean hidden)
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
		if (hidden)
		{
			questionBlock.addComponent(new HiddenBlock());
		}

		Rigidbody2D rb = new Rigidbody2D();
		rb.setBodyType(BodyType.STATIC);
		questionBlock.addComponent(rb);
		Box2DCollider box2dCollider = new Box2DCollider();
		box2dCollider.setHalfSize(new Vector2f(Settings.GRID_WIDTH, Settings.GRID_HEIGHT));
		questionBlock.addComponent(box2dCollider);
		questionBlock.addComponent(new Ground());

		return questionBlock;
	}

	public static GameObject generateCoin()
	{
		SpriteSheet items = AssetPool.getSpriteSheet("assets/images/items.png");
		GameObject coin = generateSpriteObject(items.getSprite(7), Settings.GRID_WIDTH, Settings.GRID_HEIGHT);

		Rigidbody2D rb = new Rigidbody2D();
		rb.setBodyType(BodyType.STATIC);
		rb.setFixedRotation(true);
		rb.setContinuousCollision(false);
		rb.setIsSensor();
		coin.addComponent(rb);

		CircleCollider circleCollider = new CircleCollider();
		circleCollider.setRadius(0.49f);
		coin.addComponent(circleCollider);

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
		coin.addComponent(new Coin());
		return coin;
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
		GameObject mushroom = generateSpriteObject(items.getSprite(15), Settings.GRID_WIDTH, Settings.GRID_HEIGHT);

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

	public static GameObject generateFlower()
	{
		SpriteSheet items = AssetPool.getSpriteSheet("assets/images/items.png");
		GameObject flower = generateSpriteObject(items.getSprite(20), Settings.GRID_WIDTH, Settings.GRID_HEIGHT);

		Rigidbody2D rb = new Rigidbody2D();
		rb.setBodyType(BodyType.STATIC);
		rb.setFixedRotation(true);
		rb.setContinuousCollision(false);
		flower.addComponent(rb);

		CircleCollider circleCollider = new CircleCollider();
		circleCollider.setRadius(0.49f);
		flower.addComponent(circleCollider);
		flower.addComponent(new Flower());

		AnimationState flowerAnimation = new AnimationState();
		flowerAnimation.title = "Flower";
		float defaultFrameTime = 0.23f;
		flowerAnimation.addFrame(items.getSprite(20), defaultFrameTime);
		flowerAnimation.addFrame(items.getSprite(21), defaultFrameTime);
		flowerAnimation.addFrame(items.getSprite(22), defaultFrameTime);
		flowerAnimation.addFrame(items.getSprite(23), defaultFrameTime);
		flowerAnimation.setDoesLoop(true);

		StateMachine stateMachine = new StateMachine();
		stateMachine.addState(flowerAnimation);
		stateMachine.setDefaultState(flowerAnimation.title);
		flower.addComponent(stateMachine);

		return flower;
	}

	public static GameObject generateStar()
	{
		SpriteSheet items = AssetPool.getSpriteSheet("assets/images/items.png");
		GameObject star = generateSpriteObject(items.getSprite(24), Settings.GRID_WIDTH, Settings.GRID_HEIGHT);

		Rigidbody2D rb = new Rigidbody2D();
		rb.setBodyType(BodyType.DYNAMIC);
		rb.setFixedRotation(true);
		rb.setContinuousCollision(false);
		star.addComponent(rb);

		CircleCollider circleCollider = new CircleCollider();
		circleCollider.setRadius(0.49f);
		star.addComponent(circleCollider);
		star.addComponent(new StarAI());

		AnimationState starAnimation = new AnimationState();
		starAnimation.title = "Star";
		float defaultFrameTime = 0.15f;
		starAnimation.addFrame(items.getSprite(24), defaultFrameTime);
		starAnimation.addFrame(items.getSprite(25), defaultFrameTime);
		starAnimation.addFrame(items.getSprite(26), defaultFrameTime);
		starAnimation.addFrame(items.getSprite(27), defaultFrameTime);
		starAnimation.setDoesLoop(true);

		StateMachine stateMachine = new StateMachine();
		stateMachine.addState(starAnimation);
		stateMachine.setDefaultState(starAnimation.title);
		star.addComponent(stateMachine);

		return star;
	}

	public static GameObject generateBlockBreakFragment(int offset, Vector2f position)
	{
		SpriteSheet items = AssetPool.getSpriteSheet("assets/images/items.png");
		GameObject fragment = generateSpriteObject(items.getSprite(5), Settings.GRID_WIDTH / 2, Settings.GRID_HEIGHT / 2);
		Vector2f pos = new Vector2f(position.x, position.y);
		switch (offset)
		{
			case 0 ->
			{
				pos.x -= Settings.GRID_WIDTH / 4;
				pos.y -= Settings.GRID_HEIGHT / 4;
				fragment.transform.rotation = 20f;
			}
			case 1 ->
			{

				pos.x -= Settings.GRID_WIDTH / 4;
				pos.y += Settings.GRID_HEIGHT / 4;
				fragment.transform.rotation = 20f;
			}
			case 2 ->
			{
				pos.x += Settings.GRID_WIDTH / 4;
				pos.y -= Settings.GRID_HEIGHT / 4;
				fragment.transform.rotation = -20f;
			}
			case 3 ->
			{

				pos.x += Settings.GRID_WIDTH / 4;
				pos.y += Settings.GRID_HEIGHT / 4;
				fragment.transform.rotation = -20f;
			}
		}
		fragment.transform.position = new Vector2f(pos);
		fragment.addComponent(new BreakableBrickFragment());
		return fragment;
	}

	public static GameObject generateGoomba(boolean underground)
	{
		SpriteSheet sprites = AssetPool.getSpriteSheet("assets/images/spritesheet.png");
		GameObject goomba = generateSpriteObject(sprites.getSprite(underground ? 20 : 14), Settings.GRID_WIDTH, Settings.GRID_HEIGHT);

		AnimationState walk = new AnimationState();
		walk.title = "Walk";
		float defaultFrameTime = 0.23f;
		walk.addFrame(sprites.getSprite(underground ? 20 : 14), defaultFrameTime);
		walk.addFrame(sprites.getSprite(underground ? 21 : 15), defaultFrameTime);
		walk.setDoesLoop(true);

		AnimationState squashed = new AnimationState();
		squashed.title = "Squashed";
		squashed.addFrame(sprites.getSprite(16), 0.1f);
		squashed.setDoesLoop(false);

		StateMachine stateMachine = new StateMachine();
		stateMachine.addState(walk);
		stateMachine.addState(squashed);
		stateMachine.addState(walk.title, squashed.title, "squashMe");
		stateMachine.setDefaultState(walk.title);
		goomba.addComponent(stateMachine);

		Rigidbody2D rb = new Rigidbody2D();
		rb.setBodyType(BodyType.DYNAMIC);
		rb.setFixedRotation(true);
		rb.setMass(0.1f);
		goomba.addComponent(rb);

		CircleCollider circleCollider = new CircleCollider();
		circleCollider.setRadius(0.49f);
		goomba.addComponent(circleCollider);

		goomba.addComponent(new GoombaAI());

		return goomba;
	}

	public static GameObject generatePipe(PipeDirection direction)
	{
		SpriteSheet pipes = AssetPool.getSpriteSheet("assets/images/pipes.png");
		GameObject pipe = generateSpriteObject(pipes.getSprite(direction.ordinal()), Settings.GRID_WIDTH * 2, Settings.GRID_HEIGHT * 2);

		Rigidbody2D rb = new Rigidbody2D();
		rb.setBodyType(BodyType.STATIC);
		rb.setFixedRotation(true);
		rb.setContinuousCollision(false);
		pipe.addComponent(rb);

		Box2DCollider boxCollider = new Box2DCollider();
		boxCollider.setHalfSize(new Vector2f(Settings.GRID_WIDTH * 2, Settings.GRID_HEIGHT * 2));
		pipe.addComponent(boxCollider);

		pipe.addComponent(new Pipe(direction));
		pipe.addComponent(new Ground());

		return pipe;
	}

	public static GameObject generateOneUpMushroom()
	{
		SpriteSheet items = AssetPool.getSpriteSheet("assets/images/items.png");
		GameObject mushroom = generateSpriteObject(items.getSprite(18), Settings.GRID_WIDTH, Settings.GRID_HEIGHT);

		Rigidbody2D rb = new Rigidbody2D();
		rb.setBodyType(BodyType.DYNAMIC);
		rb.setFixedRotation(true);
		rb.setContinuousCollision(false);
		mushroom.addComponent(rb);

		CircleCollider circleCollider = new CircleCollider();
		circleCollider.setRadius(0.49f);
		mushroom.addComponent(circleCollider);
		mushroom.addComponent(new MushroomAI(true));

		return mushroom;
	}

	public static GameObject generateTurtle()
	{
		SpriteSheet sprites = AssetPool.getSpriteSheet("assets/images/turtle.png");
		GameObject turtle = generateSpriteObject(sprites.getSprite(0), Settings.GRID_WIDTH, Settings.GRID_HEIGHT * 1.4f);

		AnimationState walk = new AnimationState();
		walk.title = "Walk";
		float defaultFrameTime = 0.23f;
		walk.addFrame(sprites.getSprite(0), defaultFrameTime);
		walk.addFrame(sprites.getSprite(1), defaultFrameTime);
		walk.setDoesLoop(true);

		AnimationState squashed = new AnimationState();
		squashed.title = "Squashed";
		squashed.addFrame(sprites.getSprite(2), 0.1f);
		squashed.setDoesLoop(false);

		AnimationState wake = new AnimationState();
		wake.title = "Wake";
		wake.addFrame(sprites.getSprite(2), 0.2f);
		wake.addFrame(sprites.getSprite(3), 0.2f);
		wake.setDoesLoop(true);

		StateMachine stateMachine = new StateMachine();
		stateMachine.addState(walk);
		stateMachine.addState(wake);
		stateMachine.addState(squashed);
		stateMachine.addState(walk.title, squashed.title, "squashMe");
		stateMachine.addState(wake.title, squashed.title, "squashMe");
		stateMachine.addState(squashed.title, wake.title, "wake");
		stateMachine.addState(wake.title, walk.title, "walk");
		stateMachine.setDefaultState(walk.title);
		turtle.addComponent(stateMachine);

		Rigidbody2D rb = new Rigidbody2D();
		rb.setBodyType(BodyType.DYNAMIC);
		rb.setFixedRotation(true);
		rb.setMass(0.1f);
		turtle.addComponent(rb);

		CircleCollider circleCollider = new CircleCollider();
		circleCollider.setRadius(0.49f);
		circleCollider.setOffset(new Vector2f(0f, -0.2f));
		turtle.addComponent(circleCollider);

		turtle.addComponent(new TurtleAI());

		return turtle;
	}

	public static GameObject generateGoalFlagPole()
	{
		SpriteSheet items = AssetPool.getSpriteSheet("assets/images/items.png");
		GameObject goalPole = generateSpriteObject(items.getSprite(33), Settings.GRID_WIDTH, Settings.GRID_HEIGHT);

		Rigidbody2D rb = new Rigidbody2D();
		rb.setBodyType(BodyType.STATIC);
		rb.setFixedRotation(true);
		rb.setContinuousCollision(false);
		goalPole.addComponent(rb);

		Box2DCollider box2dCollider = new Box2DCollider();
		box2dCollider.setHalfSize(new Vector2f(Settings.GRID_WIDTH * 0.4f, Settings.GRID_HEIGHT));
		box2dCollider.setOffset(new Vector2f(Settings.GRID_WIDTH * -0.3f, 0));
		goalPole.addComponent(box2dCollider);
		goalPole.addComponent(new GoalFlag(false));

		return goalPole;
	}

	public static GameObject generateGoalFlag()
	{
		SpriteSheet items = AssetPool.getSpriteSheet("assets/images/items.png");
		GameObject goalFlag = generateSpriteObject(items.getSprite(6), Settings.GRID_WIDTH, Settings.GRID_HEIGHT);

		Rigidbody2D rb = new Rigidbody2D();
		rb.setBodyType(BodyType.STATIC);
		rb.setFixedRotation(true);
		rb.setContinuousCollision(false);
		goalFlag.addComponent(rb);


		Box2DCollider box2dCollider = new Box2DCollider();
		box2dCollider.setHalfSize(new Vector2f(Settings.GRID_WIDTH * 0.4f, Settings.GRID_HEIGHT));
		box2dCollider.setOffset(new Vector2f(Settings.GRID_WIDTH * -0.3f, 0));
		goalFlag.addComponent(box2dCollider);
		goalFlag.addComponent(new GoalFlag(true));

		return goalFlag;
	}

	public static GameObject generateFireball(Vector2f position)
	{
		SpriteSheet items = AssetPool.getSpriteSheet("assets/images/items.png");
		GameObject fireball = generateSpriteObject(items.getSprite(32), Settings.GRID_WIDTH * 0.4f, Settings.GRID_HEIGHT * 0.4f);

		Rigidbody2D rb = new Rigidbody2D();
		rb.setBodyType(BodyType.DYNAMIC);
		rb.setFixedRotation(true);
		rb.setContinuousCollision(false);
		fireball.addComponent(rb);

		CircleCollider circleCollider = new CircleCollider();
		circleCollider.setRadius(Settings.GRID_WIDTH * 0.2f);
		fireball.addComponent(circleCollider);
		fireball.addComponent(new Fireball());
		fireball.transform.position.set(position);

		return fireball;
	}
}
