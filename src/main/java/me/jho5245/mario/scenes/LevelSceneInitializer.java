package me.jho5245.mario.scenes;

import me.jho5245.mario.components.GameCamera;
import me.jho5245.mario.jade.GameObject;

public class LevelSceneInitializer extends SceneInitializer
{
	private GameObject cameraObject;

	public GameCamera gameCamera;

	@Override
	public void init(Scene scene)
	{
		cameraObject = scene.createGameObject("GameCamera");
		this.gameCamera = new GameCamera(scene.getCamera(), 0, 0);
		cameraObject.addComponent(gameCamera);
		cameraObject.start();
		scene.addGameObject(cameraObject);
	}

	@Override
	public void imgui()
	{

	}
}
