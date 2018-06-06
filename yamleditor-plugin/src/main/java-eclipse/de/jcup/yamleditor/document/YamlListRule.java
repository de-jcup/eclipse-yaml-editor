package de.jcup.yamleditor.document;

import org.eclipse.jface.text.rules.IToken;

public class YamlListRule extends YamlLineStartsWithRule{

	public YamlListRule(IToken token) {
		super("-","",true, false,token);
	}

	@Override
	protected boolean isAcceptedAtStart(int c) {
		/* whitespaces before are acceppted but noth less: e.g. a "      -sample: xxx" accepts until - so only - is accepted*/
		return Character.isWhitespace(c);
	}
}
