/**
 * 
 */
package com.satt.games.sudoku.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import com.satt.games.sudoku.exceptions.InvalidValueException;
import com.satt.games.sudoku.exceptions.TechnicalException;
import com.satt.games.sudoku.models.Block;
import com.satt.games.sudoku.models.Board;
import com.satt.games.sudoku.models.Cell;
import com.satt.games.sudoku.models.CellCollection;
import com.satt.games.sudoku.models.CellCollection.CELL_COLLECTION_TYPE;
import com.satt.games.sudoku.models.Row;
import com.satt.games.sudoku.service.abs.ParentService;
import com.satt.games.sudoku.utils.MathFxns;

/**
 * @author samaruth
 *
 */
public class ReducerService extends ParentService {

	private ReducerService() {
	}

	/** Singleton */
	private static final ReducerService instance = new ReducerService();

	private RigidityService rigidityService = RigidityService.getInstance();
	private BoardService boardService = BoardService.getInstance();
	private GroupService groupService = GroupService.getInstance();

	/**
	 * Fixes the value for cells which have only a single value possible
	 * 
	 * @return - boolean. True if the problem has progress else false.
	 * @throws TechnicalException
	 * @throws InvalidValueException
	 */
	public boolean fixNakedPossibilities(Board board) throws TechnicalException, InvalidValueException {
		boolean hasProgress = false;
		for (Cell cell : board.getCells()) {

			// Update the corresponding row, column, block possibilities
			rigidityService.removeImpossibles(board, cell);

			if (!cell.isFixed() && cell.getPossibles().size() == 1) {
				boardService.setValue(board, cell, cell.getPossibles().get(0), false);
				hasProgress = true;
			}
		}

		return hasProgress;
	}

	/**
	 * If a possibility can be in only one cell of a row/column/block, then fix
	 * that value to that cell, irrespective of the other possibles
	 * 
	 * @param board
	 * @return
	 * @throws InvalidValueException
	 * @throws TechnicalException
	 */
	public boolean fixHiddenPossibles(Board board) throws TechnicalException, InvalidValueException {
		boolean hasProgress = false;

		for (CellCollection block : board.getBlocks()) {
			hasProgress = fixHiddenPossibleForGroup(board, block) || hasProgress;
		}

		for (CellCollection row : board.getRows()) {
			hasProgress = fixHiddenPossibleForGroup(board, row) || hasProgress;
		}

		for (CellCollection column : board.getColumns()) {
			hasProgress = fixHiddenPossibleForGroup(board, column) || hasProgress;
		}

		return hasProgress;
	}

	/**
	 * Fixes the hidden possibles for a group
	 * 
	 * @param group
	 * @return
	 * @throws InvalidValueException
	 * @throws TechnicalException
	 */
	private boolean fixHiddenPossibleForGroup(Board board, CellCollection group) throws TechnicalException, InvalidValueException {
		boolean hasProgress = false;

		for (int p = 1; p <= 9; p++) {
			int noOfOccurence = 0;
			Cell occurenceCell = null;
			for (Cell cell : group) {
				if (!cell.isFixed() && cell.hasPossible(p)) {
					noOfOccurence++;
					occurenceCell = cell;
				}
			}

			// If the possibilty is only one, then fix it to the cell
			if (noOfOccurence == 1) {
				hasProgress = true;
				boardService.setValue(board, occurenceCell, p, false);
			}
		}

		return hasProgress;
	}

	/**
	 * Remove the couplets from each block or cell
	 * 
	 * @param board
	 * @return
	 */
	public boolean fixNakedPairs(Board board) {
		boolean hasProgress = false;

		// check rows for couplets
		for (CellCollection row : board.getRows()) {
			hasProgress = groupService.reduceCouplets(row) || hasProgress;
		}

		// check columns for couplets
		for (CellCollection column : board.getColumns()) {
			hasProgress = groupService.reduceCouplets(column) || hasProgress;
		}

		// check blocks for couplets
		for (CellCollection block : board.getBlocks()) {
			hasProgress = groupService.reduceCouplets(block) || hasProgress;
		}

		// return the progress
		return hasProgress;
	}

