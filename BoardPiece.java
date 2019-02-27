
import javafx.event.EventHandler;
import javafx.scene.Cursor;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.StrokeType;
import javafx.scene.effect.*;

public class BoardPiece extends Circle {
	
	public static Game game;
	
	public static final int radius = 30;
	
	// Player 1 Colors
	private static final Color player1_fill = Color.web("#EEE");
	private static final Color player1_outline = Color.web("#555");
	
	// Player 2 Colors
	private static final Color player2_fill = Color.web("#555");
	private static final Color player2_outline = Color.web("EEE");
	
	public int gridX = 0;
	public int gridY = 0;
	
	public boolean isWhite;
	
	private int m_nMouseX = 0;
	private int m_nMouseY = 0;
	
	// The drop-shadow effect used to create a sense of depth
	private DropShadow dropShadow = new DropShadow();
	
	// Compute offset of local coordinates
	private double tX = getBoundsInParent().getMinX() - BoardRenderer.paddingX;
	private double tY = getBoundsInParent().getMinY() - BoardRenderer.paddingY;
	
	private void useLiftedEffects() {
		dropShadow.setRadius(10.0);
		dropShadow.setOffsetX(2.0);
		dropShadow.setOffsetY(8.0);
	}
	
	private void useNormalEffects() {
		// Create a small shadow effect
		dropShadow.setRadius(2.0);
		dropShadow.setOffsetX(1.0);
		dropShadow.setOffsetY(2.0);
	}
	
	private void initEffects() {
		useNormalEffects();
		
		setEffect(dropShadow);
	}
	
	public void enforcePosition() {
		setTranslateX(gridX * BoardRenderer.tileWidth - tX);
		setTranslateY(gridY * BoardRenderer.tileHeight - tY);
	}
	
	BoardPiece(int x, int y, boolean isWhite) {
		// Use the 'Circle' superclass
		super(radius, isWhite?player1_fill:player2_fill);
		
		this.isWhite = isWhite;
		
		// Set effects to their normal state
		initEffects();
		
		// Set the mouse-over cursor
		setCursor(Cursor.HAND);
		
		// Define the outline-appearance
		setStrokeType(StrokeType.OUTSIDE);
		setStrokeWidth(4);
		setStroke(isWhite?player1_outline:player2_outline);
		
		// Move the piece to the appropriate tile
		gridX = x;
		gridY = y;
		enforcePosition();
		
		// Setup mouse-event handlers
		setOnMousePressed(pressMouse());
		setOnMouseDragged(dragMouse());
		setOnMouseReleased(releaseMouse());
	}
	
	private EventHandler<MouseEvent> pressMouse() {
		// Create a reference to the current piece, for use in the handler
		BoardPiece piece = this;
		
		// Create the event handler
		return new EventHandler<MouseEvent>() {
			public void handle(MouseEvent event) {
				// If player input is to be ignored, skip this event
				if(!game.allowPlayerInput) return;
				
				// Visually 'lift' the piece
				useLiftedEffects();
				
				// Keep track of the mouse's relative coordinates
				m_nMouseX = (int) (piece.getTranslateX() - event.getSceneX());
				m_nMouseY = (int) (piece.getTranslateY() - event.getSceneY());
				
				game.startHints(piece.gridX, piece.gridY);

				// Move the piece in front of all other pieces
				piece.toFront();
			}
		};
	}
	
	private EventHandler<MouseEvent> releaseMouse() {
		// Create a reference to the current piece, for use in the handler
		BoardPiece piece = this;

		// Create the event handler
		return new EventHandler<MouseEvent>() {
			public void handle(MouseEvent event) {
				// If player input is to be ignored, skip this event
				if(!game.allowPlayerInput) return;
				
				// Return effect back to normal
				piece.useNormalEffects();
				
				// Determine the grid coordinates of the release event
				int newX = (int)event.getSceneX() / BoardRenderer.tileWidth;
				int newY = (int)event.getSceneY() / BoardRenderer.tileHeight;
				
				// Check the value against a checker-pattern, and if it isn't valid, use the elements center position
				if ((newX & 1) != (~newY & 1)) {
					newX = (int)piece.getTranslateX() / BoardRenderer.tileWidth;
					newY = (int)piece.getTranslateY() / BoardRenderer.tileHeight;
				}
				
				// Let the game know the player tried to make a move
				game.tryPlayerMove(piece.gridX, piece.gridY, newX, newY);
				game.stopHints();
				
				// Snap the piece to the grid
				enforcePosition();
			}
		};
	}
	
	
	
	private EventHandler<MouseEvent> dragMouse() {
		BoardPiece piece = this;
		EventHandler<MouseEvent> dragHandler = new EventHandler<MouseEvent>() {
			public void handle(MouseEvent event) {
				// If player input is to be ignored, skip this event
				if(!game.allowPlayerInput) return;
				
				// Move the piece along with the mouse
				piece.setTranslateX(piece.getTranslateX() + event.getX() + piece.m_nMouseX);
				piece.setTranslateY(piece.getTranslateY() + event.getY() + piece.m_nMouseY);
			}
		};
		return dragHandler;
	}
}
