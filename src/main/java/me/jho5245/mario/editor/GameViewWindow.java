package me.jho5245.mario.editor;

import imgui.ImGui;
import imgui.ImVec2;
import imgui.flag.ImGuiWindowFlags;
import me.jho5245.mario.jade.Window;

public class GameViewWindow
{
	public static void imgui()
	{
		ImGui.begin("Game Viewport", ImGuiWindowFlags.NoScrollbar | ImGuiWindowFlags.NoScrollWithMouse);

		ImVec2 windowSize = getLargestSizeForViewport();
		ImVec2 windowPos = getCenteredPositionForVieport(windowSize);

		ImGui.setCursorPos(windowPos.x, windowPos.y);
		int textureId = Window.getFrameBuffer().getTextureId();
		ImGui.image(textureId, windowSize.x, windowSize.y, 0, 1, 1, 0);

		ImGui.end();
	}

	private static ImVec2 getLargestSizeForViewport()
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

	private static ImVec2 getCenteredPositionForVieport(ImVec2 aspectSize)
	{
		ImVec2 windowSize = new ImVec2();
		ImGui.getContentRegionAvail(windowSize);
		windowSize.x -= ImGui.getScrollX();
		windowSize.y -= ImGui.getScrollY();

		float viewportX = (windowSize.x / 2f) - (aspectSize.x / 2f);
		float viewportY = (windowSize.y / 2f) - (aspectSize.y / 2f);

		return new ImVec2(viewportX + ImGui.getCursorPosX(), viewportY + ImGui.getCursorPosY());
	}
}
