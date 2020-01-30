package de.jcup.yamleditor.preferences;

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
 

/**
 * Constant definitions for plug-in preferences
 */
public enum YamlEditorPreferenceConstants implements PreferenceIdentifiable{

	
	P_EDITOR_MATCHING_BRACKETS_ENABLED("matchingBrackets"),
	P_EDITOR_HIGHLIGHT_BRACKET_AT_CARET_LOCATION("highlightBracketAtCaretLocation"),
	P_EDITOR_ENCLOSING_BRACKETS("enclosingBrackets"),
	P_EDITOR_MATCHING_BRACKETS_COLOR("matchingBracketsColor"),
	P_EDITOR_MARGIN_RULE_LINE_COLOR("colorMarginRule"),
	P_EDITOR_AUTO_CREATE_END_BRACKETS("autoCreateEndBrackets"),
	
	P_LINK_OUTLINE_WITH_EDITOR("linkOutlineWithEditor"),
	
	P_CODE_ASSIST_ADD_KEYWORDS("codeAssistAddsKeyWords"),
	P_CODE_ASSIST_ADD_SIMPLEWORDS("codeAssistAddsSimpleWords"),
	
	P_OPEN_NEW_EDITORS_WITH_CODE_FOLDING_ENABLED("openNewEditorsWithFoldingEnabled"),
	
	P_SOURCE_FORMAT_LINE_LENGTH("sourceFormatlineLength"),
	
	P_SOURCE_FORMAT_INDENT("sourceFormatIndent"),
	
	P_SOURCE_SCALAR_STYLE_ID("sourceFormatScalarStyleId"),
	
	P_SOURCE_FORMAT_RESCUE_COMMENTS_ENABLED("sourceFormatRescueCommentsEnabled"),
	
	P_GO_TEMPLATE_SUPPORT_ENABLED("goTemplateSupportEnabled"),

	P_PREVENT_TYPE_CONVERSION_ON_FORMAT_ENABLED("preventTypeConversionOnFormatEnabled"),
	
	;

	private String id;

	private YamlEditorPreferenceConstants(String id) {
		this.id = id;
	}

	public String getId() {
		return id;
	}
}
