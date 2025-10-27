package me.jho5245.mario.jade;

import me.jho5245.mario.renderer.DebugDraw;
import me.jho5245.mario.renderer.FrameBuffer;
import me.jho5245.mario.scenes.LevelEditorScene;
import me.jho5245.mario.scenes.LevelScene;
import me.jho5245.mario.scenes.Scene;
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
		initWindow();

		initImGui();

		// Mouse Listener
		glfwSetCursorPosCallback(glfwWindow, MouseListener::mousePosCallback);
		glfwSetMouseButtonCallback(glfwWindow, MouseListener::mouseButtonCallback);
		glfwSetScrollCallback(glfwWindow, MouseListener::mouseScrollCallback);

		// Keyboard Listener
		glfwSetKeyCallback(glfwWindow, KeyListener::keyCallback);

		glfwSetWindowSizeCallback(glfwWindow, (w,  newWidth, newHeight) -> {
			Window.setWidth(newWidth);
			Window.setHeight(newHeight);
		});

		// alpha blending
		glEnable(GL_BLEND);
		glBlendFunc(GL_ONE, GL_ONE_MINUS_SRC_ALPHA);

		this.frameBuffer = new FrameBuffer(width, height);
		glViewport(0, 0, width, height);

		Window.changeScene(0);
	}

	private void initWindow()
	{
		// Setup an error callback. The default implementation
		// will print the error message in System.err.
		GLFWErrorCallback.createPrint(System.err).set();

		// Initialize GLFW. Most GLFW functions will not work before doing this.
		if (!glfwInit())
		{
			throw new IllegalStateException("Could not init GLFW!");
		}

		glslVersion = "#version 330 core";
		glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
		glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 0);

		// Configure GLFW
		glfwDefaultWindowHints();
		glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
		glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);
		//		glfwWindowHint(GLFW_MAXIMIZED, GLFW_TRUE);

		// Create the window
		glfwWindow = glfwCreateWindow(width, height, title, NULL, NULL);
		if (glfwWindow == NULL)
		{
			throw new IllegalStateException("Failed to create the GLFW window.");
		}

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
	}

	private void initImGui()
	{
		this.imGuiLayer = new ImGuiLayer(glfwWindow);
		this.imGuiLayer.initImGui();
	}

	private void destroy()
	{
		imGuiLayer.destroyImGui();

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

		while (!glfwWindowShouldClose(glfwWindow))
		{
			// poll events
			glfwPollEvents();

			// Debug Draw
			DebugDraw.beginFrame();

			this.frameBuffer.bind();

			glClearColor(r, g, b, a);
			glClear(GL_COLOR_BUFFER_BIT);

			if (dt >= 0)
			{
				DebugDraw.draw();
				currentScene.update(dt);
			}

			this.frameBuffer.unbind();

			this.imGuiLayer.update(dt, currentScene);

			glfwSwapBuffers(glfwWindow);

			endTime = (float) glfwGetTime();
			dt = endTime - beginTime;
			beginTime = endTime;
		}
		currentScene.saveExit();
	}
}
