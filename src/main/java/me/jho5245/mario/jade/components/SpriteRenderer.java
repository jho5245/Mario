package me.jho5245.mario.jade.components;

import me.jho5245.mario.jade.Component;
import me.jho5245.mario.renderer.Texture;
import org.joml.Vector2f;
import org.joml.Vector4f;

public class SpriteRenderer extends Component
{
	private Vector4f color;
	private Sprite sprite;

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

	}

	@Override
	public void update(float dt)
	{

	}

	public Vector4f getColor()
	{
		return color;
	}

	public Texture getTexture()
	{
		return sprite.getTexture();
	}

	public Vector2f[] getTexCoords()
	{
		return sprite.getTexCoords();
	}
}
