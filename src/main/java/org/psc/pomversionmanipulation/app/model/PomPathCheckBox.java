package org.psc.pomversionmanipulation.app.model;

import javafx.scene.control.CheckBox;

public class PomPathCheckBox extends CheckBox {

	private PomPath pomPath;
	
	public PomPathCheckBox() {
		super();
	}
	
	public PomPathCheckBox(PomPath pomPath) {
		this();
		this.setPomPath(pomPath);
		setText(pomPath.toString());
	}

	public void setPomPath(PomPath pomPath) {
		this.pomPath = pomPath;
		setText(pomPath.toString());
	}

	public PomPath getPomPath() {
		return pomPath;
	}
}
