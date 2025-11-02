package me.jho5245.mario.scenes;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import me.jho5245.mario.components.Component;
import me.jho5245.mario.components.ComponentDeserializer;
import me.jho5245.mario.components.PlayerController;
import me.jho5245.mario.components.Transform;
import me.jho5245.mario.jade.Camera;
import me.jho5245.mario.jade.GameObject;
import me.jho5245.mario.jade.GameObjectDeserializer;
import me.jho5245.mario.physics2d.Physics2D;
import me.jho5245.mario.renderer.Renderer;
import org.joml.Vector2f;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
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
	private List<GameObject> pendingObjects;
	private String levelName;

	private SceneInitializer sceneInitializer;

	public Scene(SceneInitializer sceneInitializer, boolean playPhysics, String levelName)
	{
		this.sceneInitializer = sceneInitializer;
		this.physics2D = new Physics2D(playPhysics);
		this.renderer = new Renderer();
		this.gameObjects = new ArrayList<>();
		this.pendingObjects = new ArrayList<>();
		this.isRunning = false;
		this.levelName = levelName;
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
			physics2D.add(gameObject);
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
		if (!isRunning)
		{
			gameObjects.add(gameObject);
		}
		else
		{
			pendingObjects.add(gameObject);
		}
	}

	public GameObject getGameObject(int id)
	{
		Optional<GameObject> result = this.gameObjects.stream().filter(gameObject -> gameObject.getUid() == id).findFirst();
		return result.orElse(null);
	}

	public GameObject getGameObject(String name)
	{
		Optional<GameObject> result = this.gameObjects.stream().filter(gameObject -> gameObject.name.equals(name)).findFirst();
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

		for (int i = 0; i < pendingObjects.size(); i++)
		{
			GameObject gameObject = pendingObjects.get(i);
			gameObjects.add(gameObject);
			gameObject.start();
			renderer.add(gameObject);
			physics2D.add(gameObject);
		}
		pendingObjects.clear();
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

		for (int i = 0; i < pendingObjects.size(); i++)
		{
			GameObject gameObject = pendingObjects.get(i);
			gameObjects.add(gameObject);
			gameObject.start();
			renderer.add(gameObject);
			physics2D.add(gameObject);
		}
		pendingObjects.clear();
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

	/**
	 * 특정 {@link Component}를 가지고 있는 가장 첫 번째 게임오브젝트를 반환함.
	 * @param clazz 게임오브젝트가 가지고 있는 컴포넌트
	 * @return 해당 컴포넌트를 가지고 있는 게임오브젝트 혹은 없을 경우 <code>null</code>
	 * @param <T> 컴포넌트 유형
	 */
	public <T extends Component> GameObject getGameObjectWith(Class<T> clazz)
	{
		for (GameObject gameObject : gameObjects)
		{
			if (gameObject.getComponent(clazz) != null)
			{
				return gameObject;
			}
		}
		return null;
	}

	public void save()
	{
		Gson gson = new GsonBuilder().setPrettyPrinting().registerTypeAdapter(Component.class, new ComponentDeserializer())
				.registerTypeAdapter(GameObject.class, new GameObjectDeserializer()).enableComplexMapKeySerialization().create();

		try
		{
			FileWriter writer = new FileWriter(this.levelName);
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
				.registerTypeAdapter(GameObject.class, new GameObjectDeserializer()).enableComplexMapKeySerialization().create();

		String inFile;
		try
		{
			inFile = new String(Files.readAllBytes(Paths.get(this.levelName)));
		}
		catch (NoSuchFileException e)
		{
			System.out.println("No file found! Making a new one..");
			return;
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

	public Physics2D getPhysics()
	{
		return this.physics2D;
	}

	public SceneInitializer getSceneInitializer()
	{
		return sceneInitializer;
	}
}
