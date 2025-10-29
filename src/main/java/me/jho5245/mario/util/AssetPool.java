package me.jho5245.mario.util;

import me.jho5245.mario.components.SpriteSheet;
import me.jho5245.mario.renderer.Shader;
import me.jho5245.mario.renderer.Texture;
import me.jho5245.mario.sounds.Sound;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class AssetPool
{
	private static final Map<String, Shader> shaders = new HashMap<>();
	private static final Map<String, Texture> textures = new HashMap<>();
	private static final Map<String, SpriteSheet> spritesheets = new HashMap<>();
	private static final Map<String, Sound> sounds = new HashMap<>();

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
			Texture texture = new Texture();
			texture.init(filePath);
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

	public static void addSpriteSheet(String resourceName, int spriteWidth, int spriteHeight, int numSprites, int spacing)
	{
		addSpriteSheet(resourceName, new SpriteSheet(AssetPool.getTexture(resourceName), spriteWidth, spriteHeight, numSprites, spacing));
	}

	public static SpriteSheet getSpriteSheet(String resourceName)
	{
		File file = new File(resourceName);
		if (!spritesheets.containsKey(file.getAbsolutePath()))
		{
			assert false : "No SpriteSheet %s to access".formatted(resourceName);
		}
		return spritesheets.getOrDefault(file.getAbsolutePath(), null);
	}

	public static void reset()
	{
		AssetPool.spritesheets.clear();
		AssetPool.textures.clear();
		AssetPool.shaders.clear();
	}

	public static Sound getSound(String soundFile)
	{
		File file = new File(soundFile);
		if (sounds.containsKey(file.getAbsolutePath()))
		{
			return sounds.get(file.getAbsolutePath());
		}
		else
		{
			assert  false : "No Sound %s to access".formatted(soundFile);
		}
		return null;
	}

	public static Collection<Sound> getAllSounds()
	{
		return sounds.values();
	}

	public static Sound addSound(String soundFile, boolean loop)
	{
		File file = new File(soundFile);
		if (sounds.containsKey(file.getAbsolutePath()))
		{
			return sounds.get(file.getAbsolutePath());
		}
		else
		{
			Sound sound = new Sound(file.getAbsolutePath(), loop);
			AssetPool.sounds.put(file.getAbsolutePath(), sound);
			return sound;
		}
	}
}
