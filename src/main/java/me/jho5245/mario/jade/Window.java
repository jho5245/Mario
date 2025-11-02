package me.jho5245.mario.jade;

import me.jho5245.mario.components.PlayerController.PlayerState;
import me.jho5245.mario.observers.Observer;
import me.jho5245.mario.observers.ObserverHandler;
import me.jho5245.mario.observers.events.Event;
import me.jho5245.mario.observers.events.EventType;
import me.jho5245.mario.physics2d.Physics2D;
import me.jho5245.mario.renderer.*;
import me.jho5245.mario.scenes.LevelEditorSceneInitializer;
import me.jho5245.mario.scenes.LevelSceneInitializer;
import me.jho5245.mario.scenes.Scene;
import me.jho5245.mario.scenes.SceneInitializer;
import me.jho5245.mario.sounds.Sound;
import me.jho5245.mario.util.AssetPool;
import org.joml.Vector2f;
import org.joml.Vector4f;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.openal.AL;
import org.lwjgl.openal.ALC;
import org.lwjgl.openal.ALCCapabilities;
import org.lwjgl.openal.ALCapabilities;
import org.lwjgl.opengl.GL;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.openal.ALC10.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.system.MemoryUtil.NULL;

public class Window implements Observer
{
	public static boolean PLAY_MODE;

	private int width, height;
	private final String title;
	private long glfwWindow;

	private ImGuiLayer imGuiLayer;
	private static Window window = null;
	private static Scene currentScene;
	private FrameBuffer frameBuffer;
	private PickingTexture pickingTexture;
	private boolean debugDrawPhysics = false;
	private boolean runtimePlaying;

	private long audioContext;
	private long audioDevice;

	private String levelName;

	public static PlayerState playerState = PlayerState.SMALL;

	private Window(boolean releaseBuild)
	{
		Window.PLAY_MODE = releaseBuild;
		this.width = 1920;
		this.height = 1060;
		this.title = "Mario";
		ObserverHandler.addObserver(this);
	}

	public static Window getInstance()
	{
		return getInstance(false);
	}

	public static Window getInstance(boolean releaseBuild)
	{
		if (window == null)
		{
			window = new Window(releaseBuild);
		}
		return window;
	}

	public static void changeScene(SceneInitializer sceneInitializer, boolean playPhysics)
	{
		Vector2f originCameraPosition = null;
		if (currentScene != null)
		{
			originCameraPosition = currentScene.getCamera().getPosition();
			currentScene.destroy();
		}
		currentScene = new Scene(sceneInitializer, playPhysics);
		currentScene.load();
		currentScene.init(originCameraPosition);
		currentScene.start();
		if (!PLAY_MODE)
		{
			glfwSetWindowTitle(getInstance().glfwWindow, getInstance().title + " [편집 모드] %s".formatted(getInstance().levelName));
		}
	}

	public static Physics2D getPhysics()
	{
		return currentScene.getPhysics();
	}

	public static Scene getCurrentScene()
	{
		return currentScene;
	}

	public static int getWidth()
	{
		return getInstance().width;
	}

	public static int getHeight()
	{
		return getInstance().height;
	}

	public static void setWidth(int width)
	{
		getInstance().width = width;
	}

	public static void setHeight(int height)
	{
		getInstance().height = height;
	}

	public static FrameBuffer getFrameBuffer()
	{
		return getInstance().frameBuffer;
	}

	public static float getTargetAspectRatio()
	{
		// TODO: Do not hardcode
		return 16f / 9f;
	}

	public static ImGuiLayer getImGuiLayer()
	{
		return getInstance().imGuiLayer;
	}

	@Override
	public void onNotify(GameObject obj, Event event)
	{
		switch (event.type)
		{
			case GAME_ENGINE_START_PLAY ->
			{
				Window.getImGuiLayer().getPropertiesWindow().clearSelected();
				currentScene.save();
				Window.changeScene(new LevelSceneInitializer(), true);
				this.runtimePlaying = true;
			}
			case GAME_ENGINE_STOP_PLAY ->
			{
				Window.getImGuiLayer().getPropertiesWindow().clearSelected();
				Window.changeScene(new LevelEditorSceneInitializer(), false);
				AssetPool.getAllSounds().forEach(Sound::stop);
				this.runtimePlaying = false;
				Window.playerState = PlayerState.SMALL;
			}
			case TOGGLE_PHYSICS_DEBUG_DRAW ->
			{
				this.debugDrawPhysics = !this.debugDrawPhysics;
			}
			case SAVE_LEVEL ->
			{
				currentScene.save();
			}
			case LOAD_LEVEL ->
			{
				Window.changeScene(new LevelEditorSceneInitializer(), true);
				Window.playerState = PlayerState.SMALL;
			}
		}
	}

