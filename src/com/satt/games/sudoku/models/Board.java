/**
 * 
 */
package com.satt.games.sudoku.models;

import com.satt.games.sudoku.exceptions.TechnicalException;
import com.satt.games.sudoku.models.CellCollection.CELL_COLLECTION_TYPE;

/**
 * @author samaruth Main board of the game
 */
public class Board {

	private CellCollection cells = new CellCollection(CELL_COLLECTION_TYPE.CELL);
	private RowStripCollection rowStrips = new RowStripCollection();
	private ColumnStripCollection columnStrips = new ColumnStripCollection();
	private BlockCollection blocks = new BlockCollection();
	private ColumnCollection columns = new ColumnCollection();
	private RowCollection rows = new RowCollection();

	public void display(int step, boolean dodisplayPossibles) {

		System.out.println("=============== STEP " + step + " =============");

		for (int r = 1; r <= 9; r++) {
			System.out.print("\n\t");

			// Every third row print a new line
			if (r % 3 == 1) {
				System.out.print("\n\t");
			}
			for (int c = 1; c <= 9; c++) {

				// every third column print an extra space
				if (c % 3 == 1) {
					System.out.print("  ");
				}

				String val = "";
				try {
					Cell tCell = cells.get(c, r);
					if (tCell.isFixed()) {
						val = String.valueOf(tCell.getValue());
					} else if (dodisplayPossibles) {
						for (Integer p : tCell.getPossibles()) {
							val += "," + p;
						}
						val = "(" + val.substring(1) + ")";
					} else {
						val = ".";
					}
				} catch (TechnicalException e) {
					val = "$";
				}
				
				// Formatted output
				String format = dodisplayPossibles?"%1$-15s":"%s";
				System.out.print(String.format(format, val)+" ");

			}
		}

		System.out.println("\n\n\n=====================================================");
	}

	public BlockCollection getBlocks() {
		return blocks;
	}

	public void setBlocks(BlockCollection blocks) {
		this.blocks = blocks;
	}

	public ColumnCollection getColumns() {
		return columns;
	}

	public void setColumns(ColumnCollection columns) {
		this.columns = columns;
	}

	public RowStripCollection getRowStrips() {
		return rowStrips;
	}

	public void setRowStrips(RowStripCollection rowStrips) {
		this.rowStrips = rowStrips;
	}

	public ColumnStripCollection getColumnStrips() {
		return columnStrips;
	}

	public void setColumnStrips(ColumnStripCollection columnStrips) {
		this.columnStrips = columnStrips;
	}

	public CellCollection getCells() {
		return cells;
	}

	public void setCells(CellCollection cells) {
		this.cells = cells;
	}

	public RowCollection getRows() {
		return rows;
	}

	public void setRows(RowCollection rows) {
		this.rows = rows;
	}

	public Row getRow(int r) throws TechnicalException {
		return (Row) getRows().get(r);
	}

	public Column getColumn(int c) throws TechnicalException {
		return (Column) getColumns().get(c);
	}

	public Block getBlock(int b) throws TechnicalException {
		return (Block) getBlocks().get(b);
	}

	public Cell get(int c, int r) throws TechnicalException {
		return getColumn(c).get(r);
	}

}
