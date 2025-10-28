package me.jho5245.mario.components;

import imgui.ImGui;
import me.jho5245.mario.editor.JImGui;
import me.jho5245.mario.renderer.Texture;
import org.joml.Vector2f;
import org.joml.Vector4f;

public class SpriteRenderer extends Component
{
	private Vector4f color;
	private Sprite sprite;

	private transient Transform lastTransform;
	private transient boolean isDirty;

	public SpriteRenderer()
	{
		this(new Vector4f());
	}

	public SpriteRenderer(Vector4f color)
	{
		this.color = color;
		this.sprite = new Sprite(null);
		this.isDirty = true;
	}

	public SpriteRenderer(Sprite sprite)
	{
		this.color = new Vector4f(1, 1, 1, 1);
		this.sprite = sprite;
		this.isDirty = true;
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

	@Override
	public void imgui()
	{
		if (JImGui.colorPicker4("Color Picker", this.color))
		{
			this.isDirty = true;
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

	public void setTexture(Texture texture)
	{
		this.sprite.setTexture(texture);
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
