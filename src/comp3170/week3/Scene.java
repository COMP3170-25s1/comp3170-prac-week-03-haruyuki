package comp3170.week3;

import static comp3170.Math.TAU;
import static org.lwjgl.opengl.GL11.GL_FILL;
import static org.lwjgl.opengl.GL11.GL_FRONT_AND_BACK;
import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_INT;
import static org.lwjgl.opengl.GL15.GL_ELEMENT_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL11.glDrawElements;
import static org.lwjgl.opengl.GL11.glPolygonMode;
import static org.lwjgl.opengl.GL15.glBindBuffer;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import comp3170.GLBuffers;
import comp3170.Shader;
import comp3170.ShaderLibrary;

public class Scene {

    private final int vertexBuffer;
	private final int[] indices;
	private final int indexBuffer;
    private final int colourBuffer;
	private final Matrix4f translationMatrix = new Matrix4f();
	private final Matrix4f rotationMatrix = new Matrix4f();
	private final Matrix4f scaleMatrix = new Matrix4f();
    private final Matrix4f matrixModel;
	private static final float MOVEMENT_SPEED = 5f;

    private final Shader shader;

	public Scene() {

        String VERTEX_SHADER = "vertex.glsl";
        String FRAGMENT_SHADER = "fragment.glsl";
        shader = ShaderLibrary.instance.compileShader(VERTEX_SHADER, FRAGMENT_SHADER);

		// @formatter:off
			//          (0,1)
			//           /|\
			//          / | \
			//         /  |  \
			//        / (0,0) \
			//       /   / \   \
			//      /  /     \  \
			//     / /         \ \		
			//    //             \\
			//(-1,-1)           (1,-1)
			//
	 		
		Vector4f[] vertices = new Vector4f[] {
			new Vector4f( 0, 0, 0, 1),
			new Vector4f( 0, 1, 0, 1),
			new Vector4f(-1,-1, 0, 1),
			new Vector4f( 1,-1, 0, 1),
		};
			
			// @formatter:on
		vertexBuffer = GLBuffers.createBuffer(vertices);

		// @formatter:off
		// MAGENTA
		// MAGENTA
		// RED
		// BLUE
		Vector3f[] colours = new Vector3f[] {
			new Vector3f(1,0,1),	// MAGENTA
			new Vector3f(1,0,1),	// MAGENTA
			new Vector3f(1,0,0),	// RED
			new Vector3f(0,0,1),	// BLUE
		};
			// @formatter:on

		colourBuffer = GLBuffers.createBuffer(colours);

		// @formatter:off
		indices = new int[] {  
			0, 1, 2, // left triangle
			0, 1, 3, // right triangle
			};
			// @formatter:on

		indexBuffer = GLBuffers.createIndexBuffer(indices);

		matrixModel = new Matrix4f();

		// A
//		scaleMatrix(-1f, 1f, matrixModel);

		// B
//		rotationMatrix(TAU/4*3, matrixModel);

		// C
//		translationMatrix(.5f, -.5f, matrixModel);
//		scaleMatrix(.5f, .5f, matrixModel);

		// D
//		translationMatrix(-0.5f, 0.5f, matrixModel);
//		rotationMatrix(TAU/12, matrixModel);
//		scaleMatrix(0.5f, 0.5f, matrixModel);

//		translationMatrix(0f, 0.5f, matrixModel);
//		rotationMatrix(TAU/8, matrixModel);
//		scaleMatrix(0.25f, 0.25f, matrixModel);

		translationMatrix(0.5f, 0f, translationMatrix);
        scaleMatrix(0.1f, 0.1f, scaleMatrix);

		matrixModel.mul(translationMatrix).mul(scaleMatrix);
    }

	public void draw() {
		
		shader.enable();
		// set the attributes
		shader.setAttribute("a_position", vertexBuffer);
		shader.setAttribute("a_colour", colourBuffer);

		shader.setUniform("u_matrix", matrixModel);

		// draw using index buffer
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, indexBuffer);
		
		glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);
		glDrawElements(GL_TRIANGLES, indices.length, GL_UNSIGNED_INT, 0);

	}

	public void update(float delta) {
		float speed = TAU/4 * delta;

		translationMatrix(0f, speed*MOVEMENT_SPEED, translationMatrix);
		rotationMatrix(speed, rotationMatrix);
		matrixModel.mul(translationMatrix).mul(rotationMatrix);
	}

	/**
	 * Set the destination matrix to a translation matrix. Note the destination
	 * matrix must already be allocated.
	 * 
	 * @param tx   Offset in the x direction
	 * @param ty   Offset in the y direction
	 * @param dest Destination matrix to write into
	 * @return
	 */

	public static Matrix4f translationMatrix(float tx, float ty, Matrix4f dest) {
		// clear the matrix to the identity matrix
		dest.identity();

		//     [ 1 0 0 tx ]
		// T = [ 0 1 0 ty ]
	    //     [ 0 0 0 0  ]
		//     [ 0 0 0 1  ]

		// Perform operations on only the x and y values of the T vec. 
		// Leaves the z value alone, as we are only doing 2D transformations.
		
		dest.m30(tx);
		dest.m31(ty);

		return dest;
	}

	/**
	 * Set the destination matrix to a rotation matrix. Note the destination matrix
	 * must already be allocated.
	 *
	 * @param angle Angle of rotation (in radians)
	 * @param dest  Destination matrix to write into
	 * @return
	 */

	public static Matrix4f rotationMatrix(float angle, Matrix4f dest) {

		dest.identity();

		dest.m00((float) Math.cos(angle));
		dest.m01((float) Math.sin(angle));
		dest.m10((float) -Math.sin(angle));
		dest.m11((float) Math.cos(angle));

		return dest;
	}

	/**
	 * Set the destination matrix to a scale matrix. Note the destination matrix
	 * must already be allocated.
	 *
	 * @param sx   Scale factor in x direction
	 * @param sy   Scale factor in y direction
	 * @param dest Destination matrix to write into
	 * @return
	 */

	public static Matrix4f scaleMatrix(float sx, float sy, Matrix4f dest) {

		dest.identity();

		dest.m00(sx);
		dest.m11(sy);

		return dest;
	}

}
