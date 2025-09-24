package me.jho5245.mario.jade;

import org.lwjgl.*;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;
import org.lwjgl.system.*;

import java.nio.*;

import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryStack.*;
import static org.lwjgl.system.MemoryUtil.*;

public class Window
{
	private int width, height;

	private String title;

	private long glfwWindow;

	private static Window window = null;

	private Window()
	{
		this.width = 1366;
		this.height = 768;
		this.title = "Mario";
	}

	public static Window getInstance()
	{
		if (window == null)
		{
			window = new Window();
		}
		return window;
	}

	public void run()
	{
		System.out.println("Hello LWJGL " + Version.getVersion() + "!");

		init();
		loop();
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

	private void loop()
	{
		while (!glfwWindowShouldClose(glfwWindow))
		{
			// Poll events
			glfwPollEvents();

			glClearColor(1f, 0f, 0f, 0f);
			glClear(GL_COLOR_BUFFER_BIT);

			glfwSwapBuffers(glfwWindow);
		}
	}
}
