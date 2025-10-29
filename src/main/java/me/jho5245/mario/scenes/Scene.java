package me.jho5245.mario.scenes;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import me.jho5245.mario.components.Component;
import me.jho5245.mario.components.ComponentDeserializer;
import me.jho5245.mario.components.Transform;
import me.jho5245.mario.jade.Camera;
import me.jho5245.mario.jade.GameObject;
import me.jho5245.mario.jade.GameObjectDeserializer;
import me.jho5245.mario.jade.MouseListener;
import me.jho5245.mario.physics2d.Physics2D;
import me.jho5245.mario.renderer.DebugDraw;
import me.jho5245.mario.renderer.Renderer;
import org.joml.Vector2f;
import org.joml.Vector3f;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Scene
{
	private Renderer renderer;
	private Physics2D physics2D;
	private Camera camera;
	private boolean isRunning;
	private final List<GameObject> gameObjects;

	private SceneInitializer sceneInitializer;

	public Scene(SceneInitializer sceneInitializer, boolean playPhysics)
	{
		this.sceneInitializer = sceneInitializer;
		this.physics2D = new Physics2D(playPhysics);
		this.renderer = new Renderer();
		this.gameObjects = new ArrayList<>();
		this.isRunning = false;
	}

	public void init(Vector2f startCameraPosition)
	{
		if (startCameraPosition != null)
		{
			this.camera = new Camera(new Vector2f(startCameraPosition));
		}
		else
		{
			this.camera = new Camera();
		}
		this.sceneInitializer.loadResources(this);
		this.sceneInitializer.init(this);
	}

	public void start()
	{
		for (int i = 0; i < this.gameObjects.size(); i++)
		{
			GameObject gameObject = this.gameObjects.get(i);
			gameObject.start();
			renderer.add(gameObject);
			physics2D.addGameObject(gameObject);
		}
		isRunning = true;
	}

	public void destroy()
	{
		for (GameObject gameObject : gameObjects)
		{
			gameObject.destroy();
		}
	}

	public void addGameObject(GameObject gameObject)
	{
		gameObjects.add(gameObject);
		if (isRunning)
		{
			gameObject.start();
			renderer.add(gameObject);
			physics2D.addGameObject(gameObject);
		}
	}

	public GameObject getGameObject(int id)
	{
		Optional<GameObject> result = this.gameObjects.stream().filter(gameObject -> gameObject.getUid() == id).findFirst();
		return result.orElse(null);
	}

	public void editorUpdate(float dt)
	{
		this.camera.adjustProjection();

		for (int i = 0; i < gameObjects.size(); i++)
		{
			GameObject gameObject = gameObjects.get(i);
			gameObject.editorUpdate(dt);

			if (gameObject.isDead())
			{
				gameObjects.remove(i);
				this.renderer.destroyGameObject(gameObject);
				this.physics2D.destroyGameObject(gameObject);
				i--;
			}
		}
	}

	public void update(float dt)
	{
		this.camera.adjustProjection();
		this.physics2D.update(dt);

		for (int i = 0; i < gameObjects.size(); i++)
		{
			GameObject gameObject = gameObjects.get(i);
			gameObject.update(dt);

			if (gameObject.isDead())
			{
				gameObjects.remove(i);
				this.renderer.destroyGameObject(gameObject);
				this.physics2D.destroyGameObject(gameObject);
				i--;
			}
		}
	}

	public void render()
	{
		this.renderer.render();
	}

	public Camera getCamera()
	{
		return camera;
	}

	public void imgui()
	{
		this.sceneInitializer.imgui();
	}

	public void debugDrawPhysics()
	{
		this.physics2D.debugDraw();
	}

	public GameObject createGameObject(String name)
	{
		GameObject gameObject = new GameObject(name);
		gameObject.addComponent(new Transform());
		gameObject.transform = gameObject.getComponent(Transform.class);
		return gameObject;
	}

	public void save()
	{
		Gson gson = new GsonBuilder().setPrettyPrinting().registerTypeAdapter(Component.class, new ComponentDeserializer())
				.registerTypeAdapter(GameObject.class, new GameObjectDeserializer()).create();

		try
		{
			FileWriter writer = new FileWriter("level.json");
			writer.write(gson.toJson(gameObjects.stream().filter(GameObject::doSerialization).toList()));
			writer.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	public void load()
	{
		Gson gson = new GsonBuilder().setPrettyPrinting().registerTypeAdapter(Component.class, new ComponentDeserializer())
				.registerTypeAdapter(GameObject.class, new GameObjectDeserializer()).create();

		String inFile;
		try
		{
			inFile = new String(Files.readAllBytes(Paths.get("level.json")));
		}
		catch (IOException e)
		{
			e.printStackTrace();
			return;
		}

		if (!inFile.isEmpty())
		{
			int maxGameObjectId = -1;
			int maxComponentId = -1;
			GameObject[] gameObjects = gson.fromJson(inFile, GameObject[].class);
			for (GameObject gameObject : gameObjects)
			{
				addGameObject(gameObject);
				for (Component component : gameObject.getAllComponents())
				{
					if (component.getUid() > maxComponentId)
					{
						maxComponentId = component.getUid();
					}
				}

				if (gameObject.getUid() > maxGameObjectId)
				{
					maxGameObjectId = gameObject.getUid();
				}
			}
			maxGameObjectId++;
			maxComponentId++;
			GameObject.init(maxGameObjectId);
			Component.init(maxComponentId);
		}
	}

	public List<GameObject> getGameObjects()
	{
		return this.gameObjects;
	}
}
