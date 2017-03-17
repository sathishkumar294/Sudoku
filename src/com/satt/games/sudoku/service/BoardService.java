package com.satt.games.sudoku.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.satt.games.sudoku.exceptions.FixedValueModifiedException;
import com.satt.games.sudoku.exceptions.InvalidValueException;
import com.satt.games.sudoku.exceptions.TechnicalException;
import com.satt.games.sudoku.models.Board;
import com.satt.games.sudoku.models.Cell;
import com.satt.games.sudoku.service.abs.ParentService;

public class BoardService extends ParentService {

	private static final BoardService instance = new BoardService();
	private RigidityService rigidityService = RigidityService.getInstance();

	/**
	 * Reads input from the user
	 * 
	 * @param board
	 * @return
	 * @throws InvalidValueException
	 * @throws TechnicalException
	 */
	public boolean getProblem(Board board) throws TechnicalException, InvalidValueException {

		String input = "";
		boolean doExit = false;
		Scanner scan = new Scanner(System.in);
		Pattern pattern = Pattern.compile("([A-I]([0-9])-([0-9]))");
		do {
			System.out.print("Enter the next input (eg: A1-value, end to solve the problem):");
			input = scan.nextLine();
			Matcher matcher = pattern.matcher(input);
			if (matcher.find()) {
				String col = matcher.group(1);
				String row = matcher.group(2);
				String sValue = matcher.group(3);

				int colNo = ((int) col.charAt(0)) - ((int) "A".charAt(0)) + 1;
				int rowNo = Integer.valueOf(row);
				int value = Integer.valueOf(sValue);

				checkAndsetValue(board, colNo, rowNo, value, true);

			} else if (input.equalsIgnoreCase("end")) {
				doExit = true;
			} else {
				System.out.println("Invalid pattern. Continue...");
				continue;
			}
		} while (!input.equals("end") && !doExit);
		scan.close();

		// this is the input you have entered
		board.display(0, false);

		return true;
	}

	/**
	 * Reads the input from a file
	 * 
	 * @param board
	 * @param fileName
	 * @return
	 * @throws TechnicalException
	 * @throws InvalidValueException
	 */
	public boolean getProblem(Board board, String fileName) throws TechnicalException, InvalidValueException {
		String input = "";
		Pattern pattern = Pattern.compile("([A-I]([0-9])-([0-9]))");

		InputStream fileStream = null;
		fileStream = this.getClass().getResourceAsStream("/" + fileName);

		BufferedReader bReader = new BufferedReader(new InputStreamReader(fileStream));
		System.out.println("Reading from file:");
		try {
			input = bReader.readLine();

			while (input != null) {

				Matcher matcher = pattern.matcher(input);
				if (matcher.find()) {
					String col = matcher.group(1);
					String row = matcher.group(2);
					String sValue = matcher.group(3);

					int colNo = ((int) col.charAt(0)) - ((int) "A".charAt(0)) + 1;
					int rowNo = Integer.valueOf(row);
					int value = Integer.valueOf(sValue);

					checkAndsetValue(board, colNo, rowNo, value, true);

				} else {
					System.out.println("Invalid pattern. Continue...");
					continue;
				}

				input = bReader.readLine();

			}

			bReader.close();
			fileStream.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// this is the input you have entered
		board.display(0, true);

		return true;
	}

	/**
	 * Reads the input from a file
	 * 
	 * @param board
	 * @param fileName
	 * @return
	 * @throws TechnicalException
	 * @throws InvalidValueException
	 */
	public boolean getProblem2(Board board, String fileName)
			throws TechnicalException, InvalidValueException {

		String input = "";

		InputStream fileStream = null;
		fileStream = this.getClass().getResourceAsStream("/" + fileName);

		BufferedReader bReader = new BufferedReader(new InputStreamReader(fileStream));
		System.out.println("Reading from file:");
		try {
			input = bReader.readLine();
			int colNo = 1;
			int rowNo = 1;
			int value = 0;
			while (input != null && rowNo <= 9) {
				colNo = 1;
				for (char c : input.toCharArray()) {
					if (Character.isSpaceChar(c) || Character.isWhitespace(c)) {
						continue;
					}
					if (Character.isDigit(c)) {
						value = Integer.parseInt(String.valueOf(c));
						checkAndsetValue(board, colNo, rowNo, value, true);
					}
					colNo++;
				}
				input = bReader.readLine();
				rowNo++;
			}

			bReader.close();
			fileStream.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// this is the input you have entered
		board.display(0, false);

		return true;
	}

	/**
	 * Sets the value taking care of impossibles
	 * 
	 * @param board
	 * @param c
	 * @param r
	 * @param value
	 * @param isSource
	 * @throws TechnicalException
	 * @throws InvalidValueException
	 */
	public void checkAndsetValue(Board board, int c, int r, int value, boolean isSource)
			throws TechnicalException, InvalidValueException {
		Cell tCell = board.get(c, r);

		// check if the row does not have this value already
		if (board.getRow(r).hasValue(value)) {
			throw new InvalidValueException("The row " + r + " already has the value [" + value + "]");
		}
		if (board.getColumn(c).hasValue(value)) {
			throw new InvalidValueException("The column " + c + " already has the value [" + value + "]");
		}
		int b = RigidityService.getBlockNo(c, r);
		if (board.getBlock(b).hasValue(value)) {
			throw new InvalidValueException("The block " + b + " already has the value [" + value + "]");
		}

		try {
			tCell.setValue(value);
			tCell.setSource(isSource);
			rigidityService.removeImpossibles(board, tCell);
		} catch (FixedValueModifiedException e) {
			throw new TechnicalException("Fixed value modified error:", e);
		}

	}

	/**
	 * Overloaded method
	 * 
	 * @param board
	 * @param cell
	 * @param value
	 * @param isSource
	 * @throws TechnicalException
	 * @throws InvalidValueException
	 */
	public void setValue(Board board, Cell cell, int value, boolean isSource)
			throws TechnicalException, InvalidValueException {
		checkAndsetValue(board, cell.getColNo(), cell.getRowNo(), value, isSource);
	}

	/** Singleton */
	public static BoardService getInstance() {
		return instance;
	}

}
