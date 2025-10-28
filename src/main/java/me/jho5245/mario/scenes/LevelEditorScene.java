package me.jho5245.mario.scenes;

import imgui.ImGui;
import imgui.ImVec2;
import me.jho5245.mario.components.*;
import me.jho5245.mario.components.gizmo.GizmoSystem;
import me.jho5245.mario.jade.*;
import me.jho5245.mario.util.AssetPool;
import org.joml.Vector2f;

public class LevelEditorScene extends Scene
{
	private SpriteSheet spriteSheet;

	GameObject levelEditorStuff = this.createGameObject("LevelEditor");

	public LevelEditorScene()
	{
	}

	@Override
	public void init()
	{
		loadResources();
		spriteSheet = AssetPool.getSpriteSheet("assets/images/spritesheets/decorationsAndBlocks.png");
		SpriteSheet gizmos = AssetPool.getSpriteSheet("assets/images/gizmos.png");

		this.camera = new Camera(new Vector2f(0, 0));
		levelEditorStuff.addComponent(new MouseControls());
		levelEditorStuff.addComponent(new GridLines());
		levelEditorStuff.addComponent(new EditorCamera(this.camera));
		levelEditorStuff.addComponent(new GizmoSystem(gizmos));
		levelEditorStuff.start();
	}

	private void loadResources()
	{
		AssetPool.getShader("assets/shaders/default.glsl");

		AssetPool.addSpriteSheet("assets/images/spritesheets/decorationsAndBlocks.png", 16, 16, 81, 0);
		AssetPool.addSpriteSheet("assets/images/gizmos.png", 24, 48, 3, 0);

		AssetPool.getTexture("assets/images/green.png");

		for (GameObject obj : this.gameObjects)
		{
			if (obj.getComponent(SpriteRenderer.class) != null)
			{
				SpriteRenderer renderer = obj.getComponent(SpriteRenderer.class);
				if (renderer.getTexture() != null)
				{
					renderer.setTexture(AssetPool.getTexture(renderer.getTexture().getFilePath()));
				}
			}
		}
	}

	@Override
	public void update(float dt)
	{
		levelEditorStuff.update(dt);
		this.camera.adjustProjection();
		this.gameObjects.forEach(gameObject -> gameObject.update(dt));

	}

	@Override
	public void render()
	{
		this.renderer.render();
	}

	@Override
	public void imgui()
	{
		ImGui.begin("LevelEditorStuff");
		levelEditorStuff.imgui();
		ImGui.end();

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
			float spriteWidth = sprite.getWidth() * 2, spriteHeight = sprite.getHeight() * 2;
			int id = sprite.getTexId();
			Vector2f[] texCoords = sprite.getTexCoords();

			ImGui.pushID(i);
			if (ImGui.imageButton(id, spriteWidth, spriteHeight, texCoords[2].x, texCoords[0].y, texCoords[0].x, texCoords[2].y))
			{
				GameObject gameObject = Prefabs.generateSpriteObject(sprite, spriteWidth, spriteHeight);
				// Attach gameObject to the mouse cursor
				levelEditorStuff.getComponent(MouseControls.class).pickUpObject(gameObject);
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
