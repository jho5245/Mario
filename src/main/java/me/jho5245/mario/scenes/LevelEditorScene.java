package me.jho5245.mario.scenes;

import imgui.ImGui;
import imgui.ImVec2;
import me.jho5245.mario.jade.Camera;
import me.jho5245.mario.jade.GameObject;
import me.jho5245.mario.jade.Transform;
import me.jho5245.mario.components.RigidBody;
import me.jho5245.mario.components.Sprite;
import me.jho5245.mario.components.SpriteRenderer;
import me.jho5245.mario.components.SpriteSheet;
import me.jho5245.mario.util.AssetPool;
import org.joml.Vector2f;
import org.joml.Vector4f;

public class LevelEditorScene extends Scene
{
	private SpriteSheet spriteSheet;

	public LevelEditorScene()
	{
	}

	@Override
	public void init()
	{
		loadResources();
		this.camera = new Camera(new Vector2f(-250, 0));
		spriteSheet = AssetPool.getSpriteSheet("assets/images/spritesheets/decorationsAndBlocks.png");
		if (levelLoaded)
		{
			this.activeGameObject = gameObjects.getFirst();
			return;
		}

		GameObject obj1 = new GameObject("Object 1", new Transform(new Vector2f(200, 100), new Vector2f(256, 256), new Vector2f(256, 256)), 4);

		SpriteRenderer renderer1 = new SpriteRenderer(spriteSheet.getSprite(5)), renderer2 = new SpriteRenderer();
		Sprite sprite = new Sprite(AssetPool.getTexture("assets/images/red.png"));
		renderer1.setColor(new Vector4f(1, 0, 0, 1));
		renderer2.setColor(new Vector4f(1, 0, 0, 1));
		renderer2.setSprite(sprite);
		obj1.addComponent(renderer1);
		obj1.addComponent(new RigidBody());
		this.addGameObject(obj1);
		GameObject obj2 = new GameObject("Object 1", new Transform(new Vector2f(400, 100), new Vector2f(256, 256), new Vector2f(256, 256)), 2);
		obj2.addComponent(renderer2);
		this.addGameObject(obj2);

	}

	private void loadResources()
	{
		AssetPool.getShader("assets/shaders/default.glsl");
		AssetPool.addSpriteSheet("assets/images/spritesheets/decorationsAndBlocks.png",
				new SpriteSheet(AssetPool.getTexture("assets/images/spritesheets/decorationsAndBlocks.png"), 16, 16, 81, 0));
		AssetPool.getTexture("assets/images/green.png");
	}

	@Override
	public void update(float dt)
	{
		this.gameObjects.forEach(gameObject -> gameObject.update(dt));
		this.renderer.render();
	}

	@Override
	public void imgui()
	{
		ImGui.begin("test window");

		ImVec2 windowPos = new ImVec2();
		ImGui.getWindowPos(windowPos);
		ImVec2 windowSize = new ImVec2();
		ImGui.getWindowSize(windowSize);
		ImVec2 itemSpacing = new ImVec2();
		ImGui.getStyle().getItemSpacing(itemSpacing);

		float windowX2 = windowPos.x + windowSize.x;
		for (int i = 0; i < spriteSheet.size(); i++)
		{
			Sprite sprite = spriteSheet.getSprite(i);
			float spriteWidth = sprite.getWidth() * 4, spriteHeight = sprite.getHeight() * 4;
			int id = sprite.getTexId();
			Vector2f[] texCoords = sprite.getTexCoords();

			ImGui.pushID(i);
			if (ImGui.imageButton(id, spriteWidth, spriteHeight, texCoords[0].x, texCoords[0].y, texCoords[2].x, texCoords[2].y))
			{
				System.out.printf("button %s pressed%n", i);
			}
			ImGui.popID();

			ImVec2 lastButtonPos = new ImVec2();
			ImGui.getItemRectMax(lastButtonPos);
			float lastButtonX2 = lastButtonPos.x;
			float nextButtonX2 = lastButtonX2 + itemSpacing.x + spriteWidth;
			if (i + 1 < spriteSheet.size() && nextButtonX2 < windowX2)
			{
				ImGui.sameLine();
			}
		}

		ImGui.end();
	}
}
