/**
 * 
 */
package com.satt.games.sudoku.models.abs;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.satt.games.sudoku.exceptions.TechnicalException;

/**
 * @author samaruth
 *
 */
public abstract class StripCollection implements Iterable<Strip> {

	private List<Strip> strips = new ArrayList<>();

	/**
	 * Gets the nth strip from the collection
	 * 
	 * @throws TechnicalException
	 */
	public Strip get(int n) throws TechnicalException {
		if (strips.size() >= n) {
			return strips.get(n - 1);
		} else {
			throw new TechnicalException(MessageFormat
					.format("The collection contains only {0} items, but the item requested is {1}", strips.size(), n));
		}
	}

	public void add(Strip strip) {
		strips.add(strip);
	}
	
	@Override	
	public Iterator<Strip> iterator() {
		return strips.iterator();
	}

}
