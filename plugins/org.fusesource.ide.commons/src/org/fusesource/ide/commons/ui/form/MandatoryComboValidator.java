package org.fusesource.ide.commons.ui.form;

import org.eclipse.swt.widgets.Combo;

/**
 * Validates that a property is mandatory
 */
public class MandatoryComboValidator extends MandatoryValidator {
	private final Combo combo;

	public MandatoryComboValidator(String labelText, Combo combo) {
		super(labelText);
		this.combo = combo;
	}

	@Override
	protected boolean isValid(Object value) {
		int idx = combo.getSelectionIndex();
		boolean valid = super.isValid(value);
		return idx >= 0;
	}


}