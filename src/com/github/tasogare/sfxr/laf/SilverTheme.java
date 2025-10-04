package com.github.tasogare.sfxr.laf;

import java.awt.Color;

import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.metal.DefaultMetalTheme;

public class SilverTheme extends DefaultMetalTheme {
	private static final ColorUIResource primary1 = new ColorUIResource((new Color(102, 102, 153)).brighter());
	private static final ColorUIResource primary2 = new ColorUIResource((new Color(153, 153, 204)).brighter());
	private static final ColorUIResource primary3 = new ColorUIResource((new Color(204, 204, 255)).brighter());
	private static final ColorUIResource secondary1 = new ColorUIResource((new Color(102, 102, 102)).brighter());
	private static final ColorUIResource secondary2 = new ColorUIResource((new Color(153, 153, 153)).brighter());
	private static final ColorUIResource secondary3 = new ColorUIResource((new Color(204, 204, 204)).brighter());

	@Override
	public String getName() {
		return "Silver";
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
}
