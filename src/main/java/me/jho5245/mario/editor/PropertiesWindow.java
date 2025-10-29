package me.jho5245.mario.editor;

import imgui.ImGui;
import me.jho5245.mario.jade.GameObject;
import me.jho5245.mario.physics2d.components.Box2DCollider;
import me.jho5245.mario.physics2d.components.CircleCollider;
import me.jho5245.mario.physics2d.components.Rigidbody2D;
import me.jho5245.mario.renderer.PickingTexture;

import java.util.ArrayList;
import java.util.List;

public class PropertiesWindow
{
	private List<GameObject> activeGameObjects;

	private GameObject activeGameObject;

	private PickingTexture pickingTexture;

	public PropertiesWindow(PickingTexture pickingTexture)
	{
		this.activeGameObjects = new ArrayList<>();
		this.pickingTexture = pickingTexture;
		this.activeGameObject = null;
	}

	public void imgui()
	{
		if (activeGameObjects.size() == 1 && activeGameObjects.getFirst() != null)
		{
			activeGameObject = activeGameObjects.getFirst();
			ImGui.begin("Properties");
			if (ImGui.beginPopupContextWindow("ComponentAdder"))
			{
				if (ImGui.menuItem("Add Rigidbody"))
				{
					if (activeGameObject.getComponent(Rigidbody2D.class) == null)
					{
						activeGameObject.addComponent(new Rigidbody2D());
					}
				}

				if (ImGui.menuItem("Add Box Collider"))
				{
					if (activeGameObject.getComponent(Box2DCollider.class) == null && activeGameObject.getComponent(CircleCollider.class) == null)
					{
						activeGameObject.addComponent(new Box2DCollider());
					}
				}

				if (ImGui.menuItem("Add Circle Collider"))
				{
					if (activeGameObject.getComponent(Box2DCollider.class) == null && activeGameObject.getComponent(CircleCollider.class) == null)
					{
						activeGameObject.addComponent(new CircleCollider());
					}
				}

				ImGui.endPopup();
			}
			activeGameObject.imgui();
			ImGui.end();
		}
	}

	public List<GameObject> getActiveGameObjects()
	{
		return activeGameObjects;
	}

	public void clearSelected()
	{
		this.activeGameObjects.clear();
	}

	public GameObject getActiveGameObject()
	{
		return activeGameObjects.size() == 1 ? activeGameObjects.getFirst() : null;
	}

	public void setActiveGameObject(GameObject gameObject)
	{
		if (gameObject != null)
		{
			clearSelected();
			this.activeGameObjects.add(gameObject);
		}
	}

	public void addActiveGameObject(GameObject gameObject)
	{
		this.activeGameObjects.add(gameObject);
	}

	public PickingTexture getPickingTexture()
	{
		return pickingTexture;
	}
}
