package me.jho5245.mario.jade;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import imgui.ImGui;
import me.jho5245.mario.components.Component;
import me.jho5245.mario.components.ComponentDeserializer;
import me.jho5245.mario.components.SpriteRenderer;
import me.jho5245.mario.components.Transform;
import me.jho5245.mario.util.AssetPool;

import java.util.ArrayList;
import java.util.List;

public class GameObject
{
	private static int ID_COUNTER = 0;
	private int uid = -1;
	private String name;

	private List<Component> components;
	public transient Transform transform;
	private boolean doSerialization = true;
	private boolean isDead;

	public GameObject(String name)
	{
		this.name = name;
		this.components = new ArrayList<>();
		this.uid = GameObject.ID_COUNTER++;
	}

	public <T extends Component> T getComponent(Class<T> clazz)
	{
		for (Component component : components)
		{
			if (clazz.isAssignableFrom(component.getClass()))
			{
				try
				{
					return clazz.cast(component);
				}
				catch (ClassCastException e)
				{
					e.printStackTrace();
					assert false : "Error casting component";
				}
			}
		}
		return null;
	}

	public <T extends Component> void removeComponent(Class<T> clazz)
	{
		components.removeIf(component -> clazz.isAssignableFrom(component.getClass()));
	}

	public void addComponent(Component component)
	{
		component.generateId();
		components.add(component);
		component.gameObject = this;
	}

	public void update(float dt)
	{
		components.forEach(component -> component.update(dt));
	}

	public void start()
	{
		for (int i = 0; i < components.size(); i++)
		{
			components.get(i).start();
		}
	}

	public Transform getTransform()
	{
		return this.transform;
	}

	public void imgui()
	{
		for (Component component : components)
		{
			if (ImGui.collapsingHeader(component.getClass().getSimpleName()))
			{
				component.imgui();
			}
		}
	}

	public int getUid()
	{
		return this.uid;
	}

	public List<Component> getAllComponents()
	{
		return this.components;
	}

	public static void init(int maxId)
	{
		ID_COUNTER = maxId;
	}

	public boolean doSerialization()
	{
		return this.doSerialization;
	}

	public void setNoSerialize()
	{
		this.doSerialization = false;
	}

	public void destroy()
	{
		this.isDead = true;
		for (int i = 0; i < components.size(); i++)
		{
			components.get(i).destroy();
		}
	}

	public GameObject copy()
	{
		Gson gson = new GsonBuilder().setPrettyPrinting().registerTypeAdapter(Component.class, new ComponentDeserializer())
				.registerTypeAdapter(GameObject.class, new GameObjectDeserializer()).create();
		String objAsJson = gson.toJson(this);
		GameObject obj = gson.fromJson(objAsJson, GameObject.class);
		obj.generateUid();
		for (Component c : obj.getAllComponents())
		{
			c.generateId();
		}

		SpriteRenderer sprite = obj.getComponent(SpriteRenderer.class);
		if (sprite != null && sprite.getTexture() != null)
		{
			sprite.setTexture(AssetPool.getTexture(sprite.getTexture().getFilePath()));
		}

		return obj;
	}

	public void generateUid()
	{
		this.uid = ID_COUNTER++;
	}

	public boolean isDead()
	{
		return this.isDead;
	}
}
