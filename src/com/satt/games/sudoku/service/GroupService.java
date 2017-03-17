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
import com.satt.games.sudoku.models.Board;
import com.satt.games.sudoku.models.Cell;
import com.satt.games.sudoku.models.CellCollection;
import com.satt.games.sudoku.models.CellCollection.CELL_COLLECTION_TYPE;
import com.satt.games.sudoku.service.abs.ParentService;

/**
 * @author samaruth
 *
 */
public class GroupService extends ParentService {

	private static final GroupService instance = new GroupService();

	/**
	 * Function to reduce the no of possibles for all cells in a row, column or
	 * block. Logic: if two cells have two same possibles, then those two values
	 * cannot occur in any other cell possibles. Similarly if three cells have
	 * the same three values, then those three values cannot occur in any other
	 * cell possibles
	 * 
	 * @param group
	 * @return
	 */
	public boolean reduceCouplets(CellCollection group) {
		boolean hasProgress = false;

		// No of possibles for a cell, E.g. order=2 => if two cells have two
		// same possibles, then those two values cannot occur in any other cell
		// possibles. Similarly for order=3 => if three cells have the same
		// three values, then those three values cannot occur in any other cell
		// possibles
		int order;
		int couplesFound = 0;

		List<Cell> freeCells = group.getFreeCells();
		order = freeCells.size();
		while (order > 1) {
			for (Cell cell1 : freeCells) {
				// if the cell has the same no of possibilities as the target
				// order
				if (cell1.getPossibles().size() == order) {
					// this cell forms the first item of the couplets
					couplesFound = 1;

					for (Cell cell2 : freeCells) {
						// if an other cell has the same possibilities, then it
						// is part of the couplet
						if (!cell1.equals(cell2) && cell1.hasSamePossibles(cell2)) {
							couplesFound++;
						}
					}

					// If the target order and the no of couples found are same,
					// then we can reduce some
					if (couplesFound == order) {
						// remove this possibilities from other cells
						for (Cell cell2 : freeCells) {
							// if it is not the couplet cell, then remove each
							// of the couplet possibles from other cells which
							// have more than the couplet possibles. E.g. if a
							// cell has (1,2) as possibles, then remove 1,2 from
							// all the cells which has (1 or 2 or 1,2...) as
							// possibles
							if (!cell1.hasSamePossibles(cell2)) {
								hasProgress = true;
								for (Integer p1 : cell1.getPossibles()) {
									cell2.removePossibility(p1, "there is a naked pair with the same possible");
								}
							}
						}
					}
				}
			}

			// go to check for the next order
			order--;
		}
		return hasProgress;
	}

