package ph.edu.dlsu.lbycpei.chessapp.model.pieces;

import ph.edu.dlsu.lbycpei.chessapp.model.ChessBoard;
import ph.edu.dlsu.lbycpei.chessapp.model.GameLogic;

import java.util.ArrayList;
import java.util.List;

// ChessPiece.java: Abstract ChessPiece class
public abstract class ChessPiece implements ChessPieceInterface {
    public static final int WHITE = 0;
    public static final int BLACK = 1;

    protected int row;
    protected int col;
    protected int color;
    protected boolean hasMoved;

    public static enum MoveType {
        NORMAL, CAPTURE, EN_PASSANT
    }

    public static record MoveInfo(int row, int col, MoveType type) { }
//
//    public abstract List<MoveInfo> getLegalMoves(ChessBoard board);

    public ChessPiece(int row, int col, int color) {
        this.row = row;
        this.col = col;
        this.color = color;
    }

    // Helper method to check if move would cause check
    protected boolean moveWouldCauseCheck(int finalRow, int finalCol, ChessBoard board) {
        // Simulate the move
        ChessPiece capturedPiece = board.pieceAt(finalRow, finalCol);
        int originalRow = this.row;
        int originalCol = this.col;

        // Make temporary move
        board.removePiece(this.row, this.col);
        this.moveTo(finalRow, finalCol);
        board.addPiece(this);

        boolean wouldCauseCheck = GameLogic.isInCheck(board, this.color);

        // Undo the move
        board.removePiece(finalRow, finalCol);
        this.moveTo(originalRow, originalCol);
        board.addPiece(this);
        if (capturedPiece != null) {
            board.addPiece(capturedPiece);
        }
        return wouldCauseCheck;
    }

    @Override
    public void moveTo(int row, int col) {
        this.row = row;
        this.col = col;
        hasMoved = true;
    }

    @Override
    public int getRow() { return row; }

    @Override
    public int getCol() { return col; }

    @Override
    public int getColor() { return color; }

    @Override
    public abstract boolean canMoveTo(int row, int col, ChessBoard board);

    @Override
    public abstract PieceType getType();

    public void setHasMoved(boolean b) { hasMoved = b; }

    public boolean hasMoved() { return hasMoved; }


    public List<MoveInfo> getLegalMoves(ChessBoard board) {
        List<MoveInfo> moves = new ArrayList<>();
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                if (canMoveTo(row, col, board)) {
                    ChessPiece target = board.pieceAt(row, col);

                    MoveType type;
                    if (this instanceof Pawn pawn && GameLogic.canEnPassant(pawn, row, col)) {
                        type = MoveType.EN_PASSANT;
                    } else if (target != null && target.getColor() != this.color) {
                        type = MoveType.CAPTURE;
                    } else {
                        type = MoveType.NORMAL;
                    }

                    moves.add(new MoveInfo(row, col, type));
                }
            }
        }
        return moves;
    }



}