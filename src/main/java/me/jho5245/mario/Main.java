package me.jho5245.mario;

import me.jho5245.mario.jade.Window;

public class Main
{
	public static void main(String[] args)
	{
		Window window = args.length == 1 ? Window.getInstance(Boolean.parseBoolean(args[0])) : Window.getInstance();
		window.run();
	}
}
