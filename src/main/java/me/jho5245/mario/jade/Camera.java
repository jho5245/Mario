package me.jho5245.mario.jade;

import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;

public class Camera
{
	private Matrix4f projectionMatrix, viewMatrix, inverseProjection, inverseView;

	protected Vector2f position;

	private Vector2f projectionSize;

	private float zoom = 1f;

	public Camera(Vector2f position)
	{
		this.projectionMatrix = new Matrix4f();
		this.viewMatrix = new Matrix4f();
		this.inverseProjection = new Matrix4f();
		this.inverseView = new Matrix4f();
		this.position = position;
		this.projectionSize = new Vector2f(32f * 40f, 32f * 21f);
		adjustProjection();
	}

	public void adjustProjection()
	{
		projectionMatrix.identity();
		projectionMatrix.ortho(0f, projectionSize.x * this.zoom, 0f, projectionSize.y * this.zoom, 0f, 100f);
		projectionMatrix.invert(inverseProjection);
	}

	public Matrix4f getViewMatrix()
	{
		Vector3f cameraFront = new Vector3f(0f, 0f, -1f);
		Vector3f cameraUp = new Vector3f(0f, 1f, 0f);
		this.viewMatrix.identity();
		this.viewMatrix = viewMatrix.lookAt(new Vector3f(position.x, position.y, 20f), cameraFront.add(position.x, position.y, 0f), cameraUp);
		this.viewMatrix.invert(inverseView);
		return this.viewMatrix;
	}

	public Matrix4f getProjectionMatrix()
	{
		return this.projectionMatrix;
	}

	public Matrix4f getInverseProjection()
	{
		return this.inverseProjection;
	}

	public Matrix4f getInverseView()
	{
		return this.inverseView;
	}

	public Vector2f getPosition()
	{
		return this.position;
	}

	public Vector2f getProjectionSize()
	{
		return this.projectionSize;
	}

	public float getZoom()
	{
		return zoom;
	}

	public void setZoom(float zoom)
	{
		this.zoom = Math.min(4f, Math.max(zoom, 0.2f));
	}

	public void addZoom(float zoom)
	{
		setZoom(getZoom() + zoom);
	}
}
