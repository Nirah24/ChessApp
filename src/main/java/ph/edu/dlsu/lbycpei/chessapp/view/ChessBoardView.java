package ph.edu.dlsu.lbycpei.chessapp.view;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import ph.edu.dlsu.lbycpei.chessapp.model.ChessBoard;
import ph.edu.dlsu.lbycpei.chessapp.model.pieces.ChessPiece;
import ph.edu.dlsu.lbycpei.chessapp.model.pieces.PieceType;
import ph.edu.dlsu.lbycpei.chessapp.utils.ImageLoader;

import java.util.ArrayList;
import java.util.List;

public class ChessBoardView extends Canvas {
    private static final int BOARD_SIZE = 640;
    private static final int SQUARE_SIZE = BOARD_SIZE / 8;
    private static final int COORD_MARGIN = 30; // Space for coordinates
    private static final int TOTAL_WIDTH = BOARD_SIZE + COORD_MARGIN;
    private static final int TOTAL_HEIGHT = BOARD_SIZE + COORD_MARGIN;

    private final GraphicsContext gc;
    private ChessBoard board;
    private int selectedRow = -1;
    private int selectedCol = -1;
    private List<ChessPiece.MoveInfo> highlightedSquares = new ArrayList<>();


    // Chess piece images
    private Image[][] pieceImages;
    private boolean imagesLoaded = false;

    public ChessBoardView() {
        super(TOTAL_WIDTH, TOTAL_HEIGHT);
        gc = getGraphicsContext2D();
        initializePieceImages();
    }

    private void initializePieceImages() {
        pieceImages = ImageLoader.loadChessPieceImages();
        imagesLoaded = ImageLoader.isSuccess();
    }

    public void updateBoard(ChessBoard board) {
        this.board = board;
        draw();
    }

    public void selectSquare(int row, int col) {
        selectedRow = row;
        selectedCol = col;
        draw();
    }

    public void clearSelection() {
        selectedRow = -1;
        selectedCol = -1;
        draw();
    }

