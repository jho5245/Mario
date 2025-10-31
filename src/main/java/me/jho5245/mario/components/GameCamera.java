package me.jho5245.mario.components;

import me.jho5245.mario.jade.Camera;
import me.jho5245.mario.jade.GameObject;
import me.jho5245.mario.jade.Window;
import me.jho5245.mario.util.Settings;
import org.joml.Vector4f;

import java.util.Optional;

public class GameCamera extends Component
{
	private transient GameObject player;
	private transient Camera camera;
	private transient float highestX = Float.MIN_VALUE;
	private transient float undergroundYLevel;
	private transient float cameraBuffer = Settings.GRID_WIDTH * 6;
	private transient float playerBuffer = Settings.GRID_HEIGHT;

	private Vector4f skyColor = new Vector4f(92f / 255f, 148f / 255f, 252f / 255f, 1f);
	private Vector4f undergroundColor = new Vector4f();

	private boolean canGoBack = false;

	private transient PlayerController playerController;

	public GameCamera(Camera camera)
	{
		this.camera = camera;
	}

	@Override
	public void start()
	{
		this.player = Window.getCurrentScene().getGameObjectWith(PlayerController.class);
		this.playerController = Optional.ofNullable(player).orElse(new GameObject("what")).getComponent(PlayerController.class);
		this.camera.clearColor.set(skyColor);
		this.undergroundYLevel = this.camera.getPosition().y - this.camera.getProjectionSize().y - this.cameraBuffer;
	}

	@Override
	public void update(float dt)
	{
		if (player == null)
			return;
		if (playerController.isDead() || playerController.hasWon())
		{
			return;
		}
		camera.getPosition().x = Math.max(player.transform.position.x - 5f, highestX);
		if (!canGoBack)
		{
			highestX = Math.max(camera.getPosition().x, highestX);
		}

		if (playerController.isUndergrond())
		{
			this.camera.getPosition().y = undergroundYLevel;
			this.camera.clearColor.set(undergroundColor);
		}
		else
		{
			this.camera.getPosition().y = 0f;
			this.camera.clearColor.set(skyColor);
		}
	}
}
