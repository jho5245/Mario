package me.jho5245.mario.jade.components;

import me.jho5245.mario.renderer.Texture;
import org.joml.Vector2f;

import java.util.ArrayList;
import java.util.List;

public class SpriteSheet
{
	private Texture texture;
	private List<Sprite> sprites;

	public SpriteSheet(Texture texture, int spriteWidth, int spriteHeight, int numSprites, int spacing)
	{
		this.texture = texture;
		this.sprites = new ArrayList<>();

		// Lecture14 - SpriteSheets의 `SpriteSheet Texture Image with coords - Mario Image Pos` 이미지 참조
		// Lecture14 - SpriteSheets의 `LeftX, BottomY, RightX, TopYs` 이미지 참조
		// (0, 16)
		int currentX = 0;
		int currentY = texture.getHeight() - spriteHeight;
		for (int i = 0; i < numSprites; i++)
		{
			// normalized coord
			float leftX = currentX / (float) texture.getWidth();
			float bottomY = currentY / (float) texture.getHeight();
			float topY = (currentY + spriteHeight) / (float) texture.getHeight();
			float rightX = (currentX + spriteWidth) / (float) texture.getWidth();

			Vector2f[] texCoords = {
					new Vector2f(rightX, topY), 	// 1, 1
					new Vector2f(rightX, bottomY),// 1, 0
					new Vector2f(leftX, bottomY),	// 0, 0
					new Vector2f(leftX, topY),		// 0, 1
			};

			Sprite sprite = new Sprite();
			sprite.setTexture(this.texture);
			sprite.setTexCoords(texCoords);
			this.sprites.add(sprite);

			currentX += spriteWidth + spacing;
			if (currentX >= texture.getWidth()) // 한 줄의 끝, 다음 줄로 이동
			{
				currentX = 0;
				currentY -= spriteHeight + spacing;
			}
		}
	}

	public Sprite getSprite(int index)
	{
		return this.sprites.get(index);
	}
}
