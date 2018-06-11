package de.jcup.yamleditor.outline;

public class RootItem extends Item{

	public RootItem(){
		this.name="root";
	}
	
	@Override
	public boolean isRoot() {
		return true;
	}
}
