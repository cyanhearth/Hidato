package main.java.com.cyanhearth.hidato;

import java.awt.Point;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.ThreadLocalRandom;

/** This class will generate Hidato puzzles and solutions **/
public class BoardGenerator {
	
	public int count;		// number of moves made
	public int noOfSolutions;  // number of solutions found
	
		// list of numbers on board at the game start
	private ArrayList<String> solutions = new ArrayList<>();
	
	private static final int START_NUMBER = 1;
	
	// possible moves
	private static final Point[] MOVES = {
			new Point(0, 1),
			new Point(1, 1),
			new Point(1, 0),
			new Point(1, -1),
			new Point(0, -1),
			new Point(-1, -1),
			new Point(-1, 0),
			new Point(-1, 1)		
	};
	
	public ArrayList<ArrayList<Integer>> readBoard() {
		ArrayList<ArrayList<Integer>> board = new ArrayList<>();
		
		File file = new File("D:\\workspace\\Hidato\\src\\main\\resources\\file\\testempty.txt");
		
		try {
			Scanner sc = new Scanner(file);
			
			while (sc.hasNextLine()) {
				String line = sc.nextLine();
				Scanner lineScanner = new Scanner(line);
				
				ArrayList<Integer> row = new ArrayList<>();
				while (lineScanner.hasNext()) {
					row.add(Integer.valueOf(lineScanner.next()));
				}
				
				lineScanner.close();
				
				board.add(row);
				
			}
			
			sc.close();
		}
		catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		return board;
	}
	
	public ArrayList<ArrayList<Integer>> copyBoard(ArrayList<ArrayList<Integer>> board) {
		ArrayList<ArrayList<Integer>> copy = new ArrayList<>();
		
		for(ArrayList<Integer> row : board) {
			copy.add(new ArrayList<Integer>(row));
		}
		
		return copy;
	}
	
	/** find the position on the board of a given number
	 * 
	 * @param number: the number we want to find the board position for
	 * @return Point: the position of the number on the board
	 */
	public Point findNumberPosition(int number, ArrayList<ArrayList<Integer>> board) {
		int x = -1;
		int y = -1;
		for (ArrayList<Integer> row : board) {
			for (int col : row) {
				if (col == number) {
					x = row.indexOf(col);
					y = board.indexOf(row);
				}
			}
		}
		return new Point(x, y);
	}
	
	/** identify and store which numbers are present on the starting game board
	 * 
	 * @return ArrayList<Integer>: a list of the numbers present at the start of the game
	 */
	public ArrayList<Integer> getStartConfig(ArrayList<ArrayList<Integer>> board) {
		ArrayList<Integer> startConfig = new ArrayList<>();
		
		for (ArrayList<Integer> row : board) {
			for (int col : row) {
				if (col != 0 && col != -1) {
					startConfig.add(col);
				}
			}
		}
		
		return startConfig;
	}
	
	/** returns the number of occupiable spaces on the board
	 * 
	 * @return int: number of occupiable spaces
	 */
	public int getSpaces(ArrayList<ArrayList<Integer>> board) {
		int spaces = 0;
		for (ArrayList<Integer> row : board) {
			for (int col : row) {
				if (col != -1) {
					spaces++;
				}
			}
		}
		return spaces;
	}
	
