/**
 * 
 */
package com.satt.games.sudoku.models;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import com.satt.games.sudoku.exceptions.InvalidValueException;
import com.satt.games.sudoku.exceptions.TechnicalException;

/**
 * @author samaruth
 *
 */
public class CellCollection implements Iterable<Cell> {

	private List<Cell> cells = new ArrayList<>();
	protected CELL_COLLECTION_TYPE type;

	public enum CELL_COLLECTION_TYPE {
		CELL, ROW, COLUMN, BLOCK
	}

	protected CellCollection() {
	}

	public CellCollection(CELL_COLLECTION_TYPE type) {
		this.type = type;
	}

	/**
	 * Gets the cell at the cth column and rth row
	 * 
	 * @param c
	 *            - column 1..9
	 * @param r
	 *            - row 1..9
	 * @return
	 * @throws TechnicalException
	 */
	public Cell get(int c, int r) throws TechnicalException {
		int n = (r - 1) * 9 + c;
		if (n <= cells.size()) {
			return cells.get(n - 1);
		} else {
			throw new TechnicalException(MessageFormat.format(
					"The cell collection contains only {0} items, but the item requested is {1}", cells.size(), n));
		}
	}

	/**
	 * Returns the nth cell from the collection
	 * 
	 * @param n
	 * @return
	 * @throws TechnicalException
	 */
	public Cell get(int n) throws TechnicalException {
		if (n <= cells.size()) {
			return cells.get(n - 1);
		} else {
			throw new TechnicalException(MessageFormat.format(
					"The cell collection contains only {0} items, but the item requested is {1}", cells.size(), n));
		}
	}

	/**
	 * Adds a new cell to the collection of cells
	 * 
	 * @param cell
	 * @throws InvalidValueException
	 */
	public void add(Cell tCell) throws InvalidValueException {

		if (tCell.getValue() != 0) {
			for (Cell cell : cells) {
				if (tCell.getValue() == cell.getValue()) {
					throw new InvalidValueException(
							"Value[" + tCell.getValue() + "] already exists in the Cell" + cell);
				}
			}

			for (Cell cell : cells) {
				cell.removePossibility(tCell.getValue(), "it is an input for " + tCell.identity());
			}
		}

		// All ok, add the cell to the collection
		cells.add(tCell);
	}

	/**
	 * Function that checks if another cell in the collection already have this
	 * value
	 * 
	 * @param value
	 * @return
	 */
	public boolean hasValue(int value) {
		for (Cell cell : cells) {
			if (cell.getValue() == value) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Check if any of the cells has this as possible value
	 * 
	 * @param p
	 * @return
	 */
	public boolean hasPossible(int p) {
		for (Cell cell : cells) {
			if (cell.getPossibles().contains(p)) {
				return true;
			}
		}

		return false;
	}

	@Override
	public Iterator<Cell> iterator() {
		return cells.iterator();
	}

	/**
	 * Returns the list of cells with no value set
	 * 
	 * @return
	 */
	public List<Cell> getFreeCells() {

		List<Cell> freeCells = new ArrayList<>();

		for (Cell cell : cells) {
			if (!cell.isFixed()) {
				freeCells.add(cell);
			}
		}

		return freeCells;
	}

	/**
	 * Returns true, if the row/column/block has all the values 1..9
	 * 
	 * @return
	 * @throws TechnicalException
	 */
	public boolean isSolved() throws TechnicalException {
		if (cells.size() != 9) {
			throw new TechnicalException(
					"You cannot use isSolved function on a cell collection that is not a row or column or block");
		}

		List<String> valuesNotFound = new ArrayList<>(Arrays.asList("1", "2", "3", "4", "5", "6", "7", "8", "9"));

		for (Cell cell : cells) {
			if (!cell.isFixed()) {
				return false;
			}
			valuesNotFound.remove(String.valueOf(cell.getValue()));
		}

		// If any values is not found yet
		if (!valuesNotFound.isEmpty()) {
			return false;
		}

		// Its all okay
		return true;
	}

	/**
	 * Returns the list of all the values yet to be fixed for the group
	 * 
	 * @return
	 */
	public List<Integer> getAllPossibles() {
		List<Integer> allPossibles = new ArrayList<>();
		List<String> valuesNotFound = new ArrayList<>(Arrays.asList("1", "2", "3", "4", "5", "6", "7", "8", "9"));
		for (Cell cell : cells) {
			if (cell.isFixed()) {
				valuesNotFound.remove(String.valueOf(cell.getValue()));
			}
		}

		for (String sValue : valuesNotFound) {
			allPossibles.add(Integer.valueOf(sValue));
		}

		return allPossibles;
	}

	/**
	 * Returns all the cells in the group, that has the given value as possible
	 * 
	 * @param p
	 * @return
	 */
	public List<Cell> getByPossible(int p) {
		List<Cell> cellsWithPossible = new ArrayList<>();

		// If cell with the value is already present
		if (hasValue(p)) {
			return cellsWithPossible;
		}

		for (Cell cell : cells) {
			if (!cell.isFixed() && cell.hasPossible(p)) {
				cellsWithPossible.add(cell);
			}
		}

		return cellsWithPossible;
	}

}
