package me.jho5245.mario.animations;

import me.jho5245.mario.components.Sprite;

public class Frame
{
	public Sprite sprite;
	public float frameTime;

	public Frame()
	{

	}

	public Frame(Sprite sprite, float frameTime)
	{
		this.sprite = sprite;
		this.frameTime = frameTime;
	}
}
