/**
 * 
 */
package com.satt.games.sudoku.service;

import com.satt.games.sudoku.service.abs.ParentService;

/**
 * @author samaruth
 *
 */
public class StripService extends ParentService {

	private static final StripService instance = new StripService();

	protected StripService() {

	}

	public static StripService getInstance() {
		return instance;
	}

}
