package me.jho5245.mario.jade;

import me.jho5245.mario.jade.components.Sprite;
import me.jho5245.mario.jade.components.SpriteRenderer;
import me.jho5245.mario.jade.components.SpriteSheet;
import me.jho5245.mario.util.AssetPool;
import org.joml.Vector2f;

public class LevelEditorScene extends Scene
{
	private GameObject obj1;

	private SpriteSheet spriteSheet;

	public LevelEditorScene()
	{
	}

	@Override
	public void init()
	{
		loadResources();

		this.camera = new Camera(new Vector2f(-250, -100));

		spriteSheet = AssetPool.getSpriteSheet("assets/images/spritesheet.png");

		obj1 = new GameObject("Object 1", new Transform(new Vector2f(200, 100), new Vector2f(256, 256), new Vector2f(256, 256)), 4);
		obj1.addComponent(new SpriteRenderer(spriteSheet.getSprite(0)));
		this.addGameObject(obj1);
		GameObject obj2 = new GameObject("Object 1", new Transform(new Vector2f(400, 100), new Vector2f(256, 256), new Vector2f(256, 256)), 2);
		obj2.addComponent(new SpriteRenderer(spriteSheet.getSprite(15)));
		this.addGameObject(obj2);
	}

	private void loadResources()
	{
		AssetPool.getShader("assets/shaders/default.glsl");
		AssetPool.addSpriteSheet("assets/images/spritesheet.png",
				new SpriteSheet(AssetPool.getTexture("assets/images/spritesheet.png"), 16, 16, 26, 0));
	}

	@Override
	public void update(float dt)
	{
		this.gameObjects.forEach(gameObject -> gameObject.update(dt));
		this.renderer.render();
	}
}
