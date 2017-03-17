package com.satt.games.sudoku.models;

import com.satt.games.sudoku.exceptions.InvalidValueException;
import com.satt.games.sudoku.exceptions.TechnicalException;

public class Block extends CellCollection {
	private int blockNo;

	public Block() {
		super(CELL_COLLECTION_TYPE.BLOCK);
	}

	public Block(int no) {
		super(CELL_COLLECTION_TYPE.BLOCK);
		this.blockNo = no;
	}

	public int getBlockNo() {
		return blockNo;
	}

	public void setBlockNo(int blockNo) {
		this.blockNo = blockNo;
	}

	public Row getRow(int r) throws InvalidValueException, TechnicalException {
		Row tRow = new Row();
		tRow.add(get((r - 1) * 3 + 1));
		tRow.add(get((r - 1) * 3 + 2));
		tRow.add(get((r - 1) * 3 + 3));
		return tRow;
	}
	
	public Column getColumn(int c) throws InvalidValueException, TechnicalException{
		Column tColumn = new Column();
		tColumn.add(get(c));
		tColumn.add(get(c+3));
		tColumn.add(get(c+6));
		return tColumn;
	}

}
