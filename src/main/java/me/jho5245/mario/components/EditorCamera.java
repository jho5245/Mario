package me.jho5245.mario.components;

import me.jho5245.mario.jade.Camera;
import me.jho5245.mario.jade.KeyListener;
import me.jho5245.mario.jade.MouseListener;
import org.joml.Vector2f;

import java.security.Key;

import static org.lwjgl.glfw.GLFW.*;

public class EditorCamera extends Component
{
	private float dragDebounce = 0.032f;

	private final Camera camera;

	private Vector2f clickOrigin;

	private float dragSensivity = 30f;

	private float scrollSensivity = 0.1f;

	private float resetSpeed = 3f;

	private float lerpTime;

	private boolean reset;

	private boolean isCtrlPressed;

	public EditorCamera(Camera camera)
	{
		this.camera = camera;
		this.clickOrigin = new Vector2f();
	}

	@Override
	public void update(float dt)
	{
		if (MouseListener.mouseButtonDown(GLFW_MOUSE_BUTTON_MIDDLE) && dragDebounce > 0)
		{
			this.clickOrigin = new Vector2f(MouseListener.getOrthoX(), MouseListener.getOrthoY());
			dragDebounce -= dt;
			return;
		}
		else if (MouseListener.mouseButtonDown(GLFW_MOUSE_BUTTON_MIDDLE))
		{
			Vector2f mousePos = new Vector2f(MouseListener.getOrthoX(), MouseListener.getOrthoY());
			Vector2f delta = new Vector2f(mousePos).sub(clickOrigin);
			camera.getPosition().sub(delta.mul(dt).mul(dragSensivity));
			clickOrigin.lerp(mousePos, dt);
			reset = false;
		}

		if (dragDebounce <= 0f && !MouseListener.mouseButtonDown(GLFW_MOUSE_BUTTON_MIDDLE))
		{
			dragDebounce = 0.1f;
		}

		if (MouseListener.getScrollY() != 0f)
		{
			float scrollSensivity = this.scrollSensivity * (isCtrlPressed ? 3 : 1);
			float addValue = (float) Math.pow(Math.abs(MouseListener.getScrollY() * scrollSensivity), 1 / camera.getZoom());
			addValue *= -Math.signum(MouseListener.getScrollY());
			camera.addZoom(addValue);
		}

		if (KeyListener.isKeyPressed(GLFW_KEY_KP_DECIMAL))
		{
			if (isCtrlPressed)
			{
				camera.setZoom(1f);
			}
			else
			{
				lerpTime = 0f;
				reset = true;
			}
		}

		isCtrlPressed = KeyListener.isKeyPressed(GLFW_KEY_LEFT_CONTROL);

		if (reset)
		{
			camera.getPosition().lerp(new Vector2f(), lerpTime);
			camera.setZoom(camera.getZoom() + ((1f - camera.getZoom()) * lerpTime));
			lerpTime += dt * resetSpeed / 100f;
			if (Math.abs(camera.getPosition().x) <= 1f && Math.abs(camera.getPosition().y) <= 1f)
			{
				lerpTime = 0f;
				camera.getPosition().set(0f, 0f);
				camera.setZoom(1f);
				reset = false;
			}
		}
	}
}
