package com.gemserk.commons.gdx.gui.animation4j;

import com.gemserk.animation4j.converters.TypeConverter;
import com.gemserk.commons.gdx.gui.ImageButton;

public class ImageButtonSizeConverter implements TypeConverter<ImageButton> {
	
	@Override
	public int variables() {
		return 2;
	}

	@Override
	public float[] copyFromObject(ImageButton object, float[] x) {
		if (x == null)
			x = new float[variables()];
		x[0] = object.getWidth();
		x[1] = object.getHeight();
		return x;
	}

	@Override
	public ImageButton copyToObject(ImageButton object, float[] x) {
		object.setSize(x[0], x[1]);
		return object;
	}
}