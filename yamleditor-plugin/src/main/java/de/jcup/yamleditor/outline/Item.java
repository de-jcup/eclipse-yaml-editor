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

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Item {

    ItemType type;
	String name;
	int offset;
	int length;
	int endOffset;
	private List<Item> children=new ArrayList<>();
	Item parent;
	
	/**
	 * @return item type , or <code>null</code>
	 */
	public ItemType getItemType(){
		return type;
	}

	public String getName() {
		return name;
	}

	public int getOffset() {
		return offset;
	}

	public int getLength() {
		return length;
	}
	
	public int getEndOffset() {
		return endOffset;
	}
	
	public Item getParent() {
        return parent;
    }
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Item:");
		sb.append("name:");
		sb.append(name);
		sb.append(",type:");
		sb.append(type);
		sb.append(",offset:");
		sb.append(offset);
		sb.append(",length:");
		sb.append(length);
		sb.append(",endOffset:");
		sb.append(endOffset);
		return sb.toString();
	}
	
	@Override
    public int hashCode() {
        return Objects.hash(endOffset, name, offset, type);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof Item)) {
            return false;
        }
        Item other = (Item) obj;
        return endOffset == other.endOffset && Objects.equals(name, other.name) && offset == other.offset && type == other.type;
    }


	public String buildSearchString() {
		return name;
	}

	public boolean hasChildren() {
		return children.size()>0;
	}

	public List<Item> getChildren() {
		return children;
	}

	public boolean isRoot() {
		return false;
	}
}
