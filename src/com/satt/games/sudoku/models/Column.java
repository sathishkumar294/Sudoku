/**
 * 
 */
package com.satt.games.sudoku.models;

/**
 * @author samaruth
 *
 */
public class Column extends CellCollection {
	private int colNo;

	public Column() {
		super(CELL_COLLECTION_TYPE.COLUMN);
	}

	public Column(int colNo) {
		super(CELL_COLLECTION_TYPE.COLUMN);
		this.colNo = colNo;
	}

	public int getColNo() {
		return colNo;
	}

	public void setColNo(int colNo) {
		this.colNo = colNo;
	}

}