	/**
	 * Function to reduce the no of possibles for all cells in a row, column or
	 * block. Logic: if two possibles occur in same two cells, then other
	 * possibles of those two cells can be removed.Similarly for order=3 => if
	 * three possibles occur in the same three cells, then those three cells
	 * cannot contain any other possibles
	 * 
	 * @param group
	 * @return
	 */
	public boolean reduceHiddenPairs(CellCollection group) {
		boolean hasProgress = false;

		// No of possibles for a cell, E.g. order=2 => if two possibles occur in
		// same two cells, then other possibles of those two cells can be
		// removed.Similarly for order=3 => if three possibles occur in the same
		// three cells, then those three cells cannot contain any other
		// possibles
		int order;
		List<Cell> freeCells = group.getFreeCells();
		order = freeCells.size();
		HashMap<Integer, Integer> pOccurences = new HashMap<>();
		List<Integer> allPossibles = group.getAllPossibles();
		for (Integer p : allPossibles) {
			int nOccurence = 0;
			for (Cell cell : freeCells) {
				if (cell.hasPossible(p)) {
					nOccurence++;
				}
			}
			pOccurences.put(p, nOccurence);
		}
		order = freeCells.size();
		order=2;//TODO: remove later
		while (order > 1) {
			int remainingCellsToGetWithSameOccurence = order;
			for (Integer p1 : allPossibles) {
				for (Integer p2 : allPossibles) {
					if (p1 != p2 && pOccurences.get(p1) == pOccurences.get(p2)) {
						int nOccurence = pOccurences.get(p1);
						// Since already two cells are found
						remainingCellsToGetWithSameOccurence = nOccurence - 2;
						// TODO: How to check other orders?
						if (remainingCellsToGetWithSameOccurence == 0) {

							List<Cell> cellsWithPossible1 = getCellsWithPossibles(group, null, Arrays.asList(p1));
							List<Cell> cellsWithPossible2 = getCellsWithPossibles(group, null, Arrays.asList(p2));

							boolean isPair = true;
							for (Cell cell1 : cellsWithPossible1) {
								if (!cellsWithPossible2.contains(cell1)) {
									isPair = false;
									break;
								}
							}

							if (isPair) {
								// For log
								String logPairCells = "";
								for (Cell cell : cellsWithPossible1) {
									logPairCells += "," + cell.identity();
								}
								logPairCells = "(" + logPairCells.substring(1) + ")";

								// Remove other possibles from the cell
								for (Cell cell : cellsWithPossible1) {
									List<Integer> impossibles = new ArrayList<>();
									for (Integer integer : cell.getPossibles()) {
										if (integer != p1 && integer != p2) {
											impossibles.add(integer);
										}
									}

									for (Integer imp : impossibles) {
										cell.removePossibility(imp,
												" it occurs in in the " + cell.identity() + " - part of hidden pair");
										hasProgress = true;
									}
								}
							}
						}
					}
				}
			}

			// reduce order to iterate
			order--;
		}

		return hasProgress;
	}

	/**
	 * Returns other cells in the same row(column), given the current block
	 * 
	 * @param board
	 * @param block
	 * @param groupNo
	 * @param groupType
	 * @return
	 * @throws TechnicalException
	 * @throws InvalidValueException
	 */
	public List<Cell> getOtherCellsInGroup(Board board, Cell iCell, CELL_COLLECTION_TYPE groupType)
			throws TechnicalException, InvalidValueException {
		List<Cell> otherCells = new ArrayList<>();
		CellCollection group = null;

		if (groupType == CELL_COLLECTION_TYPE.ROW) {
			group = board.getRow(iCell.getRowNo());
		} else if (groupType == CELL_COLLECTION_TYPE.COLUMN) {
			group = board.getColumn(iCell.getColNo());
		}

		// Get the cells in the group belonging to other blocks
		for (Cell cell : group) {
			if (!cell.isSameBlock(iCell)) {
				otherCells.add(cell);
			}
		}

		return otherCells;
	}

	/**
	 * Gets all the matching possible values in a group
	 * 
	 * @param group
	 * @return
	 */
	public HashMap<Integer, List<Cell>> getMatchingPossibles(CellCollection group) {
		HashMap<Integer, List<Cell>> matchingPossible = new HashMap<>();
		int noOfOccurence = 0;
		for (int p = 1; p <= 9; p++) {
			noOfOccurence = 0;
			List<Cell> tCells = new ArrayList<>();
			for (Cell cell : group) {
				if (cell.hasPossible(p)) {
					tCells.add(cell);
					noOfOccurence++;
					if (noOfOccurence > 2) {
						break;
					}
				}
			}

			if (noOfOccurence == 2) {
				matchingPossible.put(p, tCells);
			}
		}

		return matchingPossible;
	}

