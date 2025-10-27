package me.jho5245.mario.jade;

import me.jho5245.mario.renderer.*;
import me.jho5245.mario.scenes.LevelEditorScene;
import me.jho5245.mario.scenes.LevelScene;
import me.jho5245.mario.scenes.Scene;
import me.jho5245.mario.util.AssetPool;
import org.lwjgl.Version;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.NULL;

public class Window
{
	private int width, height;

	private final String title;

	private long glfwWindow;

	public float r, g, b, a;

	private String glslVersion;

	private ImGuiLayer imGuiLayer;

	private static Window window = null;

	private static Scene currentScene;

	private FrameBuffer frameBuffer;

	private PickingTexture pickingTexture;

	private Window()
	{
		this.width = 1920;
		this.height = 1060;
		this.title = "Mario";
		r = 1;
		g = 1;
		b = 1;
		a = 1;
	}

	public static Window getInstance()
	{
		if (window == null)
		{
			window = new Window();
		}
		return window;
	}

	public static void changeScene(int newScene)
	{
		switch (newScene)
		{
			case 0 -> currentScene = new LevelEditorScene();
			case 1 -> currentScene = new LevelScene();
			default ->
			{
				assert false : "Unknown scene '%s'".formatted(newScene);
			}
		}
		currentScene.load();
		currentScene.init();
		currentScene.start();
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

	public static String getGLSLVersion()
	{
		return getInstance().glslVersion;
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

		Window.changeScene(0);
	}

	private void destroy()
	{
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
			glClearColor(0f, 0f, 0f, 0f);
			glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
			Renderer.bindShader(pickingShader);
			currentScene.render();

			pickingTexture.disableWriting();
			glEnable(GL_BLEND);

			// render pass 2. render actual game
			// Debug Draw
			DebugDraw.beginFrame();

			this.frameBuffer.bind();

			glClearColor(r, g, b, a);
			glClear(GL_COLOR_BUFFER_BIT);

			if (dt >= 0)
			{
				DebugDraw.draw();
				Renderer.bindShader(defaultShader);
				currentScene.update(dt);
				currentScene.render();
			}

			this.frameBuffer.unbind();

			this.imGuiLayer.update(dt, currentScene);

			glfwSwapBuffers(glfwWindow);
			MouseListener.endFrame();

			endTime = (float) glfwGetTime();
			dt = endTime - beginTime;
			beginTime = endTime;
		}
		currentScene.saveExit();
	}
}
