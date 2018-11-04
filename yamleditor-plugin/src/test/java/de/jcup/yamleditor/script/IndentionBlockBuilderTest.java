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

import org.junit.Before;
import org.junit.Test;

public class IndentionBlockBuilderTest {

	private IndentionBlockBuilder builderToTest;

	@Before
	public void before() throws Exception {
		builderToTest = new IndentionBlockBuilder();
	}

	
	@Test
	public void found_NO_block_when_children_block_has_only_1_entry() {
		/* prepare */
		StringBuilder sb = new StringBuilder();
		// .......012345
		sb.append("1234\n");
		// ........678/9/0
		sb.append("  8\n\n");
		// ........11-15
		sb.append("1234\n");
		String text = sb.toString();
		
		/* execute */
		List<IndentionBlock> result = builderToTest.build(text);
		
		/* test */
		assertEquals(0,result.size());
		
	}
	
	@Test
	public void found_NO_block_when_children_block_has_only_2_entries() {
		/* prepare */
		StringBuilder sb = new StringBuilder();
		// .......012345
		sb.append("1234\n");
		// ........678/9/0
		sb.append("  8\n\n");
		sb.append("  8\n\n");
		// ........11-15
		sb.append("1234\n");
		String text = sb.toString();
		
		/* execute */
		List<IndentionBlock> result = builderToTest.build(text);
		
		/* test */
		assertEquals(0,result.size());
		
	}

	@Test
	public void found_one_Block_when_3_chilren_in_block() {
		/* prepare */
		StringBuilder sb = new StringBuilder();
		// ........12345
		sb.append("1234\n");
		// ........678/9/0
		sb.append("  8\n\n");
		// ........123/4/5
		sb.append("  8\n\n");
		// ........678/9/0
		sb.append("  8\n\n");
		// ........123/4/5
		sb.append("1234\n");
		String text = sb.toString();
		
		/* execute */
		List<IndentionBlock> result = builderToTest.build(text);
		
		/* test */
		assertEquals(1,result.size());
		Iterator<IndentionBlock> iterator = result.iterator();

		IndentionBlock block1 = iterator.next();
		assertEquals(0,block1.getStart());
		assertEquals(19,block1.getEnd());
		assertEquals(0,block1.getIndention());
		
	}
	
	@Test
	public void found_one_Block_when_3_chilren_different_indents_in_block() {
		/* prepare */
		StringBuilder sb = new StringBuilder();
		// ........12345
		sb.append("1234\n");
		// ........678/9/0
		sb.append("  8\n\n");
		// ........123/4/5
		sb.append("     8\n\n");
		// ........678/9/0
		sb.append("  8\n\n");
		// ........123/4/5
		sb.append("1234\n");
		String text = sb.toString();
		
		/* execute */
		List<IndentionBlock> result = builderToTest.build(text);
		
		/* test */
		assertEquals(1,result.size());
		Iterator<IndentionBlock> iterator = result.iterator();

		IndentionBlock block1 = iterator.next();
		assertEquals(0,block1.getStart());
		assertEquals(22,block1.getEnd());
		assertEquals(0,block1.getIndention());
		
	}
	
	@Test
	public void found_two_Blocks_when_3_chilren_in_blocks() {
		/* prepare */
		StringBuilder sb = new StringBuilder();
		// ........12345
		sb.append("1234\n");
		sb.append("  8\n\n");
		sb.append("  8\n\n");
		sb.append("  8\n\n");

		sb.append("1234\n");
		sb.append("  8\n\n");
		sb.append("  8\n\n");
		sb.append("  8");

		String text = sb.toString();
		
		/* execute */
		List<IndentionBlock> result = builderToTest.build(text);
		
		/* test */
		assertEquals(2,result.size());
		Iterator<IndentionBlock> iterator = result.iterator();

		IndentionBlock block1 = iterator.next();
		assertEquals(0,block1.getStart());
		assertEquals(19,block1.getEnd());
		assertEquals(0,block1.getIndention());
		
		IndentionBlock block2 = iterator.next();
		assertEquals(20,block2.getStart());
		assertEquals(38,block2.getEnd());
		assertEquals(0,block2.getIndention());
		
	}
}
