package me.jho5245.mario.jade;

import me.jho5245.mario.components.Sprite;
import me.jho5245.mario.components.SpriteRenderer;
import org.joml.Vector2f;

public class Prefabs
{
	public static GameObject generateSpriteObject(Sprite sprite, float sizeX, float sizeY)
	{
		GameObject gameObject = new GameObject("Sprite_Object_Gen",
				new Transform(
						new Vector2f(),
						new Vector2f(),
						new Vector2f(sizeX, sizeY)
				), 0);
		SpriteRenderer renderer = new SpriteRenderer(sprite);
		gameObject.addComponent(renderer);
		return gameObject;
	}
}
