package com.satt.games.sudoku.service;

import com.satt.games.sudoku.exceptions.TechnicalException;
import com.satt.games.sudoku.models.Board;
import com.satt.games.sudoku.models.Cell;
import com.satt.games.sudoku.service.abs.ParentService;

public class RigidityService extends ParentService {

	private RigidityService() {
	};

	private static final RigidityService instance = new RigidityService();

	/**
	 * Function that updates the possibilities for the column, row and block
	 * that contains the cell
	 * 
	 * @param tCell
	 * @return
	 * @throws TechnicalException
	 */
	public void removeImpossibles(Board board, Cell tCell) throws TechnicalException {
		if (tCell.isFixed()) {
			int rowNo = tCell.getRowNo();
			int colNo = tCell.getColNo();
			int value = tCell.getValue();
			for (Cell cell : board.getRow(rowNo)) {
				if (!cell.equals(tCell)) {
					cell.removePossibility(value, "the " + tCell.identity() + " has the value in same row");
				}
			}
			for (Cell cell : board.getColumn(colNo)) {
				if (!cell.equals(tCell)) {
					cell.removePossibility(value, "the " + tCell.identity() + " has the value in same column");
				}
			}
			int blockNo = getBlockNo(colNo, rowNo);
			for (Cell cell : board.getBlock(blockNo)) {
				if (!cell.equals(tCell)) {
					cell.removePossibility(value, "the " + tCell.identity() + " has the value in same block");
				}
			}
		}
	}

	/**
	 * Function returns the block number for the column and row
	 * 
	 * @param c
	 * @param r
	 * @return
	 */
	public static int getBlockNo(int c, int r) {
		int x0 = c, y0 = r;
		int x1 = ((int) (x0 - 1) / 3 + 1), y1 = ((int) (y0 - 1) / 3 + 1);
		int bpos = x1 + (y1 - 1) * 3;
		return bpos;
	}

	public static RigidityService getInstance() {
		return instance;
	}

}
