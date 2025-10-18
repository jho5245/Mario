package me.jho5245.mario.jade.components;

import org.joml.Vector3f;
import org.joml.Vector4f;

public class RigidBody extends Component
{
	private int colliderType;
	private float friction;
	public Vector3f velocity = new Vector3f(0f, 0.5f, 0f);
	public transient Vector4f temp = new Vector4f();
}
