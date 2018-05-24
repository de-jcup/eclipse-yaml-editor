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

import java.util.HashSet;
import java.util.Set;

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
public class YamlVariableRule implements IPredicateRule {
	// https://stackoverflow.com/questions/20635695/what-are-valid-characters-for-windows-environment-variable-names-and-values
	private final static char[] NOT_VALID_CHARACTERS = "%<>^&|=:".toCharArray();

	private IToken token;
	private Set<Character> notValidCharacterSet;
	
	public YamlVariableRule(IToken token) {
		this.token = token;
		notValidCharacterSet=new HashSet<>();
		for (char c: NOT_VALID_CHARACTERS){
			/* transform to collection with not primitive by auto boxing:*/
			notValidCharacterSet.add(c);
		}
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
		do {
			int read = scanner.read(); // use int for EOF detection, char makes
										// problems here!
			if (read == '%') {
				/* special variant for terminating % */
				break;
			}
			char c = (char) read;
			if (ICharacterScanner.EOF == read || (!isWordPart(c))) {
				scanner.unread();
				break;
			}
		} while (true);
		return getSuccessToken();
	}

	private boolean isWordPart(char c) {
		if (Character.isWhitespace(c)){
			return false;
		}
		if (notValidCharacterSet.contains(c)){
			return false;
		}
		return true;
	}

	private boolean isWordStart(char c) {
		return c == '%';
	}

}
