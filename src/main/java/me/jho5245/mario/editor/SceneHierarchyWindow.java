package me.jho5245.mario.editor;

import imgui.ImGui;
import imgui.flag.ImGuiTreeNodeFlags;
import me.jho5245.mario.jade.GameObject;
import me.jho5245.mario.jade.Window;

import java.util.List;

public class SceneHierarchyWindow
{
	private static String payloadDragDropType = "SceneHierarchy";

	public void imgui()
	{
		ImGui.begin("Scene Hierarchy");
		List<GameObject> gameObjects = Window.getCurrentScene().getGameObjects();
		int index = 0;
		for (GameObject gameObject : gameObjects)
		{
			if (!gameObject.doSerialization())
				continue;

			boolean treeNodeOpen = doTreeNode(gameObject, index);

			if (treeNodeOpen)
			{
				ImGui.treePop();
			}
			index++;
		}
		ImGui.end();
	}

	private boolean doTreeNode(GameObject gameObject, int index)
	{
		ImGui.pushID(index);
		boolean treeNodeOpen = ImGui.treeNodeEx(gameObject.name,
				ImGuiTreeNodeFlags.DefaultOpen | ImGuiTreeNodeFlags.FramePadding | ImGuiTreeNodeFlags.OpenOnArrow | ImGuiTreeNodeFlags.SpanAvailWidth,
				gameObject.name);
		ImGui.popID();

//		if (ImGui.beginDragDropSource())
//		{
//			ImGui.setDragDropPayloadObject(payloadDragDropType, gameObject);
//			ImGui.text("Move " + gameObject.name + " to..");
////			ImGui.button("Cancel");
//			ImGui.endDragDropSource();
//		}

		if (ImGui.beginDragDropTarget())
		{
			Object payloadObject = ImGui.acceptDragDropPayloadObject(payloadDragDropType);
			if (payloadObject != null)
			{
				if (payloadObject instanceof GameObject object)
				{

				}
			}
			ImGui.endDragDropTarget();
		}

		return treeNodeOpen;
	}
}
