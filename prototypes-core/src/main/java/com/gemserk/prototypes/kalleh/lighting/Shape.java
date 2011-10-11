package com.gemserk.prototypes.kalleh.lighting;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class Shape {

	public final Vector2[] vertices;
	public final Rectangle bounds;
	public float x, y;
	public float cx, cy;

	public Shape(Vector2[] vertices) {
		this.vertices = new Vector2[vertices.length];
		this.bounds = new Rectangle();
		for (int i = 0; i < vertices.length; i++)
			this.vertices[i] = new Vector2(vertices[i]);
		calculateCenter();
		calculateBounds();
	}

	public void calculateBounds() {
		bounds.x = Float.MAX_VALUE;
		bounds.y = Float.MAX_VALUE;
		
		bounds.width = -Float.MAX_VALUE;
		bounds.height = -Float.MAX_VALUE;
		
		for (int i = 0; i < vertices.length; i++) { 
			Vector2 vertex = vertices[i];
			
			if (vertex.x < bounds.x)
				bounds.x = vertex.x;

			if (vertex.y < bounds.y)
				bounds.y = vertex.y;
			
			if (vertex.x > bounds.x + bounds.width) 
				bounds.width = vertex.x - bounds.x;

			if (vertex.y > bounds.y + bounds.height) 
				bounds.height = vertex.y - bounds.y;
		}
	}

	public Shape(Shape shape) {
		this(shape.vertices);
	}

	public void rotate(float angle) {
		// calculate shape center, then center on that point and then rotate all vertices.
		centerVertices();
		for (int i = 0; i < vertices.length; i++) 
			vertices[i].rotate(angle);
	}

	public void calculateCenter() {
		cx = 0f;
		cy = 0f;
		for (int i = 0; i < vertices.length; i++) {
			cx += vertices[i].x;
			cy += vertices[i].y;
		}
		cx /= vertices.length;
		cy /= vertices.length;
	}

	public void centerVertices() {
		for (int i = 0; i < vertices.length; i++) 
			vertices[i].sub(cx, cy);
	}
	
}