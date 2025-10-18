package me.jho5245.mario.components;

import me.jho5245.mario.jade.GameObject;
import me.jho5245.mario.jade.MouseListener;
import me.jho5245.mario.jade.Window;

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
			holdingObject.getTransform().getPosition().x = MouseListener.getOrthoX() - 16;
			holdingObject.getTransform().getPosition().y = MouseListener.getOrthoY() - 16;

			if (MouseListener.mouseButtonDown(GLFW_MOUSE_BUTTON_LEFT))
			{
				place();
			}
		}
	}
}
