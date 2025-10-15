package me.jho5245.mario.jade;

import org.joml.Vector2f;

import java.util.ArrayList;
import java.util.List;

public class GameObject
{
	private String name;
	private List<Component> components;
	private Transform transform;
	private int zIndex;

	public GameObject(String name)
	{
		this(name, new Transform(), 0);
	}

	public GameObject(String name, Transform transform, int zIndex)
	{
		this.name = name;
		this.components = new ArrayList<>();
		this.transform = transform;
		this.zIndex = zIndex;
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
					assert false: "Error casting component";
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
		components.add(component);
		component.gameObject = this;
	}

	public void update(float dt)
	{
		components.forEach(component -> component.update(dt));
	}

	public void start()
	{
		components.forEach(Component::start);
	}

	public Transform getTransform()
	{
		return this.transform;
	}

	public int zIndex()
	{
		return this.zIndex;
	}
}
