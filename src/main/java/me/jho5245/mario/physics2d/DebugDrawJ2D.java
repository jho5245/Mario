package me.jho5245.mario.physics2d;

import org.jbox2d.callbacks.DebugDraw;
import org.jbox2d.common.*;
import org.jbox2d.particle.ParticleColor;
import org.jbox2d.pooling.arrays.Vec2Array;
import org.joml.Vector2f;
import org.joml.Vector3f;

public class DebugDrawJ2D extends DebugDraw
{
	public static int circlePoints = 13;
	private Vec2Array vec2Array = new Vec2Array();

	public DebugDrawJ2D()
	{
		super(new OBBViewportTransform());
		viewportTransform.setYFlip(true);
		setFlags(
				DebugDraw.e_aabbBit | DebugDraw.e_centerOfMassBit | DebugDraw.e_dynamicTreeBit | DebugDraw.e_jointBit | DebugDraw.e_pairBit | DebugDraw.e_shapeBit);
	}

	@Override
	public void drawCircle(Vec2 center, float radius, Color3f color)
	{
		Vec2[] vecs = vec2Array.get(circlePoints);
		generateCirle(center, radius, vecs, circlePoints);
		drawPolygon(vecs, circlePoints, color);
	}

	@Override
	public void drawPoint(Vec2 argPoint, float argRadiusOnScreen, Color3f argColor)
	{
		me.jho5245.mario.renderer.DebugDraw.addCircle(new Vector2f(argPoint.x, argPoint.y), argRadiusOnScreen, new Vector3f(argColor.x, argColor.y, argColor.z), 1);
	}

	@Override
	public void drawSegment(Vec2 p1, Vec2 p2, Color3f color)
	{
		me.jho5245.mario.renderer.DebugDraw.addLine2D(new Vector2f(p1.x, p1.y), new Vector2f(p2.x, p2.y), new Vector3f(color.x, color.y, color.z));
	}

	@Override
	public void drawSolidCircle(Vec2 center, float radius, Vec2 axis, Color3f color)
	{
		Vec2[] vecs = vec2Array.get(circlePoints);
		generateCirle(center, radius, vecs, circlePoints);
		drawSolidPolygon(vecs, circlePoints, color);
		if (axis != null)
		{
			drawSegment(center, axis, color);
		}
	}

	@Override
	public void drawSolidPolygon(Vec2[] vertices, int vertexCount, Color3f color)
	{
		for (int i = 0; i < vertices.length; i++)
		{
			float x1 = vertices[i].x;
			float y1 = vertices[i].y;
			float x2 = vertices[(i + 1) % vertices.length].x;
			float y2 = vertices[(i + 1) % vertices.length].y;
			drawSegment(new Vec2(x1, y1), new Vec2(x2, y2), color);
		}

		// outside
		drawPolygon(vertices, vertexCount, color);
	}

	@Override
	public void drawString(float x, float y, String s, Color3f color)
	{

	}

	@Override
	public void drawParticles(Vec2[] centers, float radius, ParticleColor[] colors, int count)
	{

	}

	@Override
	public void drawParticlesWireframe(Vec2[] centers, float radius, ParticleColor[] colors, int count)
	{

	}

	@Override
	public void drawTransform(Transform xf)
	{

	}

	private void generateCirle(Vec2 argCenter, float argRadius, Vec2[] argPoints, int argNumPoints)
	{
		float inc = MathUtils.TWOPI / argNumPoints;

		for (int i = 0; i < argNumPoints; i++)
		{
			argPoints[i].x = (argCenter.x + MathUtils.cos(i * inc) * argRadius);
			argPoints[i].y = (argCenter.y + MathUtils.sin(i * inc) * argRadius);
		}
	}
}