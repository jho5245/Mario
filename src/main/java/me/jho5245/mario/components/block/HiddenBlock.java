package me.jho5245.mario.components.block;

import me.jho5245.mario.components.Component;
import me.jho5245.mario.components.SpriteRenderer;
import org.joml.Vector4f;

public class HiddenBlock extends Component
{
	@Override
	public void editorUpdate(float dt)
	{
		this.gameObject.getComponent(SpriteRenderer.class).setColor(new Vector4f(0.5f, 0.5f, 0.5f, 0.5f));
	}

	@Override
	public void update(float dt)
	{
		if (this.gameObject.getComponent(QuestionBlock.class) != null && !this.gameObject.getComponent(QuestionBlock.class).isActive())
		{
			this.gameObject.getComponent(SpriteRenderer.class).setColor(new Vector4f(1f));
		}
		else
		{
			this.gameObject.getComponent(SpriteRenderer.class).setColor(new Vector4f(0f));
		}
	}
}
