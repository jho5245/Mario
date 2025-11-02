package me.jho5245.mario;

import me.jho5245.mario.jade.Window;

import java.util.Scanner;

public class Main
{
	public static void main(String[] args)
	{
		Window window = args.length == 1 ? Window.getInstance(Boolean.parseBoolean(args[0])) : Window.getInstance();
		if (!Window.PLAY_MODE)
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
		}
		window.run();
	}
}
