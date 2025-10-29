package me.jho5245.mario.editor;

import imgui.ImGui;
import me.jho5245.mario.components.SpriteRenderer;
import me.jho5245.mario.jade.GameObject;
import me.jho5245.mario.physics2d.components.Box2DCollider;
import me.jho5245.mario.physics2d.components.CircleCollider;
import me.jho5245.mario.physics2d.components.Rigidbody2D;
import me.jho5245.mario.renderer.PickingTexture;
import org.joml.Vector4f;

import java.util.ArrayList;
import java.util.List;

public class PropertiesWindow
{
	private List<GameObject> activeGameObjects;
	// 선택한 게임오브젝트 원래 색상
	private List<Vector4f> activeGameObjectsOriginColor;
	private GameObject activeGameObject;

	private PickingTexture pickingTexture;

	public PropertiesWindow(PickingTexture pickingTexture)
	{
		this.activeGameObjects = new ArrayList<>();
		this.activeGameObjectsOriginColor = new ArrayList<>();
		this.pickingTexture = pickingTexture;
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
		if (!activeGameObjectsOriginColor.isEmpty())
		{
			int i = 0;
			for (GameObject gameObject : activeGameObjects)
			{
				SpriteRenderer renderer = gameObject.getComponent(SpriteRenderer.class);
				if (renderer != null)
				{
					renderer.setColor(activeGameObjectsOriginColor.get(i));
				}
				i++;
			}
		}
		this.activeGameObjects.clear();
		this.activeGameObjectsOriginColor.clear();
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
		SpriteRenderer renderer = gameObject.getComponent(SpriteRenderer.class);
		if (renderer != null)
		{
			this.activeGameObjectsOriginColor.add(new Vector4f(renderer.getColor()));
			renderer.setColor(new Vector4f(1, 1, 1, 0.5f));
		}
		// 색깔이 없어도 빈 색상 추가
		else
		{
			this.activeGameObjectsOriginColor.add(new Vector4f());
		}
		this.activeGameObjects.add(gameObject);

	}

	public PickingTexture getPickingTexture()
	{
		return pickingTexture;
	}
}
