package com.github.tasogare.sfxr.laf;

import java.awt.Color;

import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.metal.DefaultMetalTheme;

public class InvertTheme extends DefaultMetalTheme {
	private static final ColorUIResource primary1 = new ColorUIResource((new Color(255 - 102, 255 - 102, 255 - 153)));
	private static final ColorUIResource primary2 = new ColorUIResource((new Color(255 - 153, 255 - 153, 255 - 204)));
	private static final ColorUIResource primary3 = new ColorUIResource((new Color(255 - 204, 255 - 204, 255 - 255)));
	private static final ColorUIResource secondary1 = new ColorUIResource((new Color(255 - 102, 255 - 102, 255 - 102)));
	private static final ColorUIResource secondary2 = new ColorUIResource((new Color(255 - 153, 255 - 153, 255 - 153)));
	private static final ColorUIResource secondary3 = new ColorUIResource((new Color(255 - 204, 255 - 204, 255 - 204)));

	private static final ColorUIResource white = new ColorUIResource(255, 255, 255);
	private static final ColorUIResource black = new ColorUIResource(0, 0, 0);

	@Override
	protected ColorUIResource getBlack() {
		return white;
	}

	@Override
	public String getName() {
		return "Invert";
	}

	@Override
	protected ColorUIResource getPrimary1() {
		return primary1;
	}

	@Override
	protected ColorUIResource getPrimary2() {
		return primary2;
	}

	@Override
	protected ColorUIResource getPrimary3() {
		return primary3;
	}

	@Override
	protected ColorUIResource getSecondary1() {
		return secondary1;
	}

	@Override
	protected ColorUIResource getSecondary2() {
		return secondary2;
	}

	@Override
	protected ColorUIResource getSecondary3() {
		return secondary3;
	}

	@Override
	protected ColorUIResource getWhite() {
		return black;
	}
}
