package me.jho5245.mario.renderer;

import static org.lwjgl.opengl.GL30.*;

public class FrameBuffer
{
	private int fboID;

	private Texture texture;

	public FrameBuffer(int width, int height)
	{
		// generate frame buffer
		fboID = glGenFramebuffers();

		// create texture to render the data to & attach it to framebuffer
		this.texture = new Texture(width, height);
		glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, this.texture.getId(), 0);

		// Create render buffer
		int rboID = glGenRenderbuffers();
		glBindRenderbuffer(GL_RENDERBUFFER, rboID);
		// 32 bits depth
		glRenderbufferStorage(GL_RENDERBUFFER, GL_DEPTH_COMPONENT32, width, height);
		glFramebufferRenderbuffer(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, GL_RENDERBUFFER, rboID);

		if (glCheckFramebufferStatus(GL_FRAMEBUFFER) != GL_FRAMEBUFFER_COMPLETE)
		{
			assert false : "error: frame buffer is not complete.";
		}
	}

	public void bind()
	{
		glBindFramebuffer(GL_FRAMEBUFFER, fboID);
	}

	public void unbind()
	{
		glBindFramebuffer(GL_FRAMEBUFFER, 0);
	}

	public int getFboID()
	{
		return fboID;
	}

	public Texture getTexture()
	{
		return texture;
	}

	public int getTextureId()
	{
		return texture.getId();
	}
}
