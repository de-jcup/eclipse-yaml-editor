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

import java.util.List;

import org.junit.Before;
import org.junit.Test;

public class YamlScriptModelBuilderTest {

	private YamlScriptModelBuilder builderToTest;

	@Before
	public void before() {
		builderToTest = new YamlScriptModelBuilder();
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
		assertEquals(1,nodes.size());
		YamlNode node1 = nodes.iterator().next();
		YamlNode node2 = node1.getChildren().iterator().next();
		assertEquals("value1",node2.getName());
	}

}
