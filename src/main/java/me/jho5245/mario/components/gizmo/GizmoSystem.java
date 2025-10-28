package me.jho5245.mario.components.gizmo;

import me.jho5245.mario.components.Component;
import me.jho5245.mario.components.SpriteSheet;
import me.jho5245.mario.jade.KeyListener;
import me.jho5245.mario.jade.Window;
import org.lwjgl.glfw.GLFW;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_E;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_R;

public class GizmoSystem extends Component
{
	private SpriteSheet gizmos;
	private int usingGizmo;

	public GizmoSystem(SpriteSheet gizmos)
	{
		this.gizmos = gizmos;
	}

	@Override
	public void start()
	{
		gameObject.addComponent(new TranslateGizmo(gizmos.getSprite(1), Window.getImGuiLayer().getPropertiesWindow()));
		gameObject.addComponent(new ScaleGizmo(gizmos.getSprite(2), Window.getImGuiLayer().getPropertiesWindow()));
	}

	@Override
	public void update(float dt)
	{
		if (usingGizmo == 0)
		{
			gameObject.getComponent(TranslateGizmo.class).setUsing();
			gameObject.getComponent(ScaleGizmo.class).setNotUsing();
		}
		else if (usingGizmo == 1)
		{
			gameObject.getComponent(TranslateGizmo.class).setNotUsing();
			gameObject.getComponent(ScaleGizmo.class).setUsing();
		}

		if (KeyListener.isKeyPressed(GLFW_KEY_E))
		{
			usingGizmo = 0;
		}
		else if (KeyListener.isKeyPressed(GLFW_KEY_R))
		{
			usingGizmo = 1;
		}
	}
}
