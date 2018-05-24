/*
 * Copyright 2018 Albert Tregnaghi
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *		http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions
 * and limitations under the License.
 *
 */
package de.jcup.yamleditor;

import org.eclipse.jface.text.IPaintPositionManager;
import org.eclipse.jface.text.IPainter;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Rectangle;

public class YamlMarginRulePainter implements IPainter, PaintListener, ModifyListener {

	private StyledText styledText;
	private int cachedWidgetX = -1;
	private boolean active = false;

	private MarginPaintSetup setup;
	private int pixels;

	public YamlMarginRulePainter(ITextViewer textViewer, MarginPaintSetup setup) {
		styledText = textViewer.getTextWidget();
		this.setup = setup;
		
		initialize();
	}

	private void initialize() {
		computeWidgetX();
		redrawWidget();
	}

	/**
	 * Computes and remembers the x-offset of the margin column for the current
	 * widget font.
	 */
	private void computeWidgetX() {
		int pixels = getCalculatedPixelsForOneCharacter();

		cachedWidgetX = pixels * setup.column;
	}

	private int getCalculatedPixelsForOneCharacter() {
		if (pixels!=-1){
			return pixels;
		}
		GC gc = new GC(styledText);
		pixels = gc.getFontMetrics().getAverageCharWidth();
		gc.dispose();
		return pixels;
	}

	public void setX(int x){
		if (x==cachedWidgetX){
			return;
		}
		cachedWidgetX=x;
		redrawWidget();
	}
	
	public void deactivate(boolean redraw) {
		if (!active) {
			return;
		}
		active = false;
		cachedWidgetX = -1;
		styledText.removePaintListener(this);
		if (redraw) {
			redrawWidget();
		}
	}

	protected void redrawWidget() {
		styledText.redraw();
	}

	public void dispose() {
		styledText = null;
	}

	public void paint(int reason) {
		if (active) {
			if (CONFIGURATION == reason || INTERNAL == reason) {
				redrawWidget();
			}
			return;
		}
		active = true;
		styledText.addPaintListener(this);
		if (cachedWidgetX == -1){
			computeWidgetX();
		}
		redrawWidget();
	}

	public void paintControl(PaintEvent e) {
		if (styledText != null) {
			int x = cachedWidgetX - styledText.getHorizontalPixel();
			if (x >= 0) {
				Rectangle area = styledText.getClientArea();
				e.gc.setForeground(setup.lineColor);
				e.gc.setLineStyle(setup.lineStyle);
				e.gc.setLineWidth(setup.lineWidth);
				
				/* paint line */
				e.gc.drawLine(x, 0, x, area.height);
				
			}
		}
	}

	public void setPositionManager(IPaintPositionManager manager) {
	}

	public void modifyText(ModifyEvent e) {
	}

	public static class MarginPaintSetup {
		public int column = 1;
		public Color lineColor;
		public int lineStyle = SWT.LINE_SOLID;
		public int lineWidth = 1;
	}
}