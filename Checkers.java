import java.util.ArrayList;

public class Checkers {
	
	/**
	 * 
	 * 
	Board Coordinates
	(0,0)(0,1)(0,2)(0,3)(0,4)(0,5)(0,6)
	(1,0)(1,1)(1,2)(1,3)(1,4)(1,5)(1,6)
	(2,0)(2,1)(2,2)(2,3)(2,4)(2,5)(2,6)
	(3,0)(3,1)(3,2)(3,3)(3,4)(3,5)(3,6)
	(4,0)(4,1)(4,2)(4,3)(4,4)(4,5)(4,6)
	(5,0)(5,1)(5,2)(5,3)(5,4)(5,5)(5,6)
	(6,0)(6,1)(6,2)(6,3)(6,4)(6,5)(6,6)
	
	White Piece = -2
	White King = -1
	Nothing = 0
	Black Piece = 1
	Black King = 2
	 */
	
	public class Move
	{
		public int startRow, startColumn, endRow, endColumn;
		public boolean removeFlag;
		public int removeRow, removeColumn;
		
		public Move(int startRow, int startColumn, int endRow, int endColumn)
		{
			this.startRow = startRow;
			this.startColumn = startColumn;
			this.endRow = endRow;
			this.endColumn = endColumn;
		}
		
		public int[][] makeMove(int[][] board)
		{
			int[][] tempBoard = new int[8][8];
			//Clone Board
			for(int row = 0; row < board.length; row++)
				for(int column = 0; column < board[0].length; column++)
					tempBoard[row][column] = board[row][column];
				
			int piece = board[startRow][startColumn];
			tempBoard[startRow][startColumn] = 0;
			tempBoard[endRow][endColumn] = piece;
			if(removeFlag)
			{
				tempBoard[removeRow][removeColumn] = 0;
			}
			
			return tempBoard;
		}
		
	}
	
	public int[][] board;
	
	public Checkers(int[][] input)
	{
		board = input;
	}
	
	/**
	 * Returns the best move for the bot. 
	 * @param
	 * @return Best move for the bot
	 */
	public Move getBestMove(BoardPiece[][] board, int depth)
	{
		
		int[][] tempBoard = new int[8][8];
		
		for(BoardPiece[] x: board)
		{
			for(BoardPiece piece: x)
			{
				if(piece != null)
				{
					if(piece.isWhite)
					{
						tempBoard[piece.gridY][piece.gridX] = -1;
					}
					else
					{
						tempBoard[piece.gridY][piece.gridX] = 1;
					}
				}
			}
		}
		
		Checkers.Move botMove = getBestMove(tempBoard, false, depth);
		return botMove;
	}
	
	/**
	 * Returns the best move for the bot. Default Depth is 3
	 * @param
	 * @return Best move for the bot
	 */
	public Move getBestMove(BoardPiece[][] board)
	{
		return getBestMove(board, 3);
	}

	public Move getBestMove(int[][] board, boolean isBlack, int depth)
	{
		int[][] tempBoard = new int[8][8];
		//Clone Board
		for(int row = 0; row < board.length; row++)
			for(int column = 0; column < board[0].length; column++)
				tempBoard[row][column] = board[row][column];
		
		//Finding Best Move For Bot
		ArrayList<Move> moves = getAllValidMoves(tempBoard, isBlack);
		Move bestMove = null;
		int bestScore = -99;
		for(Move move: moves)
		{
			int score = getMoveScore(tempBoard, isBlack, depth);
			if(score > bestScore)
			{
				bestScore = score;
				bestMove = move;
			}
		}
		return bestMove;
	}
	
	public int getMoveScore(int[][] board, boolean isBlack, int depth)
	{
		if(depth == 0 || !gameContinue(board))
			return whiteScore(board)-blackScore(board);
		
		int[][] tempBoard = new int[8][8];
		//Clone Board
		for(int row = 0; row < board.length; row++)
			for(int column = 0; column < board[0].length; column++)
				tempBoard[row][column] = board[row][column];
		
		int bestScore;
		
		//Finding Best Move for Bot
		if(!isBlack)
		{
			ArrayList<Move> possibleMoves = getAllValidMoves(tempBoard, isBlack);
			Move bestMove = null;
			bestScore = -99;
			for(Move move: possibleMoves)
			{
				int score = getMoveScore(move.makeMove(tempBoard), !isBlack, depth-1);
				if(score > bestScore)
				{
					bestMove = move;
					bestScore = score;
				}
			}
		}
		//Finding All Moves for Black
		else
		{
			ArrayList<Move> possibleMoves = getAllValidMoves(tempBoard, !isBlack);
			Move bestMove = null;
			bestScore = 99;
			for(Move move: possibleMoves)
			{
				int score = getMoveScore(move.makeMove(tempBoard), !isBlack, depth);
				if(score < bestScore)
				{
					bestMove = move;
					bestScore = score;
				}
			}
		}
		
		return bestScore;
	}
	
