/**
 * 
 */
package com.satt.games.sudoku.service;

import java.util.ArrayList;
import java.util.List;

import com.satt.games.sudoku.exceptions.TechnicalException;
import com.satt.games.sudoku.models.Block;
import com.satt.games.sudoku.models.Board;
import com.satt.games.sudoku.models.Cell;

/**
 * @author samaruth
 *
 */
public final class BlockService extends GroupService {

	private static final BlockService instance = new BlockService();

	protected BlockService() {

	}

	/**
	 * Returns the first, second or third row of a block
	 * 
	 * @param block
	 * @param r
	 * @return
	 * @throws TechnicalException
	 */
	public List<Cell> getCellsInRow(Block block, int r) throws TechnicalException {
		if (r < 1 || r > 3) {
			throw new TechnicalException("The requested row no[" + r + "] is out of bounds [1,3]");
		}

		List<Cell> cellsInRow = new ArrayList<>();

		for (int i = 1; i <= 3; i++) {
			cellsInRow.add(block.get((r - 1) * 3 + i));
		}
		return cellsInRow;
	}

	/**
	 * Returns the first, second or third column of a block
	 * 
	 * @param block
	 * @param c
	 * @return
	 * @throws TechnicalException
	 */
	public List<Cell> getCellsInCol(Block block, int c) throws TechnicalException {
		if (c < 1 || c > 3) {
			throw new TechnicalException("The requested col no[" + c + "] is out of bounds [1,3]");
		}

		List<Cell> cellsInCol = new ArrayList<>();

		for (int i = 1; i <= 3; i++) {
			cellsInCol.add(block.get((i - 1) * 3 + i));
		}
		return cellsInCol;
	}

	/**
	 * Returns the blocks adjacent to the blocks 
	 * @param board
	 * @param block
	 * @param isRowAdjacent
	 * @return
	 * @throws TechnicalException
	 */
	public List<Block> getAdjacentBlocks(Board board, Block block, boolean isRowAdjacent) throws TechnicalException {
		List<Block> adjacentBlocks = new ArrayList<>();

		int thisBlockNo = block.getBlockNo();

		if (isRowAdjacent) {
			switch (thisBlockNo % 3) {
			case 1:
				adjacentBlocks.add(board.getBlock(thisBlockNo + 1));
				adjacentBlocks.add(board.getBlock(thisBlockNo + 2));
				break;
			case 2:
				adjacentBlocks.add(board.getBlock(thisBlockNo - 1));
				adjacentBlocks.add(board.getBlock(thisBlockNo + 1));
				break;
			case 0:
				adjacentBlocks.add(board.getBlock(thisBlockNo - 1));
				adjacentBlocks.add(board.getBlock(thisBlockNo - 2));
				break;

			default:
				break;
			}
		} else {

			switch ((thisBlockNo-1) / 3) {
			case 1:
				adjacentBlocks.add(board.getBlock(thisBlockNo + 3));
				adjacentBlocks.add(board.getBlock(thisBlockNo - 3));
				break;
			case 2:
				adjacentBlocks.add(board.getBlock(thisBlockNo - 6));
				adjacentBlocks.add(board.getBlock(thisBlockNo - 3));
				break;
			case 0:
				adjacentBlocks.add(board.getBlock(thisBlockNo + 3));
				adjacentBlocks.add(board.getBlock(thisBlockNo + 6));
				break;

			default:
				break;
			}

		}
		return adjacentBlocks;
	}

	/** Singleton */
	public static BlockService getInstance() {
		return instance;
	}

}
