package me.jho5245.mario.util;

import me.jho5245.mario.jade.components.SpriteSheet;
import me.jho5245.mario.renderer.Shader;
import me.jho5245.mario.renderer.Texture;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class AssetPool
{
	private static final Map<String, Shader> shaders = new HashMap<>();

	private static final Map<String, Texture> textures = new HashMap<>();

	private static final Map<String, SpriteSheet> spritesheets = new HashMap<>();

	public static Shader getShader(String filePath)
	{
		File file = new File(filePath);
		String absolutePath = file.getAbsolutePath();
		if (shaders.containsKey(absolutePath))
		{
			return shaders.get(absolutePath);
		}
		// if not exists, create a new one
		else
		{
			Shader shader = new Shader(filePath);
			shader.compile();
			shaders.put(absolutePath, shader);
			return shader;
		}
	}

	public static Texture getTexture(String filePath)
	{
		File file = new File(filePath);
		String absolutePath = file.getAbsolutePath();
		if (textures.containsKey(absolutePath))
		{
			return textures.get(absolutePath);
		}
		else
		{
			Texture texture = new Texture(filePath);
			textures.put(absolutePath, texture);
			return texture;
		}
	}

	public static void addSpriteSheet(String resourceName, SpriteSheet spriteSheet)
	{
		File file = new File(resourceName);
		if (!spritesheets.containsKey(file.getAbsolutePath()))
		{
			spritesheets.put(file.getAbsolutePath(), spriteSheet);
		}
	}

	public static SpriteSheet getSpriteSheet(String resourceName)
	{
		File file = new File(resourceName);
		if (!spritesheets.containsKey(file.getAbsolutePath()))
		{
			assert false: "No SpriteSheet %s to access".formatted(resourceName);
		}
		return spritesheets.getOrDefault(file.getAbsolutePath(), null);
	}
}
