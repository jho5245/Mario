package me.jho5245.mario.components;

import me.jho5245.mario.jade.Camera;
import me.jho5245.mario.jade.KeyListener;
import me.jho5245.mario.jade.MouseListener;
import me.jho5245.mario.util.Settings;
import org.joml.Vector2f;

import static org.lwjgl.glfw.GLFW.*;

public class EditorCamera extends Component
{
	private final float dragDebounceOrigin = 0.032f;
	private float dragDebounce = dragDebounceOrigin;

	private final float keyDelayOrigin = 0.1f;
	private float keyDelay = keyDelayOrigin;

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
	public void editorUpdate(float dt)
	{
		final boolean MOUSE_MIDDLE = MouseListener.mouseButtonDown(GLFW_MOUSE_BUTTON_MIDDLE);
		final boolean KEY_LEFT_ALT = KeyListener.isKeyPressed(GLFW_KEY_LEFT_ALT);

		if (MOUSE_MIDDLE && dragDebounce > 0)
		{
			this.clickOrigin = MouseListener.getWorld();
			dragDebounce -= dt;
			return;
		}
		else if (MOUSE_MIDDLE)
		{
			Vector2f mousePos = MouseListener.getWorld();
			Vector2f delta = new Vector2f(mousePos).sub(clickOrigin);
			camera.getPosition().sub(delta.mul(dt).mul(dragSensivity));
			clickOrigin.lerp(mousePos, dt);
			if (!KEY_LEFT_ALT)
			{
				float x = camera.getPosition().x;
				float y = camera.getPosition().y;
				camera.getPosition().x = (int) (x / Settings.GRID_WIDTH) * Settings.GRID_WIDTH - Settings.GRID_WIDTH / 2;
				camera.getPosition().y = (int) (y / Settings.GRID_HEIGHT) * Settings.GRID_HEIGHT -  Settings.GRID_HEIGHT / 2;
			}
			reset = false;
		}

		// mouse camera by keyboard
		{
			boolean moving = false;
			float dx = 0;
			float dy = 0;
			if (KeyListener.isKeyPressed(GLFW_KEY_A))
			{
				moving = true;
				if (keyDelay < 0)
				{
					dx = -Settings.GRID_WIDTH;
					keyDelay = keyDelayOrigin;
				}
			}
			else if (KeyListener.isKeyPressed(GLFW_KEY_D))
			{
				moving = true;
				if (keyDelay < 0)
				{
					dx = Settings.GRID_WIDTH;
					keyDelay = keyDelayOrigin;
				}
			}
			if (KeyListener.isKeyPressed(GLFW_KEY_W))
			{
				moving = true;
				if (keyDelay < 0)
				{
					dy = Settings.GRID_HEIGHT;
					keyDelay = keyDelayOrigin;
				}
			}
			else if (KeyListener.isKeyPressed(GLFW_KEY_S))
			{
				moving = true;
				if (keyDelay < 0)
				{
					dy = -Settings.GRID_HEIGHT;
					keyDelay = keyDelayOrigin;
				}
			}

			if (moving)
			{
				keyDelay -= dt;
				// double speed when shift is pressed
				if (KeyListener.isKeyPressed(GLFW_KEY_LEFT_SHIFT))
				{
					dx *= 2;
					dy *= 2;
				}
				else if (KEY_LEFT_ALT)
				{
					dx /= 2;
					dy /= 2;
				}
				camera.getPosition().x += dx;
				camera.getPosition().y += dy;
			}
		}

		if (dragDebounce <= 0f && !MouseListener.mouseButtonDown(GLFW_MOUSE_BUTTON_MIDDLE))
		{
			dragDebounce = dragDebounceOrigin;
		}

		if (MouseListener.getScrollY() != 0f)
		{
			float scrollSensivity = this.scrollSensivity * (isCtrlPressed ? 3 : 1);
			float addValue = (float) Math.pow(Math.abs(MouseListener.getScrollY() * scrollSensivity), 1 / camera.getZoom());
			addValue *= -Math.signum(MouseListener.getScrollY());
			camera.addZoom(addValue);
			reset = false;
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
			camera.getPosition().lerp(new Vector2f(-Settings.GRID_WIDTH / 2, -Settings.GRID_HEIGHT / 2), lerpTime);
			camera.setZoom(camera.getZoom() + ((1f - camera.getZoom()) * lerpTime));
			lerpTime += dt * resetSpeed / 100f;
			if (Math.abs(camera.getZoom() - 1f) <= 0.001f && Math.abs(camera.getPosition().x + Settings.GRID_WIDTH / 2) <= 0.01f && Math.abs(camera.getPosition().y + Settings.GRID_HEIGHT / 2) <= 0.01f)
			{
				lerpTime = 0f;
				camera.getPosition().set(new Vector2f(-Settings.GRID_WIDTH / 2, -Settings.GRID_HEIGHT / 2));
				camera.setZoom(1f);
				reset = false;
			}
		}
	}
}