	/**
	 * Returns the possibles from the two row (or columns) which form an xWing
	 * 
	 * @param group1
	 * @param group2
	 * @return
	 */
	public HashMap<Integer, HashMap<Integer, List<Cell>>> getXWingPossibles(CellCollection group1,
			CellCollection group2, CELL_COLLECTION_TYPE grouptype) {
		HashMap<Integer, HashMap<Integer, List<Cell>>> xWingPossibles = new HashMap<>();

		HashMap<Integer, List<Cell>> matchingPossibles1 = getMatchingPossibles(group1);
		HashMap<Integer, List<Cell>> matchingPossibles2 = getMatchingPossibles(group2);

		for (Integer p : matchingPossibles1.keySet()) {
			if (matchingPossibles2.containsKey(p)) {
				List<Cell> g1Cells = matchingPossibles1.get(p);
				List<Cell> g2Cells = matchingPossibles2.get(p);

				Cell aCell = g1Cells.get(0);
				Cell bCell = g1Cells.get(1);
				Cell cCell = g2Cells.get(0);
				Cell dCell = g2Cells.get(1);

				HashMap<Integer, List<Cell>> tmpXWing = new HashMap<>();

				if (grouptype == CELL_COLLECTION_TYPE.ROW) {
					if (aCell.isSameColumn(cCell) && bCell.isSameColumn(dCell)) {

						List<Cell> tmpCells1 = new ArrayList<>();
						tmpCells1.add(aCell);
						tmpCells1.add(cCell);
						tmpXWing.put(aCell.getColNo(), tmpCells1);

						List<Cell> tmpCells2 = new ArrayList<>();
						tmpCells2.add(bCell);
						tmpCells2.add(dCell);
						tmpXWing.put(bCell.getColNo(), tmpCells2);

					}
					if (aCell.isSameColumn(dCell) && bCell.isSameColumn(cCell)) {

						List<Cell> tmpCells1 = new ArrayList<>();
						tmpCells1.add(aCell);
						tmpCells1.add(dCell);
						tmpXWing.put(aCell.getColNo(), tmpCells1);

						List<Cell> tmpCells2 = new ArrayList<>();
						tmpCells2.add(bCell);
						tmpCells2.add(cCell);
						tmpXWing.put(bCell.getColNo(), tmpCells2);
					}

				} else if (grouptype == CELL_COLLECTION_TYPE.COLUMN) {
					if (aCell.isSameRow(cCell) && bCell.isSameRow(dCell)) {
						List<Cell> tmpCells1 = new ArrayList<>();
						tmpCells1.add(aCell);
						tmpCells1.add(cCell);
						tmpXWing.put(aCell.getRowNo(), tmpCells1);

						List<Cell> tmpCells2 = new ArrayList<>();
						tmpCells2.add(bCell);
						tmpCells2.add(dCell);
						tmpXWing.put(bCell.getRowNo(), tmpCells2);

					}
					if (aCell.isSameRow(dCell) && bCell.isSameRow(cCell)) {

						List<Cell> tmpCells1 = new ArrayList<>();
						tmpCells1.add(aCell);
						tmpCells1.add(dCell);
						tmpXWing.put(aCell.getRowNo(), tmpCells1);

						List<Cell> tmpCells2 = new ArrayList<>();
						tmpCells2.add(bCell);
						tmpCells2.add(cCell);
						tmpXWing.put(bCell.getRowNo(), tmpCells2);
					}
				}

				xWingPossibles.put(p, tmpXWing);

			}
		}

		return xWingPossibles;
	}

	/**
	 * Function returns the other cells in the group which has the same list of
	 * possibles. The targetNoindicates the max no of such cells expected
	 * 
	 * @param possibles
	 * @param targetNoOfCells
	 * @return
	 */
	public List<Cell> getCellsWithPossibles(CellCollection group, List<Cell> cellsToExcludeFromSearch,
			List<Integer> possibles) {

		if (cellsToExcludeFromSearch == null) {
			// In case of no restriction
			cellsToExcludeFromSearch = new ArrayList<>();
		}

		List<Cell> cellsWithPossibles = new ArrayList<>();
		for (Cell cell : group) {
			// If it is a cell to be excluded from search..
			if (!cellsToExcludeFromSearch.contains(cell) && !cell.isFixed()) {
				boolean isAllPossiblesPresentInCell = true;
				for (Integer p : possibles) {
					if (!cell.hasPossible(p)) {
						isAllPossiblesPresentInCell = false;
						break;
					}
				}
				if (isAllPossiblesPresentInCell) {
					cellsWithPossibles.add(cell);
				}
			}
		}

		return cellsWithPossibles;

	}

	public static GroupService getInstance() {
		return instance;
	}

}
