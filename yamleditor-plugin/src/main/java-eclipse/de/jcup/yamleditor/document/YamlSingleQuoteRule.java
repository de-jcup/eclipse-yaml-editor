package de.jcup.yamleditor.document;

import org.eclipse.jface.text.rules.ICharacterScanner;
import org.eclipse.jface.text.rules.IPredicateRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.Token;

public class YamlSingleQuoteRule implements IPredicateRule{

	private IToken success;

	public YamlSingleQuoteRule(IToken success){
		this.success=success;
	}
	
	@Override
	public IToken evaluate(ICharacterScanner scanner) {
		return evaluate(scanner,false);
	}

	@Override
	public IToken getSuccessToken() {
		return success;
	}

	@Override
	public IToken evaluate(ICharacterScanner scanner, boolean resume) {
		ScanSlider slider = new ScanSlider(scanner);
		int first = slider.moveForward();
		if (first!='\''){
			if (first!=ICharacterScanner.EOF){
				slider.resetScanner();
			}
			return Token.UNDEFINED;
		}
		
		int readBefore=-1;
		int read=0;
		boolean foundClosing=false;
		while ( (read=slider.moveForward())!=ICharacterScanner.EOF){
			if (read=='\n'){
				break;
			}
			if (read=='#' && foundClosing){
				// we handle comments like a line brake
				slider.moveBack(); // we do not want to highlight the #
				break;
			}
			if (Character.isWhitespace(read)){
				/* just ignore*/
				continue;
			}
			if (read=='\''){
				if (foundClosing && readBefore=='\''){
					foundClosing=false;
				}else{
					foundClosing=true;
				}
			}else{
				foundClosing=false;
			}
			readBefore=read;
		}
		if (foundClosing){
			return success;
		}
		slider.resetScanner();
		return Token.UNDEFINED;
	}

}
