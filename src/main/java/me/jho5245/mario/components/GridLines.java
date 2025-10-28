package me.jho5245.mario.components;

import me.jho5245.mario.jade.Camera;
import me.jho5245.mario.jade.Window;
import me.jho5245.mario.renderer.DebugDraw;
import me.jho5245.mario.util.Settings;
import org.joml.Vector2f;
import org.joml.Vector3f;

public class GridLines extends Component
{
	@Override
	public void update(float dt)
	{
		Camera camera = Window.getCurrentScene().getCamera();
		Vector2f cameraPos = camera.getPosition();
		Vector2f projectionSize = camera.getProjectionSize();

		int firstX = (int) ((cameraPos.x / Settings.GRID_WIDTH - 1) * Settings.GRID_WIDTH);
		int firstY = (int) ((cameraPos.y / Settings.GRID_HEIGHT - 1) * Settings.GRID_HEIGHT);

		int numVerticalLines = (int) (projectionSize.x * camera.getZoom() / Settings.GRID_WIDTH) + 2;
		int numHorizontalLines = (int) (projectionSize.y * camera.getZoom() / Settings.GRID_HEIGHT) + 2;

		int height = (int) ((projectionSize.y * camera.getZoom()) + Settings.GRID_HEIGHT * 2);
		int width = (int) ((projectionSize.x * camera.getZoom()) + Settings.GRID_WIDTH * 2);

		Vector3f color = new Vector3f(0.8f, 0.8f, 0.8f);

		int maxLines = Math.max(numHorizontalLines, numVerticalLines);
		for (int i = 0; i < maxLines; i++)
		{
			float x = firstX + (Settings.GRID_WIDTH * i);
			float y = firstY + (Settings.GRID_HEIGHT * i);

			if (i < numVerticalLines)
			{
				DebugDraw.addLine2D(new Vector2f(x, firstY), new Vector2f(x, firstY + height), color);
			}

			if (i < numHorizontalLines)
			{
				DebugDraw.addLine2D(new Vector2f(firstX, y), new Vector2f(firstX + width, y), color);
			}
		}
	}
}
