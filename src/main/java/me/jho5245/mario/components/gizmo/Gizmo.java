package me.jho5245.mario.components.gizmo;

import me.jho5245.mario.components.Component;
import me.jho5245.mario.components.NonPickable;
import me.jho5245.mario.components.Sprite;
import me.jho5245.mario.components.SpriteRenderer;
import me.jho5245.mario.editor.PropertiesWindow;
import me.jho5245.mario.jade.GameObject;
import me.jho5245.mario.jade.MouseListener;
import me.jho5245.mario.jade.Prefabs;
import me.jho5245.mario.jade.Window;
import org.joml.Vector2f;
import org.joml.Vector4f;

import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_LEFT;

public class Gizmo extends Component
{
	private Vector4f xAxisColor = new Vector4f(1, 0.3f, 0.3f, 1);
	private Vector4f xAxisColorHover = new Vector4f(1, 0, 0, 1);
	private Vector4f yAxisColor = new Vector4f(0.3f, 1, 0.3f, 1);
	private Vector4f yAxisColorHover = new Vector4f(0, 1, 0, 1);

	private GameObject xAxisObject;
	private GameObject yAxisObject;
	private SpriteRenderer xAxisSprite;
	private SpriteRenderer yAxisSprite;
	protected GameObject activeGameObject;

	private Vector2f xAxisOffset;
	private Vector2f yAxisOffset;

	private int gizmoWidth = 16;
	private int gizmoHeight = 48;

	protected boolean xAxisActive, yAxisActive;

	private boolean using;

	private PropertiesWindow propertiesWindow;

	public Gizmo(Sprite arrowSprite, PropertiesWindow propertiesWindow)
	{
		this.xAxisObject = Prefabs.generateSpriteObject(arrowSprite, 16, 48);
		this.yAxisObject = Prefabs.generateSpriteObject(arrowSprite, 16, 48);
		this.xAxisSprite = this.xAxisObject.getComponent(SpriteRenderer.class);
		this.yAxisSprite = this.yAxisObject.getComponent(SpriteRenderer.class);
		this.xAxisOffset = new Vector2f(64, -5);
		this.yAxisOffset = new Vector2f(10, 64);
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
		if (!using)
			return;
		this.activeGameObject = this.propertiesWindow.getActiveGameObject();
		if (this.activeGameObject != null)
		{
			this.setActive();
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
		Vector2f mousePos = new Vector2f(MouseListener.getOrthoX(), MouseListener.getOrthoY());
		if (mousePos.x <= xAxisObject.getTransform().getPosition().x && mousePos.x >= xAxisObject.getTransform().getPosition().x - gizmoHeight && mousePos.y >= xAxisObject.getTransform().getPosition().y && mousePos.y <= xAxisObject.getTransform().getPosition().y + gizmoWidth)
		{
			xAxisSprite.setColor(xAxisColorHover);
			return true;
		}
		xAxisSprite.setColor(xAxisColor);
		return false;
	}

	private boolean checkYHoverState()
	{
		Vector2f mousePos = new Vector2f(MouseListener.getOrthoX(), MouseListener.getOrthoY());
		if (mousePos.x <= yAxisObject.getTransform().getPosition().x && mousePos.x >= yAxisObject.getTransform().getPosition().x - gizmoWidth && mousePos.y <= yAxisObject.getTransform().getPosition().y && mousePos.y >= yAxisObject.getTransform().getPosition().y - gizmoHeight)
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
