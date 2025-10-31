package me.jho5245.mario.components;

import imgui.ImGui;
import imgui.type.ImInt;
import me.jho5245.mario.editor.JImGui;
import me.jho5245.mario.jade.GameObject;
import org.jbox2d.dynamics.contacts.Contact;
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

	public void editorUpdate(float dt)
	{

	}

	public void update(float dt)
	{

	}

	public void render()
	{
	}

	public GameObject getGameObject()
	{
		return this.gameObject;
	}

	public void beginCollision(GameObject collidingObject, Contact contact, Vector2f hitNormal)
	{

	}

	public void endCollision(GameObject collidingObject, Contact contact, Vector2f hitNormal)
	{

	}

	public void preSolve(GameObject collidingObject, Contact contact, Vector2f hitNormal)
	{

	}

	public void postSolve(GameObject collidingObject, Contact contact, Vector2f hitNormal)
	{

	}

	public void imgui()
	{
		try
		{
			Field[] fields = this.getClass().getDeclaredFields();
			for (Field field : fields)
			{
				boolean isTransient = Modifier.isTransient(field.getModifiers());
				if (isTransient)
					continue;
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
					float[] imVector3f = {
							v.x,
							v.y,
							v.z
					};
					if (ImGui.dragFloat3(name + ": ", imVector3f))
					{
						v.set(imVector3f[0], imVector3f[1], imVector3f[2]);
					}
				}
				else if (type == Vector4f.class)
				{
					Vector4f v = (Vector4f) value;
					float[] imVector4f = {
							v.x,
							v.y,
							v.z,
							v.w
					};
					if (ImGui.dragFloat4(name + ": ", imVector4f))
					{
						v.set(imVector4f[0], imVector4f[1], imVector4f[2], imVector4f[3]);
					}
				}
				else if (type.isEnum())
				{
					String[] enumValues = getEnumValues(type);
					String enumType = ((Enum<?>) value).name();
					ImInt index = new ImInt(indexOf(enumType, enumValues));
					if (ImGui.combo(field.getName(), index, enumValues, enumValues.length))
					{
						field.set(this, type.getEnumConstants()[index.get()]);
					}
				}
				else if (type == String.class)
				{
					field.set(this, JImGui.inputText(field.getName() + ":", (String) value));
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

	@SuppressWarnings("unchecked")
	private <T extends Enum<T>> String[] getEnumValues(Class<?> enumType)
	{
		String[] enumValues = new String[enumType.getEnumConstants().length];
		int i = 0;
		for (T c : (T[]) enumType.getEnumConstants())
		{
			enumValues[i] = c.name();
			i++;
		}
		return enumValues;
	}

	public void destroy()
	{

	}

	private int indexOf(String obj, String[] arr)
	{
		for (int i = 0; i < arr.length; i++)
		{
			if (obj.equals(arr[i]))
			{
				return i;
			}
		}

		return -1;
	}

	public static void init(int maxId)
	{
		ID_COUNTER = maxId;
	}
}
