package me.jho5245.mario.animations;

import me.jho5245.mario.components.Sprite;
import me.jho5245.mario.util.AssetPool;

import java.util.ArrayList;
import java.util.List;

public class AnimationState
{
	public String title;
	public List<Frame> animationFrames = new ArrayList<>();

	private static final Sprite defaultSprite = new Sprite();
	private transient float timeTracker;
	private transient int currentSprite;
	private transient float speed = 1f;

	public boolean doesLoop;

	public AnimationState()
	{

	}

	public void refreshTextures()
	{
		for (Frame frame : animationFrames)
		{
			frame.sprite.setTexture(AssetPool.getTexture(frame.sprite.getTexture().getFilePath()));
		}
	}

	public void addFrame(Sprite sprite, float frameTime)
	{
		animationFrames.add(new Frame(sprite, frameTime));
	}

	public void update(float dt)
	{
		if (currentSprite < animationFrames.size())
		{
			timeTracker -= dt * speed;
			if (timeTracker <= 0)
			{
				if (currentSprite != animationFrames.size() - 1 || doesLoop)
				{
					currentSprite = (currentSprite + 1) % animationFrames.size();
				}
				timeTracker = animationFrames.get(currentSprite).frameTime;
			}
		}
	}

	public Sprite getCurrentSprite()
	{
		if (currentSprite < animationFrames.size())
		{
			return animationFrames.get(currentSprite).sprite;
		}
		return defaultSprite;
	}

	public boolean doesLoop()
	{
		return doesLoop;
	}

	public void setDoesLoop(boolean doesLoop)
	{
		this.doesLoop = doesLoop;
	}

	public float getSpeed()
	{
		return speed;
	}

	public void setSpeed(float speed)
	{
		this.speed = speed;
	}
}
