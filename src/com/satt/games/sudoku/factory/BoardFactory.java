/**
 * 
 */
package com.satt.games.sudoku.factory;

import com.satt.games.sudoku.exceptions.InvalidValueException;
import com.satt.games.sudoku.exceptions.TechnicalException;
import com.satt.games.sudoku.models.Block;
import com.satt.games.sudoku.models.Board;
import com.satt.games.sudoku.models.Cell;
import com.satt.games.sudoku.models.Column;
import com.satt.games.sudoku.models.ColumnStrip;
import com.satt.games.sudoku.models.Row;
import com.satt.games.sudoku.models.RowStrip;
import com.satt.games.sudoku.service.RigidityService;

/**
 * @author samaruth
 *
 */
public class BoardFactory {

	private static final BoardFactory instance = new BoardFactory();

	private BoardFactory() {
	};	

	/**
	 * Gets a brand new board with no values
	 * 
	 * @return
	 * @throws TechnicalException
	 */
	public Board getNewBoard() throws TechnicalException {

		Board board = new Board();

		// add new groups
		for (int k = 1; k <= 3; k++) {
			RowStrip tRowStrip = new RowStrip(k);
			ColumnStrip tColumnStrip = new ColumnStrip(k);

			for (int l = 1; l <= 3; l++) {
				// Nine rows
				Row tRow = new Row((k - 1) * 3 + l);
				tRowStrip.add(tRow);
				board.getRows().add(tRow);

				// Nine columns
				Column tColumn = new Column((k - 1) * 3 + l);
				tColumnStrip.add(tColumn);
				board.getColumns().add(tColumn);

				// Nine blocks
				board.getBlocks().add(new Block((k - 1) * 3 + 1));

			}

			// Three strips each
			board.getRowStrips().add(tRowStrip);
			board.getColumnStrips().add(tColumnStrip);
		}

		// add cells to existing groups
		int r, c;

		try {
			for (r = 1; r <= 9; r++) {
				for (c = 1; c <= 9; c++) {
					// a new cell
					Cell tCell = new Cell(c, r);

					// add the cell to the board
					board.getCells().add(tCell);

					// add the cells to their rows
					board.getRow(r).add(tCell);

					// add the cells to their columns
					board.getColumn(c).add(tCell);

					// add the cells to their blocks
					int bpos = RigidityService.getBlockNo(c, r);
					board.getBlock(bpos).add(tCell);
				}
			}
		} catch (TechnicalException e) {
			throw new TechnicalException("Some technical Exception", e);
		} catch (InvalidValueException e) {
			throw new TechnicalException("Invalid value exception", e);
		}
		return board;

	}

	public static BoardFactory getInstance() {
		return instance;
	}
}
