package me.jho5245.mario.jade;

import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector2i;
import org.joml.Vector4f;
import org.lwjgl.system.CallbackI.S;

import java.util.Arrays;

import static org.lwjgl.glfw.GLFW.GLFW_PRESS;
import static org.lwjgl.glfw.GLFW.GLFW_RELEASE;

public class MouseListener
{
	private static MouseListener instance;

	private double scrollX, scrollY;
	private double xPos, yPos, worldX, worldY;
	private final boolean[] mouseButtonPressed = new boolean[3];
	private boolean isDragging;
	private int mouseButtonDown = 0;

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
		if (!Window.getImGuiLayer().getGameViewWindow().getWantCaptureMouse())
		{
			clear();
		}
		if (get().mouseButtonDown > 0)
		{
			get().isDragging = true;
		}
		get().xPos = xPos;
		get().yPos = yPos;
	}

	public static void mouseButtonCallback(long window, int button, int action, int mods)
	{
		if (button < get().mouseButtonPressed.length)
		{
			if (action == GLFW_PRESS)
			{
				get().mouseButtonDown++;
				get().mouseButtonPressed[button] = true;
			}
			else if (action == GLFW_RELEASE)
			{
				get().mouseButtonDown--;
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
		get().scrollX = 0;
		get().scrollY = 0;
	}

	public static void clear()
	{
		get().scrollX = 0;
		get().scrollY = 0;
		get().xPos = 0;
		get().yPos = 0;
		get().mouseButtonDown = 0;
		get().isDragging = false;
		Arrays.fill(get().mouseButtonPressed, false);
	}

	public static float getX()
	{
		return (float) get().xPos;
	}

	public static float getY()
	{
		return (float) get().yPos;
	}

	public static float getScreenX()
	{
		return getScreen().x;
	}

	public static float getScreenY()
	{
		return getScreen().y;
	}

	public static Vector2f screenToWorld(Vector2f screenCoords)
	{
		Vector2f normalizedScreenCoords = new Vector2f(screenCoords.x / Window.getWidth(), screenCoords.y / Window.getHeight());
		normalizedScreenCoords.mul(2f).sub(new Vector2f(1f, 1f));
		Camera camera = Window.getCurrentScene().getCamera();
		Vector4f temp = new Vector4f(normalizedScreenCoords.x, normalizedScreenCoords.y, 0, 1);
		Matrix4f inverseView = new Matrix4f(camera.getInverseView());
		Matrix4f inverseProjection = new Matrix4f(camera.getInverseProjection());
		temp.mul(inverseView.mul(inverseProjection));
		return new Vector2f(temp.x, temp.y);
	}

	public static Vector2f worldToScreen(Vector2f worldCoords)
	{
		Camera camera = Window.getCurrentScene().getCamera();
		Vector4f ndcSpacePos = new Vector4f(worldCoords.x, worldCoords.y, 0, 1);
		Matrix4f viewMatrix = new Matrix4f(camera.getViewMatrix());
		Matrix4f projectionMatrix = new Matrix4f(camera.getProjectionMatrix());
		ndcSpacePos.mul(projectionMatrix.mul(viewMatrix));
		Vector2f windowSpace = new Vector2f(ndcSpacePos.x, ndcSpacePos.y).mul(1f / ndcSpacePos.w);
		windowSpace.add(new Vector2f(1f,1f)).mul(0.5f);
		windowSpace.mul(new Vector2f(Window.getWidth(), Window.getHeight()));
		return windowSpace;
	}

	public static Vector2f getScreen()
	{
		float currentX = getX() - get().gameViewportPos.x;
		currentX = (currentX / get().gameViewportSize.x) * Window.getWidth();
		float currentY = getY() - get().gameViewportPos.y;
		currentY = (1.0f - (currentY / get().gameViewportSize.y)) * Window.getHeight();
		return new Vector2f(currentX, currentY);
	}

	public static float getWorldX()
	{
		return getWorld().x;
	}

	public static float getWorldY()
	{
		return getWorld().y;
	}

	public static Vector2f getWorld()
	{
		float currentX = getX() - get().gameViewportPos.x;
		currentX = (currentX / get().gameViewportSize.x) * 2f - 1f;
		float currentY = getY() - get().gameViewportPos.y;
		// y값 반전 - ImGUI는 y축 증가가 위로 향함
		currentY = -((currentY / get().gameViewportSize.y) * 2f - 1f);

		Vector4f temp = new Vector4f(currentX, currentY, 0, 1);

		Camera camera = Window.getCurrentScene().getCamera();
		Matrix4f inverseView = new Matrix4f(camera.getInverseView());
		Matrix4f inverseProjection = new Matrix4f(camera.getInverseProjection());
		temp.mul(inverseView.mul(inverseProjection));

		return new Vector2f(temp.x, temp.y);
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
