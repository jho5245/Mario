package me.jho5245.mario.renderer;

import me.jho5245.mario.jade.Window;
import me.jho5245.mario.jade.components.SpriteRenderer;
import me.jho5245.mario.util.AssetPool;
import org.joml.Vector4f;

import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

public class RenderBatch
{
	// Vertex
	// ===================
	// Pos								Color
	// float, float, 			float, float, float, float
	private final int POS_SIZE = 2;
	private final int	COLOR_SIZE = 4;

	private final int POS_OFFSET = 0;
	private final int COLOR_OFFSET = POS_OFFSET + POS_SIZE * Float.BYTES;
	private final int VERTEX_SIZE = 6;
	private final int VERTEX_SIZE_BYTES = VERTEX_SIZE * Float.BYTES;

	private SpriteRenderer[] sprites;
	private int numSprites;
	private boolean hasRoom;
	private float[] vertices;

	private int vaoID, vboID;
	private int maxBatchSize;
	private Shader shader;

	public RenderBatch(int maxBatchSize)
	{
		shader = AssetPool.getShader("assets/shaders/default.glsl");
		this.sprites = new SpriteRenderer[maxBatchSize];
		this.maxBatchSize = maxBatchSize;

		// 4 vertices quads
		vertices = new float[maxBatchSize * 4 * VERTEX_SIZE];

		this.numSprites = 0;
		this.hasRoom = true;
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
	}

	public void render()
	{
		// rebuffer all data every frame
		glBindBuffer(GL_ARRAY_BUFFER, vboID);
		glBufferSubData(GL_ARRAY_BUFFER, 0, vertices);

		// use shader
		shader.use();
		shader.uploadMat4f("uProjection", Window.getCurrentScene().getCamera().getProjectionMatrix());
		shader.uploadMat4f("uView", Window.getCurrentScene().getCamera().getViewMatrix());

		glBindVertexArray(vaoID);
		glEnableVertexAttribArray(0);
		glEnableVertexAttribArray(1);

		glDrawElements(GL_TRIANGLES, numSprites * 6, GL_UNSIGNED_INT, 0); // indices = offset

		glDisableVertexAttribArray(0);
		glDisableVertexAttribArray(1);
		glBindVertexArray(0);

		shader.detach();
	}

	public void addSprite(SpriteRenderer sprite)
	{
		// get index & add renderObject
		int index = this.numSprites;
		this.sprites[index] = sprite;
		this.numSprites++;

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

		// add vertices with the appropriate properties

		// *		*
		// *		*

		float xAdd = 1f;
		float yAdd = 1f;
		for (int i = 0; i < 4; i++)
		{
			if (i == 1)
			{
				yAdd = 0f;
			}
			else if (i == 2)
			{
				xAdd = 0f;
			}
			else if (i == 3)
			{
				yAdd = 1f;
			}

			// load position
			vertices[offset] = sprite.getGameObject().getTransform().getPosition().x + (xAdd * sprite.getGameObject().getTransform().getScale().x);
			vertices[offset + 1] = sprite.getGameObject().getTransform().getPosition().y + (yAdd * sprite.getGameObject().getTransform().getScale().y);

			// load color
			vertices[offset + 2] = color.x;
			vertices[offset + 3] = color.y;
			vertices[offset + 4] = color.z;
			vertices[offset + 5] = color.w;

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
}
