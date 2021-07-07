/*
 * Copyright 2020 Albert Tregnaghi
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

import static org.junit.Assert.*;

import org.junit.Test;

public class ItemTest {

    @Test
    public void item_add_child() {
        Item item1 = new Item();
        Item item2 = new Item();

        /* execute */
        item1.add(item2);

        /* test */
        assertEquals(item1, item2.getParent());
        assertTrue(item1.getChildren().contains(item2));
    }
    
    @Test
    public void item_createKeyFullPath() {
        Item key1 = new Item();
        key1.name="key1";
        Item key2 = new Item();
        key2.name="key2";
        Item key3 = new Item();
        
        key3.name="key3";
        Item value4 = new Item();
        
        value4.name="value4";
        Item key5 = new Item();
        key5.name="key5";
        
        Item value6 = new Item();
        value6.name="value6";
        
        RootItem rootItem = new RootItem();
        rootItem.name="rootitem-not-in-keys";
        rootItem.add(key1);

        /* execute */
        key1.add(key2);
        key1.add(key3);
        key3.add(value4); // no children, so leave and so value
        key3.add(key5);
        key5.add(value6); // no children, so leave and so value

        /* test */
        assertEquals("key1", key1.createKeyFullPath());
        assertEquals("key1", key2.createKeyFullPath());
        assertEquals("key1.key3", key3.createKeyFullPath());
        assertEquals("key1.key3", value4.createKeyFullPath());
        assertEquals("key1.key3.key5", key5.createKeyFullPath());
        assertEquals("key1.key3.key5", value6.createKeyFullPath());
    }

}
