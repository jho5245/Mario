package me.jho5245.mario.scenes;

import imgui.ImGui;
import imgui.ImVec2;
import me.jho5245.mario.animations.StateMachine;
import me.jho5245.mario.components.*;
import me.jho5245.mario.components.block.BreakableBrick;
import me.jho5245.mario.components.gizmo.GizmoSystem;
import me.jho5245.mario.jade.PipeDirection;
import me.jho5245.mario.physics2d.components.Box2DCollider;
import me.jho5245.mario.physics2d.components.Rigidbody2D;
import me.jho5245.mario.physics2d.enums.BodyType;
import me.jho5245.mario.sounds.Sound;
import me.jho5245.mario.util.AssetPool;
import me.jho5245.mario.util.Settings;
import org.joml.Vector2f;

import me.jho5245.mario.jade.GameObject;
import me.jho5245.mario.jade.Prefabs;

import java.io.File;
import java.util.Collection;
import java.util.List;

public class LevelEditorSceneInitializer extends SceneInitializer
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
		levelEditorStuff.addComponent(new KeyControls());
		levelEditorStuff.addComponent(new GridLines());
		levelEditorStuff.addComponent(new EditorCamera(scene.getCamera()));
		levelEditorStuff.addComponent(new GizmoSystem(gizmos));
		scene.addGameObject(levelEditorStuff);

		//levelEditorStuff.start();
	}

	@Override
	public void imgui()
	{
		ImGui.begin("Level Editor Stuff");
		levelEditorStuff.imgui();
		ImGui.end();

		ImGui.begin("Objects");

		if (ImGui.beginTabBar("WindowTabBar"))
		{

			if (ImGui.beginTabItem("Solid Blocks"))
			{
				ImVec2 windowPos = new ImVec2();
				ImGui.getWindowPos(windowPos);
				ImVec2 windowSize = new ImVec2();
				ImGui.getWindowSize(windowSize);
				ImVec2 itemSpacing = new ImVec2();
				ImGui.getStyle().getItemSpacing(itemSpacing);

				float windowX2 = windowPos.x + windowSize.x;
				for (int i = 0; i < sprites.size(); i++)
				{
					// skip for non box collider tiles
					if (i == 34 || i >= 38 && i < 61) continue;

					Sprite sprite = sprites.getSprite(i);
					float spriteWidth = sprite.getWidth() * 2;
					float spriteHeight = sprite.getHeight() * 2;
					int id = sprite.getTexId();
					Vector2f[] texCoords = sprite.getTexCoords();

					ImGui.pushID(i);
					if (ImGui.imageButton(id, spriteWidth, spriteHeight, texCoords[2].x, texCoords[0].y, texCoords[0].x, texCoords[2].y))
					{
						GameObject object = Prefabs.generateSpriteObject(sprite, Settings.GRID_WIDTH, Settings.GRID_HEIGHT);

						Rigidbody2D rigidbody2D = new Rigidbody2D();
						rigidbody2D.setBodyType(BodyType.STATIC);
						Box2DCollider box2DCollider = new Box2DCollider();
						box2DCollider.setHalfSize(new Vector2f(Settings.GRID_WIDTH, Settings.GRID_HEIGHT));
						object.addComponent(rigidbody2D);
						object.addComponent(box2DCollider);
						object.addComponent(new Ground());
						// breakable bricks
						if (List.of(1, 5, 6, 12, 13).contains(i))
						{
							object.addComponent(new BreakableBrick());
						}

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
				ImGui.endTabItem();
			}

			if (ImGui.beginTabItem("Decoration Blocks"))
			{
				ImVec2 windowPos = new ImVec2();
				ImGui.getWindowPos(windowPos);
				ImVec2 windowSize = new ImVec2();
				ImGui.getWindowSize(windowSize);
				ImVec2 itemSpacing = new ImVec2();
				ImGui.getStyle().getItemSpacing(itemSpacing);

				float windowX2 = windowPos.x + windowSize.x;
				for (int i = 0; i < sprites.size(); i++)
				{
					// skip for box collider tiles
					if (!(i == 34 || i >= 38 && i < 61)) continue;

					Sprite sprite = sprites.getSprite(i);
					float spriteWidth = sprite.getWidth() * 2;
					float spriteHeight = sprite.getHeight() * 2;
					int id = sprite.getTexId();
					Vector2f[] texCoords = sprite.getTexCoords();

					ImGui.pushID(i);
					if (ImGui.imageButton(id, spriteWidth, spriteHeight, texCoords[2].x, texCoords[0].y, texCoords[0].x, texCoords[2].y))
					{
						GameObject object = Prefabs.generateSpriteObject(sprite, Settings.GRID_WIDTH, Settings.GRID_HEIGHT);
						object.transform.zIndex = -10;
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
				ImGui.endTabItem();
			}

			if (ImGui.beginTabItem("Prefabs"))
			{
				int uid = 0;
				// Mario
				{
					SpriteSheet playerSprites = AssetPool.getSpriteSheet("assets/images/spritesheet.png");
					Sprite sprite = playerSprites.getSprite(0);
					float spriteWidth = sprite.getWidth() * 2;
					float spriteHeight = sprite.getHeight() * 2;
					int id = sprite.getTexId();
					Vector2f[] texCoords = sprite.getTexCoords();

					ImGui.pushID(uid++);
					if (ImGui.imageButton(id, spriteWidth, spriteHeight, texCoords[2].x, texCoords[0].y, texCoords[0].x, texCoords[2].y))
					{
						GameObject object = Prefabs.generateMario();
						object.transform.zIndex = 20;
						levelEditorStuff.getComponent(MouseControls.class).pickUpObject(object);
					}
					ImGui.popID();
					ImGui.sameLine();
				}
				// Question Block
				{
					SpriteSheet items = AssetPool.getSpriteSheet("assets/images/items.png");
					Sprite sprite = items.getSprite(0);
					float spriteWidth = sprite.getWidth() * 2;
					float spriteHeight = sprite.getHeight() * 2;
					int id = sprite.getTexId();
					Vector2f[] texCoords = sprite.getTexCoords();

					ImGui.pushID(uid++);
					if (ImGui.imageButton(id, spriteWidth, spriteHeight, texCoords[2].x, texCoords[0].y, texCoords[0].x, texCoords[2].y))
					{
						GameObject object = Prefabs.generateQuestionBlock(false);
						levelEditorStuff.getComponent(MouseControls.class).pickUpObject(object);
					}
					ImGui.popID();
					ImGui.sameLine();
				}
				// Hidden Question Block
				{
					SpriteSheet items = AssetPool.getSpriteSheet("assets/images/items.png");
					Sprite sprite = items.getSprite(1);
					float spriteWidth = sprite.getWidth() * 2;
					float spriteHeight = sprite.getHeight() * 2;
					int id = sprite.getTexId();
					Vector2f[] texCoords = sprite.getTexCoords();

					ImGui.pushID(uid++);
					if (ImGui.imageButton(id, spriteWidth, spriteHeight, texCoords[2].x, texCoords[0].y, texCoords[0].x, texCoords[2].y))
					{
						GameObject object = Prefabs.generateQuestionBlock(true);
						levelEditorStuff.getComponent(MouseControls.class).pickUpObject(object);
					}
					ImGui.popID();
					ImGui.sameLine();
				}
				// Coin
				{
					SpriteSheet items = AssetPool.getSpriteSheet("assets/images/items.png");
					Sprite sprite = items.getSprite(7);
					float spriteWidth = sprite.getWidth() * 2;
					float spriteHeight = sprite.getHeight() * 2;
					int id = sprite.getTexId();
					Vector2f[] texCoords = sprite.getTexCoords();

					ImGui.pushID(uid++);
					if (ImGui.imageButton(id, spriteWidth, spriteHeight, texCoords[2].x, texCoords[0].y, texCoords[0].x, texCoords[2].y))
					{
						GameObject object = Prefabs.generateCoin();
						object.transform.zIndex = 10;
						levelEditorStuff.getComponent(MouseControls.class).pickUpObject(object);
					}
					ImGui.popID();
					ImGui.sameLine();
				}
				// Mushroom
				{
					SpriteSheet playerSprites = AssetPool.getSpriteSheet("assets/images/items.png");
					Sprite sprite = playerSprites.getSprite(15);
					float spriteWidth = sprite.getWidth() * 2;
					float spriteHeight = sprite.getHeight() * 2;
					int id = sprite.getTexId();
					Vector2f[] texCoords = sprite.getTexCoords();

					ImGui.pushID(uid++);
					if (ImGui.imageButton(id, spriteWidth, spriteHeight, texCoords[2].x, texCoords[0].y, texCoords[0].x, texCoords[2].y))
					{
						GameObject object = Prefabs.generateMushroom();
						object.transform.zIndex = 10;
						levelEditorStuff.getComponent(MouseControls.class).pickUpObject(object);
					}
					ImGui.popID();
					ImGui.sameLine();
				}
				// Goomba
				{
					SpriteSheet playerSprites = AssetPool.getSpriteSheet("assets/images/spritesheet.png");
					Sprite sprite = playerSprites.getSprite(14);
					float spriteWidth = sprite.getWidth() * 2;
					float spriteHeight = sprite.getHeight() * 2;
					int id = sprite.getTexId();
					Vector2f[] texCoords = sprite.getTexCoords();

					ImGui.pushID(uid++);
					if (ImGui.imageButton(id, spriteWidth, spriteHeight, texCoords[2].x, texCoords[0].y, texCoords[0].x, texCoords[2].y))
					{
						GameObject object = Prefabs.generateGoomba(false);
						object.transform.zIndex = 10;
						levelEditorStuff.getComponent(MouseControls.class).pickUpObject(object);
					}
					ImGui.popID();
					ImGui.sameLine();
				}
				// UndergroundGoomba
				{
					SpriteSheet playerSprites = AssetPool.getSpriteSheet("assets/images/spritesheet.png");
					Sprite sprite = playerSprites.getSprite(20);
					float spriteWidth = sprite.getWidth() * 2;
					float spriteHeight = sprite.getHeight() * 2;
					int id = sprite.getTexId();
					Vector2f[] texCoords = sprite.getTexCoords();

					ImGui.pushID(uid++);
					if (ImGui.imageButton(id, spriteWidth, spriteHeight, texCoords[2].x, texCoords[0].y, texCoords[0].x, texCoords[2].y))
					{
						GameObject object = Prefabs.generateGoomba(true);
						object.transform.zIndex = 10;
						levelEditorStuff.getComponent(MouseControls.class).pickUpObject(object);
					}
					ImGui.popID();
					ImGui.sameLine();
				}
				// Turtle
				{
					SpriteSheet turtle = AssetPool.getSpriteSheet("assets/images/turtle.png");
					Sprite sprite = turtle.getSprite(0);
					float spriteWidth = sprite.getWidth() * 2;
					float spriteHeight = sprite.getHeight() * 2;
					int id = sprite.getTexId();
					Vector2f[] texCoords = sprite.getTexCoords();

					ImGui.pushID(uid++);
					if (ImGui.imageButton(id, spriteWidth, spriteHeight, texCoords[2].x, texCoords[0].y, texCoords[0].x, texCoords[2].y))
					{
						GameObject object = Prefabs.generateTurtle();
						object.transform.zIndex = 10;
						levelEditorStuff.getComponent(MouseControls.class).pickUpObject(object);
					}
					ImGui.popID();
					ImGui.sameLine();
				}
				// Pipes
				{
					SpriteSheet pipes = AssetPool.getSpriteSheet("assets/images/pipes.png");
					for (PipeDirection direction : PipeDirection.values())
					{
						Sprite sprite = pipes.getSprite(direction.ordinal());
						float spriteWidth = sprite.getWidth() * 2;
						float spriteHeight = sprite.getHeight() * 2;
						int id = sprite.getTexId();
						Vector2f[] texCoords = sprite.getTexCoords();

						ImGui.pushID(uid++);
						if (ImGui.imageButton(id, spriteWidth, spriteHeight, texCoords[2].x, texCoords[0].y, texCoords[0].x, texCoords[2].y))
						{
							GameObject object = Prefabs.generatePipe(direction);
							object.transform.zIndex = 10;
							levelEditorStuff.getComponent(MouseControls.class).pickUpObject(object);
						}
						ImGui.popID();
						ImGui.sameLine();
					}
				}
				// GoalFlagPole
				{
					SpriteSheet goalFlagPole = AssetPool.getSpriteSheet("assets/images/items.png");
					Sprite sprite = goalFlagPole.getSprite(33);
					float spriteWidth = sprite.getWidth() * 2;
					float spriteHeight = sprite.getHeight() * 2;
					int id = sprite.getTexId();
					Vector2f[] texCoords = sprite.getTexCoords();

					ImGui.pushID(uid++);
					if (ImGui.imageButton(id, spriteWidth, spriteHeight, texCoords[2].x, texCoords[0].y, texCoords[0].x, texCoords[2].y))
					{
						GameObject object = Prefabs.generateGoalFlagPole();
						object.transform.zIndex = 10;
						levelEditorStuff.getComponent(MouseControls.class).pickUpObject(object);
					}
					ImGui.popID();
					ImGui.sameLine();
				}
				// GoalFlag
				{
					SpriteSheet goalFlag = AssetPool.getSpriteSheet("assets/images/items.png");
					Sprite sprite = goalFlag.getSprite(6);
					float spriteWidth = sprite.getWidth() * 2;
					float spriteHeight = sprite.getHeight() * 2;
					int id = sprite.getTexId();
					Vector2f[] texCoords = sprite.getTexCoords();

					ImGui.pushID(uid++);
					if (ImGui.imageButton(id, spriteWidth, spriteHeight, texCoords[2].x, texCoords[0].y, texCoords[0].x, texCoords[2].y))
					{
						GameObject object = Prefabs.generateGoalFlag();
						object.transform.zIndex = 10;
						levelEditorStuff.getComponent(MouseControls.class).pickUpObject(object);
					}
					ImGui.popID();
					ImGui.sameLine();
				}

				ImGui.endTabItem();
			}

			if (ImGui.beginTabItem("Sounds"))
			{
				Collection<Sound> sounds = AssetPool.getAllSounds();
				for (Sound sound : sounds)
				{
					File temp = new File(sound.getFilePath());
					if (ImGui.button(temp.getName()))
					{
						if (!sound.isPlaying())
						{
							sound.play();
						}
						else
							sound.stop();
					}

					if (ImGui.getContentRegionAvailX() > 100)
					{
						ImGui.sameLine();
					}
				}
				ImGui.endTabItem();
			}

			ImGui.endTabBar();
		}

		ImGui.end();
	}
}
