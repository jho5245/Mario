package me.jho5245.mario.renderer;

import me.jho5245.mario.jade.GameObject;
import me.jho5245.mario.jade.Window;
import me.jho5245.mario.components.SpriteRenderer;
import me.jho5245.mario.util.AssetPool;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.lang.ProcessBuilder.Redirect;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

public class RenderBatch implements Comparable<RenderBatch>
{
	// Vertex
	// ===================
	// Pos								Color														tex coords			tex id
	// float, float, 			float, float, float, float			float, float		float
	private final int POS_SIZE = 2;

	private final int COLOR_SIZE = 4;

	private final int TEX_COORDS_SIZE = 2;

	private final int TEX_ID_SIZE = 1;

	private final int ENTITY_ID_SIZE = 1;

	private final int POS_OFFSET = 0;

	private final int COLOR_OFFSET = POS_OFFSET + POS_SIZE * Float.BYTES;

	private final int TEX_COORDS_OFFSET = COLOR_OFFSET + COLOR_SIZE * Float.BYTES;

	private final int TEX_ID_OFFSET = TEX_COORDS_OFFSET + TEX_COORDS_SIZE * Float.BYTES;

	private final int ENTITY_ID_OFFSET = TEX_ID_OFFSET + TEX_ID_SIZE * Float.BYTES;

	private final int VERTEX_SIZE = 10;

	private final int VERTEX_SIZE_BYTES = VERTEX_SIZE * Float.BYTES;

	private SpriteRenderer[] sprites;

	private int numSprites;

	private boolean hasRoom;

	private float[] vertices;

	private int[] texSlots = {
			0,
			1,
			2,
			3,
			4,
			5,
			6,
			7
	};

	private List<Texture> textures;

	private int vaoID, vboID;

	private int maxBatchSize;

	private int zIndex;

	private Renderer renderer;

	public RenderBatch(int maxBatchSize, int zIndex, Renderer renderer)
	{
		this.renderer = renderer;
		this.zIndex = zIndex;
		this.sprites = new SpriteRenderer[maxBatchSize];
		this.maxBatchSize = maxBatchSize;

		// 4 vertices quads
		vertices = new float[maxBatchSize * 4 * VERTEX_SIZE];

		this.numSprites = 0;
		this.hasRoom = true;
		this.textures = new ArrayList<>();
	}

	public void start()
	{
		// Generate & bind a VAO
		vaoID = glGenVertexArrays();
		glBindVertexArray(vaoID);

		// Allocate space for vertices
		vboID = glGenBuffers();
		glBindBuffer(GL_ARRAY_BUFFER, vboID);
		glBufferData(GL_ARRAY_BUFFER, (long) vertices.length * Float.BYTES, GL_STATIC_DRAW);

		// Create & upload indices buffer
		int ebo = glGenBuffers();
		int[] indices = generateIndices();
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ebo);
		glBufferData(GL_ELEMENT_ARRAY_BUFFER, indices, GL_STATIC_DRAW);

