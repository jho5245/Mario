package me.jho5245.mario.components.gizmo;

import me.jho5245.mario.components.Component;
import me.jho5245.mario.components.NonPickable;
import me.jho5245.mario.components.Sprite;
import me.jho5245.mario.components.SpriteRenderer;
import me.jho5245.mario.editor.PropertiesWindow;
import me.jho5245.mario.jade.*;
import me.jho5245.mario.util.Settings;
import org.joml.Vector2f;
import org.joml.Vector4f;

import static org.lwjgl.glfw.GLFW.*;

public class Gizmo extends Component
{
	private Vector4f xAxisColor = new Vector4f(1, 0.5f, 0.5f, 0.8f);
	private Vector4f xAxisColorHover = new Vector4f(1, 0, 0, 1);
	private Vector4f yAxisColor = new Vector4f(0.5f, 1, 0.5f, 0.8f);
	private Vector4f yAxisColorHover = new Vector4f(0, 1, 0, 1);

	private GameObject xAxisObject;
	private GameObject yAxisObject;
	private SpriteRenderer xAxisSprite;
	private SpriteRenderer yAxisSprite;
	protected GameObject activeGameObject;

	private Vector2f xAxisOffset = new Vector2f(24f / 40f, -6f / 20f);
	private Vector2f yAxisOffset = new Vector2f(-7f / 20f, 21f / 40f);

	private float gizmoWidth = 16f / 40f;
	private float gizmoHeight = 48f / 40f;

	protected boolean xAxisActive, yAxisActive;

	private boolean using;

	private PropertiesWindow propertiesWindow;

	public Gizmo(Sprite arrowSprite, PropertiesWindow propertiesWindow)
	{
		this.xAxisObject = Prefabs.generateSpriteObject(arrowSprite, gizmoWidth, gizmoHeight);
		this.yAxisObject = Prefabs.generateSpriteObject(arrowSprite, gizmoWidth, gizmoHeight);
		this.xAxisSprite = this.xAxisObject.getComponent(SpriteRenderer.class);
		this.yAxisSprite = this.yAxisObject.getComponent(SpriteRenderer.class);
		this.propertiesWindow = propertiesWindow;

		this.xAxisObject.addComponent(new NonPickable());
		this.yAxisObject.addComponent(new NonPickable());

		Window.getCurrentScene().addGameObject(this.xAxisObject);
		Window.getCurrentScene().addGameObject(this.yAxisObject);
	}

	@Override
	public void start()
	{
		this.xAxisObject.transform.zIndex = 1000;
		this.yAxisObject.transform.zIndex = 1000;
		this.xAxisObject.getTransform().setRotation(90f);
		this.yAxisObject.getTransform().setRotation(180f);
		this.xAxisObject.setNoSerialize();
		this.yAxisObject.setNoSerialize();
	}

	@Override
	public void update(float dt)
	{
		if (using)
		{
			this.setInactive();
		}
		// 플레이 도중에는 Gizmo 투명화 처리
		xAxisSprite.setColor(new Vector4f(0));
		yAxisSprite.setColor(new Vector4f(0));
	}

	@Override
	public void editorUpdate(float dt)
	{
		if (!using)
			return;
		this.activeGameObject = this.propertiesWindow.getActiveGameObject();
		if (this.activeGameObject != null)
		{
			this.setActive();
			if (KeyListener.isKeyPressed(GLFW_KEY_LEFT_CONTROL) && KeyListener.keyBeginPress(GLFW_KEY_D))
			{
				GameObject newObj = this.activeGameObject.copy();
				Window.getCurrentScene().addGameObject(newObj);
				newObj.transform.position.add(new Vector2f(Settings.GRID_WIDTH, 0f));
				this.propertiesWindow.setActiveGameObject(newObj);
				return;
			}
			else if (KeyListener.keyBeginPress(GLFW_KEY_DELETE))
			{
				activeGameObject.destroy();
				this.setInactive();
				this.propertiesWindow.setActiveGameObject(null);
				return;
			}
		}
		else
		{
			this.setInactive();
			return;
		}

		boolean xAxisHot = checkXHoverState();
		boolean yAxisHot = checkYHoverState();

		if ((xAxisHot || xAxisActive) && MouseListener.isDragging() && MouseListener.mouseButtonDown(GLFW_MOUSE_BUTTON_LEFT))
		{
			xAxisActive = true;
			yAxisActive = false;
		}
		else if ((yAxisHot || yAxisActive) && MouseListener.isDragging() && MouseListener.mouseButtonDown(GLFW_MOUSE_BUTTON_LEFT))
		{
			xAxisActive = false;
			yAxisActive = true;
		}
		else
		{
			xAxisActive = false;
			yAxisActive = false;
		}

		if (this.activeGameObject != null)
		{
			Vector2f gameObjectPosition = this.activeGameObject.getTransform().getPosition();
			this.xAxisObject.getTransform().getPosition().set(new Vector2f(gameObjectPosition.x, gameObjectPosition.y).add(this.xAxisOffset));
			this.yAxisObject.getTransform().getPosition().set(new Vector2f(gameObjectPosition.x, gameObjectPosition.y).add(this.yAxisOffset));
		}
	}

	private void setActive()
	{
		this.xAxisSprite.setColor(xAxisColor);
		this.yAxisSprite.setColor(yAxisColor);
	}

	private void setInactive()
	{
		this.activeGameObject = null;
		this.xAxisSprite.setColor(new Vector4f(0));
		this.yAxisSprite.setColor(new Vector4f(0));
	}

	private boolean checkXHoverState()
	{
		Vector2f mousePos = MouseListener.getWorld();
		if (xAxisActive || mousePos.x <= xAxisObject.transform.position.x + (gizmoHeight / 2.0f) && mousePos.x >= xAxisObject.transform.position.x - (gizmoHeight / 2.0f)
				&& mousePos.y >= xAxisObject.transform.position.y - (gizmoWidth / 2.0f) && mousePos.y <= xAxisObject.transform.position.y + (gizmoWidth / 2.0f))
		{
			xAxisSprite.setColor(xAxisColorHover);
			return true;
		}
		xAxisSprite.setColor(xAxisColor);
		return false;
	}

	private boolean checkYHoverState()
	{
		Vector2f mousePos = MouseListener.getWorld();
		if (yAxisActive || mousePos.x <= yAxisObject.transform.position.x + (gizmoWidth / 2.0f) && mousePos.x >= yAxisObject.transform.position.x - (gizmoWidth / 2.0f)
				&& mousePos.y <= yAxisObject.transform.position.y + (gizmoHeight / 2.0f) && mousePos.y >= yAxisObject.transform.position.y - (gizmoHeight / 2.0f))
		{
			yAxisSprite.setColor(yAxisColorHover);
			return true;
		}
		yAxisSprite.setColor(yAxisColor);
		return false;
	}

	public void setUsing()
	{
		this.using = true;
	}

	public void setNotUsing()
	{
		this.using = false;
		setInactive();
	}
}
