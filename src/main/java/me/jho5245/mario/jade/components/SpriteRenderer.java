package me.jho5245.mario.jade.components;

import me.jho5245.mario.jade.Component;
import me.jho5245.mario.renderer.Texture;
import org.joml.Vector2f;
import org.joml.Vector4f;

public class SpriteRenderer extends Component
{
	private Vector4f color;
	private Vector2f[] texCoords;
	private Texture texture;

	public SpriteRenderer()
	{
		this(new Vector4f());
	}

	public SpriteRenderer(Vector4f color)
	{
		this.color = color;
		this.texture = null;;
	}

	public SpriteRenderer(Texture texture)
	{
		this.color = new Vector4f(1, 1, 1, 1);
		this.texture = texture;
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
		return texture;
	}

	public Vector2f[] getTexCoords()
	{
		return new Vector2f[] {
				new Vector2f(1, 1),
				new Vector2f(1, 0),
				new Vector2f(0, 0),
				new Vector2f(0, 1),
		};
	}
}
