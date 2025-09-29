package me.jho5245.mario.renderer;

import org.joml.*;
import org.lwjgl.BufferUtils;

import java.io.IOException;
import java.nio.FloatBuffer;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.lwjgl.opengl.GL11.GL_FALSE;
import static org.lwjgl.opengl.GL20.*;

public class Shader
{
	private int shaderProgramID;

	private boolean using = false;

	private String vertexSource;

	private String fragmentSource;

	private String filePath;

	public Shader(String filePath)
	{
		this.filePath = filePath;

		try
		{
			String source = new String(Files.readAllBytes(Paths.get(filePath)));
			String[] split = source.split("(#type)( )+([a-zA-Z]+)");

			// Find the first pattern after #type pattern
			int index = source.indexOf("#type") + 6;
			int eol = source.indexOf("\r\n", index);
			String firstPattern = source.substring(index, eol).trim();

			// Find the second pattern after #type pattern
			index = source.indexOf("#type", eol) + 6;
			eol = source.indexOf("\r\n", index);
			String secondPattern = source.substring(index, eol).trim();

			if (firstPattern.equals("vertex"))
			{
				vertexSource = split[1];
			}
			else if (firstPattern.equals("fragment"))
			{
				fragmentSource = split[1];
			}
			else
			{
				throw new IOException("Unexpected token '%s'".formatted(firstPattern));
			}

			if (secondPattern.equals("vertex"))
			{
				vertexSource = split[2];
			}
			else if (secondPattern.equals("fragment"))
			{
				fragmentSource = split[2];
			}
			else
			{
				throw new IOException("Unexpected token '%s'".formatted(secondPattern));
			}
		}
		catch (IOException e)
		{
			e.printStackTrace();
			assert false : "Error : Could not open file for shader: %s".formatted(filePath);
		}
	}

	public void compile()
	{
		// Compile & Link shaders
		int vertexID, fragmentID;

		// Load & Compile the vertex shader
		vertexID = glCreateShader(GL_VERTEX_SHADER);
		// Pass the shader source to the GPU
		glShaderSource(vertexID, vertexSource);
		glCompileShader(vertexID);

		// Check for errors in compliation
		int success = glGetShaderi(vertexID, GL_COMPILE_STATUS);
		if (success == GL_FALSE)
		{
			int length = glGetShaderi(vertexID, GL_INFO_LOG_LENGTH);
			System.out.println("Error compiling vertex shader: " + filePath);
			System.out.println(glGetShaderInfoLog(vertexID, length));
			assert false : "Error compiling vertex shader";
		}

		// Load & Compile the vertex shader
		fragmentID = glCreateShader(GL_FRAGMENT_SHADER);
		// Pass the shader source to the GPU
		glShaderSource(fragmentID, fragmentSource);
		glCompileShader(fragmentID);

		// Check for errors in compliation
		success = glGetShaderi(fragmentID, GL_COMPILE_STATUS);
		if (success == GL_FALSE)
		{
			int length = glGetShaderi(fragmentID, GL_INFO_LOG_LENGTH);
			System.out.println("Error compiling fragment shader: " + filePath);
			System.out.println(glGetShaderInfoLog(fragmentID, length));
			assert false : "Error compiling fragment shader";
		}

		// Link shaders & check for errors
		shaderProgramID = glCreateProgram();
		glAttachShader(shaderProgramID, vertexID);
		glAttachShader(shaderProgramID, fragmentID);
		glLinkProgram(shaderProgramID);

		// Check for linking errors
		success = glGetProgrami(shaderProgramID, GL_LINK_STATUS);
		if (success == GL_FALSE)
		{
			int length = glGetProgrami(shaderProgramID, GL_INFO_LOG_LENGTH);
			System.out.println("Error linking program: " + filePath);
			System.out.println(glGetProgramInfoLog(shaderProgramID, length));
			assert false : "Error linking program";
		}
	}

	public void use()
	{
		if (using)
			return;
		using = true;
		// Bind shader program
		glUseProgram(shaderProgramID);
	}

	public void detach()
	{
		glUseProgram(0);
		using = false;
	}

	public void uploadMat4f(String varName, Matrix4f mat4)
	{
		int varLocation = glGetUniformLocation(shaderProgramID, varName);
		use();
		FloatBuffer matBuffer = BufferUtils.createFloatBuffer(16); // 4*4 matrix
		mat4.get(matBuffer);
		glUniformMatrix4fv(varLocation, false, matBuffer);
	}

	public void uploadMat3f(String varName, Matrix3f mat3)
	{
		int varLocation = glGetUniformLocation(shaderProgramID, varName);
		use();
		FloatBuffer matBuffer = BufferUtils.createFloatBuffer(9); // 3*3 matrix
		mat3.get(matBuffer);
		glUniformMatrix3fv(varLocation, false, matBuffer);
	}

	public void uploadVec4f(String varName, Vector4f vec)
	{
		int varLocation = glGetUniformLocation(shaderProgramID, varName);
		use();
		glUniform4f(varLocation, vec.x, vec.y, vec.z, vec.w);
	}

	public void uploadVec3f(String varName, Vector3f vec)
	{
		int varLocation = glGetUniformLocation(shaderProgramID, varName);
		use();
		glUniform3f(varLocation, vec.x, vec.y, vec.z);
	}

	public void uploadVec2f(String varName, Vector2f vec)
	{
		int varLocation = glGetUniformLocation(shaderProgramID, varName);
		use();
		glUniform2f(varLocation, vec.x, vec.y);
	}

	public void uploadFloat(String varName, float val)
	{
		int varLocation = glGetUniformLocation(shaderProgramID, varName);
		use();
		glUniform1f(varLocation, val);
	}

	public void uploadInt(String varName, int val)
	{
		int varLocation = glGetUniformLocation(shaderProgramID, varName);
		use();
		glUniform1i(varLocation, val);
	}
}