		// Enable the buffer attribute pointers
		glVertexAttribPointer(0, POS_SIZE, GL_FLOAT, false, VERTEX_SIZE_BYTES, POS_OFFSET);
		glEnableVertexAttribArray(0);
		glVertexAttribPointer(1, COLOR_SIZE, GL_FLOAT, false, VERTEX_SIZE_BYTES, COLOR_OFFSET);
		glEnableVertexAttribArray(1);
		glVertexAttribPointer(2, TEX_COORDS_SIZE, GL_FLOAT, false, VERTEX_SIZE_BYTES, TEX_COORDS_OFFSET);
		glEnableVertexAttribArray(2);
		glVertexAttribPointer(3, TEX_ID_SIZE, GL_FLOAT, false, VERTEX_SIZE_BYTES, TEX_ID_OFFSET);
		glEnableVertexAttribArray(3);
		glVertexAttribPointer(4, ENTITY_ID_SIZE, GL_FLOAT, false, VERTEX_SIZE_BYTES, ENTITY_ID_OFFSET);
		glEnableVertexAttribArray(4);

	}

	public boolean destroyIfExists(GameObject gameObject)
	{
		SpriteRenderer sprite = gameObject.getComponent(SpriteRenderer.class);
		for (int i = 0; i < numSprites; i++)
		{
			if (sprites[i] == sprite)
			{
				// 스프라이트 제거 후 정렬
				for (int j = i; j < numSprites - 1; j++)
				{
					sprites[j] = sprites[j + 1];
					sprites[j].setDirty();
				}
				numSprites--;
				return true;
			}
		}
		return false;
	}

	public void render()
	{
		boolean rebufferData = false;
		for (int i = 0; i < numSprites; i++)
		{
			SpriteRenderer sprite = sprites[i];
			if (sprite.isDirty())
			{
				loadVertexProperties(i);
				sprite.setClean();
				rebufferData = true;
			}

			if (sprite.gameObject.transform.zIndex != this.zIndex)
			{
				destroyIfExists(sprite.gameObject);
				renderer.add(sprite.gameObject);
				i--;
			}
		}
		// rebuffer data every frame
		if (rebufferData)
		{
			glBindBuffer(GL_ARRAY_BUFFER, vboID);
			glBufferSubData(GL_ARRAY_BUFFER, 0, vertices);
		}

		// use shader
		Shader shader = Renderer.getBoundSHader();
		shader.use();
		shader.uploadMat4f("uProjection", Window.getCurrentScene().getCamera().getProjectionMatrix());
		shader.uploadMat4f("uView", Window.getCurrentScene().getCamera().getViewMatrix());
		for (int i = 0; i < textures.size(); i++)
		{
			glActiveTexture(GL_TEXTURE0 + i + 1); // 첫 번째 요소는 0이기 때문에 1을 더하고 바인딩... REASON 1
			textures.get(i).bind();
		}
		shader.uploadIntArray("uTextures", texSlots);

		glBindVertexArray(vaoID);
		glEnableVertexAttribArray(0);
		glEnableVertexAttribArray(1);

		glDrawElements(GL_TRIANGLES, numSprites * 6, GL_UNSIGNED_INT, 0); // indices = offset

		glDisableVertexAttribArray(0);
		glDisableVertexAttribArray(1);
		glBindVertexArray(0);

		for (Texture texture : textures)
		{
			texture.unbind();
		}
		shader.detach();
	}

	public void addSprite(SpriteRenderer sprite)
	{
		// get index & add renderObject
		int index = this.numSprites;
		this.sprites[index] = sprite;
		this.numSprites++;
		sprite.setDirty();

		if (sprite.getTexture() != null)
		{
			if (!textures.contains(sprite.getTexture()))
			{
				textures.add(sprite.getTexture());
			}
		}

		// add properties to local vertices array
		loadVertexProperties(index);

		if (numSprites >= maxBatchSize)
		{
			this.hasRoom = false;
		}
	}

	private void loadVertexProperties(int index)
	{
		SpriteRenderer sprite = this.sprites[index];

		// find offset within array (4 vertices per sprite)
		int offset = index * 4 * VERTEX_SIZE;
		// float float		float float float float		float

		Vector4f color = sprite.getColor();
		Vector2f[] texCoords = sprite.getTexCoords();

		int texId = 0;
		// [0, tex, tex, tex, text, ...] <- 0부터 시작해서 texId = i + 1; 을 해줌... REASON 1
		if (sprite.getTexture() != null)
		{
			for (int i = 0; i < textures.size(); i++)
			{
				if (textures.get(i).equals(sprite.getTexture()))
				{
					texId = i + 1; // <- 1을 더하는 이유... WHY 1
					break;
				}
			}
		}

		float rotation = sprite.getGameObject().getTransform().getRotation();
		boolean isRotated = rotation != 0f;
		Matrix4f transformMatrfix = new Matrix4f().identity();
		if (isRotated)
		{
			Vector2f position = sprite.getGameObject().getTransform().getPosition();
			Vector2f scale = sprite.getGameObject().getTransform().getScale();
			transformMatrfix.translate(position.x, position.y, 0);
			transformMatrfix.rotate((float) Math.toRadians(rotation), 0, 0, 1);
			transformMatrfix.scale(scale.x, scale.y, 0);
		}

		// add vertices with the appropriate properties
		// *		*
		// *		*
		float xAdd = 0.5f;
		float yAdd = 0.5f;
		for (int i = 0; i < 4; i++)
		{
			if (i == 1)
			{
				yAdd = -0.5f;
			}
			else if (i == 2)
			{
				xAdd = -0.5f;
			}
			else if (i == 3)
			{
				yAdd = 0.5f;
			}

			// transform
			Vector4f currentPosition = new Vector4f(
					sprite.getGameObject().getTransform().getPosition().x + (xAdd * sprite.getGameObject().getTransform().getScale().x),
					sprite.getGameObject().getTransform().getPosition().y + (yAdd * sprite.getGameObject().getTransform().getScale().y), 0, 1);
			if (isRotated)
			{
				currentPosition = new Vector4f(xAdd, yAdd, 0, 1).mul(transformMatrfix);
			}

			// load position
			vertices[offset] = currentPosition.x;
			vertices[offset + 1] = currentPosition.y;

			// load color
			vertices[offset + 2] = color.x;
			vertices[offset + 3] = color.y;
			vertices[offset + 4] = color.z;
			vertices[offset + 5] = color.w;

			// load texture coords
			vertices[offset + 6] = texCoords[i].x;
			vertices[offset + 7] = texCoords[i].y;

			// load texture id
			vertices[offset + 8] = texId;

			// load entity id
			vertices[offset + 9] = sprite.getGameObject().getUid() + 1;

			offset += VERTEX_SIZE;
		}
	}

	private int[] generateIndices()
	{
		// 6 indices per quad (3 per triangle)
		int[] elements = new int[6 * maxBatchSize];
		for (int i = 0; i < maxBatchSize; i++)
		{
			loadElementIndices(elements, i);
		}
		return elements;
	}

	private void loadElementIndices(int[] elements, int index)
	{
		int offsetArrayIndex = 6 * index;
		int offset = 4 * index;

		// 3, 2, 0, 0, 2, 1		7, 6, 4, 4, 6, 5
		// Triangle 1
		elements[offsetArrayIndex + 0] = offset + 3;
		elements[offsetArrayIndex + 1] = offset + 2;
		elements[offsetArrayIndex + 2] = offset + 0;

		// Triangle 2
		elements[offsetArrayIndex + 3] = offset + 0;
		elements[offsetArrayIndex + 4] = offset + 2;
		elements[offsetArrayIndex + 5] = offset + 1;
	}

	public boolean hasRoom()
	{
		return this.hasRoom;
	}

	public boolean hasTextureRoom()
	{
		return this.textures.size() < 8;
	}

	public boolean hasTexture(Texture texture)
	{
		return this.textures.contains(texture);
	}

	public int zIndex()
	{
		return this.zIndex;
	}

	@Override
	public int compareTo(RenderBatch o)
	{
		return Integer.compare(this.zIndex, o.zIndex);
	}
}
