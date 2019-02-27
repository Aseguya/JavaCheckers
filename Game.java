import java.util.ArrayList;

public class Game {
	public boolean allowPlayerInput = true;
	
	private Board board;
	private BoardRenderer renderer;
	
	public Game(BoardRenderer renderer) {
		// Give the pieces a reference to the main game object, for use in the event handlers
    	BoardPiece.game = this;
    	this.renderer = renderer;
    	board = new Board(renderer);
	}

	
	private class Move {
		public int startX, startY;
		public int endX, endY;
		
		// A move's validity depends on which color piece is making the move
		public boolean isWhite;
		
		public Move(int startX, int startY, int endX, int endY, boolean isWhite) {
			this.startX = startX; this.startY = startY;
			this.endX = endX; this.endY = endY;
			this.isWhite = isWhite;
		}
		
		public boolean isValid() {
			if(
				(endX & 1) != (~endY & 1) ||	// 1. The end position is on the empty tiles of the checker-pattern
				!board.inBounds(endX, endY) ||	// 2. The end position is outside of the board
				!board.isEmpty(endX, endY) 		// 3. The end position is already occupied
			) return false;
			
			int dX = endX - startX, dY = endY - startY;
			
			// The move must be on a diagonal
			if(Math.abs(dX) != Math.abs(dY))
				return false;
			
			switch(Math.abs(dX)) {
				// If attempting to move normally
				case 1: return true;
					
				// If attempting to jump a piece
				case 2: return isWhite ? board.isBlack(startX + dX/2, startY + dY/2) : board.isWhite(startX + dX/2, startY + dY/2);
				
				// No other types of moves are possible
				default: return false;
			}
		}
	}
	
	private ArrayList<Move> getPossibleMoves(int x, int y) {
		// If there are additional moves to be made, return those moves.
		ArrayList<Move> moves = new ArrayList<Move>();
		
		boolean isWhite = board.isWhite(x, y);
		
		// Create a list of moves that should be checked from here
		Move[] immediateMoves = {
			new Move(x, y, x + 1, y + 1, isWhite),
			new Move(x, y, x - 1, y + 1, isWhite),
			new Move(x, y, x + 1, y - 1, isWhite),
			new Move(x, y, x - 1, y - 1, isWhite),
			new Move(x, y, x + 2, y + 2, isWhite),
			new Move(x, y, x - 2, y + 2, isWhite),
			new Move(x, y, x + 2, y - 2, isWhite),
			new Move(x, y, x - 2, y - 2, isWhite)
		};
		
		for(Move move : immediateMoves)
			if(move.isValid())
				moves.add(move);
		
		return moves;
	}
	
	public void startHints(int x, int y) {
		for (Move move : getPossibleMoves(x, y))
			renderer.highlightTile(move.endX, move.endY);
	}
	
	public void stopHints() {
		renderer.clearHighlights();
	}
	
	public void start() {
		board.setup();
	}
	
	// The player is attempting to move a piece at the given coordinates
	// Return whether or not that move is valid.
	public boolean tryPlayerMove(int oldX, int oldY, int newX, int newY) {
		// Create a move from the input coordinates
		Move playerMove = new Move(oldX, oldY, newX, newY, board.isWhite(oldX, oldY));
		
		// If the move is possible
		if(playerMove.isValid()) {
			// Move the piece to the new coordinates
			board.movePiece(oldX, oldY, newX, newY);
			return true;
		}
		
		// The move wasn't made
		return false;
	}
	
}
