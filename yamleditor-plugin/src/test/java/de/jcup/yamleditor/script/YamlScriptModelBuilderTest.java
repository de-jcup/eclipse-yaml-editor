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

import static org.junit.Assert.*;

import java.util.Iterator;
import java.util.List;
import java.util.SortedSet;

import org.junit.Before;
import org.junit.Test;

import de.jcup.yamleditor.script.YamlScriptModel.FoldingPosition;

public class YamlScriptModelBuilderTest {

    private YamlScriptModelBuilder builderToTest;

    @Before
    public void before() {
        builderToTest = new YamlScriptModelBuilder().setCalculateFoldings(true);
    }

    @Test
    public void bugfix_72_outline_simple_kubernetes_example() {
        /* prepare */
        StringBuilder sb = new StringBuilder();
        sb.append("apiVersion: v1\n");
        sb.append("kind: Pod\n");
        sb.append("metadata:\n");
        sb.append("    name: busybox-sleep\n");
        
        /* execute */
        YamlScriptModel result = builderToTest.build(sb.toString());
        
        /* test */
        List<YamlNode> rootChildren = result.getRootNode().getChildren();
        assertEquals(3, rootChildren.size()); // AList
        Iterator<YamlNode> iterator = rootChildren.iterator();
        YamlNode apiVersion = iterator.next();
        assertEquals("apiVersion",apiVersion.getName());
        assertEquals(1,apiVersion.getChildren().size());
        assertEquals("v1",apiVersion.getChildren().iterator().next().getName());
        
        YamlNode kind = iterator.next();
        assertEquals("kind",kind.getName());
        assertEquals(1,kind.getChildren().size());
        assertEquals("Pod",kind.getChildren().iterator().next().getName());
        
        YamlNode metadata = iterator.next();
        assertEquals("metadata", metadata.getName());
        List<YamlNode> metaDataChildren = metadata.getChildren();
        assertEquals(1, metaDataChildren.size());
        YamlNode metaDataName = metaDataChildren.iterator().next();
        assertEquals("name", metaDataName.getName());
    }

    @Test
    public void bugfix_72_outline_contains_two_list_items_for_sequence_mapping() {
        /* prepare */
        StringBuilder sb = new StringBuilder();
        sb.append("AList:\n");
        sb.append("    - Name: A\n");
        sb.append("      Year: 1970\n");
        sb.append("    - Name: B\n");
        sb.append("      Year: 1971\n");
        sb.append("BList:\n");
        sb.append("    - Name: C\n");
        sb.append("      Year: 1972\n");
        sb.append("    - Name: D\n");
        sb.append("      Year: 1973\n");
        sb.append("CList:\n");
        sb.append("    - Name: D\n");
        sb.append("      Year: 1974\n");

        /* execute */
        YamlScriptModel result = builderToTest.build(sb.toString());

        /* test */
        List<YamlNode> rootChildren = result.getRootNode().getChildren();
        assertEquals(3, rootChildren.size()); // AList
        Iterator<YamlNode> iterator = rootChildren.iterator();
        YamlNode aList = iterator.next();
        YamlNode bList = iterator.next();
        YamlNode cList = iterator.next();

        testAListParts(aList);
        testBListParts(bList);
        testCListParts(cList);

    }

    private void testAListParts(YamlNode aList) {
        // # +AList
        // # |_[0]
        // # | |+Name
        // # | | |_A
        // # | |+Year
        // # | |_1970
        // # |_[1]
        // # | |+Name
        // # | | |_B
        // # | |+Year
        // # | |_1971
        assertEquals("AList", aList.getName());
        assertEquals(2, aList.getChildren().size()); // Entry0, Entry1

        Iterator<YamlNode> alistIterator = aList.getChildren().iterator();

        /* entry 0 */
        YamlNode entry0 = alistIterator.next();
        assertEquals("[0]", entry0.getName());
        assertEquals(2, entry0.getChildren().size());
        Iterator<YamlNode> entry0It = entry0.getChildren().iterator();
        YamlNode nameA = entry0It.next();
        assertEquals("Name", nameA.getName());
        assertEquals("A", nameA.getChildren().iterator().next().getName());

        YamlNode year1970 = entry0It.next();
        assertEquals("Year", year1970.getName());
        assertEquals("1970", year1970.getChildren().iterator().next().getName());

        /* entry 1 */
        YamlNode entry1 = alistIterator.next();
        assertEquals("[1]", entry1.getName());
        assertEquals(2, entry1.getChildren().size());
        Iterator<YamlNode> entry1It = entry1.getChildren().iterator();
        YamlNode nameB = entry1It.next();
        assertEquals("Name", nameB.getName());
        assertEquals("B", nameB.getChildren().iterator().next().getName());

        YamlNode year1971 = entry1It.next();
        assertEquals("Year", year1971.getName());
        assertEquals("1971", year1971.getChildren().iterator().next().getName());
    }

