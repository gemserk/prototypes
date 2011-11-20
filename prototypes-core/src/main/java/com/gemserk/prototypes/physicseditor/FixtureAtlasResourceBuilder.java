package com.gemserk.prototypes.physicseditor;

import aurelienribon.box2deditor.FixtureAtlas;

import com.badlogic.gdx.files.FileHandle;
import com.gemserk.commons.gdx.resources.ResourceBuilder;

public class FixtureAtlasResourceBuilder implements ResourceBuilder<FixtureAtlas> {
	
	FileHandle shapeFile;

	@Override
	public boolean isVolatile() {
		return false;
	}
	
	public FixtureAtlasResourceBuilder shapeFile(FileHandle shapeFile) {
		this.shapeFile = shapeFile;
		return this;
	}
	
	@Override
	public FixtureAtlas build() {
		if (shapeFile == null)
			throw new RuntimeException("couldn't build FixtureAtlas, shape file cannot be null");
		return new FixtureAtlas(shapeFile);
	}

}
