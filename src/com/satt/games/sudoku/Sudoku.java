package com.satt.games.sudoku;

import com.satt.games.sudoku.exceptions.InvalidValueException;
import com.satt.games.sudoku.exceptions.TechnicalException;
import com.satt.games.sudoku.factory.BoardFactory;
import com.satt.games.sudoku.models.Board;
import com.satt.games.sudoku.service.BoardService;
import com.satt.games.sudoku.service.ReducerService;

public class Sudoku {

	private static final BoardService boardService = BoardService.getInstance();
	private static final BoardFactory boardFactory = BoardFactory.getInstance();
	private static final ReducerService reducerService = ReducerService.getInstance();
	Board board = new Board();

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		// get a new board
		Board board = null;
		try {
			board = boardFactory.getNewBoard();
		} catch (TechnicalException e) {
			System.err.println("Error while creating a new board, Error : " + e);
			return;
		}

		// Get the problem
		String iFile = "VERY-HARD-1";
		try {
			boardService.getProblem(board, iFile);
		} catch (TechnicalException e) {
			System.err.println("Error while reading the problem from file[" + iFile + "], Error: " + e);
			return;
		} catch (InvalidValueException e) {
			System.err.println("Invalid Value Exception, Error " + e);
			return;
		}

		// Solve the problem
		int maxSteps = 10;
		int step = 1;
		boolean isSolved = false;
		boolean hasProgress = true;
		try {
			while (!isSolved && hasProgress && step <= maxSteps) {
				hasProgress = reducerService.fixNakedPossibilities(board);
				hasProgress = reducerService.fixHiddenPossibles(board) || hasProgress;
				hasProgress = reducerService.fixPossiblesLimitedToRowCol(board) || hasProgress;
				hasProgress = reducerService.fixNakedPairs(board) || hasProgress;
				hasProgress = reducerService.fixHiddenPairs(board) || hasProgress;
				hasProgress = reducerService.fixXWings(board) || hasProgress;
				hasProgress = reducerService.fixSwordFish(board) || hasProgress;

				isSolved = reducerService.isBoardSolved(board);
				board.display(step, true);
				step++;
			}
			if (!isSolved) {
				board.display(step - 1, true);
			} else {
				System.out.println("SOLVED!!");
			}
		} catch (TechnicalException e) {
			System.err.println("Some technical exception, Error: " + e);
			return;
		} catch (InvalidValueException e) {
			System.err.println("Invalid Value Exception, Error " + e);
			return;
		}

	}

}
