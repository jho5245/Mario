package me.jho5245.mario.jade.components;

import me.jho5245.mario.renderer.Texture;
import org.joml.Vector2f;

public class Sprite
{
	private float width, height;

	private Texture texture;

	private Vector2f[] texCoords;

	public Sprite(Texture texture)
	{
		this.texture = texture;
		this.texCoords = new Vector2f[] {
				new Vector2f(1, 1),
				new Vector2f(1, 0),
				new Vector2f(0, 0),
				new Vector2f(0, 1),
				};
	}

	public Sprite(Texture texture, Vector2f[] texCoords)
	{
		this.texture = texture;
		this.texCoords = texCoords;
	}

	public float getHeight()
	{
		return height;
	}

	public void setHeight(float height)
	{
		this.height = height;
	}

	public float getWidth()
	{
		return width;
	}

	public void setWidth(float width)
	{
		this.width = width;
	}

	public Texture getTexture()
	{
		return texture;
	}

	public void setTexture(Texture texture)
	{
		this.texture = texture;
	}

	public Vector2f[] getTexCoords()
	{
		return texCoords;
	}

	public void setTexCoords(Vector2f[] texCoords)
	{
		this.texCoords = texCoords;
	}

	public int getTexId()
	{
		return texture == null ? -1 : texture.getId();
	}
}