    private void testBListParts(YamlNode bList) {
        // # +BList
        // # |_[0]
        // # | |+Name
        // # | | |_C
        // # | |+Year
        // # | |_1972
        // # |_[1]
        // # | |+Name
        // # | | |_D
        // # | |+Year
        // # | |_1973
        assertEquals("BList", bList.getName());
        assertEquals(2, bList.getChildren().size()); // Entry0, Entry1

        Iterator<YamlNode> alistIterator = bList.getChildren().iterator();

        /* entry 0 */
        YamlNode entry0 = alistIterator.next();
        assertEquals("[0]", entry0.getName());
        assertEquals(2, entry0.getChildren().size());
        Iterator<YamlNode> entry0It = entry0.getChildren().iterator();
        YamlNode nameC = entry0It.next();
        assertEquals("Name", nameC.getName());
        assertEquals("C", nameC.getChildren().iterator().next().getName());

        YamlNode year1972 = entry0It.next();
        assertEquals("Year", year1972.getName());
        assertEquals("1972", year1972.getChildren().iterator().next().getName());

        /* entry 1 */
        YamlNode entry1 = alistIterator.next();
        assertEquals("[1]", entry1.getName());
        assertEquals(2, entry1.getChildren().size());
        Iterator<YamlNode> entry1It = entry1.getChildren().iterator();
        YamlNode nameD = entry1It.next();
        assertEquals("Name", nameD.getName());
        assertEquals("D", nameD.getChildren().iterator().next().getName());

        YamlNode year1973 = entry1It.next();
        assertEquals("Year", year1973.getName());
        assertEquals("1973", year1973.getChildren().iterator().next().getName());
    }

    private void testCListParts(YamlNode cList) {
        /* special: having only ONE entry here, we want not: */
        // # +CList
        // # |_[0]
        // # | |+Name
        // # | | |_D
        // # | |+Year
        // # | |_1974
        /* but instead: */
        // # +CList
        // #  |+Name
        // #  | |_D
        // #  |+Year
        // #  |_1974
        assertEquals("CList", cList.getName());
        assertEquals(2, cList.getChildren().size()); // Entry0

        Iterator<YamlNode> clistIterator = cList.getChildren().iterator();

        YamlNode nameC = clistIterator.next();
        assertEquals("Name", nameC.getName());
        assertEquals("D", nameC.getChildren().iterator().next().getName());

        YamlNode year1974 = clistIterator.next();
        assertEquals("Year", year1974.getName());
        assertEquals("1974", year1974.getChildren().iterator().next().getName());
    }

    @Test
    public void bugfix_72_outline_contains_four_list_items_for_scalars() {
        /* prepare */
        StringBuilder sb = new StringBuilder();
        sb.append("AList:\n");
        sb.append("    - A\n");
        sb.append("    - B\n");
        sb.append("    - C\n");
        sb.append("    - D\n");

        /* execute */
        YamlScriptModel result = builderToTest.build(sb.toString());

        /* test */
        List<YamlNode> rootChildren = result.getRootNode().getChildren();
        assertEquals(1, rootChildren.size()); // AList
        YamlNode aList = rootChildren.iterator().next();

        assertEquals(4, aList.getChildren().size()); // A, B, C, D

        Iterator<YamlNode> alistIterator = aList.getChildren().iterator();

        assertEquals("A", alistIterator.next().getName());
        assertEquals("B", alistIterator.next().getName());
        assertEquals("C", alistIterator.next().getName());
        assertEquals("D", alistIterator.next().getName());

    }

    @Test
    public void folding_pos_correct_when_last_line() {
        /* prepare */
        StringBuilder sb = new StringBuilder();
        sb.append("entry:\n");
        sb.append("  subentry1:\n");
        sb.append("  subentry2:\n");
        sb.append("  subentry3:\n");

        /* execute */
        YamlScriptModel result = builderToTest.build(sb.toString());

        /* test */
        SortedSet<FoldingPosition> foldingPositions = result.getFoldingPositions();
        assertEquals(1, foldingPositions.size());
        FoldingPosition first = foldingPositions.iterator().next();
        assertEquals(0, first.getOffset());
        assertEquals(sb.length(), first.getLength());
    }

    @Test
    public void folding_pos_correct_when_has_following_line() {
        /* execute */
        StringBuilder sb = new StringBuilder();
        sb.append("entry:\n");
        sb.append("  subentry1:\n");
        sb.append("  subentry2:\n");
        sb.append("  subentry3:\n");

        StringBuilder sb2 = new StringBuilder();
        sb2.append(sb.toString());
        sb2.append("entry:\n");
        sb2.append("  subentry1:\n");

        YamlScriptModel result = builderToTest.build(sb2.toString());

        /* test */
        SortedSet<FoldingPosition> foldingPositions = result.getFoldingPositions();
        assertEquals(1, foldingPositions.size());
        FoldingPosition first = foldingPositions.iterator().next();
        assertEquals(0, first.getOffset());
        assertEquals(sb.length(), first.getLength());
    }

    @Test
    public void empty_string_has_no_nodes() {
        /* execute */
        YamlScriptModel result = builderToTest.build("");

        /* test */
        assertTrue(result.getRootNode().getChildren().isEmpty());
        assertFalse(result.hasErrors());

    }

    @Test
    public void test_tab_makes_problems() {
        /* execute */
        String text = "test:\txx";
        YamlScriptModel result = builderToTest.build(text);

        /* test */
        List<YamlNode> nodes = result.getRootNode().getChildren();
        assertTrue(nodes.isEmpty());
        assertTrue(result.hasErrors());
    }

    @Test
    public void scalar_with_test_has_name_test_and_child_with_value1() {
        /* execute */
        String text = "test:\n  value1";
        YamlScriptModel result = builderToTest.build(text);

        /* test */
        List<YamlNode> nodes = result.getRootNode().getChildren();
        assertFalse(result.hasErrors());
        assertEquals(1, nodes.size());
        YamlNode node1 = nodes.iterator().next();
        YamlNode node2 = node1.getChildren().iterator().next();
        assertEquals("value1", node2.getName());
    }

}
