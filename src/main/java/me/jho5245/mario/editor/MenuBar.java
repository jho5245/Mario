package me.jho5245.mario.editor;

import imgui.ImGui;
import me.jho5245.mario.jade.KeyListener;
import me.jho5245.mario.jade.Window;
import me.jho5245.mario.observers.ObserverHandler;
import me.jho5245.mario.observers.events.Event;
import me.jho5245.mario.observers.events.EventType;
import org.lwjgl.glfw.GLFW;

import java.util.Scanner;

import static org.lwjgl.glfw.GLFW.*;

public class MenuBar
{

	public void imgui()
	{
		ImGui.beginMenuBar();

		if (ImGui.beginMenu("File"))
		{
			if (ImGui.menuItem("Save (CTRL + S)"))
			{
				ObserverHandler.notify(null, new Event(EventType.SAVE_LEVEL));
			}
			else if (ImGui.menuItem("Load Level"))
			{
				Scanner scanner = new Scanner(System.in);
				System.out.print("편집할 레벨 이름을 입력해주세요. (공백 입력 시 level): ");
				String levelName = scanner.nextLine();
				if (levelName.isBlank())
				{
					levelName = "level";
				}
				Window.getInstance().setLevelName(levelName + ".json");
				System.out.println(levelName + " 레벨을 편집합니다.");
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