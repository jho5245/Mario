package me.jho5245.mario.physics2d.components;

import me.jho5245.mario.components.Component;
import me.jho5245.mario.editor.JImGui;
import org.joml.Vector2f;
import me.jho5245.mario.renderer.DebugDraw;

public class Box2DCollider extends Component
{
	private Vector2f halfSize = new Vector2f(1);
	private Vector2f origin = new Vector2f();
	protected Vector2f offset = new Vector2f();

	public Vector2f getHalfSize()
	{
		return halfSize;
	}

	public void setHalfSize(Vector2f halfSize)
	{
		this.halfSize.set(halfSize);
	}

	public Vector2f getOrigin()
	{
		return this.origin;
	}

	@Override
	public void editorUpdate(float dt)
	{
		Vector2f center = new Vector2f(this.gameObject.transform.position).add(offset);
		DebugDraw.addBox(center, this.halfSize, this.gameObject.transform.rotation);
	}

	@Override
	public void imgui()
	{
		JImGui.drawVec2Control("Half Size", halfSize);
		JImGui.drawVec2Control("Origin", origin);
		JImGui.drawVec2Control("Offset", offset);
	}

	public Vector2f getOffset()
	{
		return this.offset;
	}

	public void setOffset(Vector2f offset)
	{
		this.offset.set(offset);
	}
}
