package ph.edu.dlsu.lbycpei.chessapp.model.pieces;

import ph.edu.dlsu.lbycpei.chessapp.model.ChessBoard;

/**
 * Represents a Queen chess piece, the most powerful piece on the board.
 * The Queen combines the movement capabilities of both the Rook and Bishop,
 * allowing it to move any number of squares in eight directions:
 * horizontally, vertically, and diagonally. This makes the Queen the most
 * versatile and valuable piece in chess, second only to the King in importance.
 *
 * The Queen can move across the entire board in a single move, provided
 * the path is clear of other pieces. It can capture opponent pieces by
 * moving to their square, but cannot jump over pieces like the Knight.
 *
 */
public class Queen extends ChessPiece {

    /**
     * Constructs a new Queen at the specified position with the given color.
     *
     * @param row the initial row position (0-7) of the queen
     * @param col the initial column position (0-7) of the queen
     * @param color the color of the queen (ChessPiece.WHITE or ChessPiece.BLACK)
     */
    public Queen(int row, int col, int color) {
        super(row, col, color);
    }

    /**
     * Determines whether this queen can legally move to the specified position.
     * A queen can move if:
     * <ul>
     * <li>The move is either straight (like a rook: same row or column) or
     *     diagonal (like a bishop: equal row and column differences)</li>
     * <li>All squares along the path between current position and destination are empty</li>
     * <li>The destination square is either empty or contains an opponent's piece</li>
     * <li>The move does not leave the king in check</li>
     * </ul>
     *
     * The queen essentially combines rook and bishop movement patterns:
     * <ul>
     * <li><strong>Rook-like movement:</strong> horizontally or vertically any number of squares</li>
     * <li><strong>Bishop-like movement:</strong> diagonally any number of squares</li>
     * </ul>
     *
     * @param row the target row position (0-7)
     * @param col the target column position (0-7)
     * @param board the current state of the chess board
     * @return true if the queen can legally move to the specified position, false otherwise
     */
    @Override
    public boolean canMoveTo(int row, int col, ChessBoard board) {
        // Calculate the difference in rows and columns
        int rowDiff = Math.abs(row - this.row);
        int colDiff = Math.abs(col - this.col);

        boolean isStraight = (this.row == row || this.col == col);
        boolean isDiagonal = (rowDiff == colDiff);

        if (!isStraight && !isDiagonal || (this.row == row && this.col == col)) {
            return false;
        }

        int rowStep = Integer.compare(row, this.row);
        int colStep = Integer.compare(col, this.col);

        int currentRow = this.row + rowStep;
        int currentCol = this.col + colStep;

        System.out.println("Queen trying to move from (" + this.row + ", " + this.col + ") to (" + row + ", " + col + ")");

        // Check if the path is clear (excluding the destination)
        while (currentRow != row || currentCol != col) {
            System.out.println("Checking square (" + currentRow + ", " + currentCol + ")");
            ChessPiece pieceOnPath = board.pieceAt(currentRow, currentCol);
            if (board.pieceAt(currentRow, currentCol) != null) {
                System.out.println("Blocked by " + board.pieceAt(currentRow, currentCol).getType() + " at (" + currentRow + ", " + currentCol + ")");
                return false;
            }

            currentRow += rowStep;
            currentCol += colStep;
        }

        // Check the destination square
        ChessPiece targetPiece = board.pieceAt(row, col);
        if (targetPiece != null && targetPiece.getColor() == this.color) {
            System.out.println("Target square occupied by same color at (" + row + ", " + col + ")");
            return false;
        }

        boolean safe = !moveWouldCauseCheck(row, col, board);
        System.out.println("Queen move to (" + row + ", " + col + ") is " + (safe ? "valid" : "invalid") + ".");
        return safe;
    }


    /**
     * Returns the type of this chess piece.
     *
     * @return PieceType.QUEEN indicating this is a queen piece
     */
    @Override
    public PieceType getType() {
        return PieceType.QUEEN;
    }
}