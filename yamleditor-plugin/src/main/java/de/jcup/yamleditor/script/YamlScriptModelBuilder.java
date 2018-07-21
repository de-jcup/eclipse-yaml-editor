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
import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;

import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.error.Mark;
import org.yaml.snakeyaml.error.MarkedYAMLException;
import org.yaml.snakeyaml.nodes.MappingNode;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.NodeTuple;
import org.yaml.snakeyaml.nodes.ScalarNode;
import org.yaml.snakeyaml.nodes.SequenceNode;

import de.jcup.yamleditor.YamlEditorDebugSettings;
import de.jcup.yamleditor.preferences.YamlEditorPreferences;
import de.jcup.yamleditor.script.YamlScriptModel.FoldingPosition;

public class YamlScriptModelBuilder {
	private Yaml yamlParser;

	public YamlScriptModelBuilder() {
		yamlParser = new Yaml();
	}

	public YamlScriptModel build(String text) {

		YamlScriptModel model = new YamlScriptModel();
		try {
			StringReader reader = new StringReader(text);

			Iterable<Node> nodes = yamlParser.composeAll(reader);
			YamlNode root = model.getRootNode();
			for (Node node : nodes) {
				buildNode(model, root, node);
			}
			buildFoldingAll(model);

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

	private void buildFoldingAll(YamlScriptModel model) {
		if (!YamlEditorPreferences.getInstance().isCodeFoldingEnabled()) {
			return;
		}

		YamlNode node = model.getRootNode();
		Context context = new Context(model.getFoldingPositions());
		buildFolding(context, node);
		model.getRootNode().getChildren().addAll(context.debugNodes);
	}

	private class Context {
		private SortedSet<FoldingPosition> foldingPositions;

		private Context(SortedSet<FoldingPosition> foldingPositions) {
			this.foldingPositions = foldingPositions;
		}

		private List<YamlNode> debugNodes = new ArrayList<YamlNode>();
	}

	private void buildFolding(Context context, YamlNode node) {
		List<YamlNode> children = node.getChildren();
		YamlNode childBefore= null;
		for (YamlNode child : children) {
			buildFolding(context, child);
			if (childBefore!=null){
				FoldingPosition foldingForChildBefore = new FoldingPosition(childBefore.pos, child.pos - childBefore.pos);
				context.foldingPositions.add(foldingForChildBefore);
				
				if (YamlEditorDebugSettings.DEBUG_OUTLINE_ENABLED) {
					String name = node.getName() + "=" + foldingForChildBefore.toString();
					
					YamlNode debugNode = new YamlNode(name);
					debugNode.pos = foldingForChildBefore.getOffset();
					debugNode.end = foldingForChildBefore.getOffset()+foldingForChildBefore.getLength();
					debugNode.debug = true;
					
					context.debugNodes.add(debugNode);
				}
			}
			childBefore=child;
		}
	}

	private void buildNode(YamlScriptModel model, YamlNode parent, Node node) {
		if (node instanceof MappingNode) {
			appendMappings(model, parent, (MappingNode) node);
			return;
		} else if (node instanceof SequenceNode) {
			appendSequence(model, parent, (SequenceNode) node);
		} else if (node instanceof ScalarNode) {
			appendScalar(model, parent, (ScalarNode) node);
		} else {
			/* anchor nodes are ignored */
		}
		return;
	}

	private void appendScalar(YamlScriptModel model, YamlNode parent, ScalarNode node) {
		YamlNode yamlNode = new YamlNode(resolveName(node));
		prepare(yamlNode, node);
		parent.getChildren().add(yamlNode);

	}

	protected String resolveName(Node node) {
		if (node instanceof ScalarNode) {
			return ((ScalarNode) node).getValue();
		}
		if (node instanceof SequenceNode) {
			return "<sequence>";
		}
		if (node instanceof MappingNode) {
			return "<mapping>";
		}
		return node.getType().getName();
	}

	private void appendSequence(YamlScriptModel model, YamlNode parent, SequenceNode node) {
		for (Node element : node.getValue()) {
			createYamlNodeAndAddToParent(model, parent, element);
		}

	}

	private void appendMappings(YamlScriptModel model, YamlNode parent, MappingNode node) {
		for (NodeTuple nodeTuple : node.getValue()) {
			Node keyNode = nodeTuple.getKeyNode();
			YamlNode yamlkeyNode = createYamlNodeAndAddToParent(model, parent, keyNode);

			Node valNode = nodeTuple.getValueNode();
			createYamlNodeAndAddToParent(model, yamlkeyNode, valNode);
		}
	}

	protected YamlNode createYamlNodeAndAddToParent(YamlScriptModel model, YamlNode parent, Node node) {
		YamlNode yamlNodeToAppendNext = parent;
		if (isShown(node)) {
			String keyName = resolveName(node);
			YamlNode yamlNode = new YamlNode(keyName);
			prepare(yamlNode, node);
			parent.getChildren().add(yamlNode);
			yamlNodeToAppendNext = yamlNode;
		} else {
			buildNode(model, parent, node);
		}

		return yamlNodeToAppendNext;
	}

	private boolean isShown(Node node) {
		return node instanceof ScalarNode;
	}

	void prepare(YamlNode yamlNode, Node node) {
		Mark start = node.getStartMark();
		yamlNode.pos = start.getIndex();
		yamlNode.end = yamlNode.pos + yamlNode.getName().length();
		yamlNode.snakeNode = node;
	}
}
