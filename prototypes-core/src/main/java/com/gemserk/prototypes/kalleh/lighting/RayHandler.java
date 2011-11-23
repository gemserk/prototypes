package com.gemserk.prototypes.kalleh.lighting;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.RayCastCallback;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;

/**
 * @author kalle_h
 * 
 */
public class RayHandler {

	private static final int MIN_RAYS = 3;
	final int MAX_RAYS;
	private final GL10 gl;
	public Mesh box;
	public World world;
	boolean culling = false;
	public OrthographicCamera cam;
	/**
	 * This option need frame buffer with alpha channel You also need create box mesh by hand
	 */
	public final static boolean shadows = false;
	public float ambientLight = 0.2f;
	final public Array<Light> lightList = new Array<Light>(false, 16, Light.class);

	/**
	 * cam need to be set for this feature
	 */
	public void enableCulling() {
		culling = true;
	}

	public void disableCulling() {
		culling = false;
	}

	/**
	 * Light is data container for all the light parameters You can create instance of Light also with help of rayHandler addLight method
	 */
	public class Light {
		public boolean culled = false;
		public boolean active = true;
		public boolean soft = true;
		public boolean xray = false;
		public boolean staticLight = false;
		int rayNum;
		int vertexNum;
		public float distance;
		float coneDegree;
		float direction;
		final float sin[];
		final float cos[];
		Color color;
		final Vector2 start = new Vector2();
		final Vector2 end[];
		final Mesh lightMesh;
		final Mesh softShadowMesh;
		float softShadowLenght = 5f;

		public Light(int rays) {
			this(rays, false, false);
		}

		public Light(int rays, boolean staticLight, boolean xray) {
			this.staticLight = staticLight;
			this.xray = xray;
			setRayNum(rays);
			sin = new float[rays];
			cos = new float[rays];
			end = new Vector2[rays];
			for (int i = 0; i < rays; i++)
				end[i] = new Vector2();

			lightMesh = new Mesh(staticLight, vertexNum, 0, new VertexAttribute(Usage.Position, 2, "vertex_positions"), new VertexAttribute(Usage.ColorPacked, 4, "quad_colors"));
			softShadowMesh = new Mesh(staticLight, vertexNum * 2, 0, new VertexAttribute(Usage.Position, 2, "vertex_positions"), new VertexAttribute(Usage.ColorPacked, 4, "quad_colors"));
			lightList.add(this);
		}

		public void setRayNum(int rays) {
			if (rays > MAX_RAYS) {
				rays = MAX_RAYS;
			}
			if (rays < MIN_RAYS) {
				rays = MIN_RAYS;
			}
			rayNum = rays;
			vertexNum = rays + 1;

		}

		public void setRotation(float direction) {
			setPosAndRotation(start.x, start.y, direction);
		}

		public void setPos(float x, float y) {
			setPosAndRotation(x, y, direction);
		}

		/**
		 * set the starting point and direction, call this if you need to change both positon and rotation for slightly better perfirmance
		 */
		public void setPosAndRotation(float x, float y, float direction) {

			start.x = x;
			start.y = y;
			this.direction = direction;
			for (int i = 0; i < rayNum; i++) {
				float angle = direction + coneDegree - 2f * coneDegree * (float) i / ((float) rayNum - 1f);
				final float s = sin[i] = MathUtils.sinDeg(angle);
				final float c = cos[i] = MathUtils.cosDeg(angle);
				end[i].set(x + distance * s, y + distance * c);
			}
			if (staticLight) {
				staticLight = false;
				update();
				staticLight = true;
			}
		}

		public void remove() {
			lightMesh.dispose();
			lightList.removeValue(this, true);
		}

		boolean testCull() {
			return (culled = !intersect(start.x, start.y, distance));
		}

		public void update() {
			if (!active && staticLight) {
				return;
			}

			if (culling)
				if (testCull())
					return;

			for (int i = 0; i < rayNum; i++) {
				m_index = i;
				m_f[i] = 1f;
				m_x[i] = end[i].x;
				m_y[i] = end[i].y;
				if (!xray)
					world.rayCast(ray, start, end[i]);
			}
			updateLightMesh();
		}

		void updateLightMesh() {

			final float r = color.r;
			final float g = color.g;
			final float b = color.b;
			final float a = color.a;
			// ray starting point
			int size = 0;
			m_segments[size++] = start.x;
			m_segments[size++] = start.y;
			m_segments[size++] = Color.toFloatBits(r, g, b, a);
			// rays ending points.
			final int arraySize = rayNum;
			for (int i = 0; i < arraySize; i++) {
				m_segments[size++] = m_x[i];
				m_segments[size++] = m_y[i];
				final float s = 1f - m_f[i];
				m_segments[size++] = Color.toFloatBits(color.r * s, color.g * s, color.b * s, a * s);
			}
			lightMesh.setVertices(m_segments, 0, size);

			if (!soft || xray)
				return;

			size = 0;
			// rays ending points.
			final float zero = Color.toFloatBits(0f, 0f, 0f, 0f);

			for (int i = 0; i < arraySize; i++) {
				m_segments[size++] = m_x[i];
				m_segments[size++] = m_y[i];
				final float s = 1f - m_f[i];
				m_segments[size++] = Color.toFloatBits(color.r * s, color.g * s, color.b * s, a * s);
				m_segments[size++] = m_x[i] + softShadowLenght * sin[i];
				m_segments[size++] = m_y[i] + softShadowLenght * cos[i];
				m_segments[size++] = zero;
			}
			softShadowMesh.setVertices(m_segments, 0, size);

		}

