/**
 * 
 */
package com.satt.games.sudoku.exceptions;

import com.satt.games.sudoku.exceptions.abs.SudokuException;

/**
 * @author samaruth
 *
 */
public class FixedValueModifiedException extends SudokuException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3185931550232650569L;
	
	protected FixedValueModifiedException() {
	}

	public FixedValueModifiedException(String message) {
		super(message);
	}

	public FixedValueModifiedException(String message, Exception e) {
		super(message, e);
	}

}
