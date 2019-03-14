public class Board {
	// Width must always be an even number
	public static final int width = 8;
	public static final int height = 8;
	private BoardRenderer renderer;
	
	public Board(BoardRenderer renderer) {
		this.renderer = renderer;
	}
	
	// Note: The array is dense along the X-axis, with the empty checkered spaces removed
	private BoardPiece[][] nodes = new BoardPiece[width/2][height];
	
	public void addPiece(BoardPiece piece) {
		nodes[piece.gridX/2][piece.gridY] = piece;
		renderer.addPiece(piece);
	}
	
	public void removePiece(BoardPiece piece) {
		nodes[piece.gridX/2][piece.gridY] = null;
		renderer.removePiece(piece);
	}
	
	// Populate the board with pieces according to standard rules
	public void setup() {
		// For each row of the board
		for (int y = 0; y < Board.height; y++) {
			// Skip the no-man's land
			if(y >= 3 && y < Board.height - 3) continue;
			
			// For each tile in that row
        	for(int x = 0; x < Board.width; ++x) {
        		// If it doesn't match the checker pattern, continue along
        		if((x & 1) != (~y & 1)) continue;
        		
        		// Place a piece of the appropriate color
        		addPiece(new BoardPiece(x, y, y < 3));
        	}
        }
	}
	
	// Check if a given location is within the boundaries of the board
	public boolean inBounds(int x, int y) {
		return !(x >= width || x < 0 ||y >= height || y < 0);
	}
	
	// Move the piece located at the given coordinate.
	// If a piece occupies the end position, or if the starting position has no piece, nothing happens.
	public void movePiece(int oldX, int oldY, int newX, int newY) {
		if(!inBounds(oldX, oldY) || !inBounds(newX, newY)) return;
		
		BoardPiece piece = nodes[oldX/2][oldY];
		if(piece == null || nodes[newX/2][newY] != null) return;
		
		piece.gridX = newX;
		piece.gridY = newY;
		piece.enforcePosition();
		
		nodes[oldX/2][oldY] = null;
		nodes[newX/2][newY] = piece;
	}
	
	public BoardPiece[][] getBoard()
	{
		return nodes;
	}
	
	// Simplified access methods --------------------------------------------------
	public boolean isBlack(int x, int y) {
		return inBounds(x, y) && nodes[x/2][y] != null && !nodes[x/2][y].isWhite;
	}
	public boolean isWhite(int x, int y) {	
		return inBounds(x, y) && nodes[x/2][y] != null && nodes[x/2][y].isWhite;
	}	
	public boolean isEmpty(int x, int y) {
		return inBounds(x, y) && nodes[x/2][y] == null;
	}
	// ----------------------------------------------------------------------------
}
