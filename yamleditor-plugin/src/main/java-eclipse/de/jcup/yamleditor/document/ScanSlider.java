package de.jcup.yamleditor.document;

import org.eclipse.jface.text.rules.ICharacterScanner;

public class ScanSlider {
	private static final int INITIAL_POS = 0;

	private int pos;
	private ICharacterScanner scanner;

	public ScanSlider(ICharacterScanner scanner) {
		this.pos = INITIAL_POS;
		this.scanner = scanner;
	}

	public void moveBack() {
		if (scanner.getColumn() <= 0) {
			return;
		}
		scanner.unread();
		pos--;
	}

	public int readBefore() {
		scanner.unread();
		int cbefore = scanner.read();
		return cbefore;
	}

	public void resetScanner() {
		while (pos > INITIAL_POS) {
			scanner.unread();
			pos--;
		}
		while (pos < INITIAL_POS) {
			scanner.read();
			pos++;
		}
		
	}

	public int moveForward() {
		int value = scanner.read();
		if (value != ICharacterScanner.EOF) {
			pos++;
		}
		return value;
	}

}