package me.jho5245.mario.jade;

import imgui.ImGui;
import me.jho5245.mario.renderer.Renderer;

import java.util.ArrayList;
import java.util.List;

public abstract class Scene
{
	protected Renderer renderer = new Renderer();

	protected Camera camera;

	protected boolean running = false;

	protected final List<GameObject> gameObjects = new ArrayList<>();
	protected GameObject activeGameObject;

	public Scene()
	{

	}

	public void init()
	{

	}

	public void start()
	{
		gameObjects.forEach(gameObject ->
		{
			gameObject.start();
			renderer.add(gameObject);
		});
		running = true;
	}

	public void addGameObject(GameObject gameObject)
	{
		gameObjects.add(gameObject);
		if (running)
		{
			gameObject.start();
			renderer.add(gameObject);
		}
	}

	public abstract void update(float dt);

	public Camera getCamera()
	{
		return camera;
	}

	public void sceneImgui()
	{
		if (activeGameObject != null)
		{
			ImGui.begin("Inspector");
			activeGameObject.imgui();
			ImGui.end();
		}

		imgui();
	}

	public void imgui()
	{

	}
}
