package me.jho5245.mario.renderer;

import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL14.GL_DEPTH_COMPONENT32;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.opengl.GL30.GL_FRAMEBUFFER_COMPLETE;

public class PickingTexture
{
	private int pickingTextureID;

	private int fboID;

	private int depthTexture;

	public PickingTexture(int width, int height)
	{
		if (!init(width, height))
		{
			assert false: "error init picking texture";
		}
	}

	public boolean init(int width, int height)
	{
		// generate frame buffer
		fboID = glGenFramebuffers();
		glBindFramebuffer(GL_FRAMEBUFFER, fboID);

		// create texture to render the data to & attach it to framebuffer
		pickingTextureID = glGenTextures();
		glBindTexture(GL_TEXTURE_2D, pickingTextureID);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB32F, width, height, 0, GL_RGB, GL_FLOAT, 0);
		glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, pickingTextureID, 0);

		// Create texture object for the depth buffer
		glEnable(GL_TEXTURE_2D);
		depthTexture = glGenTextures();
		glBindTexture(GL_TEXTURE_2D, depthTexture);
		glTexImage2D(GL_TEXTURE_2D, 0, GL_DEPTH_COMPONENT, width, height, 0, GL_DEPTH_COMPONENT, GL_FLOAT, 0);
		glFramebufferTexture2D(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, GL_TEXTURE_2D, depthTexture, 0);

		// disable the reading
		glReadBuffer(GL_NONE);
		glDrawBuffer(GL_COLOR_ATTACHMENT0);

		if (glCheckFramebufferStatus(GL_FRAMEBUFFER) != GL_FRAMEBUFFER_COMPLETE)
		{
			assert false : "error: frame buffer is not complete.";
			return false;
		}

		// unbind the texture
		glBindTexture(GL_TEXTURE_2D, 0);
		glBindFramebuffer(GL_FRAMEBUFFER, 0);
		return true;
	}

	public void enableWriting()
	{
		glBindFramebuffer(GL_FRAMEBUFFER, fboID);
	}

	public void disableWriting()
	{
		glBindFramebuffer(GL_FRAMEBUFFER, 0);
	}

	public int readPixel(int x, int y)
	{
		glBindFramebuffer(GL_READ_FRAMEBUFFER, fboID);
		glReadBuffer(GL_COLOR_ATTACHMENT0);

		float[] pixels = new float[3];
		glReadPixels(x, y, 1, 1, GL_RGB, GL_FLOAT, pixels);

		return (int) (pixels[0]) - 1;
	}
}
