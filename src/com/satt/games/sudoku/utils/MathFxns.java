/**
 * 
 */
package com.satt.games.sudoku.utils;

/**
 * @author samaruth
 *
 */
public class MathFxns {
	
	public static int factorial(int i){
		if(i<=1){
			return 1;
		}else {
			return i*factorial(i-1);
		}
	}
	
	public static int combination(int n, int r){
		return factorial(n)/(factorial(r)*factorial(n-r));
	}
	
}
