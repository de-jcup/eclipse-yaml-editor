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
import java.util.Collection;
import java.util.SortedSet;
import java.util.TreeSet;

public class YamlScriptModel {

	Collection<YamlError> errors = new ArrayList<>();

	private YamlNode rootNode;

	private SortedSet<FoldingPosition> foldingPositions = new TreeSet<>();

	public YamlScriptModel() {
		rootNode = new YamlNode("root");
	}

	public YamlNode getRootNode() {
		return rootNode;
	}

	public boolean hasErrors() {
		return errors.size() > 0;
	}

	public Collection<YamlError> getErrors() {
		return errors;
	}


	public SortedSet<FoldingPosition> getFoldingPositions() {
		return foldingPositions;
	}
	
	public void addFolding(FoldingPosition folding) {
		this.foldingPositions.add(folding);
	}

	public static class FoldingPosition implements Comparable<FoldingPosition>{
		private int offset;
		private int length;

		public FoldingPosition(int offset, int length) {
			super();
			this.offset = offset;
			this.length = length;
		}

		public int getLength() {
			return length;
		}

		public int getOffset() {
			return offset;
		}

		@Override
		public int compareTo(FoldingPosition o) {
			return offset-o.offset;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + offset;
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			FoldingPosition other = (FoldingPosition) obj;
			if (offset != other.offset)
				return false;
			return true;
		}

		@Override
		public String toString() {
			return "FoldingPosition [offset=" + offset + ", length=" + length + "]";
		}
		
	}

}
