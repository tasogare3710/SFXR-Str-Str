package com.github.tasogare.sfxr.laf;

import java.awt.Color;

import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.metal.DefaultMetalTheme;

public class MatchaTheme extends DefaultMetalTheme {
	private static final ColorUIResource primary1 = new ColorUIResource(new Color(0x112318));
	private static final ColorUIResource primary2 = new ColorUIResource(new Color(0x1e3a29));
	private static final ColorUIResource primary3 = new ColorUIResource(new Color(0x305d42));
	private static final ColorUIResource secondary1 = new ColorUIResource(new Color(0x4d8061));
	private static final ColorUIResource secondary2 = new ColorUIResource(new Color(0x89a257));
	private static final ColorUIResource secondary3 = new ColorUIResource(new Color(0xbedc7f));

	private static final ColorUIResource white = new ColorUIResource(0xeeffcc);
	private static final ColorUIResource black = new ColorUIResource(0x040c06);

	@Override
	protected ColorUIResource getBlack() {
		return black;
	}

	@Override
	public String getName() {
		return "Matcha";
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
		return white;
	}
}