	public void run()
	{
//		System.out.printf("Hello LWJGL %s!%n", Version.getVersion());

		init();
		loop();
		destroy();
	}

	private void init()
	{
		// Setup an error callback
		GLFWErrorCallback.createPrint(System.err).set();

		// Initialize GLFW
		if (!glfwInit())
		{
			throw new IllegalStateException("Unable to initialize GLFW.");
		}

		// Configure GLFW
		glfwDefaultWindowHints();
		glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
		glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);
		glfwWindowHint(GLFW_MAXIMIZED, GLFW_TRUE);

		// Create the window
		glfwWindow = glfwCreateWindow(this.width, this.height, this.title, NULL, NULL);
		if (glfwWindow == NULL)
		{
			throw new IllegalStateException("Failed to create the GLFW window.");
		}

		glfwSetCursorPosCallback(glfwWindow, MouseListener::mousePosCallback);
		glfwSetMouseButtonCallback(glfwWindow, MouseListener::mouseButtonCallback);
		glfwSetScrollCallback(glfwWindow, MouseListener::mouseScrollCallback);
		glfwSetKeyCallback(glfwWindow, KeyListener::keyCallback);
		glfwSetWindowSizeCallback(glfwWindow, (w, newWidth, newHeight) ->
		{
			Window.setWidth(newWidth);
			Window.setHeight(newHeight);
		});

		// Make the OpenGL context current
		glfwMakeContextCurrent(glfwWindow);
		// Enable v-sync
		glfwSwapInterval(1);

		// Make the window visible
		glfwShowWindow(glfwWindow);

		// Init audio device
		String defaultDeviceName = alcGetString(0, ALC_DEFAULT_DEVICE_SPECIFIER);
		assert defaultDeviceName != null : "defaultDeviceName is null";
		audioDevice = alcOpenDevice(defaultDeviceName);

		int[] attributes = { 0 };
		audioContext = alcCreateContext(audioDevice, attributes);
		alcMakeContextCurrent(audioContext);

		ALCCapabilities alcCapabilities = ALC.createCapabilities(audioDevice);
		ALCapabilities alCapabilities = AL.createCapabilities(alcCapabilities);
		if (!alCapabilities.OpenAL10)
		{
			assert false : "OpenAL10 is not supported";
		}

		// This line is critical for LWJGL's interoperation with GLFW's
		// OpenGL context, or any context that is managed externally.
		// LWJGL detects the context that is current in the current thread,
		// creates the GLCapabilities instance and makes the OpenGL
		// bindings available for use.
		GL.createCapabilities();

		glEnable(GL_BLEND);
		glBlendFunc(GL_ONE, GL_ONE_MINUS_SRC_ALPHA);

		this.frameBuffer = new FrameBuffer(width, height);
		this.pickingTexture = new PickingTexture(width, height);
		glViewport(0, 0, width, height);

		this.imGuiLayer = new ImGuiLayer(glfwWindow, pickingTexture);
		this.imGuiLayer.initImGui();

		loadResources();

