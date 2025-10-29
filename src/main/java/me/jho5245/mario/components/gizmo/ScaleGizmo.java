package me.jho5245.mario.components.gizmo;

import me.jho5245.mario.components.Sprite;
import me.jho5245.mario.editor.PropertiesWindow;
import me.jho5245.mario.jade.KeyListener;
import me.jho5245.mario.jade.MouseListener;
import me.jho5245.mario.util.Settings;

import static org.lwjgl.glfw.GLFW.*;

public class ScaleGizmo extends Gizmo
{
	public ScaleGizmo(Sprite scaleSprite, PropertiesWindow propertiesWindow)
	{
		super(scaleSprite, propertiesWindow);
	}

	@Override
	public void editorUpdate(float dt)
	{
		if (activeGameObject != null)
		{
			if (xAxisActive && !yAxisActive)
			{
				if (KeyListener.isKeyPressed(GLFW_KEY_LEFT_ALT))
				{
					activeGameObject.getTransform().getScale().x = MouseListener.getWorldX() - activeGameObject.getTransform().getPosition().x;
					if (activeGameObject.getTransform().getScale().x < 0)
						activeGameObject.getTransform().getScale().x = 0;
				}
				else
				{
					activeGameObject.getTransform().getScale().x = (int) Math.max(1, ((MouseListener.getWorldX() - activeGameObject.getTransform().getPosition().x) / Settings.GRID_WIDTH)) * Settings.GRID_WIDTH;
				}
			}
			else if (yAxisActive && !xAxisActive)
			{
				if (KeyListener.isKeyPressed(GLFW_KEY_LEFT_ALT))
				{
					activeGameObject.getTransform().getScale().y = MouseListener.getWorldY() - activeGameObject.getTransform().getPosition().y;
					if (activeGameObject.getTransform().getScale().y < 0)
						activeGameObject.getTransform().getScale().y = 0;
				}
				else
				{
					activeGameObject.getTransform().getScale().y = (int) Math.max(1, ((MouseListener.getWorldY() - activeGameObject.getTransform().getPosition().y) / Settings.GRID_HEIGHT)) * Settings.GRID_HEIGHT;
				}
			}
		}

		super.editorUpdate(dt);
	}
}
