package me.jho5245.mario.editor;

import imgui.ImGui;
import imgui.ImVec2;
import imgui.flag.ImGuiWindowFlags;
import me.jho5245.mario.jade.KeyListener;
import me.jho5245.mario.jade.MouseListener;
import me.jho5245.mario.jade.Window;
import me.jho5245.mario.observers.ObserverHandler;
import me.jho5245.mario.observers.events.Event;
import me.jho5245.mario.observers.events.EventType;
import org.joml.Vector2f;
import org.lwjgl.glfw.GLFW;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_F5;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_SPACE;

public class GameViewWindow
{
	private float leftX, rightX, topY, bottomY;
	private boolean isPlaying;

	public void imgui()
	{
		ImGui.begin("Game Viewport", ImGuiWindowFlags.NoScrollbar | ImGuiWindowFlags.NoScrollWithMouse | ImGuiWindowFlags.MenuBar);

		ImGui.beginMenuBar();
		if (!isPlaying && ImGui.menuItem("Play (F5)", "", false, true))
		{
			isPlaying = true;
			ObserverHandler.notify(null, new Event(EventType.GAME_ENGINE_START_PLAY));
		}
		else if (isPlaying && ImGui.menuItem("Stop (F5)", "", false, true))
		{
			isPlaying = false;
			ObserverHandler.notify(null, new Event(EventType.GAME_ENGINE_STOP_PLAY));
		}
		ImGui.endMenuBar();

		if (KeyListener.keyBeginPress(GLFW_KEY_F5))
		{
			ObserverHandler.notify(null, new Event(isPlaying ? EventType.GAME_ENGINE_STOP_PLAY : EventType.GAME_ENGINE_START_PLAY));
			isPlaying = !isPlaying;
		}

		ImVec2 windowSize = getLargestSizeForViewport();
		ImVec2 windowPos = getCenteredPositionForVieport(windowSize);

		ImGui.setCursorPos(windowPos.x, windowPos.y);
		ImVec2 topLeft = new ImVec2();
		ImGui.getCursorScreenPos(topLeft);
		topLeft.x -= ImGui.getScrollX();
		topLeft.y -= ImGui.getScrollY();
		leftX = topLeft.x;
		bottomY = topLeft.y;
		rightX = topLeft.x + windowSize.x;
		topY = topLeft.y + windowSize.y;

		int textureId = Window.getFrameBuffer().getTextureId();
		ImGui.image(textureId, windowSize.x, windowSize.y, 0, 1, 1, 0);

		// 마우스 위치를 게임 뷰포트 화면에 맞게 조정
		MouseListener.setGameViewportPos(new Vector2f(topLeft.x, topLeft.y));
		MouseListener.setGameViewportSize(new Vector2f(windowSize.x, windowSize.y));

		ImGui.end();
	}

	private ImVec2 getLargestSizeForViewport()
	{
		ImVec2 windowSize = new ImVec2();
		ImGui.getContentRegionAvail(windowSize);
		windowSize.x -= ImGui.getScrollX();
		windowSize.y -= ImGui.getScrollY();

		float aspectWidth = windowSize.x;
		float aspectHeight = aspectWidth / Window.getTargetAspectRatio();
		if (aspectHeight > windowSize.y)
		{
			// pillarbox mode
			aspectHeight = windowSize.y;
			aspectWidth = aspectHeight * Window.getTargetAspectRatio();
		}

		return new ImVec2(aspectWidth, aspectHeight);
	}

	private ImVec2 getCenteredPositionForVieport(ImVec2 aspectSize)
	{
		ImVec2 windowSize = new ImVec2();
		ImGui.getContentRegionAvail(windowSize);
		windowSize.x -= ImGui.getScrollX();
		windowSize.y -= ImGui.getScrollY();

		float viewportX = (windowSize.x / 2f) - (aspectSize.x / 2f);
		float viewportY = (windowSize.y / 2f) - (aspectSize.y / 2f);

		return new ImVec2(viewportX + ImGui.getCursorPosX(), viewportY + ImGui.getCursorPosY());
	}

	public boolean getWantCaptureMouse()
	{
		return MouseListener.getX() >= leftX && MouseListener.getX() <= rightX && MouseListener.getY() <= topY && MouseListener.getY() >= bottomY;
	}
}
