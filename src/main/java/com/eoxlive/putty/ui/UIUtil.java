package com.eoxlive.putty.ui;

import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.KeyStroke;

public final class UIUtil {

	public static void mapKeyStrokeAction(JComponent component,
			String actionMapKey, Action action, KeyStroke keyStroke) {
		component.getActionMap().put(actionMapKey, action);
		component.getInputMap().put(keyStroke, actionMapKey);
	}

}
