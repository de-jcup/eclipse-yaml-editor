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

import org.yaml.snakeyaml.nodes.Node;

public class YamlNode {

	private String name;
	private List<YamlNode> children = new ArrayList<>();
	int pos;
	int end;
	Node snakeNode;
	boolean debug;
	
	public boolean isDebug() {
		return debug;
	}

	public YamlNode(String name) {
		this.name = name;
	}
	
	public Node getSnakeNode() {
		return snakeNode;
	}

	public String getName() {
		return name;
	}

	public int getPosition() {
		return pos;
	}

	public int getLengthToNameEnd() {
		return name.length();
	}

	public int getEnd() {
		return end;
	}

	public boolean hasChildren() {
		return children.size()>0;
	}
	
	public List<YamlNode> getChildren() {
		return children;
	}

}
