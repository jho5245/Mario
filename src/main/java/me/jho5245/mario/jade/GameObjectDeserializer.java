package me.jho5245.mario.jade;

import com.google.gson.*;
import me.jho5245.mario.components.Component;

import java.lang.reflect.Type;

public class GameObjectDeserializer implements JsonDeserializer<GameObject>
{
	@Override
	public GameObject deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException
	{
		JsonObject jsonObject = json.getAsJsonObject();
		String name = jsonObject.get("name").getAsString();
		JsonArray components = jsonObject.getAsJsonArray("components");
		Transform transform = context.deserialize(jsonObject.get("transform"), Transform.class);
		int zIndex = context.deserialize(jsonObject.get("zIndex"), Integer.class);

		GameObject gameObject = new GameObject(name, transform, zIndex);
		for (JsonElement component : components)
		{
			Component comp = context.deserialize(component, Component.class);
			gameObject.addComponent(comp);
		}
		return gameObject;
	}
}
