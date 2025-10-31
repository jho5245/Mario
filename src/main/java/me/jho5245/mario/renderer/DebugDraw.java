package me.jho5245.mario.renderer;

import me.jho5245.mario.jade.Camera;
import me.jho5245.mario.jade.Window;
import me.jho5245.mario.util.AssetPool;
import me.jho5245.mario.util.JMath;
import org.joml.Vector2f;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.lwjgl.opengl.ARBVertexArrayObject.glBindVertexArray;
import static org.lwjgl.opengl.ARBVertexArrayObject.glGenVertexArrays;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;

public class DebugDraw
{
	private static final int MAX_LINES = 5000;

	private static List<Line2D> lines = new ArrayList<>();

	// 6 floats per vertex, 2 vertices per line
	private static float[] vertexArray = new float[MAX_LINES * 6 * 2];

	private static Shader shader = AssetPool.getShader("assets/shaders/debugLine2D.glsl");

	private static int vaoID, vboID;

	private static boolean started = false;

	public static void start()
	{
		// Generate the vao
		vaoID = glGenVertexArrays();
		glBindVertexArray(vaoID);

		// Create the bvo and buffer some memory
		vboID = glGenBuffers();
		glBindBuffer(GL_ARRAY_BUFFER, vboID);
		glBufferData(GL_ARRAY_BUFFER, (long) vertexArray.length * Float.BYTES, GL_DYNAMIC_DRAW);

		// Enable the vertex array attributes
		// Position
		glVertexAttribPointer(0, 3, GL_FLOAT, false, 6 * Float.BYTES, 0);
		glEnableVertexAttribArray(0);
		// Color
		glVertexAttribPointer(1, 3, GL_FLOAT, false, 6 * Float.BYTES, 3 * Float.BYTES);
		glEnableVertexAttribArray(1);

		glLineWidth(1f);
	}

	public static void beginFrame()
	{
		if (!started)
		{
			started = true;
			start();
		}

		// Remove dead lines
		for (int i = 0; i < lines.size(); i++)
		{
			if (lines.get(i).beginFrame() < 0)
			{
				lines.remove(i);
				i--;
			}
		}
	}

	public static void draw()
	{
		if (lines.isEmpty())
			return;
		int index = 0;
		for (Line2D line : lines)
		{
			for (int i = 0; i < 2; i++)
			{
				Vector2f position = i == 0 ? line.getFrom() : line.getTo();
				Vector3f color = line.getColor();

				// Load position
				vertexArray[index + 0] = position.x;
				vertexArray[index + 1] = position.y;
				vertexArray[index + 2] = -10f;

				// Load color
				vertexArray[index + 3] = color.x;
				vertexArray[index + 4] = color.y;
				vertexArray[index + 5] = color.z;

				index += 6;
			}
		}

		glBindBuffer(GL_ARRAY_BUFFER, vboID);
		glBufferSubData(GL_ARRAY_BUFFER, 0, Arrays.copyOfRange(vertexArray, 0, lines.size() * 6 * 2));

		// use shader
		shader.use();
		shader.uploadMat4f("uProjection", Window.getCurrentScene().getCamera().getProjectionMatrix());
		shader.uploadMat4f("uView", Window.getCurrentScene().getCamera().getViewMatrix());

		// bind toe vao
		glBindVertexArray(vaoID);
		glEnableVertexAttribArray(0);
		glEnableVertexAttribArray(1);

		// draw the batch
		glDrawArrays(GL_LINES, 0, lines.size() * 2);

		// disable location
		glDisableVertexAttribArray(0);
		glDisableVertexAttribArray(1);
		glBindVertexArray(0);

		// unbind shader
		shader.detach();
	}

	// ============================
	// Add line2d Methods
	// ============================
	public static void addLine2D(Vector2f from, Vector2f to)
	{
		addLine2D(from, to, new Vector3f(0, 1, 0));
	}

	public static void addLine2D(Vector2f from, Vector2f to, Vector3f color)
	{
		addLine2D(from, to, color, 1);
	}

	public static void addLine2D(Vector2f from, Vector2f to, Vector3f color, int lifetime)
	{
		Camera camera = Window.getCurrentScene().getCamera();
		Vector2f cameraBottomLeft = new Vector2f(camera.getPosition()).add(new Vector2f(-2f, -2f));
		Vector2f cameraTopRight = new Vector2f(camera.getPosition()).add(camera.getProjectionSize()).mul(camera.getZoom()).add(new Vector2f(4f, 4f));
		if (!(from.x >= cameraBottomLeft.x && from.x <= cameraTopRight.x && from.y >= cameraBottomLeft.y && from.y <= cameraTopRight.y
				|| to.x >= cameraBottomLeft.x && to.x <= cameraTopRight.x && to.y >= cameraBottomLeft.y && to.y <= cameraTopRight.y))
			return;
		if (lines.size() >= MAX_LINES)
			return;
		lines.add(new Line2D(from, to, color, lifetime));
	}

	// ============================
	// Add box2d Methods
	// ============================

	public static void addBox(Vector2f center, Vector2f dimensions, float rotation)
	{
		addBox(center, dimensions, rotation, new Vector3f(0, 1, 0), 1);
	}

	// dimensions = (width, height)
	public static void addBox(Vector2f center, Vector2f dimensions, float rotation, Vector3f color, int lifetime)
	{
		Vector2f min = new Vector2f(center).sub(new Vector2f(dimensions).mul(0.5f));
		Vector2f max = new Vector2f(center).add(new Vector2f(dimensions).mul(0.5f));
		Vector2f[] vertices = {
				new Vector2f(min.x, min.y),
				new Vector2f(min.x, max.y),
				new Vector2f(max.x, max.y),
				new Vector2f(max.x, min.y)
		};

		if (rotation != 0f)
		{
			for (Vector2f vertex : vertices)
			{
				JMath.rotate(vertex, rotation, center);
			}
		}

		for (int i = 0; i < vertices.length; i++)
		{
			addLine2D(vertices[i], vertices[i + 1 == vertices.length ? 0 : i + 1], color, lifetime);
		}
	}

	// ============================
	// Add circle2d Methods
	// ============================

	public static void addCircle(Vector2f center, float radius, Vector3f color, int lifetime)
	{
		Vector2f[] points = new Vector2f[40];
		int increment = 360 / points.length;
		int currentAngle = 0;
		for (int i = 0; i < points.length; i++)
		{
			Vector2f temp = new Vector2f(radius, 0);
			JMath.rotate(temp, currentAngle, new Vector2f());
			points[i] = new Vector2f(temp).add(center);

			if (i > 0)
			{
				addLine2D(points[i], points[i - 1], color, lifetime);
			}
			currentAngle += increment;
		}

		addLine2D(points[0], points[points.length - 1], color, lifetime);
	}
}
