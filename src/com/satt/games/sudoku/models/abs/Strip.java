/**
 * 
 */
package com.satt.games.sudoku.models.abs;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import com.satt.games.sudoku.exceptions.TechnicalException;
import com.satt.games.sudoku.models.CellCollection;

/**
 * @author samaruth
 *
 */
public abstract class Strip {
	private List<CellCollection> groups = new ArrayList<>();
	protected STRIP_TYPE stripType;

	public Strip(STRIP_TYPE stripType) {
		this.stripType = stripType;
	}

	public enum STRIP_TYPE {
		ROWSTRIP, COLSTRIP
	}

	public void add(CellCollection group) {
		groups.add(group);
	}

	public CellCollection get(int n) throws TechnicalException {
		if (groups.size() >= n) {
			return groups.get(n - 1);
		} else {
			throw new TechnicalException(MessageFormat
					.format("The strip contains only {0} items, but the item requested is {1}", groups.size(), n));
		}
	}

	public List<CellCollection> getGroups() {
		return groups;
	}
}
