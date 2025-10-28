package me.jho5245.mario.jade;

import com.google.gson.*;
import me.jho5245.mario.components.Component;
import me.jho5245.mario.components.Transform;

import java.lang.reflect.Type;

public class GameObjectDeserializer implements JsonDeserializer<GameObject>
{
	@Override
	public GameObject deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException
	{
		JsonObject jsonObject = json.getAsJsonObject();
		String name = jsonObject.get("name").getAsString();
		JsonArray components = jsonObject.getAsJsonArray("components");

		GameObject gameObject = new GameObject(name);
		for (JsonElement component : components)
		{
			Component comp = context.deserialize(component, Component.class);
			gameObject.addComponent(comp);
		}
		gameObject.transform = gameObject.getComponent(Transform.class);
		return gameObject;
	}
}
