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
		char start = (char) scanner.read();
		if (!isWordStart(start)) {
			scanner.unread();
			return Token.UNDEFINED;
		}
		/* okay is a variable, so read until end reached */
		int readDone=0;
		do {
			int read = scanner.read(); // use int for EOF detection, char makes
										// problems here!
			readDone++;
			char c = (char) read;
			if (ICharacterScanner.EOF == read || (!isWordPart(c))) {
				for (int i=0;i<readDone;i++){
					scanner.unread();
				}
				return Token.UNDEFINED;
			}
			if (c==':'){
				return getSuccessToken();
			}
		} while (true);
	}

	private boolean isWordPart(char c) {
		
		if (c==':' || c=='-' || c=='_'){
			return true;
		}
		// spaces are allowed inside mappings, see http://yaml.org/spec/1.2/spec.html#id2761803
		if (c==' '){
			return true;
		}
		if (Character.isAlphabetic(c)){
			return true;
		}
		return false;
	}

	private boolean isWordStart(char c) {
		return Character.isAlphabetic(c);
	}

}
