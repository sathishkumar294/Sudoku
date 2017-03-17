/**
 * 
 */
package com.satt.games.sudoku.exceptions;

import com.satt.games.sudoku.exceptions.abs.SudokuException;

/**
 * @author samaruth
 *
 */
public class TechnicalException extends SudokuException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5979988427061223776L;

	protected TechnicalException() {
	}

	public TechnicalException(String message) {
		super(message);
	}

	public TechnicalException(String message, Exception e) {
		super(message, e);
	}

}
