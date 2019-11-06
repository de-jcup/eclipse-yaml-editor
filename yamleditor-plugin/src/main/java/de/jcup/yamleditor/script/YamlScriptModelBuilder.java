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
import java.util.List;

import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.error.Mark;
import org.yaml.snakeyaml.error.MarkedYAMLException;
import org.yaml.snakeyaml.nodes.MappingNode;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.NodeTuple;
import org.yaml.snakeyaml.nodes.ScalarNode;
import org.yaml.snakeyaml.nodes.SequenceNode;

import de.jcup.yamleditor.script.YamlScriptModel.FoldingPosition;

public class YamlScriptModelBuilder {

    private static final YamlSanitizer[] NO_SANITIZING = new YamlSanitizer[0];
    private static boolean DEBUG = Boolean.getBoolean("de.jcup.yamleditor.model.debug");
    private Yaml yamlParser;
    private boolean calculateFoldings;
    private YamlSanitizer[] sanitizers = NO_SANITIZING;

    public YamlScriptModelBuilder() {
        yamlParser = new Yaml();
    }

    public YamlScriptModelBuilder setSanitizers(YamlSanitizer... sanitizers) {
        if (sanitizers == null) {
            this.sanitizers = NO_SANITIZING;
        } else {
            this.sanitizers = sanitizers;
        }
        return this;
    }

    public YamlScriptModelBuilder setCalculateFoldings(boolean calculateFoldings) {
        this.calculateFoldings = calculateFoldings;
        return this;
    }

    public YamlScriptModel build(String unsafeSourceCode) {

        YamlScriptModel model = new YamlScriptModel();
        try {
            YamlSanitizerContext context = new YamlSanitizerContext();
            String escapedSource = unsafeSourceCode;
            for (YamlSanitizer yamlSanitizer : this.sanitizers) {
                escapedSource = yamlSanitizer.sanitize(escapedSource,context);
            }
            model.getMessages().addAll(context.getSanitizerMessages());
            
            StringReader reader = new StringReader(escapedSource);

            YamlNode root = model.getRootNode();
            Iterable<Node> nodes = yamlParser.composeAll(reader);

            for (Node node : nodes) {
                buildNode(model, root, node);
            }
            if (calculateFoldings) {
                /* build folding */
                IndentionBlockBuilder builder = new IndentionBlockBuilder();
                List<IndentionBlock> blocks = builder.build(escapedSource);
                transformIndentionsToFoldings(model, blocks);
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

    private void transformIndentionsToFoldings(YamlScriptModel model, List<IndentionBlock> blocks) {
        for (IndentionBlock block : blocks) {
            model.addFolding(new FoldingPosition(block.getStart(), block.getLength()));
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
        return resolveName(node, -1);
    }

    protected String resolveName(Node node, int count) {
        String name = internalResolveName(node, count);
        if (DEBUG) {
            name = node.getClass().getSimpleName() + ":" + name;
        }
        return name;
    }

    private String internalResolveName(Node node, int count) {
        if (node instanceof ScalarNode) {
            return ((ScalarNode) node).getValue();
        }
        if (node instanceof SequenceNode) {
            if (count > -1) {
                return "" + count + ",";
            } else {
                return "<sequence>";
            }
        }
        if (node instanceof MappingNode) {
            if (count > -1) {
                return "[" + count + "]";
            } else {
                return "<mapping>";
            }

        }
        return node.getType().getName();
    }

    private void appendSequence(YamlScriptModel model, YamlNode parent, SequenceNode node) {
        int count = 0;
        List<Node> values = node.getValue();
        int valueCount = values.size();
        for (Node element : node.getValue()) {
            createYamlNodeAndAddToParent(model, parent, element, count++, valueCount);
        }

    }

    private void appendMappings(YamlScriptModel model, YamlNode parent, MappingNode node) {
        int count = 0;
        List<NodeTuple> values = node.getValue();
        int valueCount = values.size();
        for (NodeTuple nodeTuple : values) {
            Node keyNode = nodeTuple.getKeyNode();
            YamlNode yamlkeyNode = createYamlNodeAndAddToParent(model, parent, keyNode, count++, valueCount);

            Node valNode = nodeTuple.getValueNode();
            createYamlNodeAndAddToParent(model, yamlkeyNode, valNode, -1, -1);
        }
    }

    protected YamlNode createYamlNodeAndAddToParent(YamlScriptModel model, YamlNode parent, Node node, int count, int maxCount) {
        YamlNode yamlNodeToAppendNext = parent;
        if (isShown(node, maxCount)) {
            yamlNodeToAppendNext = buildShownNode(model, parent, node, count);
        } else {
            buildNode(model, parent, node);
        }

        return yamlNodeToAppendNext;
    }

    private YamlNode buildShownNode(YamlScriptModel model, YamlNode parent, Node node, int count) {
        YamlNode yamlNodeToAppendNext = null;
        if (node instanceof ScalarNode) {
            String keyName = resolveName(node);
            YamlNode yamlNode = new YamlNode(keyName);
            prepare(yamlNode, node);
            parent.getChildren().add(yamlNode);
            yamlNodeToAppendNext = yamlNode;

        } else if (node instanceof MappingNode) {
            MappingNode mp = (MappingNode) node;
            String keyName = resolveName(node, count);
            YamlNode yamlNode = new YamlNode(keyName);
            prepare(yamlNode, node);
            parent.getChildren().add(yamlNode);

            appendMappings(model, yamlNode, mp);

            yamlNodeToAppendNext = yamlNode;
        } else {
            yamlNodeToAppendNext = new YamlNode("<failure>");
        }
        return yamlNodeToAppendNext;
    }

    private boolean isShown(Node node, int maxCount) {
        if (node instanceof ScalarNode) {
            return true;
        }
        if (node instanceof MappingNode) {
            if (maxCount > 1) {

                /*
                 * @formattter:off special: we have problems to differ between
                 * 
                 * X - val1: A val2: B ---- and ----- X val1: A val2: B
                 * 
                 * To avoid having [0] as output for last variant we do not show array indexes
                 * for mappings with one key only!
                 * 
                 * @formattter:on
                 * 
                 */
                return true;
            }
            return false;
        }
        return false;
    }

    void prepare(YamlNode yamlNode, Node node) {
        Mark start = node.getStartMark();
        yamlNode.pos = start.getIndex();
        yamlNode.end = yamlNode.pos + yamlNode.getName().length();
        yamlNode.snakeNode = node;
    }
}
