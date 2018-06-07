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

import static de.jcup.yamleditor.document.YamlDocumentIdentifiers.*;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.text.rules.IPredicateRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.RuleBasedPartitionScanner;
import org.eclipse.jface.text.rules.SingleLineRule;
import org.eclipse.jface.text.rules.Token;

import de.jcup.yamleditor.document.keywords.YamlReservedWords;
import de.jcup.yamleditor.document.keywords.YamlBooleanKeyWords;
import de.jcup.yamleditor.document.keywords.DocumentKeyWord;

public class YamlDocumentPartitionScanner extends RuleBasedPartitionScanner {

	private OnlyLettersKeyWordDetector onlyLettersWordDetector = new OnlyLettersKeyWordDetector();
//	private VariableDefKeyWordDetector variableDefKeyWordDetector = new VariableDefKeyWordDetector();

	public int getOffset(){
		return fOffset;
	}
	
	public YamlDocumentPartitionScanner() {
		IToken block = createToken(BLOCK_KEYWORD);
		IToken comment = createToken(COMMENT);
		IToken doubleString = createToken(DOUBLE_STRING);
		IToken mappings = createToken(MAPPINGS);
		IToken lists = createToken(LISTS);
		IToken yamlReservedWords = createToken(RESERVED_WORDS);

		IToken booleans = createToken(BOOLEANS);

		List<IPredicateRule> rules = new ArrayList<>();
//		rules.add(new YamlOldSimpleMappingRule(mappings));
		rules.add(new SingleLineRule("#", "", comment, (char) -1, true));
		rules.add(new YamlStringRule("\"", "\"", doubleString));
		rules.add(new SingleLineRule("---", "", block, (char) -1, true));

		rules.add(new YamlListRule(lists));
		rules.add(new YamlMappingRule(mappings));

		buildWordRules(rules, booleans, YamlBooleanKeyWords.values());
		buildWordRules(rules, yamlReservedWords, YamlReservedWords.values());

		setPredicateRules(rules.toArray(new IPredicateRule[rules.size()]));
	}

	private void buildWordRules(List<IPredicateRule> rules, IToken token, DocumentKeyWord[] values) {
		for (DocumentKeyWord keyWord : values) {
			ExactWordPatternRule rule1 = new ExactWordPatternRule(onlyLettersWordDetector, createWordStart(keyWord), token,
					keyWord.isBreakingOnEof());
			rules.add(rule1);
		}
	}

	private String createWordStart(DocumentKeyWord keyWord) {
		return keyWord.getText();
	}

	private IToken createToken(YamlDocumentIdentifier identifier) {
		return new Token(identifier.getId());
	}
}
