package me.jho5245.mario.components.block;

import me.jho5245.mario.components.Component;
import me.jho5245.mario.components.PlayerController;
import me.jho5245.mario.jade.GameObject;
import me.jho5245.mario.jade.KeyListener;
import me.jho5245.mario.jade.PipeDirection;
import me.jho5245.mario.jade.Window;
import me.jho5245.mario.sounds.Sound;
import me.jho5245.mario.util.AssetPool;
import org.jbox2d.dynamics.contacts.Contact;
import org.joml.Vector2f;

import static org.lwjgl.glfw.GLFW.*;

public class Pipe extends Component
{
	private PipeDirection direction;

	private String connectingPipeName = "";

	@SuppressWarnings("unused")
	private boolean isEntrance;
	private boolean toggleUnderground;

	private transient GameObject connectingPipeGameObject;
	private transient Pipe connectingPipe;
	transient final float entranceVectorTolerance = 0.6f;
	private transient GameObject playerGameObject;
	private transient PlayerController playerController;
	// 파이프 사용 애니메이션 재생 시간
	private transient float pipeUseAnimationTimeLeft;
	private transient final float pipeUseAnimationTime = 2f;

	private transient Sound pipeSound;

	private transient boolean pipeEntering;

	// 파이프 나올 때 1번만 소리 재생
	private transient boolean pipeExited;

	public Pipe(PipeDirection direction)
	{
		this.direction = direction;
	}

	@Override
	public void start()
	{
		connectingPipeGameObject = Window.getCurrentScene().getGameObject(connectingPipeName);
		if (connectingPipeGameObject != null)
		{
			connectingPipe = connectingPipeGameObject.getComponent(Pipe.class);
		}
		pipeSound = AssetPool.getSound("assets/sounds/pipe.ogg");
	}

	@Override
	public void update(float dt)
	{
		if (connectingPipeGameObject == null || connectingPipe == null || playerController == null || playerGameObject == null)
		{
			return;
		}

		if (!isEntrance)
		{
			return;
		}

		if (!pipeEntering)
		{
			switch (direction)
			{
				case LEFT ->
				{
					if (KeyListener.isKeyPressed(GLFW_KEY_LEFT))
					{
						pipeEntering = true;
					}
				}
				case RIGHT ->
				{
					if (KeyListener.isKeyPressed(GLFW_KEY_RIGHT))
					{
						pipeEntering = true;
					}
				}
				case UP ->
				{
					if (KeyListener.isKeyPressed(GLFW_KEY_UP))
					{
						pipeEntering = true;
					}
				}
				case DOWN ->
				{
					if (KeyListener.isKeyPressed(GLFW_KEY_DOWN))
					{
						pipeEntering = true;
					}
				}
			}
			return;
		}

		// 최초 1회 소리 재생
		if (pipeUseAnimationTimeLeft == pipeUseAnimationTime)
		{
			pipeSound.play();
			// 파이프 사용하는 도중 앉기 방지
			playerController.preventSittingFor(pipeUseAnimationTime);
		}
		Window.getPhysics().setPlaying(false);
		pipeUseAnimationTimeLeft -= dt;

		playerController.getStateMachine().trigger("stopJumping");
		playerController.getStateMachine().trigger("stopRunning");
		playerGameObject.transform.zIndex = -100;

		Vector2f position;

		// 파이프 나오기 시작할 때 1번만 소리 재생
		if (pipeUseAnimationTimeLeft <= pipeUseAnimationTime / 2)
		{
			position = enteringPipe(connectingPipe.direction.inverse());
			if (!pipeExited)
			{
				pipeExited = true;
				pipeSound.play();
				if (toggleUnderground)
				{
					playerController.setUndergrond(!playerController.isUndergrond());
					if (playerController.isUndergrond())
					{
						System.out.println("hi");
						playerController.backgroundMusic.stop();
						playerController.undergroundMusic.play();
					}
					else
					{
						playerController.undergroundMusic.stop();
						playerController.backgroundMusic.play();
					}
				}
			}
		}
		else
		{
			position = enteringPipe(this.direction);
		}
		playerController.setPosition(position);

		// 파이프 다 빠져나옴
		if (pipeUseAnimationTimeLeft < 0)
		{
			pipeUseAnimationTimeLeft = 0;
			pipeEntering = false;
			pipeExited = false;
			Window.getPhysics().setPlaying(true);
			playerController.velocity.set(0f);
			playerGameObject.transform.zIndex = playerController.getStartZIndex();
			playerController.setStopSittingTimeLeft(0);
			playerGameObject = null;
			playerController = null;
		}
	}

