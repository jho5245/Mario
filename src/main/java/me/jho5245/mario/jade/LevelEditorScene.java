package me.jho5245.mario.jade;

import me.jho5245.mario.jade.components.SpriteRenderer;
import org.joml.Vector2f;
import org.joml.Vector4f;

public class LevelEditorScene extends Scene
{
	public LevelEditorScene()
	{
	}

	@Override
	public void init()
	{
		this.camera = new Camera(new Vector2f(0, 0));

		int xOffset = 10;
		int yOffset = 10;

		float totalWidth = (float)(600 - xOffset * 2);
		float totalHeight = (float)(300 - yOffset * 2);
		float sizeX = totalWidth / 100.0f;
		float sizeY = totalHeight / 100.0f;
		float padding = 0;

		for (int x = 0; x < 100; x++)
		{
			for (int y = 0; y < 100; y++)
			{
				float xPos = xOffset + (x * sizeX) + (padding * x);
				float yPos = yOffset + (y * sizeY) + (padding * y);

				GameObject gameObject = new GameObject(
						"obj%s:%s".formatted(x, y),
						new Transform(new Vector2f(xPos, yPos),
						new Vector2f(),
						new Vector2f(sizeX, sizeY)));
				gameObject.addComponent(new SpriteRenderer(new Vector4f(xPos / totalWidth, yPos / totalHeight, 1, 1)));
				this.addGameObject(gameObject);
			}
		}
	}

	@Override
	public void update(float dt)
	{
		this.gameObjects.forEach(gameObject -> gameObject.update(dt));
		this.renderer.render();
	}
}
