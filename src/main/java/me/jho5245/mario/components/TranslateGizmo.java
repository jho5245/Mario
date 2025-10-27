package me.jho5245.mario.components;

import me.jho5245.mario.editor.PropertiesWindow;
import me.jho5245.mario.jade.GameObject;
import me.jho5245.mario.jade.Prefabs;
import me.jho5245.mario.jade.Window;
import org.joml.Vector2f;
import org.joml.Vector4f;

public class TranslateGizmo extends Component
{
	private Vector4f xAxisColor = new Vector4f(1, 0, 0, 1);
	private Vector4f xAxisColorHover = new Vector4f();
	private Vector4f yAxisColor = new Vector4f(0, 1, 0, 1);
	private Vector4f yAxisColorHover = new Vector4f();

	private GameObject xAxisObject;
	private GameObject yAxisObject;
	private SpriteRenderer xAxisSprite;
	private SpriteRenderer yAxisSprite;
	private GameObject activeGameObject;

	private Vector2f xAxisOffset;
	private Vector2f yAxisOffset;

	private PropertiesWindow propertiesWindow;

	public TranslateGizmo(Sprite arrowSprite, PropertiesWindow propertiesWindow)
	{
		this.xAxisObject = Prefabs.generateSpriteObject(arrowSprite, 16, 48);
		this.yAxisObject = Prefabs.generateSpriteObject(arrowSprite, 16, 48);
		this.xAxisSprite = this.xAxisObject.getComponent(SpriteRenderer.class);
		this.yAxisSprite = this.yAxisObject.getComponent(SpriteRenderer.class);
		this.xAxisOffset = new Vector2f(64, -5);
		this.yAxisOffset = new Vector2f(10, 64);
		this.propertiesWindow = propertiesWindow;

		Window.getCurrentScene().addGameObject(this.xAxisObject);
		Window.getCurrentScene().addGameObject(this.yAxisObject);
	}

	@Override
	public void start()
	{
		this.xAxisObject.getTransform().setRotation(90f);
		this.yAxisObject.getTransform().setRotation(180f);
		this.xAxisObject.setNoSerialize();
		this.yAxisObject.setNoSerialize();
	}

	@Override
	public void update(float dt)
	{
		if (this.activeGameObject != null)
		{
			Vector2f gameObjectPosition = this.activeGameObject.getTransform().getPosition();
			this.xAxisObject.getTransform().getPosition().set(new Vector2f(gameObjectPosition.x, gameObjectPosition.y).add(this.xAxisOffset));
			this.yAxisObject.getTransform().getPosition().set(new Vector2f(gameObjectPosition.x, gameObjectPosition.y).add(this.yAxisOffset));
		}

		this.activeGameObject = this.propertiesWindow.getActiveGameObject();
		if (this.activeGameObject != null)
		{
			this.setActive();
		}
		else
		{
			this.setInactive();
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
}
