package me.jho5245.mario.editor;

import imgui.ImGui;
import me.jho5245.mario.components.NonPickable;
import me.jho5245.mario.jade.GameObject;
import me.jho5245.mario.jade.MouseListener;
import me.jho5245.mario.physics2d.components.Box2DCollider;
import me.jho5245.mario.physics2d.components.CircleCollider;
import me.jho5245.mario.physics2d.components.Rigidbody2D;
import me.jho5245.mario.renderer.PickingTexture;
import me.jho5245.mario.scenes.Scene;

import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_LEFT;

public class PropertiesWindow
{
	private GameObject activeGameObject;

	private PickingTexture pickingTexture;

	private float debounceTime = 0.2f;

	public PropertiesWindow(PickingTexture pickingTexture)
	{
		this.pickingTexture = pickingTexture;
		this.activeGameObject = null;
	}

	public void update(float dt, Scene currentScene)
	{
		debounceTime -= dt;

		if (MouseListener.mouseButtonDown(GLFW_MOUSE_BUTTON_LEFT) && debounceTime < 0)
		{
			int x = (int) MouseListener.getScreenX();
			int y = (int) MouseListener.getScreenY();
			int gameObjectId = pickingTexture.readPixel(x, y);
			GameObject pickedObject = currentScene.getGameObject(gameObjectId);
			if (pickedObject != null && pickedObject.getComponent(NonPickable.class) == null)
			{
				activeGameObject = pickedObject;
			}
			else if (pickedObject == null && !MouseListener.isDragging())
			{
				activeGameObject = null;
			}
			this.debounceTime = 0.2f;
		}
	}

	public void imgui()
	{
		if (activeGameObject != null)
		{
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

	public GameObject getActiveGameObject()
	{
		return activeGameObject;
	}

	public void setActiveGameObject(GameObject gameObject)
	{
		this.activeGameObject = gameObject;
	}
}
