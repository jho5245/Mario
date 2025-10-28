package me.jho5245.mario.observers;

import me.jho5245.mario.jade.GameObject;
import me.jho5245.mario.observers.events.Event;

public interface Observer {
	void onNotify(GameObject object, Event event);
}