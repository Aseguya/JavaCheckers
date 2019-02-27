import javafx.application.*;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

public class BoardRenderer extends Application {
	
	/*==============================================================*\
	| Predefined Values												 |
	\*--------------------------------------------------------------*/
	public static final int paddingX = 10;
	public static final int paddingY = 10;
	
	/*==============================================================*\
	| Derived Values												 |
	\*--------------------------------------------------------------*/
	// Tile Dimensions (pixels)
	public static final int tileWidth = (BoardPiece.radius + paddingX) * 2;
	public static final int tileHeight = (BoardPiece.radius + paddingY) * 2;
	
	/*============================================================-*\
	| Member Variables                                              |
	\*-------------------------------------------------------------*/
	private Game game;
	
	// The group that contain all the highlighted tiles
	private Group highlights = new Group();
	
	// The group that contains all the pieces on the board
	private Group pieces = new Group();
	
	// Create the application
    public static void main(String[] args) {
        launch(args);
    }
    
    public void addPiece(BoardPiece piece) {
    	pieces.getChildren().add(piece);
    }
    
    public void removePiece(BoardPiece piece) {
    	pieces.getChildren().remove(piece);
    }
    
    public void highlightTile(int x, int y) {
    	Rectangle highlight = new Rectangle(x * tileWidth, y * tileHeight, tileWidth, tileHeight);
    	highlight.setFill(Color.web("#FF0"));
    	highlights.getChildren().add(highlight);
    }
    
    public void clearHighlights() {
    	highlights.getChildren().clear();
    }
    
    private Group createGrid() {
    	Group grid = new Group();
        
    	for(int i = 0; i < Board.width; ++i) {
	        // Create a vertical line
        	Line vLine = new Line(tileWidth * i, 0, tileWidth * i, Board.height * tileHeight);
	        grid.getChildren().add(vLine);
        }
        
        for(int i = 0; i < Board.height; ++i) {
	        // Create a horizontal line
	        Line hLine = new Line(0, tileHeight * i, Board.width * tileWidth, tileHeight * i);
	        grid.getChildren().add(hLine);
        }
        
        return grid;
    }
    
    @Override
    public void start(Stage primaryStage) throws Exception {
    	game = new Game(this);
    	
    	
        Group root = new Group();
        Scene scene = new Scene(root, Board.width * tileWidth, Board.height* tileHeight, Color.web("#AAA"));
        primaryStage.setScene(scene);
        
        root.getChildren().add(highlights);
        root.getChildren().add(createGrid());
        root.getChildren().add(pieces);
        
        primaryStage.show();
        
        game.start();
    }
}
