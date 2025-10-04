package com.github.tasogare.sfxr.app;

import java.awt.Component;
import java.awt.Font;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;

/**
 * @author tasogare
 * @param <E> cell value
 */
final class FontCellRenderer<E> extends JLabel implements ListCellRenderer<E> {
	private static final long serialVersionUID = 5134627897075165292L;

	private static final Border DEFAULT_NO_FOCUS_BORDER = new EmptyBorder(1, 1, 1, 1);

	private static Border noFocusBorder = DEFAULT_NO_FOCUS_BORDER;

	public FontCellRenderer() {
		setOpaque(true);
	}

	@Override
	public Component getListCellRendererComponent(JList<? extends E> list, E value, int index, boolean isSelected,
			boolean cellHasFocus) {
		if (value != null) {
			if (value instanceof String item) {
				if (isSelected) {
					setBackground(list.getSelectionBackground());
					setForeground(list.getSelectionForeground());
				} else {
					setBackground(list.getBackground());
					setForeground(list.getForeground());
				}
				setText(item);
				var font = new Font(item, Font.PLAIN, 16);
				setFont(font);
			}
		} else {
			setText(null);
		}

		setEnabled(list.isEnabled());

		Border border = null;
		if (cellHasFocus) {
			if (isSelected) {
				border = UIManager.getBorder("List.focusSelectedCellHighlightBorder", getLocale());
			}

			if (border == null) {
				border = UIManager.getBorder("List.focusCellHighlightBorder", getLocale());
			}
		} else {
			border = getNoFocusBorder();
		}
		setBorder(border);

		setName("List.cellRenderer");
		return this;
	}

	private Border getNoFocusBorder() {
		var border = UIManager.getBorder("List.cellNoFocusBorder", getLocale());
		if (border != null && noFocusBorder == null || noFocusBorder == DEFAULT_NO_FOCUS_BORDER) {
			return border;
		} else {
			return noFocusBorder;
		}
	}
}