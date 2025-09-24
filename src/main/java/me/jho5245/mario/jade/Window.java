package me.jho5245.mario.jade;

import me.jho5245.mario.util.Time;
import org.lwjgl.*;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;

import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.*;

public class Window
{
	private final int width, height;

	private final String title;

	private long glfwWindow;

	public float r, g, b, a;

	private boolean fadeToBlack;

	private static Window window = null;

	private static Scene currentScene;

	private Window()
	{
		this.width = 1366;
		this.height = 768;
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
	}

	public void run()
	{
		System.out.printf("Hello LWJGL %s!%n", Version.getVersion());

		init();
		loop();

		// Free the memory
		glfwFreeCallbacks(glfwWindow);
		glfwDestroyWindow(glfwWindow);

		// Terminate GLFW and the free the error callback
		glfwTerminate();
		var callBack = glfwSetErrorCallback(null);
		if (callBack != null)
			callBack.free();
	}

	private void init()
	{
		// Setup an error callback. The default implementation
		// will print the error message in System.err.
		GLFWErrorCallback.createPrint(System.err).set();

		// Initialize GLFW. Most GLFW functions will not work before doing this.
		if (!glfwInit())
		{
			throw new IllegalStateException("Could not init GLFW!");
		}

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

		// Mouse Listener
		glfwSetCursorPosCallback(glfwWindow, MouseListener::mousePosCallback);
		glfwSetMouseButtonCallback(glfwWindow, MouseListener::mouseButtonCallback);
		glfwSetScrollCallback(glfwWindow, MouseListener::mouseScrollCallback);

		// Keyboard Listener
		glfwSetKeyCallback(glfwWindow, KeyListener::keyCallback);

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

		Window.changeScene(0);
	}

	private void loop()
	{
		float beginTime = Time.getTime();
		float endTime;
		float dt = -1f;

		while (!glfwWindowShouldClose(glfwWindow))
		{
			// Poll events
			glfwPollEvents();

			glClearColor(r, g, b, a);
			glClear(GL_COLOR_BUFFER_BIT);

			if (dt >= 0)
			{
				currentScene.update(dt);
			}

			glfwSwapBuffers(glfwWindow);

			endTime = Time.getTime();
			dt = endTime - beginTime;
			beginTime = endTime;
		}
	}
}
