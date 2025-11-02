package me.jho5245.mario.jade;

import java.util.Arrays;

import static org.lwjgl.glfw.GLFW.*;

public class KeyListener
{
	private static KeyListener instance;

	private final boolean[] keyPressed = new boolean[GLFW_KEY_LAST + 1];
	private final boolean[] keyBeginPress = new boolean[GLFW_KEY_LAST + 1];

	private KeyListener()
	{

	}

	public static void endFrame()
	{
		Arrays.fill(get().keyBeginPress, false);
	}

	public static KeyListener get()
	{
		if (instance == null)
			instance = new KeyListener();
		return instance;
	}

	public static void keyCallback(long window, int keyCode, int scanCode, int action, int mods)
	{
		if (keyCode < 0 || keyCode > GLFW_KEY_LAST)
			return;
		if (action == GLFW_PRESS)
		{
			get().keyBeginPress[keyCode] = true;
			get().keyPressed[keyCode] = true;
		}
		else if (action == GLFW_RELEASE)
		{
			get().keyBeginPress[keyCode] = false;
			get().keyPressed[keyCode] = false;
		}
	}

	public static boolean isKeyPressed(int keyCode)
	{
		if (keyCode < 0 || keyCode > GLFW_KEY_LAST)
			return false;
		if (keyCode < get().keyPressed.length)
			return get().keyPressed[keyCode];
		return false;
	}

	public static boolean keyBeginPress(int keyCode)
	{
		if (keyCode < 0 || keyCode > GLFW_KEY_LAST)
			return false;
		return get().keyBeginPress[keyCode];
	}
}
