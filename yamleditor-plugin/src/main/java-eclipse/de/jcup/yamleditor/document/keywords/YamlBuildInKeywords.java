/*
 * Copyright 2017 Albert Tregnaghi
 *
 * Licensed under the Apache License, Version 2.0 (the"License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *		http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an"AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions
 * and limitations under the License.
 *
 */
package de.jcup.yamleditor.document.keywords;

//see https://en.wikibooks.org/wiki/Windows_Yaml_Scripting
public enum YamlBuildInKeywords implements DocumentKeyWord {
	ASSOC("Associates an extension with a file type (FTYPE)."),

	BREAK("Sets or clears extended CTRL+C checking."),

	CALL("Calls one yaml program from another."),

	CD("Displays or sets the current directory."),

	CHDIR("Displays or sets the current directory."),

	CHCP("Displays or sets the active code page number."),

	CLS("Clears the screen."),

	COLOR("Sets the console foreground and background colors."),

	COPY("Copies files."),

	DATE("Displays and sets the system date."),

	// http://www.robvanderwoude.com/battech_defined.php
	DEFINED("In Windows NT 4's CMD.EXE a new IF statement was introduced:\n"
			+ "The DEFINED conditional [...] takes an environment variable name and returns true if the environment variable is defined.\n"
			+ "This does require the Command Extensions to be enabled, so make sure you check this before using IF DEFINED."),
	DEL,

	ERASE("Deletes one or more files."),

	DIR("Displays a list of files and subdirectories in a directory."),

	ECHO("Displays messages, or turns command echoing on or off."),
	ECHO_DOT("echo.","Special variant for empty lines"),

	ELSE("Performs conditional processing in yaml programs when \"IF\" is not true."),

	ENDLOCAL("Ends localization of environment changes in a yaml file."),

	EXIT("Quits the CMD.EXE program (command interpreter)."),

	FOR("Runs a specified command for each file in a set of files."),

	FTYPE("Sets the file type command."),

	/* was missing */ GOTO,

	IF("Performs conditional processing in yaml programs."),

	/* was missing */ IN,
	
	/* was missing */ NOT,

	MD("Creates a directory."), MKDIR("Creates a directory."), MOVE("Moves a file to a new location"),

	PATH("Sets or modifies the PATH environment"),

	PAUSE("Causes the command session to pause for user input."),

	POPD("Changes to the drive and directory popped from the directory stack"),

	PROMPT("Sets or modifies the string displayed when waiting for input."),

	PUSHD("Pushes the current directory onto the stack, and changes to the new directory."),

	RD("Removes the directory."),

	RMDIR("Removes the directory."),
	// REM A comment command. Unlike double-colon (::), the command can be
	// executed.

	REN("Renames a file or directory"),

	RENAME("Renames a file or directory"),

	SET("Sets or displays shell environment variables"),

	SETLOCAL("Creates a child-environment for the yaml file."),

	SHIFT("Moves the yaml parameters forward."),

	START("Starts a program with various options."),

	TIME("Displays or sets the system clock"),

	TITLE("Changes the window title"),

	TYPE("Prints the content of a file to the console."),

	VER("Shows the command processor, operating system versions."),

	VERIFY("Verifies that file copy has been done correctly."),

	VOL("Shows the label of the current volume."),

	;

	private String text;
	private String description;

	YamlBuildInKeywords() {
		this(null, null);
	}

	YamlBuildInKeywords(String description) {
		this(null, description);
	}

	YamlBuildInKeywords(String text, String description) {
		this.text = text;
		this.description = description;
		if (this.description == null) {
			this.description = "none";
		}
	}

	@Override
	public String getText() {
		if (text == null) {
			text = name().toLowerCase();
		}
		return text;
	}

	public String getDescription() {
		return description;
	}

	@Override
	public boolean isBreakingOnEof() {
		return true;
	}
}
