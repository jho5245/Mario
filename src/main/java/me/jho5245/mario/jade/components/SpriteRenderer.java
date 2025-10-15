package me.jho5245.mario.jade.components;

import me.jho5245.mario.jade.Component;
import me.jho5245.mario.jade.Transform;
import me.jho5245.mario.renderer.Texture;
import org.joml.Vector2f;
import org.joml.Vector4f;

public class SpriteRenderer extends Component
{
	private Vector4f color;
	private Sprite sprite;

	private Transform lastTransform;
	private boolean isDirty = false;

	public SpriteRenderer()
	{
		this(new Vector4f());
	}

	public SpriteRenderer(Vector4f color)
	{
		this.color = color;
		this.sprite = new Sprite(null);
	}

	public SpriteRenderer(Sprite sprite)
	{
		this.color = new Vector4f(1, 1, 1, 1);
		this.sprite = sprite;
	}

	@Override
	public void start()
	{
		this.lastTransform = gameObject.getTransform().copy();
	}

	@Override
	public void update(float dt)
	{
		if (!this.lastTransform.equals(this.gameObject.getTransform()))
		{
			this.getGameObject().getTransform().copy(this.lastTransform);
			isDirty = true;
		}
	}

	public Vector4f getColor()
	{
		return color;
	}

	public void setColor(Vector4f color)
	{
		if (!this.color.equals(color))
		{
			this.color = color;
			this.isDirty = true;
		}
	}

	public Texture getTexture()
	{
		return sprite.getTexture();
	}

	public Vector2f[] getTexCoords()
	{
		return sprite.getTexCoords();
	}

	public Sprite getSprite()
	{
		return sprite;
	}

	public void setSprite(Sprite sprite)
	{
		this.sprite = sprite;
		this.isDirty = true;
	}

	public boolean isDirty()
	{
		return isDirty;
	}

	public void setClean()
	{
		this.isDirty = false;
	}
}
