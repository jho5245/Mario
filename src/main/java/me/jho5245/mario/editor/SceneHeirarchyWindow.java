package me.jho5245.mario.editor;

import imgui.ImGui;
import imgui.flag.ImGuiTreeNodeFlags;
import me.jho5245.mario.jade.GameObject;
import me.jho5245.mario.jade.Window;

import java.util.List;

public class SceneHeirarchyWindow
{
	public void imgui()
	{
		ImGui.begin("Scene Heirarchy");
		List<GameObject> gameObjects = Window.getCurrentScene().getGameObjects();
		int index = 0;
		for (GameObject gameObject : gameObjects)
		{
			if (!gameObject.doSerialization())
				continue;

			ImGui.pushID(index);
			boolean treeNodeOpen = ImGui.treeNodeEx(gameObject.name,
					ImGuiTreeNodeFlags.DefaultOpen | ImGuiTreeNodeFlags.FramePadding | ImGuiTreeNodeFlags.OpenOnArrow | ImGuiTreeNodeFlags.SpanAvailWidth,
					gameObject.name);
			ImGui.popID();

			if (treeNodeOpen)
			{
				ImGui.treePop();
			}
			index++;
		}
		ImGui.end();
	}
}
