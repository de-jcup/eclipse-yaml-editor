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

import de.jcup.yamleditor.document.keywords.YamlBuildInKeywords;
import de.jcup.yamleditor.document.keywords.YamlExternalKeyWords;
import de.jcup.yamleditor.document.keywords.YamlSpecialVariableKeyWords;
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
		IToken list = createToken(LISTS);
		IToken yamlBuildIn = createToken(YAML_KEYWORD);

		IToken knownVariables = createToken(KNOWN_VARIABLES);
		IToken variables = createToken(VARIABLES);
		IToken yamlExternalCommands = createToken(YAML_COMMAND);

		List<IPredicateRule> rules = new ArrayList<>();
//		rules.add(new YamlOldSimpleMappingRule(mappings));
		rules.add(new SingleLineRule("#", "", comment, (char) -1, true));
		rules.add(new YamlStringRule("\"", "\"", doubleString));
		rules.add(new SingleLineRule("---", "", block, (char) -1, true));

		rules.add(new YamlListRule(list));
		rules.add(new YamlMappingRule(mappings));
		rules.add(new YamlVariableRule(variables));

		buildWordRules(rules, yamlBuildIn, YamlBuildInKeywords.values());
		buildWordRules(rules, yamlExternalCommands, YamlExternalKeyWords.values());
		
		buildWordRules(rules, knownVariables, YamlSpecialVariableKeyWords.values());

		/* TODO ATR: 24.11.2017: remove the next linecomplete or support variable setup like in bash editor*/
//		buildVarDefRules(rules, knownVariables, YamlSpecialVariableKeyWords.values());

		setPredicateRules(rules.toArray(new IPredicateRule[rules.size()]));
	}

	private void buildWordRules(List<IPredicateRule> rules, IToken token, DocumentKeyWord[] values) {
		for (DocumentKeyWord keyWord : values) {
			ExactWordPatternRule rule1 = new ExactWordPatternRule(onlyLettersWordDetector, createWordStart(keyWord), token,
					keyWord.isBreakingOnEof());
			rule1.setAllowedPrefix('@');
			rule1.setAllowedPostfix(':');
			rules.add(rule1);

			ExactWordPatternRule rule2 = new ExactWordPatternRule(onlyLettersWordDetector, keyWord.getText().toUpperCase(), token,
					keyWord.isBreakingOnEof());
			rule2.setAllowedPrefix('@');
			rule2.setAllowedPostfix(':');
			rules.add(rule2);
			
		}
	}

//	private void buildVarDefRules(List<IPredicateRule> rules, IToken token, DocumentKeyWord[] values) {
//		for (DocumentKeyWord keyWord : values) {
//			rules.add(new VariableDefKeyWordPatternRule(variableDefKeyWordDetector, createWordStart(keyWord), token,
//					keyWord.isBreakingOnEof()));
//		}
//	}

	private String createWordStart(DocumentKeyWord keyWord) {
		return keyWord.getText();
	}

	private IToken createToken(YamlDocumentIdentifier identifier) {
		return new Token(identifier.getId());
	}
}
