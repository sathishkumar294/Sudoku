/**
 * 
 */
package com.satt.games.sudoku.exceptions;

import com.satt.games.sudoku.exceptions.abs.SudokuException;

/**
 * @author samaruth
 *
 */
public class InvalidValueException extends SudokuException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1113837670921607439L;
	
	protected InvalidValueException() {
	}

	public InvalidValueException(String message) {
		super(message);
	}

	public InvalidValueException(String message, Exception e) {
		super(message, e);
	}

}
