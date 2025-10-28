package me.jho5245.mario.components;

import imgui.ImGui;
import me.jho5245.mario.editor.JImGui;
import me.jho5245.mario.jade.GameObject;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

public abstract class Component
{
	private static int ID_COUNTER = 0;

	private int uid = -1;

	public transient GameObject gameObject = null;

	public void start()
	{

	}

	public void update(float dt)
	{

	}

	public GameObject getGameObject()
	{
		return this.gameObject;
	}

	public void imgui()
	{
		try
		{
			Field[] fields = this.getClass().getDeclaredFields();
			for (Field field : fields)
			{
				boolean isTransient = Modifier.isTransient(field.getModifiers());
				if (isTransient) continue;
				boolean isPrivate = Modifier.isPrivate(field.getModifiers());

				if (isPrivate)
					field.setAccessible(true);

				Class<?> type = field.getType();
				Object value = field.get(this);
				String name = field.getName();

				if (type == int.class)
				{
					int v = (int) value;
					field.set(this, JImGui.dragInt(name, v));
				}
				else if (type == float.class)
				{
					float v = (float) value;
					field.set(this, JImGui.dragFloat(name, v));
				}
				else if (type == boolean.class)
				{
					boolean v = (boolean) value;
					if (ImGui.checkbox(name + ": ", v))
					{
						field.set(this, !v);
					}
				}
				else if (type == Vector2f.class)
				{
					Vector2f v = (Vector2f) value;
					JImGui.drawVec2Control(name, v);
				}
				else if (type == Vector3f.class)
				{
					Vector3f v = (Vector3f) value;
					float[] imVector3f = {v.x, v.y, v.z};
					if (ImGui.dragFloat3(name + ": ", imVector3f))
					{
						v.set(imVector3f[0], imVector3f[1], imVector3f[2]);
					}
				}
				else if (type == Vector4f.class)
				{
					Vector4f v = (Vector4f) value;
					float[] imVector4f = {v.x, v.y, v.z, v.w};
					if (ImGui.dragFloat4(name + ": ", imVector4f))
					{
						v.set(imVector4f[0], imVector4f[1], imVector4f[2], imVector4f[3]);
					}
				}

				if (isPrivate)
					field.setAccessible(false);
			}
		}
		catch (IllegalAccessException e)
		{
			e.printStackTrace();
		}
	}

	public void generateId()
	{
		this.uid = ID_COUNTER++;
	}

	public int getUid()
	{
		return this.uid;
	}

	public static void init(int maxId)
	{
		ID_COUNTER = maxId;
	}
}