		public void render() {
			if (active && !culled) {

				lightMesh.render(GL10.GL_TRIANGLE_FAN, 0, vertexNum);

				if (soft && !xray) {
					softShadowMesh.render(GL10.GL_TRIANGLE_STRIP, 0, (vertexNum - 1) * 2);
				}
			}

		}
	}

	public boolean intersect(float x, float y, float side) {
		final float bx = x - side;
		final float bx2 = x + side;
		final float by = y - side;
		final float by2 = y + side;
		return (x1 < bx2 && x2 > bx && y1 < by2 && y2 > by);
	}

	void updateCameraCorners() {
		final float halfWidth = (cam.viewportWidth / 2f) * cam.zoom;
		final float halfHeight = (cam.viewportHeight / 2f) * cam.zoom;
		final Vector3 campPos = cam.position;
		x1 = campPos.x - halfWidth;
		x2 = campPos.x + halfWidth;
		y1 = campPos.y - halfHeight;
		y2 = campPos.y + halfHeight;
	}

	public float x1;
	public float x2;
	public float y1;
	public float y2;

	static final int defaultMaximum = 1023;

	public RayHandler(World world) {
		this(world, defaultMaximum);
	}

	public RayHandler(World world, int maxRayCount) {
		this.world = world;
		MAX_RAYS = maxRayCount;
		gl = Gdx.graphics.getGL10();
		m_segments = new float[maxRayCount * 6];
		m_x = new float[maxRayCount];
		m_y = new float[maxRayCount];
		m_f = new float[maxRayCount];
		box = new Mesh(false, 12, 0, new VertexAttribute(Usage.Position, 2, "vertex_positions"), new VertexAttribute(Usage.ColorPacked, 4, "quad_colors"));
		setShadowBox();
	}

	/**
	 * addLight
	 * 
	 */
	public Light addLight(float x, float y, float directionDegree, float coneDegree, float distance, int rays, Color color, boolean staticLight, boolean xray) {
		final Light light = new Light(rays, staticLight, xray);
		light.distance = distance;
		light.coneDegree = coneDegree;
		light.color = color;
		light.setPosAndRotation(x, y, directionDegree);
		return light;
	}

	/**
	 * REMEMBER CALL updateRays(World world) BEFORE render. Don't call this inside of any begin/end statements. Call this method after you have rendered background but before UI Box2d bodies can be rendered before or after depending how you want x-ray light interact with bodies
	 */
	public void renderLightsAndShadows() {
		if (shadows) {
			alphaChannelClear();
		}

		renderLights();
		if (shadows) {
			renderShadows();
		}
	}

	public void renderLights() {
		gl.glEnable(GL10.GL_BLEND);
		gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE);
		final int size = lightList.size;
		for (int i = 0; i < size; i++) {
			lightList.items[i].render();
		}
		gl.glDisable(GL10.GL_BLEND);
	}

	/**
	 * call alphaChannelClear() before light rendering and after lights call this. Use renderLightAndShadows() for simplicity
	 */
	private void renderShadows() {
		gl.glEnable(GL10.GL_BLEND);
		// rendering shadow box over screen
		gl.glBlendFunc(GL10.GL_ONE, GL10.GL_DST_ALPHA);
		box.render(GL10.GL_TRIANGLE_FAN, 0, 4);

		gl.glDisable(GL10.GL_BLEND);

	}

	private void alphaChannelClear() {
		// clearing the alpha channel
		gl.glClearColor(0f, 0f, 0f, 0f);
		gl.glColorMask(false, false, false, true);
		gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		gl.glColorMask(true, true, true, true);
		gl.glClearColor(0f, 0f, 0f, 1f);

	}

	public void dispose() {
		final int size = lightList.size;
		for (int i = 0; i < size; i++) {
			lightList.items[i].lightMesh.dispose();
		}
	}

	/**
	 * Don't call this inside of any begin/end statements. Call this method after you have rendered background but before UI Box2d bodies can be rendered before or after depending how you want x-ray light interact with bodies
	 */
	public final void updateAndRender() {
		updateRays();
		if (shadows)
			renderLightsAndShadows();
		else
			renderLights();
	}

	// Rays
	public final void updateRays() {
		if (culling)
			updateCameraCorners();

		final int size = lightList.size;
		for (int j = 0; j < size; j++)
			lightList.items[j].update();

	}

	final float m_segments[];
	final float[] m_x;
	final float[] m_y;
	final float[] m_f;
	int m_index = 0;

	RayCastCallback ray = new RayCastCallback() {
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
		// This need some work, maybe camera matrix would needed
		float c = Color.toFloatBits(0, 0, 0, ambientLight);
		m_segments[i++] = -8f;
		m_segments[i++] = 0f;
		m_segments[i++] = c;
		m_segments[i++] = -8f;
		m_segments[i++] = 27f;
		m_segments[i++] = c;
		m_segments[i++] = 8f;
		m_segments[i++] = 27f;
		m_segments[i++] = c;
		m_segments[i++] = 8f;
		m_segments[i++] = 0f;
		m_segments[i++] = c;
		box.setVertices(m_segments, 0, i);

	}
}