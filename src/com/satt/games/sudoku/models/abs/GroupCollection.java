/**
 * 
 */
package com.satt.games.sudoku.models.abs;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.satt.games.sudoku.exceptions.TechnicalException;
import com.satt.games.sudoku.models.CellCollection;

/**
 * @author samaruth
 *
 */
public abstract class GroupCollection implements Iterable<CellCollection> {

	private List<CellCollection> groups = new ArrayList<>();

	/**
	 * Returns the nth element of the collection
	 * 
	 * @param n
	 *            = 1...N
	 * @return
	 * @throws TechnicalException
	 */
	public CellCollection get(int n) throws TechnicalException {
		if (groups.size() >= n) {
			return groups.get(n - 1);
		} else {
			throw new TechnicalException(MessageFormat
					.format("The collection contains only {0} items, but the item requested is {1}", groups.size(), n));
		}
	}

	public List<CellCollection> getGroups() {
		return groups;
	}

	public void setGroups(List<CellCollection> groups) {
		this.groups = groups;
	}

	/**
	 * Add a Row, Column or Block to the collection
	 * 
	 * @param group
	 */
	public void add(CellCollection group) {
		groups.add(group);
	}
	
	@Override
	/**
	 * Iterator
	 */
	public Iterator<CellCollection> iterator() {
		return groups.iterator();
	}
	
	/**
	 * Returns the no of groups available in the collection yet
	 * @return
	 */
	public int count(){
		return groups.size();
	}

}
