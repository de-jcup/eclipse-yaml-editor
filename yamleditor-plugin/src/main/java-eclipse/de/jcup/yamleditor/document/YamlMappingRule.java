/*
 * Copyright 2017 Albert Tregnaghi
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
package de.jcup.yamleditor.document;

import org.eclipse.jface.text.rules.ICharacterScanner;
import org.eclipse.jface.text.rules.IPredicateRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.Token;

/**
 * A special rule to scan yaml variables
 * 
 * @author Albert Tregnaghi
 *
 */
public class YamlMappingRule implements IPredicateRule {

	private IToken token;

	public YamlMappingRule(IToken token) {
		this.token = token;
	}

	@Override
	public IToken getSuccessToken() {
		return token;
	}

	@Override
	public IToken evaluate(ICharacterScanner scanner) {
		return evaluate(scanner, false);
	}

	@Override
	public IToken evaluate(ICharacterScanner scanner, boolean resume) {
		boolean startOfDocument = scanner.getColumn() == 0;
		boolean newLine = startOfDocument;
		if (!startOfDocument) {

			/* get char before */
			scanner.unread();
			int cbefore = scanner.read();

			newLine = isNewLine(scanner, (char) cbefore);
			int backwardSteps = 0;
			StringBuilder prefixSb = null;
			while (!newLine) {
				if (prefixSb == null) {
					prefixSb = new StringBuilder();
				}
				char cb = (char) cbefore;
				prefixSb.append(cb);
				/* only when its a space before inspect further */
				boolean notAlreadyAList = prefixSb.indexOf("-")!=-1;
				boolean possibleListEntry = cb=='-' && notAlreadyAList;
				if (cb == ' ' || possibleListEntry ) {
					scanner.unread(); /*
										 * go one step back - so position of
										 * former space
										 */
					backwardSteps++;
					scanner.unread(); /*
										 * additional step back - no one char
										 * before former space
										 */
					backwardSteps++;
					if (scanner.getColumn() == 0) {
						newLine = true;
						break;
					}
					cbefore = scanner
							.read(); /*
										 * get char before former space, position
										 * now again at former space
										 */
					scanner.unread();

					newLine = isNewLine(scanner, (char) cbefore);
				} else {
					/* no space, so not an indent, so reset */
					break;
				}
			}
			resetScannerBackwards(scanner, backwardSteps);

		}

		if (!newLine) {
			return Token.UNDEFINED;
		}

		char start = (char) scanner.read();
		if (!isWordStart(start)) {
			scanner.unread();
			return Token.UNDEFINED;
		}
		/* okay is a variable, so read until end reached */
		StringBuilder sb = new StringBuilder();
		int readDone = 0;
		do {
			int read = scanner.read(); // use int for EOF detection, char makes
										// problems here!
			readDone++;
			char c = (char) read;
			if (ICharacterScanner.EOF == read || (!isWordPart(sb, c))) {
				resetScanner(scanner, readDone);
				return Token.UNDEFINED;
			}
			if (c == ':') {
				return getSuccessToken();
			}
		} while (true);
	}

	protected boolean isNewLine(ICharacterScanner scanner, char cbefore) {
		boolean newLine = scanner.getColumn() == 0;
		newLine = newLine || cbefore == '\n';
		newLine = newLine || cbefore == '\r';
		return newLine;
	}

	protected void resetScanner(ICharacterScanner scanner, int readDone) {
		commonResetScanner(scanner, readDone, true);
	}

	protected void resetScannerBackwards(ICharacterScanner scanner, int readDone) {
		commonResetScanner(scanner, readDone, false);
	}

	private void commonResetScanner(ICharacterScanner scanner, int readDone, boolean unread) {
		for (int i = 0; i < readDone; i++) {
			if (unread) {
				scanner.unread();
			} else {
				scanner.read();
			}
		}
	}

	private boolean isWordPart(StringBuilder sb, char c) {

		if (c == ':' || c == '-' || c == '_') {
			return true;
		}
		// spaces are allowed inside mappings, see
		// http://yaml.org/spec/1.2/spec.html#id2761803
		if (c == ' ') {
			if (sb.indexOf(":") != -1) {
				return false;// colon detected
			}
			return true;
		}
		if (Character.isLetterOrDigit(c)) {
			return true;
		}
		return false;
	}

	private boolean isWordStart(char c) {
		return Character.isAlphabetic(c);
	}

}