	/** this method starts from the current position.
	 * 	If the next sequential number is already on the board this is the next move.
	 * 	Otherwise, each valid move is taken individually in a depth-first manner
	 * 	to determine if the path leads to the solution.
	 * 	If it does, return true, else undo the current move, roll back the counter
	 * 	and return false.
	 * @param pos: the position to start from
	 * @param board: the board to solve
	 * @param spaces: the number of occupiable spaces on the board
	 * @param startConfig: the numbers already present on the board
	 * @param findAll: if true find all solutions to the board, otherwise return only one
	 * @param limit: stop finding solutions if the limit is reached
	 * @return boolean: true if the puzzle is solved, false if not
	 */
	public boolean solve(Point pos, ArrayList<ArrayList<Integer>> board, int spaces, 
			ArrayList<Integer> startConfig, int limit) {
		count++;
		if (board.get(pos.y).get(pos.x) == 0) {
			if (startConfig.contains(count)) {
				count--;
				return false;
			}
			board.get(pos.y).set(pos.x, count);
		}
		
		if (count == spaces) {
			noOfSolutions++;
			solutions.add(toString(board));
			printBoard(board);

		}
		shuffleArray(MOVES);
		for (Point move : MOVES) {
			int x = pos.x + move.x;
			int y = pos.y + move.y;
			
			if (y < 0 || y >= board.size())
				continue;
			else if (x < 0 || x >= board.get(0).size())
				continue;
			else if (board.get(y).get(x) != 0) {
				if (board.get(y).get(x) != count + 1) {
					continue;
				}
			}
			
			solve(new Point(x, y), board, spaces, startConfig, limit);
			if (noOfSolutions >= limit && limit > 0)
				return true;
		}
				
		
		if (!startConfig.contains(count))
			board.get(pos.y).set(pos.x, 0);
		count--;
		return noOfSolutions > 0;
	}
	static void shuffleArray(Point[] ar) {
		Random rnd = ThreadLocalRandom.current();
		for (int i = ar.length - 1; i > 0; i--)
	    {
	      int index = rnd.nextInt(i + 1);
	      // Simple swap
	      Point a = ar[index];
	      ar[index] = ar[i];
	      ar[i] = a;
	    }
	}
	
	/**
	 * generate a square board
	 * @param dimension: the value of both the length and width of the board
	 */
	public void generate(int dimension, int limit) {
		generate(dimension, dimension, limit);
	}
	
	/**
	 * generate a Hidato board with user-defined dimensions
	 * @param rows: number of rows on the board
	 * @param cols: number of columns on the board
	 */
	public void generate(int rows, int cols, int noOfPuzzles) {
		for (int num = 0; num < noOfPuzzles; num++) {
			// create an empty puzzle
			ArrayList<ArrayList<Integer>> puzzle = new ArrayList<>();
			while (puzzle.size() < rows) {
				ArrayList<Integer> row = new ArrayList<>(cols);
				for (int i = 0; i < cols; i++) {
					row.add(0);
				}
				puzzle.add(row);
			}

			// TODO: generate -1 spaces to vary shape of puzzle?
			// use solve() to find all possible solutions
		
			int x = (int) (Math.random() * cols);
			int y = (int) (Math.random() * rows);
			puzzle.get(y).set(x, 1);
			
			solve(new Point(x, y), puzzle, getSpaces(puzzle), getStartConfig(puzzle), 1);
		
			String filename = "puzzles " + rows + " x " + cols + " - " + noOfPuzzles + ".txt";
		
			Path out = Paths.get(filename);

			try {
				Files.write(out, solutions, Charset.defaultCharset());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
			count = 0;
			noOfSolutions = 0;
			
		}
		solutions.clear();
		
	}
	/**
	 * create a String representation of a board
	 * @param board
	 * @return String
	 */
	public String toString(ArrayList<ArrayList<Integer>> board) {
		String output = "";
		for (ArrayList<Integer> row : board) {
			String rowString = "";
			for (int col : row) {
				rowString += col + " ";
			}
			output += rowString + System.lineSeparator();
		}
		return output;
	}
	
	public void printBoard(ArrayList<ArrayList<Integer>> board) {
		System.out.println(toString(board));
	}
	
	public int getStartNumber() {
		return START_NUMBER;
	}

	public static void main(String[] args) {
		BoardGenerator gen = new BoardGenerator();
		
		long startTime = System.nanoTime();
		gen.generate(9, 1);	
		long endTime = System.nanoTime();

		long duration = (endTime - startTime) / 1000000;
		
		System.out.println("Time taken: " + duration + "ms");

	}

}
