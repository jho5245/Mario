package me.jho5245.mario.components;

import me.jho5245.mario.editor.PropertiesWindow;
import me.jho5245.mario.jade.GameObject;
import me.jho5245.mario.jade.KeyListener;
import me.jho5245.mario.jade.Window;
import me.jho5245.mario.util.Settings;
import org.joml.Vector2f;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.glfw.GLFW.*;

public class KeyControls extends Component
{
	@Override
	public void editorUpdate(float dt)
	{
		PropertiesWindow propertiesWindow = Window.getImGuiLayer().getPropertiesWindow();
		GameObject activeGameObject = propertiesWindow.getActiveGameObject();
		List<GameObject> activeGameObjects = propertiesWindow.getActiveGameObjects();
		if (KeyListener.isKeyPressed(GLFW_KEY_LEFT_CONTROL) && KeyListener.keyBeginPress(GLFW_KEY_D) && activeGameObject != null)
		{
			GameObject newObj = activeGameObject.copy();
			Window.getCurrentScene().addGameObject(newObj);
			newObj.transform.position.add(new Vector2f(Settings.GRID_WIDTH, 0f));
			propertiesWindow.setActiveGameObject(newObj);
		}
		else if (KeyListener.isKeyPressed(GLFW_KEY_LEFT_CONTROL) && KeyListener.keyBeginPress(GLFW_KEY_D) && activeGameObjects.size() > 1)
		{
			List<GameObject> gameObjects = new ArrayList<>(activeGameObjects);
			propertiesWindow.clearSelected();
			for (GameObject gameObject : gameObjects)
			{
				GameObject copy =  gameObject.copy();
				Window.getCurrentScene().addGameObject(copy);
				propertiesWindow.addActiveGameObject(copy);
			}
		}
		else if (KeyListener.keyBeginPress(GLFW_KEY_DELETE))
		{
			for (GameObject gameObject : activeGameObjects)
			{
				gameObject.destroy();
			}
			propertiesWindow.clearSelected();
		}
	}
}
