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
package de.jcup.yamleditor.script;

public class YamlScriptModelBuilder {

	private class YamlscriptContext {
		int pos = 0;
		int labelStart = 0;
		int posAtLine = 0;
	}

	public YamlScriptModel build(String text) {
		YamlScriptModel model = new YamlScriptModel();
		if (text == null || text.trim().length() == 0) {
			return model;
		}
		String inspect = text + "\n";// a simple workaround to get the last
										// label accessed too, if there is no
										// next line...
		/*
		 * very simple approach: a label is identified by being at first
		 * position of line
		 */
		StringBuilder labelSb = null;
		YamlscriptContext context = new YamlscriptContext();
		int errorPos=0;
		for (char c : inspect.toCharArray()) {
			if (c=='\t'){
				model.errors.add(new YamlError(errorPos, errorPos+1, "Found a tab character! This is not allowed in YAML files! You must replace it with spaces!"));
			}
			errorPos++;
		}
		for (char c : inspect.toCharArray()) {
			if (c == '\n' || c == '\r') {
				/* terminate search - got the label or none*/
				addLabelDataWhenExisting(model, labelSb, context);
				labelSb = null;
				continue;
			}
			if (c == ':') {
				if (context.posAtLine == 0) {
					labelSb = new StringBuilder();
					context.labelStart = context.pos;
				} else {
					/* :: detected - reset */
					labelSb = null;
				}
			} else {
				if (labelSb != null) {
					if (Character.isWhitespace(c)) {
						addLabelDataWhenExisting(model, labelSb, context);
						labelSb = null;
						continue;
					} else {
						labelSb.append(c);
					}
				}
			}
			context.pos++;
			context.posAtLine++;
		}

		return model;
	}

	protected void addLabelDataWhenExisting(YamlScriptModel model, StringBuilder labelSb, YamlscriptContext context) {
		if (labelSb != null) {
			String labelName = labelSb.toString().trim();
			if (! labelName.isEmpty()){

				YamlLabel label = new YamlLabel(labelName);
				label.pos = context.labelStart + 1;
				label.end = context.pos - 1;
				
				model.getLabels().add(label);
			}
		}
		context.pos++;
		context.posAtLine = 0;
	}

}
