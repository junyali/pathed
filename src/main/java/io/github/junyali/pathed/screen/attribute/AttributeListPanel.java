package io.github.junyali.pathed.screen.attribute;

import io.github.junyali.pathed.data.attribute.Attribute;
import io.github.junyali.pathed.data.attribute.AttributeRegistry;

import java.util.ArrayList;
import java.util.List;

public class AttributeListPanel {
	private static final int TAB_BAR_H = 18;
	private static final int CHIP_H = 22;
	private static final int CHIP_GAP = 2;
	private static final int SCROLLBAR_W = 5;

	private final AttributeScreen screen;
	private final List<Attribute> visible = new ArrayList<>();

	private final int left;
	private final int top;
	private final int width;
	private final int height;

	private int scrollPos = 0;
	private int maxScroll = 0;
	private boolean draggingScroll = false;
	private double dragStartMouseY;
	private int dragStartScroll;

	public AttributeListPanel(AttributeScreen screen, int left, int top, int width, int height) {
		this.screen = screen;
		this.left = left;
		this.top = top;
		this.width = width;
		this.height = height;
	}

	public void refresh() {
		visible.clear();
		if (screen.isShowAll()) {
			visible.addAll(AttributeRegistry.all());
		} else {
			for (Attribute a : AttributeRegistry.all()) {
				// if obtained
			}
		}
	}
}
