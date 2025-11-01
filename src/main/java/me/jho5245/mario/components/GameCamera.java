package me.jho5245.mario.components;

import me.jho5245.mario.jade.Camera;
import me.jho5245.mario.jade.GameObject;
import me.jho5245.mario.jade.Window;
import me.jho5245.mario.util.Settings;
import org.joml.Vector2f;
import org.joml.Vector4f;

import java.util.Optional;

public class GameCamera extends Component
{
	private transient GameObject player;
	private transient Camera camera;
	private transient float lowestX = -0.5f, lowestY = -0.5f, highestY = 25.5f, undergroundLowestY = -42.5f, undergroundHighestY = -42.5f;
	private transient final float undergroundYLevel = -42.5f;
	private transient float cameraBuffer = Settings.GRID_WIDTH * 6;
	private transient float playerBuffer = Settings.GRID_HEIGHT;

	private Vector4f skyColor = new Vector4f(92f / 255f, 148f / 255f, 252f / 255f, 1f);
	private Vector4f undergroundColor = new Vector4f();

	private boolean canGoBack = true;

	private transient PlayerController playerController;

	private transient float xOffset;

	private transient float yOffset;

	public transient final float startLerpDt = 0.05f;
	public transient float lerpDt = startLerpDt;

	public GameCamera(Camera camera, float xOffset, float yOffset)
	{
		this.camera = camera;
		this.xOffset = xOffset;
		this.yOffset = yOffset;
	}

	@Override
	public void start()
	{
		this.player = Window.getCurrentScene().getGameObjectWith(PlayerController.class);
		this.playerController = Optional.ofNullable(player).orElse(new GameObject("what")).getComponent(PlayerController.class);
		this.camera.clearColor.set(skyColor);
		camera.getPosition().set(new Vector2f(-0.5f, -0.5f));
	}

	private void limitCameraPosition()
	{
		if (xOffset < lowestX)
		{
			xOffset = lowestX;
		}
		if (playerController.isUndergrond())
		{
			if (yOffset < undergroundLowestY)
			{
				yOffset = undergroundLowestY;
			}
			else if (yOffset > undergroundHighestY)
			{
				yOffset = undergroundHighestY;
			}
		}
		else
		{
			if (yOffset < lowestY)
			{
				yOffset = lowestY;
			}
			else if (yOffset > highestY)
			{
				yOffset = highestY;
			}
		}
	}

	private void move(float xAmount, float yAmount)
	{
		xOffset += xAmount;
		yOffset += yAmount;
		limitCameraPosition();
	}

	private void centerToPlayer(GameObject player)
	{
		Vector2f position = player.transform.position;
		//		Vector2f scale = player.transform.scale;
		xOffset = position.x - camera.getProjectionSize().x / 2;// + scale.x  / 2;
		yOffset = position.y - camera.getProjectionSize().y / 2;// + scale.y  / 2;
		limitCameraPosition();
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

		// 플레이어 골인 : 카메라 움직임 정지
		if (playerController.isGoaled())
		{
			return;
		}

		centerToPlayer(player);
		//		camera.getPosition().x = Math.max(player.transform.position.x - 5f, highestX);
		camera.getPosition().lerp(new Vector2f(xOffset, yOffset), lerpDt);
		if (!canGoBack)
		{
			lowestX = Math.max(camera.getPosition().x, lowestX);
		}

		if (playerController.isUndergrond())
		{
			//			this.camera.getPosition().y = undergroundYLevel;
			this.camera.clearColor.set(undergroundColor);
		}
		else
		{
			//			this.camera.getPosition().y = 0.5f;
			this.camera.clearColor.set(skyColor);
		}
	}

	public float getxOffset()
	{
		return xOffset;
	}

	public void setxOffset(float xOffset)
	{
		this.xOffset = xOffset;
	}

	public float getyOffset()
	{
		return yOffset;
	}

	public void setyOffset(float yOffset)
	{
		this.yOffset = yOffset;
	}
}
