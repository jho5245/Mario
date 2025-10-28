package me.jho5245.mario.scenes;

import imgui.ImGui;
import imgui.ImVec2;
import me.jho5245.mario.components.*;
import me.jho5245.mario.components.gizmo.GizmoSystem;
import me.jho5245.mario.jade.*;
import me.jho5245.mario.util.AssetPool;
import me.jho5245.mario.util.Settings;
import org.joml.Vector2f;

import me.jho5245.mario.components.*;
import imgui.ImGui;
import imgui.ImVec2;
import me.jho5245.mario.components.gizmo.GizmoSystem;
import me.jho5245.mario.jade.GameObject;
import me.jho5245.mario.jade.Prefabs;
import org.joml.Vector2f;
import me.jho5245.mario.util.AssetPool;

public class LevelEditorInitializer extends SceneInitializer
{
	private SpriteSheet sprites;
	private GameObject levelEditorStuff;

	@Override
	public void init(Scene scene)
	{
		sprites = AssetPool.getSpriteSheet("assets/images/spritesheets/decorationsAndBlocks.png");
		SpriteSheet gizmos = AssetPool.getSpriteSheet("assets/images/gizmos.png");

		levelEditorStuff = scene.createGameObject("Level Editor Stuff");
		levelEditorStuff.setNoSerialize();
		levelEditorStuff.addComponent(new MouseControls());
		levelEditorStuff.addComponent(new GridLines());
		levelEditorStuff.addComponent(new EditorCamera(scene.getCamera()));
		levelEditorStuff.addComponent(new GizmoSystem(gizmos));
		scene.addGameObject(levelEditorStuff);

		//levelEditorStuff.start();
	}

	@Override
	public void loadResources(Scene scene)
	{
		AssetPool.getShader("assets/shaders/default.glsl");

		AssetPool.addSpriteSheet("assets/images/spritesheets/decorationsAndBlocks.png", 16, 16, 81, 0);
		AssetPool.addSpriteSheet("assets/images/gizmos.png", 24, 48, 3, 0);

		for (GameObject g : scene.getGameObjects())
		{
			if (g.getComponent(SpriteRenderer.class) != null)
			{
				SpriteRenderer spr = g.getComponent(SpriteRenderer.class);
				if (spr.getTexture() != null)
				{
					spr.setTexture(AssetPool.getTexture(spr.getTexture().getFilePath()));
				}
			}
		}
	}

	@Override
	public void imgui()
	{
		ImGui.begin("Level Editor Stuff");
		levelEditorStuff.imgui();
		ImGui.end();

		ImGui.begin("Test window");

		ImVec2 windowPos = new ImVec2();
		ImGui.getWindowPos(windowPos);
		ImVec2 windowSize = new ImVec2();
		ImGui.getWindowSize(windowSize);
		ImVec2 itemSpacing = new ImVec2();
		ImGui.getStyle().getItemSpacing(itemSpacing);

		float windowX2 = windowPos.x + windowSize.x;
		for (int i = 0; i < sprites.size(); i++)
		{
			Sprite sprite = sprites.getSprite(i);
			float spriteWidth = sprite.getWidth() * 2;
			float spriteHeight = sprite.getHeight() * 2;
			int id = sprite.getTexId();
			Vector2f[] texCoords = sprite.getTexCoords();

			ImGui.pushID(i);
			if (ImGui.imageButton(id, spriteWidth, spriteHeight, texCoords[2].x, texCoords[0].y, texCoords[0].x, texCoords[2].y))
			{
				GameObject object = Prefabs.generateSpriteObject(sprite, Settings.GRID_WIDTH, Settings.GRID_HEIGHT);
				levelEditorStuff.getComponent(MouseControls.class).pickUpObject(object);
			}
			ImGui.popID();

			ImVec2 lastButtonPos = new ImVec2();
			ImGui.getItemRectMax(lastButtonPos);
			float lastButtonX2 = lastButtonPos.x;
			float nextButtonX2 = lastButtonX2 + itemSpacing.x + spriteWidth;
			if (i + 1 < sprites.size() && nextButtonX2 < windowX2)
			{
				ImGui.sameLine();
			}
		}

		ImGui.end();
	}
}
