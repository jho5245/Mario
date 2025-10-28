package me.jho5245.mario.jade;

import me.jho5245.mario.components.Sprite;
import me.jho5245.mario.components.SpriteRenderer;

public class Prefabs
{
	public static GameObject generateSpriteObject(Sprite sprite, float sizeX, float sizeY)
	{
		GameObject gameObject = Window.getCurrentScene().createGameObject("Sprite_Object_Gen");
		gameObject.transform.scale.x = sizeX;
		gameObject.transform.scale.y = sizeY;
		SpriteRenderer renderer = new SpriteRenderer(sprite);
		gameObject.addComponent(renderer);
		return gameObject;
	}
}
