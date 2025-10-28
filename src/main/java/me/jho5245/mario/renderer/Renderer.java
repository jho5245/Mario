package me.jho5245.mario.renderer;

import me.jho5245.mario.jade.GameObject;
import me.jho5245.mario.components.SpriteRenderer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Renderer
{
	private final int MAX_BATCH_SIZE = 1000;

	private List<RenderBatch> batches;

	private static Shader currentShader;

	public Renderer()
	{
		this.batches = new ArrayList<>();
	}

	public void add(GameObject gameObject)
	{
		SpriteRenderer sprite = gameObject.getComponent(SpriteRenderer.class);
		if (sprite != null)
		{
			add(sprite);
		}
	}

	public void add(SpriteRenderer sprite)
	{
		boolean added = false;
		for (RenderBatch batch : batches)
		{
			if (batch.hasRoom() && batch.zIndex() == sprite.gameObject.transform.zIndex)
			{
				Texture texture = sprite.getTexture();
				// 텍스처가 없거나 || batch에 텍스처가 있거나 || 새로 추가할 공간이 있을 경우
				if (texture == null || batch.hasTexture(texture) || batch.hasTextureRoom())
				{
					batch.addSprite(sprite);
					added = true;
					break;
				}
			}
		}

		if (!added)
		{
			RenderBatch newBatch = new RenderBatch(MAX_BATCH_SIZE, sprite.gameObject.transform.zIndex);
			newBatch.start();
			batches.add(newBatch);
			newBatch.addSprite(sprite);
			Collections.sort(batches);
		}
	}

	public static void bindShader(Shader shader)
	{
		currentShader = shader;
	}

	public static Shader getBoundSHader()
	{
		return currentShader;
	}

	public void render()
	{
		currentShader.use();
		batches.forEach(RenderBatch::render);
	}
}
