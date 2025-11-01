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
