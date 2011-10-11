package com.gemserk.prototypes.kalleh.lighting;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.RayCastCallback;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;

public class RayHandler {

	private final GL10 gl;
	private final Mesh box;
	public boolean shadows = false;
	public float ambientLight = 0;

	final public Array<Light> lightList = new Array<Light>(false, 16, Light.class);

	/**
	 * Light is data container for all the light parameters You can create instance of Light only with help of rayHandler addLight method
	 */
	public final class Light {
		public boolean active = true;
		public boolean xray = false;
		private int rayNum;
		private int vertexNum;
		float distance;
		float coneDegree;
		Color color;
		final Vector2 start = new Vector2();
		final Vector2 end[];
		final Mesh lightMesh;
		float direction;

		private Light(int rays) {
			setRayNum(rays);
			end = new Vector2[rays];
			for (int i = 0; i < rays; i++) {
				end[i] = new Vector2();
			}
			lightMesh = new Mesh(false, vertexNum, 0, new VertexAttribute(Usage.Position, 2, "vertex_positions"), new VertexAttribute(Usage.ColorPacked, 4, "quad_colors"));
		}

		final public void setRayNum(int rays) {
			rayNum = rays;
			vertexNum = rays + 1;
		}

		final public void setPosAndRotation(float x, float y, float angle) {
			start.x = x;
			start.y = y;
			setRotation(angle);
		}

		final public void setPos(float x, float y) {
			setPosAndRotation(x, y, direction);
		}

		final public void setRotation(float dir) {
			direction = dir;
			for (int i = 0; i < rayNum; i++) {
				float t = direction + coneDegree - 2 * coneDegree * (float) i / ((float) rayNum - 1);
				end[i].set(start.x + distance * MathUtils.sinDeg(t), start.y + distance * MathUtils.cosDeg(t));
			}
		}

		final public void remove() {
			lightMesh.dispose();
			lightList.removeValue(this, true);
		}
	}

	static final int defaultMaximum = 1023;

	public RayHandler() {
		this(defaultMaximum);
	}

	public RayHandler(int maxRayCount) {
		gl = Gdx.graphics.getGL10();

		final int maxSegments = (maxRayCount + 1) * 3;
		m_segments = new float[maxSegments];
		m_x = new float[maxRayCount];
		m_y = new float[maxRayCount];
		m_f = new float[maxRayCount];
		box = new Mesh(false, 12, 0, new VertexAttribute(Usage.Position, 2, "vertex_positions"), new VertexAttribute(Usage.ColorPacked, 4, "quad_colors"));
		setShadowBox();
	}

	public final Light addLight(float x, float y, float directionDegree, float coneDegree, float distance, int rays, Color color) {
		final Light light = new Light(rays);
		light.distance = distance;
		light.coneDegree = coneDegree;
		light.color = color;
		light.setPosAndRotation(x, y, directionDegree);
		lightList.add(light);
		return light;
	}

	public void render() {

		if (shadows) {
			// clearing the alpha channel
			gl.glClearColor(0f, 0f, 0f, ambientLight);
			gl.glColorMask(false, false, false, true);
			gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
			gl.glColorMask(true, true, true, true);
			gl.glClearColor(0f, 0f, 0f, 1f);
		}
		// light rays
		gl.glEnable(GL10.GL_BLEND);
		gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE);
		final int size = lightList.size;
		for (int i = 0; i < size; i++) {
			Light light = lightList.items[i];
			if (light.active) {
				light.lightMesh.render(GL10.GL_TRIANGLE_FAN, 0, light.vertexNum);
			}
		}

		if (shadows) {
			// rendering shadow box over screen
			gl.glBlendFunc(GL10.GL_ONE, GL10.GL_DST_ALPHA);
			box.render(GL10.GL_TRIANGLE_FAN, 0, 4);
		}
		gl.glDisable(GL10.GL_BLEND);
	}

	private final void updateLightMesh(float x[], float y[], float f[], Light light) {

		float r = light.color.r;
		float g = light.color.g;
		float b = light.color.b;
		float aa = light.color.a;
		// ray starting point
		int size = 0;
		m_segments[size++] = light.start.x;
		m_segments[size++] = light.start.y;
		m_segments[size++] = Color.toFloatBits(r, g, b, aa);
		// rays ending points.
		final int arraySize = light.rayNum;
		for (int i = 0; i < arraySize; i++) {
			m_segments[size++] = x[i];
			m_segments[size++] = y[i];
			final float s = 1f - f[i];
			r = light.color.r * s;
			g = light.color.g * s;
			b = light.color.b * s;
			float a = aa * s;
			m_segments[size++] = Color.toFloatBits(r, g, b, a);
		}

		light.lightMesh.setVertices(m_segments, 0, size);
	}

	public void dispose() {
		final int size = lightList.size;
		for (int i = 0; i < size; i++) {
			lightList.items[i].lightMesh.dispose();
		}
	}

	public final void updateAndRender(World world) {
		updateRays(world);
		render();
	}

	// Rays
	public final void updateRays(World world) {
		final int size = lightList.size;
		for (int j = 0; j < size; j++) {
			Light light = lightList.items[j];
			if (light.active) {
				for (int i = 0; i < light.rayNum; i++) {
					m_index = i;
					m_f[i] = 1f;
					m_x[i] = light.end[i].x;
					m_y[i] = light.end[i].y;
					if (!light.xray)
						world.rayCast(ray, light.start, light.end[i]);
				}
				updateLightMesh(m_x, m_y, m_f, light);
			}
		}
	}

	final private float m_segments[];
	final private float[] m_x;
	final private float[] m_y;
	final private float[] m_f;
	private int m_index = 0;
	final private RayCastCallback ray = new RayCastCallback() {
		@Override
		public float reportRayFixture(Fixture fixture, Vector2 point, Vector2 normal, float fraction) {
			m_x[m_index] = point.x;
			m_y[m_index] = point.y;
			m_f[m_index] = fraction;
			return fraction;
		}
	};

	private void setShadowBox() {
		int i = 0;
		// This need some work, maybe camera matrix would needed do
		m_segments[i++] = -8f;
		m_segments[i++] = 0f;
		m_segments[i++] = Color.toFloatBits(0, 0, 0, 1);
		m_segments[i++] = -8f;
		m_segments[i++] = 27f;
		m_segments[i++] = Color.toFloatBits(0, 0, 0, 1);
		m_segments[i++] = 8f;
		m_segments[i++] = 27f;
		m_segments[i++] = Color.toFloatBits(0, 0, 0, 1);
		m_segments[i++] = 8f;
		m_segments[i++] = 0f;
		m_segments[i++] = Color.toFloatBits(0, 0, 0, 1);
		box.setVertices(m_segments, 0, i);

	}
}
