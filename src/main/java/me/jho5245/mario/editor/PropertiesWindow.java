package me.jho5245.mario.editor;

import imgui.ImGui;
import me.jho5245.mario.jade.GameObject;
import me.jho5245.mario.jade.MouseListener;
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
	}

	public void update(float dt, Scene currentScene)
	{
		debounceTime -= dt;

		if (MouseListener.mouseButtonDown(GLFW_MOUSE_BUTTON_LEFT) && debounceTime < 0)
		{
			int x = (int) MouseListener.getScreenX();
			int y = (int) MouseListener.getScreenY();
			int gameObjectId = pickingTexture.readPixel(x, y);
			activeGameObject = currentScene.getGameObject(gameObjectId);
			this.debounceTime = 0.2f;
		}
	}

	public void imgui()
	{
		if (activeGameObject != null)
		{
			ImGui.begin("Properties");
			activeGameObject.imgui();
			ImGui.end();
		}
	}

	public GameObject getActiveGameObject()
	{
		return activeGameObject;
	}
}
