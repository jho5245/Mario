package me.jho5245.mario.scenes;

public abstract class SceneInitializer
{
	public abstract void init(Scene scene);

	public abstract void loadResources(Scene scene);

	public abstract void imgui();
}