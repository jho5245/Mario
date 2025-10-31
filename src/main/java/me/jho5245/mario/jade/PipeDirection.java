package me.jho5245.mario.jade;

public enum PipeDirection
{
	UP,
	DOWN,
	LEFT,
	RIGHT,
	;

	public PipeDirection inverse()
	{
		return switch (this)
		{
			case UP -> DOWN;
			case DOWN -> UP;
			case LEFT -> RIGHT;
			case RIGHT -> LEFT;
		};
	}
}
