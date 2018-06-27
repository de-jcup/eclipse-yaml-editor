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
package de.jcup.yamleditor.outline;

import java.util.List;

import org.eclipse.jface.viewers.ITreeContentProvider;

import de.jcup.yamleditor.script.YamlNode;
import de.jcup.yamleditor.script.YamlScriptModel;

public class YamlEditorTreeContentProvider implements ITreeContentProvider {

	private static final String YAML_SCRIPT_CONTAINS_ERRORS = "YAML file contains errors.";
	private static final Object[] RESULT_WHEN_EMPTY = new Object[] { };
	private Object[] items;
	private Object monitor = new Object();

	YamlEditorTreeContentProvider() {
		items = RESULT_WHEN_EMPTY;
	}

	@Override
	public Object[] getElements(Object inputElement) {
		synchronized (monitor) {
			if (inputElement!=null && !(inputElement instanceof YamlScriptModel)) {
				return new Object[] { "Unsupported input element:"+inputElement };
			}
			if (items != null && items.length > 0) {
				return items;
			}
		}
		return RESULT_WHEN_EMPTY;
	}

	@Override
	public Object[] getChildren(Object parentElement) {
		if (parentElement instanceof Item){
			Item item = (Item) parentElement;
			return item.getChildren().toArray();
		}
		return null;
	}

	@Override
	public Object getParent(Object element) {
		if (element instanceof Item){
			Item item = (Item) element;
			if (item.parent==null || item.parent.isRoot()){
				return null;
			}
			return item.parent;
		}
		return null;
	}

	@Override
	public boolean hasChildren(Object element) {
		if (element instanceof Item){
			Item item = (Item) element;
			return item.hasChildren();
		}
		return false;
	}

	private Item[] createItems(YamlScriptModel model) {
		List<YamlNode> nodes = model.getRootNode().getChildren();
		RootItem root = new RootItem();
		List<Item> list = root.getChildren();
		buildItems(root, nodes);
		
		if (model.hasErrors()) {
			Item item = new Item();
			item.name = YAML_SCRIPT_CONTAINS_ERRORS;
			item.type = ItemType.META_ERROR;
			item.offset = 0;
			item.length = 0;
			item.endOffset=0;
			list.add(0, item);
		}
		return list.toArray(new Item[list.size()]);

	}

	protected void buildItems(Item parent, List<YamlNode> nodes) {
		List<Item> children = parent.getChildren();
		for (YamlNode yamlNode: nodes){
			Item child = new Item();
			child.offset = yamlNode.getPosition();
			child.endOffset=yamlNode.getEnd();
			child.length=yamlNode.getLengthToNameEnd();
			child.name=yamlNode.getName();
			children.add(child);
			child.parent=parent;
			if (yamlNode.hasChildren()){
				buildItems(child, yamlNode.getChildren());
			}
		}
	}

	public void rebuildTree(YamlScriptModel model) {
		synchronized (monitor) {
			if (model == null) {
				items = null;
				return;
			}
			items = createItems(model);
		}
	}

	public Item tryToFindByOffset(int offset) {
		synchronized (monitor) {
			if (items==null){
				return null;
			}
			for (Object oitem: items){
				if (!(oitem instanceof Item)){
					continue;
				}
				Item item = (Item) oitem;
				int itemStart = item.getOffset();
				int itemEnd = item.getEndOffset();// old: itemStart+item.getLength();
				if (offset >= itemStart && offset<=itemEnd ){
					return item;
				}
			}
			
		}
		return null;
	}

}