		if (PLAY_MODE)
		{
			if (levelName == null)
			{
				levelName = "level.json";
			}
			runtimePlaying = true;
			Window.changeScene(new LevelSceneInitializer(), true);
		}
		else
		{
			ObserverHandler.notify(null, new Event(EventType.LOAD_LEVEL));
		}
	}

	private void loadResources()
	{
		AssetPool.getShader("assets/shaders/default.glsl");

		AssetPool.addSpriteSheet("assets/images/spritesheets/decorationsAndBlocks.png", 16, 16, 81, 0);
		AssetPool.addSpriteSheet("assets/images/spritesheets/blockFragment.png", 8, 8, 2, 0);
		AssetPool.addSpriteSheet("assets/images/spritesheet.png", 16, 16, 26, 0);
		AssetPool.addSpriteSheet("assets/images/turtle.png", 16, 24, 4, 0);
		AssetPool.addSpriteSheet("assets/images/underground_turtle.png", 16, 24, 4, 0);
		AssetPool.addSpriteSheet("assets/images/bigSpritesheet.png", 16, 32, 42, 0);
		AssetPool.addSpriteSheet("assets/images/pipes.png", 32, 32, 4, 0);
		AssetPool.addSpriteSheet("assets/images/items.png", 16, 16, 43, 0);
		AssetPool.addSpriteSheet("assets/images/gizmos.png", 24, 48, 3, 0);

		AssetPool.addSound("assets/sounds/main-theme-overworld.ogg", true);
		AssetPool.addSound("assets/sounds/main-theme-underground.ogg", true);
		AssetPool.addSound("assets/sounds/flagpole.ogg", false);
		AssetPool.addSound("assets/sounds/fireball.ogg", false);
		AssetPool.addSound("assets/sounds/break_block.ogg", false);
		AssetPool.addSound("assets/sounds/bump.ogg", false);
		AssetPool.addSound("assets/sounds/coin.ogg", false);
		AssetPool.addSound("assets/sounds/gameover.ogg", false);
		AssetPool.addSound("assets/sounds/jump-small.ogg", false);
		AssetPool.addSound("assets/sounds/jump-super.ogg", false);
		AssetPool.addSound("assets/sounds/mario_die.ogg", false);
		AssetPool.addSound("assets/sounds/pipe.ogg", false);
		AssetPool.addSound("assets/sounds/powerup.ogg", false);
		AssetPool.addSound("assets/sounds/powerup_appears.ogg", false);
		AssetPool.addSound("assets/sounds/stage_clear.ogg", false);
		AssetPool.addSound("assets/sounds/world_clear.ogg", false);
		AssetPool.addSound("assets/sounds/stomp.ogg", false);
		AssetPool.addSound("assets/sounds/kick.ogg", false);
		AssetPool.addSound("assets/sounds/invincible.ogg", true);
		AssetPool.addSound("assets/sounds/1-up.ogg", false);
	}

	private void destroy()
	{
		// Destroy audio context
		alcDestroyContext(audioContext);
		alcCloseDevice(audioDevice);

		// Free the memory
		glfwFreeCallbacks(glfwWindow);
		glfwDestroyWindow(glfwWindow);

		// Terminate GLFW and the free the error callback
		glfwTerminate();
		var callBack = glfwSetErrorCallback(null);
		if (callBack != null)
			callBack.free();
	}

	private void loop()
	{
		float beginTime = (float) glfwGetTime();
		float endTime;
		float dt = -1f;

		Shader defaultShader = AssetPool.getShader("assets/shaders/default.glsl");
		Shader pickingShader = AssetPool.getShader("assets/shaders/pickingShader.glsl");

		while (!glfwWindowShouldClose(glfwWindow))
		{
			// poll events
			glfwPollEvents();

			// Render pass 1. render to picking texture
			glDisable(GL_BLEND);
			pickingTexture.enableWriting();

			glViewport(0, 0, width, height);
			glClearColor(0, 0, 0, 0);
			glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
			Renderer.bindShader(pickingShader);
			currentScene.render();

			if (debugDrawPhysics)
			{
				currentScene.debugDrawPhysics();
			}

			pickingTexture.disableWriting();
			glEnable(GL_BLEND);

			// render pass 2. render actual game
			// Debug Draw
			DebugDraw.beginFrame();

			this.frameBuffer.bind();

			Vector4f clearColor = currentScene.getCamera().clearColor;
			glViewport(0, 0, width, height);
			glClearColor(clearColor.x, clearColor.y, clearColor.z, clearColor.w);
			glClear(GL_COLOR_BUFFER_BIT);

			if (dt >= 0)
			{
				Renderer.bindShader(defaultShader);
				if (runtimePlaying)
				{
					currentScene.update(dt);
				}
				else
				{
					currentScene.editorUpdate(dt);
				}
				currentScene.render();
				DebugDraw.draw();
			}

			this.frameBuffer.unbind();

			if (PLAY_MODE)
			{
				// NOTE: This is the most complicated piece for release builds. In release builds
				//       we want to just blit the framebuffer to the main window so we can see the game
				//
				//       In non-release builds, we usually draw the framebuffer to an ImGui component as an image.
				glBindFramebuffer(GL_READ_FRAMEBUFFER, frameBuffer.getFboID());
				glBindFramebuffer(GL_DRAW_FRAMEBUFFER, 0);
				glBlitFramebuffer(0, 0, frameBuffer.width, frameBuffer.height, 0, 0, this.width, this.height, GL_COLOR_BUFFER_BIT, GL_NEAREST);
			}
			else
			{
				this.imGuiLayer.update(dt, currentScene);
			}

			MouseListener.endFrame();
			KeyListener.endFrame();
			glfwSwapBuffers(glfwWindow);

			endTime = (float) glfwGetTime();
			dt = endTime - beginTime;
			beginTime = endTime;
		}
	}

	public String getLevelName()
	{
		return levelName;
	}

	public void setLevelName(String levelName)
	{
		this.levelName = levelName;
	}
}
