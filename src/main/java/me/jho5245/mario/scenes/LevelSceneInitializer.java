package me.jho5245.mario.scenes;

import me.jho5245.mario.animations.StateMachine;
import me.jho5245.mario.components.*;
import me.jho5245.mario.jade.GameObject;
import me.jho5245.mario.util.AssetPool;

public class LevelSceneInitializer extends SceneInitializer
{
	private GameObject cameraObject;

	@Override
	public void init(Scene scene)
	{
		cameraObject = scene.createGameObject("GameCamera");
		cameraObject.addComponent(new GameCamera(scene.getCamera()));
		cameraObject.start();
		scene.addGameObject(cameraObject);
	}

	@Override
	public void loadResources(Scene scene)
	{
		AssetPool.getShader("assets/shaders/default.glsl");

		AssetPool.addSpriteSheet("assets/images/spritesheets/decorationsAndBlocks.png", 16, 16, 81, 0);
		AssetPool.addSpriteSheet("assets/images/spritesheet.png", 16, 16, 26, 0);
		AssetPool.addSpriteSheet("assets/images/turtle.png", 16, 24, 4, 0);
		AssetPool.addSpriteSheet("assets/images/bigSpritesheet.png", 16, 32, 42, 0);
		AssetPool.addSpriteSheet("assets/images/pipes.png", 32, 32, 4, 0);
		AssetPool.addSpriteSheet("assets/images/items.png", 16, 16, 43, 0);
		AssetPool.addSpriteSheet("assets/images/gizmos.png", 24, 48, 3, 0);

		AssetPool.addSound("assets/sounds/main-theme-overworld.ogg", true);
		AssetPool.addSound("assets/sounds/flagpole.ogg", false);
		AssetPool.addSound("assets/sounds/break_block.ogg", false);
		AssetPool.addSound("assets/sounds/bump.ogg", false);
		AssetPool.addSound("assets/sounds/coin.ogg", false);
		AssetPool.addSound("assets/sounds/gameover.ogg", false);
		AssetPool.addSound("assets/sounds/jump-small.ogg", false);
		AssetPool.addSound("assets/sounds/jump-super.ogg", false);
		AssetPool.addSound("assets/sounds/mario_die.ogg", false);
		AssetPool.addSound("assets/sounds/pipe.ogg", false);
		AssetPool.addSound("assets/sounds/powerup.ogg", false);
		AssetPool.addSound("assets/sounds/powerup_appears.ogg", false);
		AssetPool.addSound("assets/sounds/stage_clear.ogg", false);
		AssetPool.addSound("assets/sounds/stomp.ogg", false);
		AssetPool.addSound("assets/sounds/kick.ogg", false);
		AssetPool.addSound("assets/sounds/invincible.ogg", true);
		AssetPool.addSound("assets/sounds/1-up.ogg", false);

		for (GameObject g : scene.getGameObjects())
		{
			if (g.getComponent(SpriteRenderer.class) != null)
			{
				SpriteRenderer spr = g.getComponent(SpriteRenderer.class);
				if (spr.getTexture() != null)
				{
					spr.setTexture(AssetPool.getTexture(spr.getTexture().getFilePath()));
				}
			}

			if (g.getComponent(StateMachine.class) != null)
			{
				StateMachine stateMachine = g.getComponent(StateMachine.class);
				stateMachine.refreshTextures();
			}
		}
	}

	@Override
	public void imgui()
	{

	}
}