    private void draw() {
        gc.clearRect(0, 0, TOTAL_WIDTH, TOTAL_HEIGHT);

        // Draw coordinate labels first
        drawCoordinateLabels();

        // Draw squares (offset by coordinate margin)
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                boolean isLight = (row + col) % 2 == 0;
                gc.setFill(isLight ? Color.WHEAT : Color.SADDLEBROWN);


                // Highlight selected square - ADDED
                if (row == selectedRow && col == selectedCol) {
                    gc.setFill(Color.YELLOW);
                }

                // Highlight legal move squares - ADDED
                for (ChessPiece.MoveInfo move : highlightedSquares) {
                    if (move.row() == row && move.col() == col) {
                        switch (move.type()) {
                            case NORMAL -> gc.setFill(Color.LIGHTGREEN);
                            case CAPTURE -> gc.setFill(Color.CRIMSON);
                            case EN_PASSANT -> gc.setFill(Color.ORANGE);
                        }
                    }
                }


                for (ChessPiece.MoveInfo move : highlightedSquares) {
                    if (move.row() == row && move.col() == col) {
                        switch (move.type()) {
                            case NORMAL -> gc.setFill(Color.LIGHTGREEN);
                            case CAPTURE -> gc.setFill(Color.CRIMSON);
                            case EN_PASSANT -> gc.setFill(Color.ORANGE);
                        }
                    }
                }

                double x = col * SQUARE_SIZE + COORD_MARGIN;
                double y = row * SQUARE_SIZE;
                gc.fillRect(x, y, SQUARE_SIZE, SQUARE_SIZE);

                // Draw piece
                if (board != null) {
                    ChessPiece piece = board.pieceAt(row, col);
                    if (piece != null) {
                        drawPiece(piece, row, col);
                    }
                }

                // Draw black dot for NORMAL moves
                for (ChessPiece.MoveInfo move : highlightedSquares) {
                    if (move.row() == row && move.col() == col && move.type() == ChessPiece.MoveType.NORMAL) {
                        double centerX = col * SQUARE_SIZE + COORD_MARGIN + SQUARE_SIZE / 2.0;
                        double centerY = row * SQUARE_SIZE + SQUARE_SIZE / 2.0;
                        double radius = SQUARE_SIZE * 0.1;

                        gc.setFill(Color.BLACK);
                        gc.fillOval(centerX - radius / 2, centerY - radius / 2, radius, radius);
                    }
                }

            }
        }

        // Draw the board border
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(2);
        gc.strokeRect(COORD_MARGIN, 0, BOARD_SIZE, BOARD_SIZE);
    }

    private void drawCoordinateLabels() {
        gc.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        gc.setFill(Color.BLACK);

        // Draw file labels (a-h) at bottom
        String[] files = {"a", "b", "c", "d", "e", "f", "g", "h"};
        for (int i = 0; i < 8; i++) {
            double x = COORD_MARGIN + i * SQUARE_SIZE + SQUARE_SIZE / 2.0 - 5;
            double y = BOARD_SIZE + 20;
            gc.fillText(files[i], x, y);
        }

        // Draw rank labels (8-1) on left side
        String[] ranks = {"8", "7", "6", "5", "4", "3", "2", "1"};
        for (int i = 0; i < 8; i++) {
            double x = 15;
            double y = i * SQUARE_SIZE + SQUARE_SIZE / 2.0 + 5;
            gc.fillText(ranks[i], x, y);
        }
    }

    private void drawPiece(ChessPiece piece, int row, int col) {
        if (imagesLoaded) {
            // Draw using images
            Image pieceImage = pieceImages[piece.getColor()][piece.getType().getCode()];
            if (pieceImage != null && !pieceImage.isError()) {
                double x = col * SQUARE_SIZE + COORD_MARGIN;
                double y = row * SQUARE_SIZE;

                // Calculate image size to fit within square with some padding
                double imageSize = SQUARE_SIZE * 0.8; // 80% of square size
                double offset = (SQUARE_SIZE - imageSize) / 2; // Center the image

                gc.drawImage(pieceImage, x + offset, y + offset, imageSize, imageSize);
                return;
            }
        }

        // Fallback to text symbols if images aren't available
        drawPieceAsText(piece, row, col);
    }

    private void drawPieceAsText(ChessPiece piece, int row, int col) {
        String symbol = getPieceSymbol(piece);
        gc.setFont(Font.font("Arial", FontWeight.BOLD, 40));
        double x = col * SQUARE_SIZE + COORD_MARGIN + SQUARE_SIZE / 2.0 - 15;
        double y = row * SQUARE_SIZE + SQUARE_SIZE / 2.0 + 15;

        // Draw shadow for better visibility
        gc.setFill(piece.getColor() == ChessPiece.WHITE ? Color.BLACK : Color.WHITE);
        gc.fillText(symbol, x + 2, y + 2);
        gc.setFill(piece.getColor() == ChessPiece.WHITE ? Color.WHITE : Color.BLACK);
        gc.fillText(symbol, x, y);
    }

    public String getPieceSymbol(ChessPiece piece) {
        return getPieceSymbol(piece.getType());
    }

    public String getPieceSymbol(PieceType type) {
        return switch (type) { // Fallback
            case PieceType.PAWN -> "♟";
            case PieceType.ROOK -> "♜";
            case PieceType.KNIGHT -> "♞";
            case PieceType.BISHOP -> "♝";
            case PieceType.QUEEN -> "♛";
            case PieceType.KING -> "♚";
        };
    }

    public int[] getLocation(double x, double y) {
        // Adjust for coordinate margin
        int col = (int) ((x - COORD_MARGIN) / SQUARE_SIZE);
        int row = (int) (y / SQUARE_SIZE);
        return new int[]{row, col};
    }


    // testing
    public void highlightSquares(List<ChessPiece.MoveInfo> legalMoves) {
        this.highlightedSquares = legalMoves;
        draw(); // Redraw board with highlight info
    }


    public void clearHighlights() {
        this.highlightedSquares = List.of(); // empty
        draw();
    }

}