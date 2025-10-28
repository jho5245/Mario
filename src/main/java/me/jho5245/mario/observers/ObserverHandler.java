package me.jho5245.mario.observers;

import me.jho5245.mario.jade.GameObject;
import me.jho5245.mario.observers.events.Event;

import java.util.ArrayList;
import java.util.List;

public class ObserverHandler
{
	private static final List<Observer> observers = new ArrayList<>();

	public static void addObserver(Observer observer)
	{
		observers.add(observer);
	}

	public static void removeObserver(Observer observer)
	{
		observers.remove(observer);
	}

	public static void notify(GameObject obj, Event event)
	{
		for (Observer observer : observers)
		{
			observer.onNotify(obj, event);
		}
	}
}
