/**
 * 
 */
package com.satt.games.sudoku.models;

import com.satt.games.sudoku.models.abs.Strip;

/**
 * @author samaruth
 *
 */
public class ColumnStrip extends Strip {

	private int columnGroupNo;

	public ColumnStrip() {
		super(STRIP_TYPE.COLSTRIP);
	}

	public ColumnStrip(int no) {
		super(STRIP_TYPE.COLSTRIP);
		this.columnGroupNo = no;
	}

	public int getColumnGroupNo() {
		return columnGroupNo;
	}

	public void setColumnGroupNo(int columnGroupNo) {
		this.columnGroupNo = columnGroupNo;
	}

}
