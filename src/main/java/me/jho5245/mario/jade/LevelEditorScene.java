package me.jho5245.mario.jade;

import me.jho5245.mario.renderer.Shader;
import org.joml.Vector2f;
import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

public class LevelEditorScene extends Scene
{
	private Shader defaultShader;

	private float[] vertexArray = {
			// position,                   color
			50.5f, -50.5f, 0.0f,             1.0f, 0.0f, 0.0f, 1.0f, // Bottom right    0
			-50.5f, 50.5f, 0.0f,             0.0f, 1.0f, 0.0f, 1.0f, // Top left				1
			50.5f, 50.5f, 0.0f,              0.0f, 0.0f, 1.0f, 1.0f, // Top right				2
			-50.5f, -50.5f, 0.0f,            1.0f, 1.0f, 1.0f, 1.0f, // Bottom left			3
	};

	// must be in counter-clockwise order
	private int[] elementArray = {
			/*
						x				x


						x				x
			*/
			2, 1, 0, // Top right triangle
			0, 1, 3, // bottom left triangle
	};

	private int vaoID, vboID, eboID;

	public LevelEditorScene()
	{
	}

	@Override
	public void init()
	{
		this.camera = new Camera(new Vector2f());
		defaultShader = new Shader("assets/shaders/default.glsl");
		defaultShader.compile();



		// generate VAO, VBO, EBO buffer object and send to GPU
		vaoID = glGenVertexArrays();
		glBindVertexArray(vaoID);

		// Create a float buffer of vertices
		FloatBuffer vertexBuffer = BufferUtils.createFloatBuffer(vertexArray.length);
		vertexBuffer.put(vertexArray).flip();

		// Create VBO upload the vertex buffer
		vboID = glGenBuffers();
		glBindBuffer(GL_ARRAY_BUFFER, vboID);
		glBufferData(GL_ARRAY_BUFFER, vertexBuffer, GL_STATIC_DRAW);

		// Create the indices and upload
		IntBuffer elementBuffer = BufferUtils.createIntBuffer(elementArray.length);
		elementBuffer.put(elementArray).flip();

		eboID = glGenBuffers();
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, eboID);
		glBufferData(GL_ELEMENT_ARRAY_BUFFER, elementBuffer, GL_STATIC_DRAW);

		// Add the vertex attribute pointers
		// 어디까지가 position이고 color인지 지시
		int positionSize = 3; // xyz
		int colorSize = 4; // rgba
		int floatSizeBytes = 4;
		int vertexSizeBytes = (positionSize + colorSize) * floatSizeBytes;
		// default.glsl의 location = 0 인 곳은 position, 따라서 index에 0 지정
		glVertexAttribPointer(0, positionSize, GL_FLOAT, false, vertexSizeBytes, 0);
		glEnableVertexAttribArray(0);

		// default.glsl의 location = 1 인 곳은 color, 따라서 index에 1 지정
		glVertexAttribPointer(1, colorSize, GL_FLOAT, false, vertexSizeBytes, positionSize * floatSizeBytes);
		glEnableVertexAttribArray(1);
	}

	@Override
	public void update(float dt)
	{
		camera.position.x -= dt * 50f;

		defaultShader.use();
		defaultShader.uploadMat4f("uProjection", camera.getProjectionMatrix());
		defaultShader.uploadMat4f("uView", camera.getViewMatrix());
		// Bind the VAO to use
		glBindVertexArray(vaoID);
		// Enable VAO pointers
		glEnableVertexAttribArray(0);
		glEnableVertexAttribArray(1);

		glDrawElements(GL_TRIANGLES, elementArray.length, GL_UNSIGNED_INT, 0);

		// Unbind
		glDisableVertexAttribArray(0);
		glDisableVertexAttribArray(1);
		glBindVertexArray(0); // bind nothing
		defaultShader.detach();
	}
}
