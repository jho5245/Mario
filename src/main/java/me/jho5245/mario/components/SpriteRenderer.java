package me.jho5245.mario.components;

import imgui.ImGui;
import me.jho5245.mario.jade.Transform;
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
		float[] imColor = {color.x, color.y, color.z, color.w};
		if (ImGui.colorPicker4("Color Picker: ", imColor))
		{
			setColor(new Vector4f(imColor[0], imColor[1], imColor[2], imColor[3]));
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