	/**
	 * Fix the hidden pairs
	 * 
	 * @param board
	 * @return
	 */
	public boolean fixHiddenPairs(Board board) {
		boolean hasProgress = false;

		// check rows for couplets
		for (CellCollection row : board.getRows()) {
			hasProgress = groupService.reduceHiddenPairs(row) || hasProgress;
		}

		// check columns for couplets
		for (CellCollection column : board.getColumns()) {
			hasProgress = groupService.reduceHiddenPairs(column) || hasProgress;
		}

		// check blocks for couplets
		for (CellCollection block : board.getBlocks()) {
			hasProgress = groupService.reduceHiddenPairs(block) || hasProgress;
		}

		return hasProgress;
	}

	/**
	 * Function to remove the hidden possibilities and fix the values in
	 * adjacent rows, columns, blocks. If all the possible cells for a value in
	 * the same row (column) of a block, then remove this possible from other
	 * cells of the same row (column)
	 * 
	 * @param board
	 * @return
	 */
	public boolean fixPossiblesLimitedToRowCol(Board board) {
		boolean hasProgress = false;

		for (CellCollection cc : board.getBlocks()) {
			Block block = (Block) cc;
			for (int tP = 1; tP <= 9; tP++) {
				if (!block.hasValue(tP)) {
					// For Row
					{
						int noOfRowsHavingPossible = 0;
						Cell cellHavingPossible = null;
						try {
							for (int r = 1; r <= 3; r++) {
								if (block.getRow(r).hasPossible(tP)) {
									noOfRowsHavingPossible++;
									cellHavingPossible = block.getRow(r).get(1);
								}
							}
							// If only one row has the possibility for a value
							// in the block, then remove this possible from
							// other cells of the same row
							if (noOfRowsHavingPossible == 1) {

								List<Cell> adjacentCells = groupService.getOtherCellsInGroup(board, cellHavingPossible, CELL_COLLECTION_TYPE.ROW);
								for (Cell cell : adjacentCells) {
									// remove this possibility
									hasProgress = true;
									cell.removePossibility(tP, " the it can occur only in another block on the same row");
								}

							}

						} catch (InvalidValueException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (TechnicalException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					// For Column
					{
						int noOfColsHavingPossible = 0;
						Cell cellHavingPossible = null;
						try {
							for (int c = 1; c <= 3; c++) {
								if (block.getColumn(c).hasPossible(tP)) {
									noOfColsHavingPossible++;
									cellHavingPossible = block.getColumn(c).get(1);
								}
							}

							// If only one of the column contains it as the
							// possible value, then remove this value from the
							// other cells of columns of adjacent column blocks
							if (noOfColsHavingPossible == 1) {

								List<Cell> adjacentBlockCells = groupService.getOtherCellsInGroup(board, cellHavingPossible, CELL_COLLECTION_TYPE.COLUMN);
								for (Cell cell : adjacentBlockCells) {
									// remove this possibility
									hasProgress = true;
									cell.removePossibility(tP, " the it can occur only in another block on the same column");
								}

							}

						} catch (InvalidValueException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (TechnicalException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
			}

		}

		return hasProgress;
	}

	/**
	 * Solves the x-wings
	 * 
	 * @param board
	 * @return
	 */
	public boolean fixXWings(Board board) {
		boolean hasProgress = false;
		try {
			int nRows = board.getRows().count();
			for (int rc1 = 1; rc1 <= nRows; rc1++) {
				for (int rc2 = rc1 + 1; rc2 <= nRows; rc2++) {
					HashMap<Integer, HashMap<Integer, List<Cell>>> xWingPossibles = groupService.getXWingPossibles(board.getRow(rc1), board.getRow(rc2),
							CELL_COLLECTION_TYPE.ROW);
					for (Integer p : xWingPossibles.keySet()) {
						HashMap<Integer, List<Cell>> colNoCellsMap = xWingPossibles.get(p);
						for (Integer colNo : colNoCellsMap.keySet()) {
							List<Cell> xCells = colNoCellsMap.get(colNo);
							for (Cell tCell : board.getColumn(colNo)) {
								if (!tCell.isFixed() && !xCells.contains(tCell)) {
									tCell.removePossibility(p, " it is in the same column as an X-Wing");
									hasProgress = true;
								}
							}
						}
					}

				}
			}

			int nCols = board.getColumns().count();
			for (int cc1 = 1; cc1 <= nCols; cc1++) {
				for (int cc2 = cc1 + 1; cc2 <= nCols; cc2++) {
					HashMap<Integer, HashMap<Integer, List<Cell>>> xWingPossibles = groupService.getXWingPossibles(board.getRow(cc1), board.getRow(cc2),
							CELL_COLLECTION_TYPE.COLUMN);
					for (Integer p : xWingPossibles.keySet()) {
						HashMap<Integer, List<Cell>> colNoCellsMap = xWingPossibles.get(p);
						for (Integer rowNo : colNoCellsMap.keySet()) {
							List<Cell> xCells = colNoCellsMap.get(rowNo);
							for (Cell tCell : board.getRow(rowNo)) {
								if (!tCell.isFixed() && !xCells.contains(tCell)) {
									tCell.removePossibility(p, " it is in the same row as an X-Wing");
									hasProgress = true;
								}
							}
						}
					}

				}
			}
		} catch (TechnicalException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}

		return hasProgress;
	}

	/**
	 * Solves the x-wings
	 * 
	 * @param board
	 * @return
	 */
	public boolean fixSwordFish(Board board) {
		boolean hasProgress = false;
		try {
			// Check for sword fishes row wise
			for (int p = 1; p <= 9; p++) {
				// no of rows with the possible occurring only twice
				int nRowsWith2Occurence = 0;
				List<Integer> rowNosSelected = new ArrayList<>();
				for (CellCollection row : board.getRows()) {
					if (groupService.getCellsWithPossibles(row, null, Arrays.asList(p)).size() == 2) {
						nRowsWith2Occurence++;
						rowNosSelected.add(((Row) row).getRowNo());
					}
				}

				// Necessary condition for swordfish, there has to be at least 3
				// rows
				// TODO: Handle the case where there are more than 3 rows with
				// the possible occurring twice
				if (nRowsWith2Occurence >= 3) {

					// no of ways of selection 3 rows from n rows
					int noOfTries = MathFxns.combination(nRowsWith2Occurence, 3);
					int thisTry = 1;
					while (thisTry <= noOfTries) {
						// Check if their columns line up
						int rowNoSelected1 = rowNosSelected.get((thisTry - 1) % nRowsWith2Occurence);
						int rowNoSelected2 = rowNosSelected.get(thisTry % nRowsWith2Occurence);
						int rowNoSelected3 = rowNosSelected.get((thisTry + 1) % nRowsWith2Occurence);

						List<Cell> selectedCellsOfRow1 = groupService.getCellsWithPossibles(board.getRow(rowNoSelected1), null, Arrays.asList(p));
						List<Cell> selectedCellsOfRow2 = groupService.getCellsWithPossibles(board.getRow(rowNoSelected2), null, Arrays.asList(p));
						List<Cell> selectedCellsOfRow3 = groupService.getCellsWithPossibles(board.getRow(rowNoSelected3), null, Arrays.asList(p));

						// No of matching columns must be three
						int totalColumnsMatched = 0;
						List<Integer> columnNosMatching = new ArrayList<>();
						boolean proceedForThisPossible = true;
						for (Cell cell1 : selectedCellsOfRow1) {

							for (Cell cell2 : selectedCellsOfRow2) {
								if (cell2.isSameColumn(cell1)) {
									totalColumnsMatched++;
									if (!columnNosMatching.contains(cell2.getColNo())) {
										columnNosMatching.add(cell2.getColNo());
									}
								}
							}

							for (Cell cell3 : selectedCellsOfRow3) {
								if (cell3.isSameColumn(cell1)) {
									totalColumnsMatched++;
									if (!columnNosMatching.contains(cell3.getColNo())) {
										columnNosMatching.add(cell3.getColNo());
									}
								}

							}

							// Only two columns should have been matched by now.
							// Else, its not a valid sword fish
							if (totalColumnsMatched != 2) {
								proceedForThisPossible = false;
								break;
							}
						}

						if (!proceedForThisPossible) {
							// Skip to the next possible
							thisTry++;
							continue;
						}

						int noOfR2R3Matching = 0;
						// Check whether the column match for row2 & row3
						for (Cell cell2 : selectedCellsOfRow2) {
							for (Cell cell3 : selectedCellsOfRow3) {
								if (cell2.isSameColumn(cell3)) {
									noOfR2R3Matching++;
									if (!columnNosMatching.contains(cell3.getColNo())) {
										columnNosMatching.add(cell3.getColNo());
									}
								}
							}
						}

						// The rows 2 & 3 should match only by one column
						if (noOfR2R3Matching != 1) {
							thisTry++;
							continue;
						}

						// If three columns are matching, then it is a valid
						// swordfish
						boolean isNoOfColumnsOK = columnNosMatching.size() == 3;

						if (isNoOfColumnsOK) {
							// For log
							String logSwordFish = "[" + selectedCellsOfRow1.get(0).identity() + "," + selectedCellsOfRow1.get(1).identity() + ","
									+ selectedCellsOfRow2.get(0).identity() + "," + selectedCellsOfRow2.get(1).identity() + ","
									+ selectedCellsOfRow3.get(0).identity() + "," + selectedCellsOfRow3.get(1).identity() + "]";

							// Remove this possible from other cells of the
							// three
							// columns
							for (Integer colNo : columnNosMatching) {
								for (Cell cell : board.getColumn(colNo)) {
									boolean isCellASwordFishCell = selectedCellsOfRow1.contains(cell) || selectedCellsOfRow2.contains(cell)
											|| selectedCellsOfRow3.contains(cell);
									boolean hasThisPossibleP = cell.hasPossible(p);

									if (!isCellASwordFishCell && hasThisPossibleP) {
										cell.removePossibility(p, " it is in " + cell.identity() + " which is in column[" + colNo + "] containing SwordFish"
												+ logSwordFish + " for possibility[" + p + "]");
										hasProgress = true;
									}

								}
							}

						}
						thisTry++;
					}
				}
			}

			// Check for sword fishes column wise
			for (int p = 1; p <= 9; p++) {
				// no of rows with the possible occurring only twice
				int nColsWith2Occurence = 0;
				List<Integer> colNosSelected = new ArrayList<>();
				for (CellCollection column : board.getRows()) {
					if (groupService.getCellsWithPossibles(column, null, Arrays.asList(p)).size() == 2) {
						nColsWith2Occurence++;
						colNosSelected.add(((Row) column).getRowNo());
					}
				}

				// Necessary condition for swordfish, there has to be at least 3
				// rows
				// TODO: Handle the case where there are more than 3 rows with
				// the possible occurring twice
				if (nColsWith2Occurence >= 3) {

					int noOfTries = MathFxns.combination(nColsWith2Occurence, 3);
					int thisTry = 1;
					while (thisTry <= noOfTries) {

						// Check if their columns line up
						int colNoSelected1 = colNosSelected.get((thisTry - 1) % nColsWith2Occurence);
						int colNoSelected2 = colNosSelected.get((thisTry) % nColsWith2Occurence);
						int colNoSelected3 = colNosSelected.get((thisTry + 1) % nColsWith2Occurence);

						List<Cell> selectedCellsOfCol1 = groupService.getCellsWithPossibles(board.getColumn(colNoSelected1), null, Arrays.asList(p));
						List<Cell> selectedCellsOfCol2 = groupService.getCellsWithPossibles(board.getColumn(colNoSelected2), null, Arrays.asList(p));
						List<Cell> selectedCellsOfCol3 = groupService.getCellsWithPossibles(board.getColumn(colNoSelected3), null, Arrays.asList(p));

						// No of matching columns must be three
						int totalRowsMatched = 0;
						List<Integer> rowNosMatching = new ArrayList<>();
						boolean proceedForThisPossible = true;
						for (Cell cell1 : selectedCellsOfCol1) {

							for (Cell cell2 : selectedCellsOfCol2) {
								if (cell2.isSameRow(cell1)) {
									totalRowsMatched++;
									if (!rowNosMatching.contains(cell2.getColNo())) {
										rowNosMatching.add(cell2.getColNo());
									}
								}
							}

							for (Cell cell3 : selectedCellsOfCol3) {
								if (cell3.isSameRow(cell1)) {
									totalRowsMatched++;
									if (!rowNosMatching.contains(cell3.getColNo())) {
										rowNosMatching.add(cell3.getColNo());
									}
								}

							}

							// Only two columns should have been matched by now.
							// Else, its not a valid sword fish
							if (totalRowsMatched != 2) {
								proceedForThisPossible = false;
								break;
							}
						}

						if (!proceedForThisPossible) {
							// Skip to the next possible
							thisTry++;
							continue;
						}

						int noOfC2C3Matching = 0;
						// Check whether the column match for row2 & row3
						for (Cell cell2 : selectedCellsOfCol2) {
							for (Cell cell3 : selectedCellsOfCol3) {
								if (cell2.isSameColumn(cell3)) {
									noOfC2C3Matching++;
									if (!rowNosMatching.contains(cell3.getColNo())) {
										rowNosMatching.add(cell3.getColNo());
									}
								}
							}
						}

						// The rows 2 & 3 should match only by one column
						if (noOfC2C3Matching != 1) {
							thisTry++;
							continue;
						}

						// If three columns are matching, then it is a valid
						// swordfish
						boolean isNoOfRowsOK = rowNosMatching.size() == 3;

						if (isNoOfRowsOK) {
							// For log
							String logSwordFish = "[" + selectedCellsOfCol1.get(0).identity() + "," + selectedCellsOfCol1.get(1).identity() + ","
									+ selectedCellsOfCol2.get(0).identity() + "," + selectedCellsOfCol2.get(1).identity() + ","
									+ selectedCellsOfCol3.get(0).identity() + "," + selectedCellsOfCol3.get(1).identity() + "]";

							// Remove this possible from other cells of the
							// three
							// columns
							for (Integer rowNo : rowNosMatching) {
								for (Cell cell : board.getRow(rowNo)) {
									boolean isCellASwordFishCell = selectedCellsOfCol1.contains(cell) || selectedCellsOfCol2.contains(cell)
											|| selectedCellsOfCol3.contains(cell);
									boolean hasThisPossibleP = cell.hasPossible(p);

									if (!isCellASwordFishCell && hasThisPossibleP) {
										cell.removePossibility(p, " it is in " + cell.identity() + " which is in row[" + rowNo + "] containing SwordFish"
												+ logSwordFish + " for possibility[" + p + "]");
										hasProgress = true;
									}

								}
							}

						}
						thisTry++;
					}
				}
			}

		} catch (TechnicalException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}

		return hasProgress;
	}

	/**
	 * Checks if a board is solved or not
	 * 
	 * @return
	 */
	public boolean isBoardSolved(Board board) {

		try {
			for (CellCollection block : board.getBlocks()) {
				if (!block.isSolved()) {
					return false;
				}
			}

			for (CellCollection row : board.getRows()) {
				if (!row.isSolved()) {
					return false;
				}
			}

			for (CellCollection column : board.getColumns()) {
				if (!column.isSolved()) {
					return false;
				}
			}
		} catch (TechnicalException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}

		// Just an extra unnecessary check
		for (Cell cell : board.getCells()) {
			if (!cell.isFixed()) {
				return false;
			}
		}
		return true;
	}

	public static ReducerService getInstance() {
		return instance;
	}

}
