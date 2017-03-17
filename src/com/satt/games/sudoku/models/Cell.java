/**
 * 
 */
package com.satt.games.sudoku.models;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.satt.games.sudoku.exceptions.FixedValueModifiedException;

/**
 * @author samaruth
 *
 */
public class Cell {

	private int rowNo;
	private int colNo;
	private int blockNo;
	private int position;
	private int value;
	private boolean isFixed = false;
	private boolean isSource = false;
	private List<Integer> possibles = new ArrayList<>(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9));

	public Cell() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * Create a new cell
	 * 
	 * @param c
	 *            - column position 1...9
	 * @param r
	 *            - row position 1...9
	 */
	public Cell(int c, int r) {
		this.setCR(c, r);
	}

	/**
	 * To string function
	 */
	public String toString() {
		return "[" + (isSource ? "SOURCE" : "") + "(" + colNo + "," + rowNo + ")" + (isFixed ? "Fixed" : "") + "="
				+ (value != 0 ? value : possibles.toString()) + "]";
	}

	/**
	 * Remove the impossible value from the possibilities
	 * 
	 * @param impossible
	 */
	public void removePossibility(int impossible, String reason) {

		if (isFixed || possibles.size() == 0) {
//			System.out.println("The cell " + identity() + " has value fixed already");
			return;
		}

		for (Integer integer : possibles) {
			if (integer.intValue() == impossible) {
				possibles.remove(integer);
				System.out.println("Removing possibilty [" + impossible + "] from " + identity() + " as " + reason);
				break;
			}
		}
	}

	public int getRowNo() {
		return rowNo;
	}

	public void setRowNo(int rowNo) {
		this.rowNo = rowNo;
	}

	public int getColNo() {
		return colNo;
	}

	public void setColNo(int colNo) {
		this.colNo = colNo;
	}

	public int getBlockNo() {
		int x0 = colNo, y0 = rowNo;
		int x1 = ((int) (x0 - 1) / 3 + 1), y1 = ((int) (y0 - 1) / 3 + 1);
		blockNo = x1 + (y1 - 1) * 3;
		return blockNo;
	}

	public void setBlockNo(int blockNo) {
		this.blockNo = blockNo;
	}

	public int getValue() {
		return value;
	}

	public void setValue(int value) throws FixedValueModifiedException {
		if (isFixed || isSource) {
			throw new FixedValueModifiedException("The value at " + colNo + "," + rowNo + " is not modifiable (New:"
					+ value + ", Old:" + this.value + ")");
		}
		this.value = value;
		// No more editing the value of this cell
		this.isFixed = true;
		// No more possible values for this cell
		this.possibles = new ArrayList<>();
	}

	public boolean isFixed() {
		return isFixed;
	}

	public void setFixed(boolean isFixed) {
		this.isFixed = isFixed;
	}

	public boolean isSource() {
		return isSource;
	}

	public void setSource(boolean isSource) {
		this.isSource = isSource;
	}

	public List<Integer> getPossibles() {
		return possibles;
	}

	public void setPossibles(List<Integer> possibles) {
		this.possibles = possibles;
	}

	public int getPosition() {
		return position;
	}

	public void setPosition(int position) {
		this.position = position;
	}

	public void setCR(int colNo, int rowNo) {
		this.setColNo(colNo);
		this.setRowNo(rowNo);
		getBlockNo();
		this.setPosition((colNo - 1) * 9 + rowNo);
	}

	/**
	 * Check if the two cells are same
	 * 
	 * @param tCell
	 * @return
	 */
	public boolean equals(Cell tCell) {
		return tCell.getColNo() == colNo && tCell.getRowNo() == rowNo;
	}

	/**
	 * Check if the cells have the same possibilities
	 * 
	 * @param tCell
	 * @return
	 */
	public boolean hasSamePossibles(Cell tCell) {
		if (possibles.size() == tCell.getPossibles().size()) {
			List<Integer> tPossibles = tCell.getPossibles();
			for (Integer p1 : possibles) {
				if (!tPossibles.contains(p1)) {
					// the other cell does not have this possible value
					return false;
				}
			}

			// All the possibles are present in the other cell and their sizes
			// are same
			return true;
		}
		// They are not even of the same size
		return false;
	}

	/**
	 * Check if the target cell has at least all the possibilities of the cell.
	 * 
	 * @param tCell
	 *            - the target (superset) cell
	 * 
	 * @return
	 */
	public boolean hasSubsetPossibles(Cell tCell) {

		List<Integer> tPossibles = tCell.getPossibles();
		for (Integer p1 : possibles) {
			if (!tPossibles.contains(p1)) {
				// the other cell does not have this possible value
				return false;
			}
		}

		// All the possibles are present in the other cell and their sizes
		// are same
		return true;

	}

	/**
	 * Check if the target cell has a possible
	 * 
	 * @param p
	 * @return
	 */
	public boolean hasPossible(int p) {
		return possibles.contains(p);
	}

	/**
	 * Checks if two cells are in the same column
	 * 
	 * @param tCell
	 * @return
	 */
	public boolean isSameColumn(Cell tCell) {
		return colNo == tCell.colNo;
	}

	/**
	 * Checks if two cells are in the same row
	 * 
	 * @param tCell
	 * @return
	 */
	public boolean isSameRow(Cell tCell) {
		return rowNo == tCell.rowNo;
	}

	/**
	 * Checks if two cells are in the same block
	 * 
	 * @param tCell
	 * @return
	 */
	public boolean isSameBlock(Cell tCell) {
		return getBlockNo() == tCell.getBlockNo();
	}

	/**
	 * Returns the possibles that are common between two cells
	 * 
	 * @param cell2
	 * @return
	 */
	public List<Integer> getCommonPossible(Cell cell2) {
		List<Integer> commonPossiles = new ArrayList<>();
		for (Integer integer : possibles) {
			if (cell2.getPossibles().contains(integer)) {
				commonPossiles.add(integer);
			}
		}

		return commonPossiles;
	}

	public String identity() {
		return "Cell[" + colNo + "," + rowNo + "]";
	}

}
