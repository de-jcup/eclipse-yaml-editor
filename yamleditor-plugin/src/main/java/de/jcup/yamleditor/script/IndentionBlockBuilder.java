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

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Yaml entry folding should work then when the "3 line must" strategy is used:
 * Example
 * 
 * <pre>
a:
 b1:
   c1:
   c2:
   c3:
 b2:
   c4:
   c5:
d:
  e:
 * </pre>
 * 
 * folding of a must contain line of d: to prevent double icons for b2: or c5
 */
public class IndentionBlockBuilder {

	private static final Pattern LINE_SPLIT_PATTERN = Pattern.compile("\\\n");

	public List<IndentionBlock> build(String text) {

		String[] lines = LINE_SPLIT_PATTERN.split(text, -1);
		IndentContext context = new IndentContext(lines);
		for (int i = 0; i < lines.length; i++) {
			context.calculateIndentBlockForLineIfNecessary(i);
		}
		return context.list;
	}

	private class IndentContext {
		List<IndentionBlock> list = new ArrayList<>();
		private String[] lines;

		public IndentContext(String[] lines) {
			this.lines = lines;
		}

		public void calculateIndentBlockForLineIfNecessary(int index) {
			/* get line and try to investigate */
			String line = getLine(index);
			int indent = calculateIndention(line);
			int startIndex = calculateStartIndex(index);
			IndentionBlock current = new IndentionBlock(indent, startIndex);
			
			/* okay, now we inspect all following lines while the indent is deeper than our one*/
			int childLines = 0;
			int lastChild=0;
			for (int i = index+1;i<lines.length;i++){
				String line2 = getLine(i);
				if (line2.isEmpty()){
					continue;
				}else{
					int indent2= calculateIndention(line2);
					if (indent2<=indent){
						/* same indent, no longer parent*/
						break;
					}
				}
				childLines++;
				lastChild=i;
			}
			
			
			boolean containsChildren=lastChild>0 && childLines>2 ;
			if (containsChildren){
				String lastLine = getLine(lastChild);
				int startPosLastChild = calculateStartIndex(lastChild);
				current.end=startPosLastChild+lastLine.length();
				if (lastChild<lines.length-1){
					/* not last line*/
					current.end++;
				}
				list.add(current);
			}

		}

		protected String getLine(int index) {
			if (lines==null){
				return "";
			}
			if (lines.length<=index){
				return "";
			}
			return lines[index];
		}

		public int calculateStartIndex(int index) {
			int pos = 0;
			for (int i = 0; i < index && i<lines.length; i++) {
				String text = getLine(i);
				pos += text.length();
				pos +=1;
			}
			return pos;
		}

	}

	int calculateIndention(String line) {
		char[] chars = line.toCharArray();
		int indention = 0;
		for (int i = 0; i < chars.length; i++) {
			if (chars[i] != ' ') {
				break;
			}
			indention++;
		}
		return indention;
	}

}
