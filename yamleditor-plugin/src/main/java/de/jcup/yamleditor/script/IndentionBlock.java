package de.jcup.yamleditor.script;

public class IndentionBlock {

	int indention;
	int start;
	int end;

	public IndentionBlock(int indention, int startIndex) {
		this.indention=indention;
		this.start=startIndex;
	}
	
	public int getStart() {
		return start;
	}
	
	public int getIndention() {
		return indention;
	}

	public int getEnd() {
		return end;
	}
	
	public int getLength(){
		return end-start;
	}
}
