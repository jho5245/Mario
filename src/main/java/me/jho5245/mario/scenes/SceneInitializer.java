package me.jho5245.mario.scenes;

import me.jho5245.mario.animations.StateMachine;
import me.jho5245.mario.components.SpriteRenderer;
import me.jho5245.mario.jade.GameObject;
import me.jho5245.mario.util.AssetPool;

public abstract class SceneInitializer
{
	public abstract void init(Scene scene);

	public void loadResources(Scene scene)
	{
		for (GameObject g : scene.getGameObjects())
		{
			if (g.getComponent(SpriteRenderer.class) != null)
			{
				SpriteRenderer spr = g.getComponent(SpriteRenderer.class);
				if (spr.getTexture() != null)
				{
					spr.setTexture(AssetPool.getTexture(spr.getTexture().getFilePath()));
				}
			}

			if (g.getComponent(StateMachine.class) != null)
			{
				StateMachine stateMachine = g.getComponent(StateMachine.class);
				stateMachine.refreshTextures();
			}
		}
	}

	public abstract void imgui();
}