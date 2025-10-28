package me.jho5245.mario.jade;

import me.jho5245.mario.components.Component;
import me.jho5245.mario.components.Transform;

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
		for (int i=0; i<components.size(); i++)
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
		components.forEach(Component::imgui);
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

}
