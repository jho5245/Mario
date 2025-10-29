package me.jho5245.mario.components.gizmo;

import me.jho5245.mario.components.Sprite;
import me.jho5245.mario.editor.PropertiesWindow;
import me.jho5245.mario.jade.KeyListener;
import me.jho5245.mario.jade.MouseListener;
import me.jho5245.mario.util.Settings;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_LEFT_ALT;

public class TranslateGizmo extends Gizmo
{
	public TranslateGizmo(Sprite arrowSprite, PropertiesWindow propertiesWindow)
	{
		super(arrowSprite, propertiesWindow);
	}

	@Override
	public void editorUpdate(float dt)
	{

		if (activeGameObject != null)
		{
			if (xAxisActive && !yAxisActive)
			{
				float x;
				if (KeyListener.isKeyPressed(GLFW_KEY_LEFT_ALT))
				{
					x = MouseListener.getWorldX();
				}
				else
				{
					x = (int) (MouseListener.getWorldX() / Settings.GRID_WIDTH) * Settings.GRID_WIDTH;
					if ((int) activeGameObject.transform.scale.x % 2 == 0)
					{
						x -= Settings.GRID_WIDTH / 2;
					}
				}
				activeGameObject.getTransform().getPosition().x = x;
			}
			else if (yAxisActive && !xAxisActive)
			{
				float y;
				if (KeyListener.isKeyPressed(GLFW_KEY_LEFT_ALT))
				{
					y = MouseListener.getWorldY();
				}
				else
				{
					y = (int) (MouseListener.getWorldY() / Settings.GRID_HEIGHT) * Settings.GRID_HEIGHT;
					if ((int) activeGameObject.transform.scale.y % 2 == 0)
					{
						y -= Settings.GRID_WIDTH / 2;
					}
				}
				activeGameObject.getTransform().getPosition().y = y;
			}
		}

		super.editorUpdate(dt);
	}
}
