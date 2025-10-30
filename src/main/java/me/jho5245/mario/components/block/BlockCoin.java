package me.jho5245.mario.components.block;

import me.jho5245.mario.components.Component;
import me.jho5245.mario.util.AssetPool;
import me.jho5245.mario.util.Settings;
import org.joml.Vector2f;

public class BlockCoin extends Component
{
	private Vector2f topY;
	private float coinSpeed = 5.6f;

	@Override
	public void start()
	{
		topY = new Vector2f(this.gameObject.transform.position.y).add(0, Settings.GRID_HEIGHT * 2);
		AssetPool.getSound("assets/sounds/coin.ogg").play();
	}

	@Override
	public void update(float dt)
	{
		if (this.gameObject.transform.position.y < topY.y)
		{
			this.gameObject.transform.position.y += dt * coinSpeed;
			this.gameObject.transform.scale.x -= (Settings.GRID_WIDTH * 2 * dt) % -1f;
		}
		else
		{
			gameObject.destroy();
		}
	}
}
