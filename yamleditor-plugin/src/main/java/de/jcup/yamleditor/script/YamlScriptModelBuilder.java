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

import java.io.StringReader;

import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.error.Mark;
import org.yaml.snakeyaml.error.MarkedYAMLException;
import org.yaml.snakeyaml.nodes.MappingNode;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.NodeTuple;
import org.yaml.snakeyaml.nodes.ScalarNode;
import org.yaml.snakeyaml.nodes.SequenceNode;
import org.yaml.snakeyaml.nodes.Tag;

public class YamlScriptModelBuilder {

	public YamlScriptModel build(String text) {

		YamlScriptModel model = new YamlScriptModel();
		Yaml yaml = new Yaml();
		try {
			StringReader reader = new StringReader(text);

			Iterable<Node> nodes = yaml.composeAll(reader);
			YamlNode root = model.getRootNode();
			for (Node node : nodes) {
				buildNode(root, node);
			}

		} catch (MarkedYAMLException e) {
			String message = e.getMessage();
			Mark problem = e.getProblemMark();
			int start = problem.getIndex();
			int end = start + 1;
			YamlError error = new YamlError(start, end, message);
			model.errors.add(error);
		}
		return model;
	}

	private void buildNode(YamlNode parent, Node node) {
		if (node instanceof MappingNode) {
			appendMappings(parent, (MappingNode) node);
			return;
		}else if(node instanceof SequenceNode){
			appendSequence(parent,(SequenceNode)node);
		}else if (node instanceof ScalarNode){
			appendScalar(parent,(ScalarNode)node);
		}else{
			/* anchor nodes are ignored*/
		}
		return;
	}

	private void appendScalar(YamlNode parent, ScalarNode node) {
		YamlNode yamlNode = new YamlNode(resolveName(node));
		prepare(yamlNode, node);
		parent.getChildren().add(yamlNode);
		
	}

	protected String resolveName(Node node) {
		if (node instanceof ScalarNode){
			return ((ScalarNode) node).getValue();
		}
		if (node instanceof SequenceNode){
			return "<sequence>";
		}
		if (node instanceof MappingNode){
			return "<mapping>";
		}
		return node.getType().getName();
	}

	private void appendSequence(YamlNode parent, SequenceNode node) {
		for (Node element : node.getValue()) {
			createYamlNodeAndAddToParent(parent, element);
		}
		
	}

	private void appendMappings(YamlNode parent, MappingNode node) {

		for (NodeTuple nodeTuple : node.getValue()) {
			Node keyNode = nodeTuple.getKeyNode();

			YamlNode yamlkeyNode = createYamlNodeAndAddToParent(parent, keyNode);

			Node valNode = nodeTuple.getValueNode();
			createYamlNodeAndAddToParent(yamlkeyNode, valNode);
		}
	}

	protected YamlNode createYamlNodeAndAddToParent(YamlNode parent, Node node) {
		YamlNode yamlNodeToAppendNext = parent;
		if (isShown(node)){
			String keyName = resolveName(node);
			YamlNode yamlNode = new YamlNode(keyName);
			prepare(yamlNode, node);
			parent.getChildren().add(yamlNode);
			yamlNodeToAppendNext= yamlNode;
		}else{
			buildNode(parent, node);
		}
		
		return yamlNodeToAppendNext;
	}

	private boolean isShown(Node node) {
		return node instanceof ScalarNode;
	}

	void prepare(YamlNode yamlNode, Node node) {
		Mark start = node.getStartMark();
		yamlNode.pos =start.getIndex();
		yamlNode.end = yamlNode.pos+yamlNode.getName().length();
	}
}
