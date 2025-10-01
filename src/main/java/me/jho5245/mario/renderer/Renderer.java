package me.jho5245.mario.renderer;

import me.jho5245.mario.jade.GameObject;
import me.jho5245.mario.jade.components.SpriteRenderer;

import java.util.ArrayList;
import java.util.List;

public class Renderer
{
	private final int MAX_BATCH_SIZE = 1000;

	private List<RenderBatch> batches;

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
			if (batch.hasRoom())
			{
				batch.addSprite(sprite);
				added = true;
			}
		}

		if (!added)
		{
			RenderBatch newBatch = new RenderBatch(MAX_BATCH_SIZE);
			newBatch.start();
			batches.add(newBatch);
			newBatch.addSprite(sprite);
		}
	}

	public void render()
	{
		batches.forEach(RenderBatch::render);
	}
}
