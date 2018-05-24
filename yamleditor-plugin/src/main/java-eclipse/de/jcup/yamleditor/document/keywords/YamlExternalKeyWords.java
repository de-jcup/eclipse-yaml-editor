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
 package de.jcup.yamleditor.document.keywords;
 
//see https://en.wikibooks.org/wiki/Windows_Yaml_Scripting
public enum YamlExternalKeyWords implements DocumentKeyWord{

	ARP, AT, ATTRIB, BCDEDIT, CACLS, CHCP, CHKDSK, CHKNTFS, CHOICE, CIPHER, CLIP, CMD, COMP, COMPACT, 
	CONVERT, DEBUG, DISKCOMP, DISKCOPY, DISKPART, DOSKEY, DRIVERQUERY, EXPAND, FC, FIND, FINDSTR, FORFILES, FORMAT, 
	FSUTIL, GPRESULT, GRAFTABL, HELP, ICACLS, IPCONFIG, LABEL, MAKECAB, MODE, MORE, NET, OPENFILES, PING, RECOVER, REG, 
	REPLACE, ROBOCOPY, RUNDLL32, SC, SCHTASKS, SETX, SHUTDOWN, SORT, SUBST, SYSTEMINFO, TASKKILL, TASKLIST, TIMEOUT, 
	TREE, WHERE, WMIC, XCOPY,
	
	/* additional windows commands not listed in wiki book:*/
	REGEDIT,

	;


	private String text;

	private YamlExternalKeyWords() {
		this.text = name().toLowerCase();
	}
	
	@Override
	public String getText() {
		return text;
	}
	
	@Override
	public boolean isBreakingOnEof() {
		return true;
	}
}
