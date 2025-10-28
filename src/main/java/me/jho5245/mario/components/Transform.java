package me.jho5245.mario.components;

import me.jho5245.mario.editor.JImGui;
import me.jho5245.mario.util.Settings;
import org.joml.Vector2f;

public class Transform extends Component
{
	public Vector2f position;
	public Vector2f scale;
	public float rotation;
	public int zIndex;

	public Transform()
	{
		this(new Vector2f());
	}

	public Transform(Vector2f position)
	{
		this(position, 0f);
	}

	public Transform(Vector2f position, float rotation)
	{
		this(position, new Vector2f(1, 1), rotation);
	}

	public Transform(Vector2f position, Vector2f scale, float rotation)
	{
		init(position, scale, rotation);
	}

	public void init(Vector2f position, Vector2f scale, float rotation)
	{
		this.position = position;
		this.rotation = rotation;
		this.scale = scale;
	}

	@Override
	public void imgui()
	{
		gameObject.name = JImGui.inputText("Name:", gameObject.name);
		JImGui.drawVec2Control("Position", position);
		JImGui.drawVec2Control("Scale", scale, Settings.GRID_HEIGHT);
		this.rotation = JImGui.dragFloat("Rotation", rotation);
		this.zIndex = JImGui.dragInt("zIndex", zIndex);
	}

	public Vector2f getPosition()
	{
		return this.position;
	}

	public float getRotation()
	{
		return this.rotation;
	}

	public void setRotation(float rotation)
	{
		this.rotation = rotation;
	}

	public Vector2f getScale()
	{
		return this.scale;
	}


	public Transform copy()
	{
		return new Transform(new Vector2f(this.position), new Vector2f(this.scale), this.rotation);
	}

	public void copy(Transform to)
	{
		to.position.set(this.position);
		to.scale.set(this.scale);
		to.rotation = this.rotation;
	}

	@Override
	public boolean equals(Object o)
	{
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		Transform transform = (Transform) o;
		if (!position.equals(transform.position))
			return false;
		if (rotation != transform.rotation)
			return false;
		if (zIndex != transform.zIndex)
			return false;
		return scale.equals(transform.scale);
	}
}
