/**
 * 
 */
package com.satt.games.sudoku.models;

import com.satt.games.sudoku.models.abs.Strip;

/**
 * @author samaruth
 *
 */
public class RowStrip extends Strip {

	private int rowGroupNo;

	public RowStrip() {
		super(STRIP_TYPE.ROWSTRIP);
	}

	public RowStrip(int no) {
		super(STRIP_TYPE.ROWSTRIP);
		this.rowGroupNo = no;
	}

	public int getRowGroupNo() {
		return rowGroupNo;
	}

	public void setRowGroupNo(int rowGroupNo) {
		this.rowGroupNo = rowGroupNo;
	}

}
