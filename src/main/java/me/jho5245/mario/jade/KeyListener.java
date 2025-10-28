package me.jho5245.mario.jade;

import static org.lwjgl.glfw.GLFW.GLFW_PRESS;
import static org.lwjgl.glfw.GLFW.GLFW_RELEASE;

public class KeyListener
{
	private static KeyListener instance;

	private final boolean[] keyPressed = new boolean[350];
	private final boolean[] keyBeginPress = new boolean[350];

	private KeyListener()
	{

	}

	public static KeyListener get()
	{
		if (instance == null)
			instance = new KeyListener();
		return instance;
	}

	public static void keyCallback(long window, int key, int scanCode, int action, int mods)
	{
		if (action == GLFW_PRESS)
		{
			get().keyBeginPress[key] = true;
			get().keyPressed[key] = true;
		}
		else if (action == GLFW_RELEASE)
		{
			get().keyBeginPress[key] = false;
			get().keyPressed[key] = false;
		}
	}

	public static boolean isKeyPressed(int keyCode)
	{
		if (keyCode < get().keyPressed.length)
			return get().keyPressed[keyCode];
		return false;
	}

	public static boolean keyBeginPress(int keyCode)
	{
		boolean result = get().keyBeginPress[keyCode];
		if (result)
		{
			get().keyBeginPress[keyCode] = false;
		}
		return result;
	}
}
