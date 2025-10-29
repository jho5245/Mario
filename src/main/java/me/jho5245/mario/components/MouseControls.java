package me.jho5245.mario.components;

import me.jho5245.mario.animations.StateMachine;
import me.jho5245.mario.editor.PropertiesWindow;
import me.jho5245.mario.jade.GameObject;
import me.jho5245.mario.jade.KeyListener;
import me.jho5245.mario.jade.MouseListener;
import me.jho5245.mario.jade.Window;
import me.jho5245.mario.renderer.DebugDraw;
import me.jho5245.mario.renderer.PickingTexture;
import me.jho5245.mario.scenes.Scene;
import me.jho5245.mario.sounds.Sound;
import me.jho5245.mario.util.Settings;
import org.joml.Vector2f;
import org.joml.Vector2i;
import org.joml.Vector4f;

import java.util.HashSet;
import java.util.Optional;
import java.util.Scanner;
import java.util.Set;

import static org.lwjgl.glfw.GLFW.*;

public class MouseControls extends Component
{
	GameObject holdingObject;

	private final float debounceTime = 0.1f;
	private float debounce = debounceTime;
	private boolean boxSelectSet;
	private Vector2f boxSelectStart = new Vector2f(), boxSelectEnd = new Vector2f();

	PropertiesWindow propertiesWindow = Window.getImGuiLayer().getPropertiesWindow();

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
		PickingTexture pickingTexture = propertiesWindow.getPickingTexture();
		Scene currentScene = Window.getCurrentScene();

		if (holdingObject != null)
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
				float halfWidth = Settings.GRID_WIDTH / 2;
				float halfHeight = Settings.GRID_HEIGHT / 2;
				if (MouseListener.isDragging() && !blockInSquare(holdingObject.transform.position.x - halfWidth, holdingObject.transform.position.y - halfHeight))
				{
					place();
				}
				else if (!MouseListener.isDragging() && debounce < 0)
				{

					place();
					debounce = debounceTime;
				}
			}

			if (KeyListener.isKeyPressed(GLFW_KEY_ESCAPE))
			{
				holdingObject.destroy();
				holdingObject = null;
			}
		}
		else if (!MouseListener.isDragging() && MouseListener.mouseButtonDown(GLFW_MOUSE_BUTTON_LEFT) && debounce < 0)
		{
			int x = (int) MouseListener.getScreenX();
			int y = (int) MouseListener.getScreenY();
			int gameObjectId = pickingTexture.readPixel(x, y);
			GameObject pickedObject = currentScene.getGameObject(gameObjectId);
			if (pickedObject != null && pickedObject.getComponent(NonPickable.class) == null)
			{
				propertiesWindow.setActiveGameObject(pickedObject);
			}
			else if (pickedObject == null && !MouseListener.isDragging())
			{
				propertiesWindow.clearSelected();
			}
			this.debounce = 0.2f;
		}
		else if (MouseListener.isDragging() && MouseListener.mouseButtonDown(GLFW_MOUSE_BUTTON_LEFT))
		{
			if (!boxSelectSet)
			{
				Window.getImGuiLayer().getPropertiesWindow().clearSelected();
				boxSelectStart = MouseListener.getScreen();
				boxSelectSet = true;
			}
			boxSelectEnd = MouseListener.getScreen();
			Vector2f boxSelectStartWorld = MouseListener.screenToWorld(boxSelectStart);
			Vector2f boxSelectEndWorld = MouseListener.screenToWorld(boxSelectEnd);
			Vector2f halfSize = (new Vector2f(boxSelectEndWorld).sub(boxSelectStartWorld)).mul(0.5f);
			DebugDraw.addBox((new Vector2f(boxSelectStartWorld)).add(halfSize), new Vector2f(halfSize).mul(2f), 0f);
			System.out.println(boxSelectStart.x + " " + boxSelectStart.y);
		}
		else if (boxSelectSet)
		{
			boxSelectSet = false;
			int screenStartX = (int) boxSelectStart.x;
			int screenStartY = (int) boxSelectStart.y;
			int screenEndX = (int) boxSelectEnd.x;
			int screenEndY = (int) boxSelectEnd.y;
			boxSelectStart.zero();
			boxSelectEnd.zero();

			if (screenEndX < screenStartX)
			{
				int temp = screenStartX;
				screenStartX = screenEndX;
				screenEndX = temp;
			}

			if (screenEndY < screenStartY)
			{
				int temp = screenStartY;
				screenStartY = screenEndY;
				screenEndY = temp;
			}

			float[] gameObjectIds = pickingTexture.readPixels(new Vector2i(screenStartX, screenStartY), new Vector2i(screenEndX, screenEndY));
			Set<Integer> uniqueGameObjectIds = new HashSet<>();
			for (float gameObjectId : gameObjectIds)
			{
				uniqueGameObjectIds.add((int) gameObjectId);
			}
			for (int gameObjectId : uniqueGameObjectIds)
			{
				GameObject pickedObject = currentScene.getGameObject(gameObjectId);
				if (pickedObject != null && pickedObject.getComponent(NonPickable.class) == null)
				{
					propertiesWindow.addActiveGameObject(pickedObject);
				}
			}
		}
	}

	private boolean blockInSquare(float x, float y)
	{
		Vector2f start = new Vector2f(x, y);
		Vector2f end = new Vector2f(start).add(new Vector2f(Settings.GRID_WIDTH, Settings.GRID_HEIGHT));
		Vector2f startScreenF = MouseListener.worldToScreen(start);
		Vector2f endScreenF = MouseListener.worldToScreen(end);
		Vector2i startScreen = new Vector2i((int) startScreenF.x + 2, (int) startScreenF.y + 2);
		Vector2i endScreen = new Vector2i((int) endScreenF.x - 2, (int) endScreenF.y - 2);
		float[] gameObjectIds = propertiesWindow.getPickingTexture().readPixels(startScreen, endScreen);
		for (float gameObjectId : gameObjectIds)
		{
			if (gameObjectId >= 0)
			{
				GameObject pickedObject = Window.getCurrentScene().getGameObject((int) gameObjectId);
				if (pickedObject.getComponent(NonPickable.class) == null)
				{
					return true;
				}
			}
		}
		return false;
	}
}
