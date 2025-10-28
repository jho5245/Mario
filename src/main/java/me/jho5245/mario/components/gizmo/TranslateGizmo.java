package me.jho5245.mario.components.gizmo;

import me.jho5245.mario.components.Sprite;
import me.jho5245.mario.editor.PropertiesWindow;
import me.jho5245.mario.jade.KeyListener;
import me.jho5245.mario.jade.MouseListener;
import me.jho5245.mario.util.Settings;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_LEFT_ALT;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_LEFT_CONTROL;

public class TranslateGizmo extends Gizmo
{
	public TranslateGizmo(Sprite arrowSprite, PropertiesWindow propertiesWindow)
	{
		super(arrowSprite, propertiesWindow);
	}

	@Override
	public void update(float dt)
	{

		if (activeGameObject != null)
		{
			if (xAxisActive && !yAxisActive)
			{
				if (KeyListener.isKeyPressed(GLFW_KEY_LEFT_ALT))
				{
					activeGameObject.getTransform().getPosition().x -= MouseListener.getWorldDx();
				}
				else
				{
					activeGameObject.getTransform().getPosition().x = (int) (MouseListener.getOrthoX() / Settings.GRID_WIDTH) * Settings.GRID_WIDTH;
				}
			}
			else if (yAxisActive && !xAxisActive)
			{
				if (KeyListener.isKeyPressed(GLFW_KEY_LEFT_ALT))
				{
					activeGameObject.getTransform().getPosition().y -= MouseListener.getWorldDy();
				}
				else
				{
					activeGameObject.getTransform().getPosition().y = (int) (MouseListener.getOrthoY() / Settings.GRID_HEIGHT) * Settings.GRID_HEIGHT;
				}
			}
		}

		super.update(dt);
	}
}
