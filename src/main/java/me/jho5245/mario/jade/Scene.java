package me.jho5245.mario.jade;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import imgui.ImGui;
import me.jho5245.mario.renderer.Renderer;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class Scene
{
	protected Renderer renderer = new Renderer();

	protected Camera camera;

	protected boolean running = false;

	protected final List<GameObject> gameObjects = new ArrayList<>();
	protected GameObject activeGameObject;

	protected boolean levelLoaded = false;

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

	public void saveExit()
	{
		Gson gson = new GsonBuilder()
				.setPrettyPrinting()
				.registerTypeAdapter(Component.class, new ComponentDeserializer())
				.registerTypeAdapter(GameObject.class, new GameObjectDeserializer())
				.create();

		try
		{
			FileWriter writer = new FileWriter("level.txt");
			writer.write(gson.toJson(gameObjects));
			writer.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	public void load()
	{
		Gson gson = new GsonBuilder()
				.setPrettyPrinting()
				.registerTypeAdapter(Component.class, new ComponentDeserializer())
				.registerTypeAdapter(GameObject.class, new GameObjectDeserializer())
				.create();

		String inFile;
		try
		{
			inFile = new String(Files.readAllBytes(Paths.get("level.txt")));
		}
		catch (IOException e)
		{
			e.printStackTrace();
			return;
		}

		if (!inFile.isEmpty())
		{
			GameObject[] gameObjects = gson.fromJson(inFile, GameObject[].class);
			Arrays.stream(gameObjects).forEach(this::addGameObject);
			this.levelLoaded = true;
		}
	}
}
