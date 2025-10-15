package me.jho5245.mario.jade;

import me.jho5245.mario.jade.components.SpriteRenderer;
import me.jho5245.mario.util.AssetPool;
import org.joml.Vector2f;
import org.joml.Vector4f;

public class LevelEditorScene extends Scene
{
	public LevelEditorScene()
	{
	}

	@Override
	public void init()
	{
		this.camera = new Camera(new Vector2f(-250, -100));

		GameObject obj1 = new GameObject("Object 1", new Transform(new Vector2f(100, 100), new Vector2f(256, 256), new Vector2f(256, 256)));
		obj1.addComponent(new SpriteRenderer(AssetPool.getTexture("assets/images/test.png")));
		this.addGameObject(obj1);
		GameObject obj2 = new GameObject("Object 1", new Transform(new Vector2f(400, 100), new Vector2f(256, 256), new Vector2f(256, 256)));
		obj2.addComponent(new SpriteRenderer(AssetPool.getTexture("assets/images/test2.png")));
		this.addGameObject(obj2);

		loadResources();
	}

	private void loadResources()
	{
		AssetPool.getShader("assets/shaders/default.glsl");
	}

	@Override
	public void update(float dt)
	{
		this.gameObjects.forEach(gameObject -> gameObject.update(dt));
		this.renderer.render();
		this.camera.position.x = (float) Math.sin(System.currentTimeMillis() / 200d) * 100f - 200f;
		this.camera.position.y = (float) Math.cos(System.currentTimeMillis() / 200d) * 100f - 200f;
	}
}
