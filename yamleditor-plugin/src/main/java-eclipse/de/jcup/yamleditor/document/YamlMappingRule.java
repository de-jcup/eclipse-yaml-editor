package de.jcup.yamleditor.document;

import org.eclipse.jface.text.rules.IToken;

public class YamlMappingRule extends YamlLineStartsWithRule{

	public YamlMappingRule(IToken token) {
		super(":","",true, false,token);
	}

	@Override
	protected boolean isAcceptedAtStart(int c) {
		/* is accepted  "      sample: xxx" accepts until :*/
		/* is accepted  " - sample: xxx" accepts until :*/
		/* is accepted  " - sample: xxx" accepts until :*/
		/* is accepted  " - sample: xxx:yyy:zzz" accepts until first:*/
		return c!=':';
	}
}
