package me.jho5245.mario.components;

import me.jho5245.mario.jade.GameObject;
import me.jho5245.mario.jade.MouseListener;
import me.jho5245.mario.jade.Window;
import me.jho5245.mario.util.Settings;

import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_LEFT;

public class MouseControls extends Component
{
	GameObject holdingObject;

	public void pickUpObject(GameObject object)
	{
		this.holdingObject = object;
		Window.getCurrentScene().addGameObject(object);
	}

	public void place()
	{
		this.holdingObject = null;
	}

	@Override
	public void update(float dt)
	{
		if (holdingObject != null)
		{
			holdingObject.getTransform().getPosition().x = MouseListener.getOrthoX();
			holdingObject.getTransform().getPosition().y = MouseListener.getOrthoY();
			holdingObject.getTransform().getPosition().x = (int) (holdingObject.getTransform().getPosition().x / Settings.GRID_WIDTH) * Settings.GRID_WIDTH;
			holdingObject.getTransform().getPosition().y = (int) (holdingObject.getTransform().getPosition().y / Settings.GRID_HEIGHT) * Settings.GRID_HEIGHT;


			if (MouseListener.mouseButtonDown(GLFW_MOUSE_BUTTON_LEFT))
			{
				place();
			}
		}
	}
}