	public ArrayList<Move> getAllValidMoves(int[][] board, boolean isBlack)
	{
		ArrayList<Move> moves = new ArrayList<Move>();
		for(int row = 0; row < board.length; row++)
		{
			for(int column = 0; column < board.length; column++)
			{
				//White
				if(board[row][column] < 0 && !isBlack)
				{
					Move left = new Move(row, column, row+1, column-1);
					Move right = new Move(row, column, row+1, column+1);
					if(validMove(board, left))
					{
						moves.add(left);
					}
					if(validMove(board, right))
					{
						moves.add(right);
					}
				}
				//Black
				else if(board[row][column] > 0 && isBlack)
				{
					Move left = new Move(row, column, row-1, column-1);
					Move right = new Move(row, column, row-1, column+1);
					if(validMove(board, left))
					{
						moves.add(left);
					}
					if(validMove(board, right))
					{
						moves.add(right);
					}
				}
			}
		}
		return moves;
	}
	
	public boolean validMove(int[][] board, Move move)
	{
		//Move Out of Bounds
		if(move.endRow < 0 || move.endColumn < 0 || move.endRow > 7 || move.endColumn > 7)
			return false;
		//Space Occupied By Similar Piece
		if(board[move.startRow][move.startColumn] == board[move.endRow][move.endColumn])
			return false;
		//Enemy Piece
		if(board[move.startRow][move.startColumn] + board[move.endRow][move.endColumn] == 0)
		{
			//Look Forward
			int jumpRow = move.endRow + (move.endRow - move.startRow);
			int jumpColumn = move.endColumn + (move.endColumn - move.startColumn);
			if(jumpRow < 0 || jumpColumn < 0 || jumpRow > 7 || jumpColumn > 7)
				return false;
			if(board[jumpRow][jumpColumn] != 0)
				return false;
			move.removeRow = move.endRow;
			move.removeColumn = move.endColumn;
			move.removeFlag = true;
			move.endRow = jumpRow;
			move.endColumn = jumpColumn;
		}
		return true;
	}
	
	public boolean gameContinue(int[][] board)
	{
		boolean black = false, white = false;
		for(int[] row: board)
		{
			for(int piece: row)
			{
				if(piece > 0)
					black = true;
				else if(piece < 0)
					white = true;
				if(black && white)
					return true;
			}
		}
		return false;
	}
	
	public int blackScore(int[][] board)
	{
		int score = 0;
		for(int[] row: board)
		{
			for(int piece: row)
			{
				if(piece > 0)
				{
					score += piece;
				}
			}
		}
		return score;
	}
	
	public int whiteScore(int[][] board)
	{
		int score = 0;
		for(int[] row: board)
		{
			for(int piece: row)
			{
				if(piece < 0)
				{
					score += piece;
				}
			}
		}
		return score * -1;
	}
	
	public int[][] cloneBoard()
	{
		int[][] tempBoard = new int[8][8];
		//Clone Board
		for(int row = 0; row < board.length; row++)
			for(int column = 0; column < board[0].length; column++)
				tempBoard[row][column] = board[row][column];
		return tempBoard;
	}
	
	public void printBoard(int[][] board)
	{
		for(int i = 0; i < 8; i++)
		{
			for(int j = 0; j < 8; j++)
			{
				System.out.print("(" + board[i][j] + ") \t");
			}
			System.out.println("\n");
		}
	}
	
	public void printBoard()
	{
		for(int i = 0; i < 8; i++)
		{
			for(int j = 0; j < 8; j++)
			{
				System.out.print("(" + board[i][j] + ") \t");
			}
			System.out.println("\n");
		}
	}
	
	public void printChords()
	{
		for(int i = 0; i < 8; i++)
		{
			for(int j = 0; j < 8; j++)
			{
				System.out.print("(" + i + "," + j + ")");
			}
			System.out.println();
		}
	}
}
