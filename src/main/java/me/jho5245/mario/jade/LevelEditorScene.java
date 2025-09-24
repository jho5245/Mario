package me.jho5245.mario.jade;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL20;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

public class LevelEditorScene extends Scene
{
	private String vertexShaderSrc = """
			#version 330 core
			layout (location=0) in vec3 aPos;
			layout (location=1) in vec4 aColor;
			
			out vec4 fColor;
			
			void main()
			{
			    fColor = aColor;
			    gl_Position = vec4(aPos, 1.0);
			}""";

	private String fragmentShaderSrc = """
			#version 330 core
			
			in vec4 fColor;
			
			out vec4 color;
			
			void main()
			{
			    color = fColor;
			}
			""";

	private int vertexID, fragmentID, shaderProgram;

	private float[] vertexArray = {
			// position,                   color
			0.5f, -0.5f, 0.0f,             1.0f, 0.0f, 0.0f, 1.0f, // Bottom right    0
			-0.5f, 0.5f, 0.0f,             0.0f, 1.0f, 0.0f, 1.0f, // Top left				1
			0.5f, 0.5f, 0.0f,              0.0f, 0.0f, 1.0f, 1.0f, // Top right				2
			-0.5f, -0.5f, 0.0f,            1.0f, 1.0f, 1.0f, 1.0f, // Bottom left			3
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
		// Compile and link shaders

		// Load & Compile the vertex shader
		vertexID = glCreateShader(GL_VERTEX_SHADER);
		// Pass the shader source to the GPU
		glShaderSource(vertexID, vertexShaderSrc);
		glCompileShader(vertexID);

		// Check for errors in compliation
		int success = glGetShaderi(vertexID, GL_COMPILE_STATUS);
		if (success == GL_FALSE)
		{
			int length = glGetShaderi(vertexID, GL_INFO_LOG_LENGTH);
			System.out.println("Error compiling vertex shader: " + length);
			System.out.println(glGetShaderInfoLog(vertexID, length));
			assert false : "Error compiling vertex shader";
		}

		// Load & Compile the vertex shader
		fragmentID = glCreateShader(GL_FRAGMENT_SHADER);
		// Pass the shader source to the GPU
		glShaderSource(fragmentID, fragmentShaderSrc);
		glCompileShader(fragmentID);

		// Check for errors in compliation
		success = glGetShaderi(fragmentID, GL_COMPILE_STATUS);
		if (success == GL_FALSE)
		{
			int length = glGetShaderi(fragmentID, GL_INFO_LOG_LENGTH);
			System.out.println("Error compiling fragment shader: " + length);
			System.out.println(glGetShaderInfoLog(fragmentID, length));
			assert false : "Error compiling fragment shader";
		}

		// Link shaders & check for errors
		shaderProgram = glCreateProgram();
		glAttachShader(shaderProgram, vertexID);
		glAttachShader(shaderProgram, fragmentID);
		glLinkProgram(shaderProgram);

		// Check for linking errors
		success = glGetProgrami(shaderProgram, GL_LINK_STATUS);
		if (success == GL_FALSE)
		{
			int length = glGetProgrami(shaderProgram, GL_INFO_LOG_LENGTH);
			System.out.println("Error linking program: " + length);
			System.out.println(glGetProgramInfoLog(shaderProgram, length));
			assert false : "Error linking program";
		}

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
		// Bind shader program
		glUseProgram(shaderProgram);
		// Bind the VAO to use
		glBindVertexArray(vaoID);
		// Enable vao pointers
		glEnableVertexAttribArray(0);
		glEnableVertexAttribArray(1);

		glDrawElements(GL_TRIANGLES, elementArray.length, GL_UNSIGNED_INT, 0);

		// Unbind
		glDisableVertexAttribArray(0);
		glDisableVertexAttribArray(1);
		glBindVertexArray(0); // bind nothing
		glUseProgram(0); // do not use program
	}
}
