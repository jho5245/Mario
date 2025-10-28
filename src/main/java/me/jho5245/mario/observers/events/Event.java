package me.jho5245.mario.observers.events;

public class Event
{
	public EventType type;

	public Event()
	{
		type = EventType.USER_EVENT;
	}

	public Event(EventType type)
	{
		this.type = type;
	}
}