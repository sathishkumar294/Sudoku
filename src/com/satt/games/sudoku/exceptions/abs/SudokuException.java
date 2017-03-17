/**
 * 
 */
package com.satt.games.sudoku.exceptions.abs;

/**
 * @author samaruth
 *
 */
public abstract class SudokuException extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = -4678223570028279179L;
	private String msg = "";
	private Exception exp = null;;

	public SudokuException() {
	}

	public SudokuException(String message) {
		this.msg = message;
	}

	public SudokuException(String message, Exception e) {
		this.msg = message;
		this.exp = e;
	}

	public String getMsg() {
		return msg;
	}

	public String getFullMessage() {
		return msg + new String((exp == null ? "" : exp.getMessage()));
	}

	public void setMsg(String message) {
		this.msg = message;
	}
	
	public String toString(){
		return getFullMessage();
	}

}
