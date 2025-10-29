package me.jho5245.mario.physics2d;

import me.jho5245.mario.components.Component;
import me.jho5245.mario.jade.GameObject;
import org.jbox2d.callbacks.ContactImpulse;
import org.jbox2d.callbacks.ContactListener;
import org.jbox2d.collision.Manifold;
import org.jbox2d.collision.WorldManifold;
import org.jbox2d.dynamics.contacts.Contact;
import org.joml.Vector2f;

public class JadeContactListener implements ContactListener
{

	@Override
	public void beginContact(Contact contact)
	{
		GameObject objA = (GameObject) contact.getFixtureA().getUserData();
		GameObject objB = (GameObject) contact.getFixtureB().getUserData();
		WorldManifold worldManifold = new WorldManifold();
		contact.getWorldManifold(worldManifold);
		Vector2f aNormal = new Vector2f(worldManifold.normal.x,  worldManifold.normal.y);
		Vector2f bNormal = new Vector2f(aNormal).negate();

		for (Component component : objA.getAllComponents())
		{
			component.beginCollision(objB, contact, aNormal);
		}

		for (Component component : objB.getAllComponents())
		{
			component.beginCollision(objA, contact, bNormal);
		}
	}

	@Override
	public void endContact(Contact contact)
	{
		GameObject objA = (GameObject) contact.getFixtureA().getUserData();
		GameObject objB = (GameObject) contact.getFixtureB().getUserData();
		WorldManifold worldManifold = new WorldManifold();
		contact.getWorldManifold(worldManifold);
		Vector2f aNormal = new Vector2f(worldManifold.normal.x,  worldManifold.normal.y);
		Vector2f bNormal = new Vector2f(aNormal).negate();

		for (Component component : objA.getAllComponents())
		{
			component.endCollision(objB, contact, aNormal);
		}

		for (Component component : objB.getAllComponents())
		{
			component.endCollision(objA, contact, bNormal);
		}
	}

	@Override
	public void preSolve(Contact contact, Manifold oldManifold)
	{
		GameObject objA = (GameObject) contact.getFixtureA().getUserData();
		GameObject objB = (GameObject) contact.getFixtureB().getUserData();
		WorldManifold worldManifold = new WorldManifold();
		contact.getWorldManifold(worldManifold);
		Vector2f aNormal = new Vector2f(worldManifold.normal.x,  worldManifold.normal.y);
		Vector2f bNormal = new Vector2f(aNormal).negate();

		for (Component component : objA.getAllComponents())
		{
			component.preSolve(objB, contact, aNormal);
		}

		for (Component component : objB.getAllComponents())
		{
			component.preSolve(objA, contact, bNormal);
		}
	}

	@Override
	public void postSolve(Contact contact, ContactImpulse impulse)
	{
		GameObject objA = (GameObject) contact.getFixtureA().getUserData();
		GameObject objB = (GameObject) contact.getFixtureB().getUserData();
		WorldManifold worldManifold = new WorldManifold();
		contact.getWorldManifold(worldManifold);
		Vector2f aNormal = new Vector2f(worldManifold.normal.x,  worldManifold.normal.y);
		Vector2f bNormal = new Vector2f(aNormal).negate();

		for (Component component : objA.getAllComponents())
		{
			component.postSolve(objB, contact, aNormal);
		}

		for (Component component : objB.getAllComponents())
		{
			component.postSolve(objA, contact, bNormal);
		}
	}
}
