package me.jho5245.mario.components;

import me.jho5245.mario.animations.StateMachine;
import me.jho5245.mario.jade.GameObject;
import me.jho5245.mario.jade.KeyListener;
import me.jho5245.mario.jade.MouseListener;
import me.jho5245.mario.jade.Window;
import me.jho5245.mario.util.Settings;
import org.joml.Vector4f;

import java.util.Optional;

import static org.lwjgl.glfw.GLFW.*;

public class MouseControls extends Component
{
	GameObject holdingObject;

	private float debounceTime = 0.05f;
	private float debounce = debounceTime;

	public void pickUpObject(GameObject object)
	{
		if (this.holdingObject != null)
		{
			this.holdingObject.destroy();
		}
		this.holdingObject = object;
		this.holdingObject.getComponent(SpriteRenderer.class).setColor(new Vector4f(1, 1, 1, 0.5f));
		this.holdingObject.addComponent(new NonPickable());
		Window.getCurrentScene().addGameObject(object);
	}

	public void place()
	{
		GameObject copy = holdingObject.copy();
		Optional.ofNullable(copy.getComponent(StateMachine.class)).ifPresent(StateMachine::refreshTextures);
		copy.getComponent(SpriteRenderer.class).setColor(new Vector4f(1));
		copy.removeComponent(NonPickable.class);
		Window.getCurrentScene().addGameObject(copy);
	}

	@Override
	public void editorUpdate(float dt)
	{
		debounce -= dt;
		if (holdingObject != null && debounce <= 0)
		{
			float x = MouseListener.getWorldX();
			float y = MouseListener.getWorldY();
			if (!KeyListener.isKeyPressed(GLFW_KEY_LEFT_ALT))
			{
				x = Math.round(x / Settings.GRID_WIDTH) * Settings.GRID_WIDTH;
				y = Math.round(y / Settings.GRID_HEIGHT) * Settings.GRID_HEIGHT;
			}
			holdingObject.getTransform().getPosition().x = x;
			holdingObject.getTransform().getPosition().y = y;

			if (MouseListener.mouseButtonDown(GLFW_MOUSE_BUTTON_LEFT))
			{
				place();
				debounce = debounceTime;
			}

			if (KeyListener.isKeyPressed(GLFW_KEY_ESCAPE))
			{
				holdingObject.destroy();
				holdingObject = null;
			}
		}
	}
}
