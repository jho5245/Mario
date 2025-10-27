package me.jho5245.mario.renderer;

import org.lwjgl.BufferUtils;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.Objects;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.stb.STBImage.*;

public class Texture
{
	private String filePath;

	private transient int textureID;

	private int width, height;

	public Texture()
	{
		textureID = -1;
		width = height = -1;
	}

	public Texture(int width, int height)
	{
		this.filePath = "Generated";
		this.width = width;
		this.height = height;

		// Generate texture on GPU
		textureID = glGenTextures();
		glBindTexture(GL_TEXTURE_2D, textureID);

		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);

		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB, width, height, 0, GL_RGB, GL_UNSIGNED_BYTE, 0);
	}

	public void init(String filePath)
	{
		this.filePath = filePath;

		// Generate texture on GPU
		textureID = glGenTextures();
		glBindTexture(GL_TEXTURE_2D, textureID);

		// Set texture parameters
		// Repeat image in both directions
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
		// stretching/pixelating
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
		// shrinking/pixelating
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);

		// Load image
		IntBuffer width = BufferUtils.createIntBuffer(1);
		IntBuffer height = BufferUtils.createIntBuffer(1);
		IntBuffer channels = BufferUtils.createIntBuffer(1); // rgb or rgba
		// 텍스처가 상하반전 되는 현상 완화
		stbi_set_flip_vertically_on_load(true);
		ByteBuffer image = stbi_load(filePath, width, height, channels, 0);

		if (image != null)
		{
			this.width = width.get(0);
			this.height = height.get(0);
			switch (channels.get(0))
			{
				// RGB
				case 3 -> glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB, width.get(0), height.get(0), 0, GL_RGB, GL_UNSIGNED_BYTE, image);
				// RGBA
				case 4 -> glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width.get(0), height.get(0), 0, GL_RGBA, GL_UNSIGNED_BYTE, image);
				default -> throw new RuntimeException("Unknown texture channel: " + channels.get(0));
			}

		}
		else
		{
			assert false : "Error loading texture image: " + filePath;
		}

		stbi_image_free(image);
	}

	public void bind()
	{
		glBindTexture(GL_TEXTURE_2D, textureID);
	}

	public void unbind()
	{
		glBindTexture(GL_TEXTURE_2D, 0);
	}

	public int getWidth()
	{
		return width;
	}

	public int getHeight()
	{
		return height;
	}

	public int getId()
	{
		return textureID;
	}

	public String getFilePath()
	{
		return filePath;
	}

	@Override
	public boolean equals(Object o)
	{
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		Texture texture = (Texture) o;
		if (width != texture.width)
			return false;
		if (height != texture.height)
			return false;
		if (textureID != texture.textureID)
			return false;
		return Objects.equals(filePath, texture.filePath);
	}
}
