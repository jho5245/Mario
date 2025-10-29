package me.jho5245.mario.jade;

import me.jho5245.mario.observers.Observer;
import me.jho5245.mario.observers.ObserverHandler;
import me.jho5245.mario.observers.events.Event;
import me.jho5245.mario.physics2d.Physics2D;
import me.jho5245.mario.renderer.*;
import me.jho5245.mario.scenes.LevelEditorInitializer;
import me.jho5245.mario.scenes.Scene;
import me.jho5245.mario.scenes.SceneInitializer;
import me.jho5245.mario.util.AssetPool;
import org.joml.Vector2f;
import org.lwjgl.Version;
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
import static org.lwjgl.system.MemoryUtil.NULL;

public class Window implements Observer
{
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

	private Window()
	{
		this.width = 1920;
		this.height = 1060;
		this.title = "Mario";
		ObserverHandler.addObserver(this);
	}

	public static Window getInstance()
	{
		if (window == null)
		{
			window = new Window();
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
		getImGuiLayer().getPropertiesWindow().setActiveGameObject(null);
		currentScene = new Scene(sceneInitializer, playPhysics);
		currentScene.load();
		currentScene.init(originCameraPosition);
		currentScene.start();
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
				currentScene.save();
				Window.changeScene(new LevelEditorInitializer(), true);
				this.runtimePlaying = true;
			}
			case GAME_ENGINE_STOP_PLAY ->
			{
				Window.changeScene(new LevelEditorInitializer(), false);
				this.runtimePlaying = false;
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
				Window.changeScene(new LevelEditorInitializer(), true);
			}
		}
	}

	public void run()
	{
		System.out.printf("Hello LWJGL %s!%n", Version.getVersion());

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

		Window.changeScene(new LevelEditorInitializer(), false);
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

			glClearColor(1, 1, 1, 1);
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

			this.imGuiLayer.update(dt, currentScene);

			MouseListener.endFrame();
			KeyListener.endFrame();
			glfwSwapBuffers(glfwWindow);

			endTime = (float) glfwGetTime();
			dt = endTime - beginTime;
			beginTime = endTime;
		}
	}
}
