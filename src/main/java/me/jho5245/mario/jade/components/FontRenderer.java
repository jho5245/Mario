package me.jho5245.mario.jade.components;

import me.jho5245.mario.jade.Component;
public class FontRenderer extends Component
{
	public FontRenderer()
	{

	}

	@Override
	public void start()
	{
		if (gameObject.getComponent(SpriteRenderer.class) != null)
		{
			System.out.println("SpriteRenderer already started");
		}
	}

	@Override
	public void update(float dt)
	{

	}
}
