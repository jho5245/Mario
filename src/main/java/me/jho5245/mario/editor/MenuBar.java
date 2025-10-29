package me.jho5245.mario.editor;

import imgui.ImGui;
import me.jho5245.mario.jade.KeyListener;
import me.jho5245.mario.observers.ObserverHandler;
import me.jho5245.mario.observers.events.Event;
import me.jho5245.mario.observers.events.EventType;
import org.lwjgl.glfw.GLFW;

import static org.lwjgl.glfw.GLFW.*;

public class MenuBar
{

	public void imgui()
	{
		ImGui.beginMenuBar();

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

		if (KeyListener.keyBeginPress(GLFW_KEY_F3))
		{
			ObserverHandler.notify(null, new Event(EventType.TOGGLE_PHYSICS_DEBUG_DRAW));
		}

		if (KeyListener.keyBeginPress(GLFW_KEY_S) && KeyListener.isKeyPressed(GLFW_KEY_LEFT_CONTROL))
		{
			ObserverHandler.notify(null, new Event(EventType.SAVE_LEVEL));
		}

		ImGui.endMenuBar();
	}
}