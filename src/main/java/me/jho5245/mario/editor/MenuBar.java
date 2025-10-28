package me.jho5245.mario.editor;

import imgui.ImGui;
import me.jho5245.mario.observers.ObserverHandler;
import me.jho5245.mario.observers.events.Event;
import me.jho5245.mario.observers.events.EventType;

public class MenuBar
{

	public void imgui()
	{
		ImGui.beginMainMenuBar();

		if (ImGui.beginMenu("File"))
		{
			if (ImGui.menuItem("Save"))
			{
				ObserverHandler.notify(null, new Event(EventType.SAVE_LEVEL));
			}
			else if (ImGui.menuItem("Load Level"))
			{
				ObserverHandler.notify(null, new Event(EventType.LOAD_LEVEL));
			}

			ImGui.endMenu();
		}

		if (ImGui.beginMenu("Settings"))
		{
			if (ImGui.menuItem("Toggle Physics Debug Draw"))
			{
				ObserverHandler.notify(null, new Event(EventType.TOGGLE_PHYSICS_DEBUG_DRAW));
			}
			ImGui.endMenu();
		}

		ImGui.endMainMenuBar();
	}
}