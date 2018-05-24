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

import org.junit.Before;
import org.junit.Test;

import de.jcup.yamleditor.script.CodePosSupport;
import de.jcup.yamleditor.script.EchoParserSupport;
import de.jcup.yamleditor.script.EchoParserSupport.EchoParserSupportContext;

public class EchoParserSupportTest {

	private EchoParserSupport supportToTest;
	private EchoParserSupportContext contextToTest;

	@Before
	public void before() {
		supportToTest = new EchoParserSupport();
		contextToTest = new EchoParserSupportContext();
	}

	@Test
	public void internal_context__no_chars_is_not_in_escape_mode() {
		assertFalse(contextToTest.isInEscapeMode());
	}
	
	@Test
	public void internal_context__no_chars_is_terminating_on_ampersand() {
		assertTrue(contextToTest.isTerminating('&'));
	}
	
	@Test
	public void internal_context__escape_char_is_NOT_terminating_on_ampersand() {
		contextToTest.nextChar('^');
		assertFalse(contextToTest.isTerminating('&'));
	}
	
	@Test
	public void internal_context__no_chars_is_terminating_on_pipe() {
		assertTrue(contextToTest.isTerminating('|'));
	}
	
	@Test
	public void internal_context__escape_char_is_NOT_terminating_on_pipe() {
		contextToTest.nextChar('^');
		assertFalse(contextToTest.isTerminating('|'));
	}
	
	@Test
	public void internal_context__no_chars_is_terminating_on_lower() {
		assertTrue(contextToTest.isTerminating('<'));
	}
	
	@Test
	public void internal_context__escape_char_is_NOT_terminating_on_lower() {
		contextToTest.nextChar('^');
		assertFalse(contextToTest.isTerminating('<'));
	}
	
	@Test
	public void internal_context__no_chars_is_terminating_on_higher() {
		assertTrue(contextToTest.isTerminating('>'));
	}
	
	@Test
	public void internal_context__escape_char_is_NOT_terminating_on_higher() {
		contextToTest.nextChar('^');
		assertFalse(contextToTest.isTerminating('>'));
	}

	@Test
	public void internal_context__char_a_is_not_in_escape_mode() {
		contextToTest.nextChar('a');
		assertFalse(contextToTest.isInEscapeMode());
	}

	@Test
	public void internal_context__char_escape_is_in_escape_mode() {
		contextToTest.nextChar('^');
		assertTrue(contextToTest.isInEscapeMode());
	}

	@Test
	public void internal_context__char_escape_escape_is_NOT_in_escape_mode() {
		contextToTest.nextChar('^');
		contextToTest.nextChar('^');
		assertFalse(contextToTest.isInEscapeMode());
	}

	@Test
	public void internal_context__char_escape_escape_escape_is_in_escape_mode() {
		contextToTest.nextChar('^');
		contextToTest.nextChar('^');
		contextToTest.nextChar('^');
		assertTrue(contextToTest.isInEscapeMode());
	}
	
	@Test
	public void internal_context__char_escape_pipe_escape_is_in_escape_mode() {
		contextToTest.nextChar('^');
		contextToTest.nextChar('|');
		contextToTest.nextChar('^');
		assertTrue(contextToTest.isInEscapeMode());
	}

	@Test
	public void internal_context__char_escape_escape_escape_escape_is_NOT_in_escape_mode() {
		contextToTest.nextChar('^');
		contextToTest.nextChar('^');
		contextToTest.nextChar('^');
		contextToTest.nextChar('^');
		assertFalse(contextToTest.isInEscapeMode());
	}

	@Test
	public void internal_context__char_escape_pipe_is_NOT_in_escape_mode() {
		contextToTest.nextChar('^');
		contextToTest.nextChar('|');
		assertFalse(contextToTest.isInEscapeMode());
	}

	@Test
	public void echo_hello_pos_4_is_after_echo_handled_pos_after_end_is_9() {
		TestCodeSupport codePosSupport = new TestCodeSupport("echo hello", 4);
		// ....................................................01234567890

		assertTrue(supportToTest.isAfterEchoHandled(codePosSupport));
		assertEquals(9, codePosSupport.pos);
	}

	@Test
	public void ECHO_hello_pos_4_is_after_echo_handled_pos_after_end_is_9() {
		TestCodeSupport codePosSupport = new TestCodeSupport("ECHO hello", 4);
		// ....................................................0123456789

		assertTrue(supportToTest.isAfterEchoHandled(codePosSupport));
		assertEquals(9, codePosSupport.pos);
	}

	@Test
	public void echx_hello_pos_4_is_after_echo_NOT_handled_pos_after_end_is_4() {
		TestCodeSupport codePosSupport = new TestCodeSupport("echx hello", 4);
		// ....................................................0123456789

		assertFalse(supportToTest.isAfterEchoHandled(codePosSupport));
		assertEquals(4, codePosSupport.pos);
	}

	@Test
	public void echo_hello_pipe_gargamel_pos_4_is_after_echo_NOT_handled_pos_after_end_is_9() {
		TestCodeSupport codePosSupport = new TestCodeSupport("echo hello|pipe", 4);
		// ....................................................01234567890

		assertTrue(supportToTest.isAfterEchoHandled(codePosSupport));
		assertEquals(9, codePosSupport.pos);
	}

	@Test
	public void echo_hello_escape_pipe_gargamel_pos_4_is_after_echo_NOT_handled_pos_after_end_is_9() {
		TestCodeSupport codePosSupport = new TestCodeSupport("echo hello^|pipe", 4);
		// ....................................................0123456789012345

		assertTrue(supportToTest.isAfterEchoHandled(codePosSupport));
		assertEquals(15, codePosSupport.pos);
	}

	private class TestCodeSupport implements CodePosSupport {

		private String text;
		private int startpos;

		public TestCodeSupport(String text, int startPos) {
			this.text = text;
			this.startpos = startPos;
		}

		private int pos;

		@Override
		public void moveToPos(int newPos) {
			this.pos = newPos;

		}

		@Override
		public int getInitialStartPos() {
			return startpos;
		}

		@Override
		public Character getCharacterAtPosOrNull(int pos) {
			if (pos >= text.length() || pos < 0) {
				return null;
			}
			return text.charAt(pos);
		}

	}
}