	private Vector2f enteringPipe(PipeDirection direction)
	{
		Vector2f enterPipePosition = gameObject.transform.position;
		Vector2f enterPipeScale = gameObject.transform.scale;
		Vector2f exitPipePosition = connectingPipeGameObject.transform.position;
		Vector2f exitPipeScale = connectingPipeGameObject.transform.scale;
		Vector2f playerScale = new Vector2f(playerGameObject.transform.scale).absolute();

		return switch (direction)
		{
			case DOWN ->
			{
				if (pipeUseAnimationTimeLeft >= pipeUseAnimationTime / 2)
					yield new Vector2f(enterPipePosition).add(0, enterPipeScale.y * (pipeUseAnimationTimeLeft - 1.2f));
				else
					yield new Vector2f(exitPipePosition).add(0, exitPipeScale.y * (pipeUseAnimationTimeLeft - 0.8f));
			}
			case LEFT ->
			{
				if (pipeUseAnimationTimeLeft >= pipeUseAnimationTime / 2)
					yield new Vector2f(enterPipePosition).add(enterPipeScale.x * (pipeUseAnimationTimeLeft - 1.2f), playerScale.y == 1f ? enterPipeScale.y / -8 : 0);
				else
					yield new Vector2f(exitPipePosition).add(exitPipeScale.x * (pipeUseAnimationTimeLeft - 0.8f), playerScale.y == 1f ? enterPipeScale.y / -8 : 0);
			}
			case RIGHT ->
			{
				if (pipeUseAnimationTimeLeft >= pipeUseAnimationTime / 2)
					yield new Vector2f(enterPipePosition).add(enterPipeScale.x * (1.2f - pipeUseAnimationTimeLeft), playerScale.y == 1f ? enterPipeScale.y / -8 : 0);
				else
					yield new Vector2f(exitPipePosition).add(exitPipeScale.x * (0.8f - pipeUseAnimationTimeLeft), playerScale.y == 1f ? enterPipeScale.y / -8 : 0);
			}
			case UP ->
			{
				if (pipeUseAnimationTimeLeft >= pipeUseAnimationTime / 2)
					yield new Vector2f(enterPipePosition).add(0, enterPipeScale.y * (1.2f - pipeUseAnimationTimeLeft));
				else
					yield new Vector2f(exitPipePosition).add(0, exitPipeScale.y * (0.8f - pipeUseAnimationTimeLeft));
			}
		};
	}

	@Override
	public void beginCollision(GameObject object, Contact contact, Vector2f contactNormal)
	{
		if (pipeEntering)
			return;
		PlayerController playerController = object.getComponent(PlayerController.class);
		if (playerController != null)
		{
			float x = contactNormal.x, y = contactNormal.y;
			Vector2f pipePosition = gameObject.transform.position;
			Vector2f pipeScale = gameObject.transform.scale;
			Vector2f playerPosition = object.transform.position;
			Vector2f playerScale = new Vector2f(object.transform.scale).absolute();
			switch (direction)
			{
				case DOWN ->
				{
					if (y < entranceVectorTolerance || playerPosition.x < pipePosition.x + pipeScale.x * 0.1f - playerScale.x
							|| playerPosition.x > pipePosition.x + pipeScale.x * 0.75f + playerScale.x)
					{
						return;
					}
				}
				case LEFT ->
				{
					if (x < entranceVectorTolerance || playerPosition.y > pipePosition.y + pipeScale.y * 0.8f || playerPosition.y < pipePosition.y - pipeScale.y * 0.4f)
					{
						return;
					}
				}
				case UP ->
				{
					if (y > -entranceVectorTolerance)
					{
						return;
					}
				}
				case RIGHT ->
				{
					if (x > -entranceVectorTolerance || playerPosition.y > pipePosition.y + pipeScale.y * 0.8f || playerPosition.y < pipePosition.y - pipeScale.y * 0.4f)
					{
						return;
					}
				}
			}
			this.playerGameObject = object;
			this.playerController = playerController;
			this.pipeUseAnimationTimeLeft = this.pipeUseAnimationTime;
		}
	}

	@Override
	public void endCollision(GameObject object, Contact contact, Vector2f contactNormal)
	{
		if (pipeEntering)
			return;
		PlayerController playerController = object.getComponent(PlayerController.class);
		if (playerController != null)
		{
			this.playerGameObject = null;
			this.playerController = null;
		}
	}
}
