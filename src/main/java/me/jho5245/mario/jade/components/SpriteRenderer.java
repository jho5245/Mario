package me.jho5245.mario.jade.components;

import me.jho5245.mario.jade.Component;

public class SpriteRenderer extends Component
{
	@Override
	public void start()
	{
		System.out.println("SpriteRenderer start");
	}

	@Override
	public void update(float dt)
	{
		System.out.println("SpriteRenderer update");
	}
}
