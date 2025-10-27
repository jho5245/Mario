package me.jho5245.mario.jade;

import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector4f;

import static org.lwjgl.glfw.GLFW.GLFW_PRESS;
import static org.lwjgl.glfw.GLFW.GLFW_RELEASE;

public class MouseListener
{
	private static MouseListener instance;

	private double scrollX, scrollY;

	private double xPos, yPos, lastX, lastY;

	private final boolean[] mouseButtonPressed = new boolean[3];

	private boolean isDragging;

	private Vector2f gameViewportPos = new Vector2f(), gameViewportSize = new Vector2f();

	private MouseListener()
	{

	}

	public static MouseListener get()
	{
		if (instance == null)
			instance = new MouseListener();
		return instance;
	}

	public static void mousePosCallback(long window, double xPos, double yPos)
	{
		get().lastX = get().xPos;
		get().lastY = get().yPos;
		get().xPos = xPos;
		get().yPos = yPos;
		get().isDragging = get().mouseButtonPressed[0] || get().mouseButtonPressed[1] || get().mouseButtonPressed[2];
	}

	public static void mouseButtonCallback(long window, int button, int action, int mods)
	{
		if (button < get().mouseButtonPressed.length)
		{
			if (action == GLFW_PRESS)
			{
				get().mouseButtonPressed[button] = true;
			}
			else if (action == GLFW_RELEASE)
			{
				get().mouseButtonPressed[button] = false;
				get().isDragging = false;
			}
		}
	}

	public static void mouseScrollCallback(long window, double xOffset, double yOffset)
	{
		get().scrollX = xOffset;
		get().scrollY = yOffset;
	}

	public static void endFrame()
	{
		get().scrollY = 0;
		get().scrollY = 0;
		get().lastX = get().xPos;
		get().lastY = get().yPos;
	}

	public static float getX()
	{
		return (float) get().xPos;
	}

	public static float getY()
	{
		return (float) get().yPos;
	}

	public static float getOrthoX()
	{
		float currentX = getX() - get().gameViewportPos.x;
		currentX = (currentX / get().gameViewportSize.x) * 2f - 1f;
		Vector4f temp = new Vector4f(currentX, 0, 0, 1);
		Camera camera = Window.getCurrentScene().getCamera();
		Matrix4f viewProjection = new Matrix4f();
		camera.getInverseView().mul(camera.getInverseProjection(), viewProjection);
		temp.mul(viewProjection);
		currentX = temp.x;
		return currentX;
	}

	public static float getOrthoY()
	{
		float currentY = getY() - get().gameViewportPos.y;
		// y값 반전 - ImGUI는 y축 증가가 위로 향함
		currentY = -((currentY / get().gameViewportSize.y) * 2f - 1f);
		Vector4f temp = new Vector4f(0, currentY, 0, 1);
		Camera camera = Window.getCurrentScene().getCamera();
		Matrix4f viewProjection = new Matrix4f();
		camera.getInverseView().mul(camera.getInverseProjection(), viewProjection);
		temp.mul(viewProjection);
		currentY = temp.y;
		return currentY;
	}

	public static float getDx()
	{
		return (float) (get().lastX - get().xPos);
	}

	public static float getDy()
	{
		return (float) (get().lastY - get().yPos);
	}

	public static float getScrollX()
	{
		return (float) get().scrollX;
	}

	public static float getScrollY()
	{
		return (float) get().scrollY;
	}

	public static boolean isDragging()
	{
		return get().isDragging;
	}

	public static boolean mouseButtonDown(int button)
	{
		if (button < get().mouseButtonPressed.length)
			return get().mouseButtonPressed[button];
		return false;
	}

	public Vector2f getGameViewportPos()
	{
		return gameViewportPos;
	}

	public static void setGameViewportPos(Vector2f gameViewportPos)
	{
		get().gameViewportPos.set(gameViewportPos);
	}

	public Vector2f getGameViewportSize()
	{
		return gameViewportSize;
	}

	public static void setGameViewportSize(Vector2f gameViewportSize)
	{
		get().gameViewportSize.set(gameViewportSize);
	}
}
