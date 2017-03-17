/**
 * 
 */
package com.satt.games.sudoku.models;

/**
 * @author samaruth
 *
 */
public class Row extends CellCollection {
	private int rowNo;

	public Row() {
		super(CELL_COLLECTION_TYPE.ROW);
	}

	public Row(int no) {
		super(CELL_COLLECTION_TYPE.ROW);
		this.rowNo = no;
	}

	public int getRowNo() {
		return rowNo;
	}

	public void setRowNo(int rowNo) {
		this.rowNo = rowNo;
	}
}
