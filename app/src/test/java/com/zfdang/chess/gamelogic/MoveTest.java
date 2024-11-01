package com.zfdang.chess.gamelogic;


import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class MoveTest {

    @Test
    public void testGetCoordDescription() {
        Position from = new Position(7, 7); // h2
        Position to = new Position(4, 7); // e2
        Move move = new Move(from, to);
        String expectedDescription = "h2e2";
        assertEquals(expectedDescription, move.getCoordDescription());
    }

    @Test
    public void testGetChineseStyleDescription() {
        Board board = new Board();
        Move move = new Move(board);

        // red pieces
        move.fromPosition = new Position(8, 9);
        move.toPosition = new Position(8, 3);
        board.setPieceByPosition(move.fromPosition, Piece.WJU); // Set a piece at the from position
        assertEquals("车一进六", move.getChineseStyleDescription());

        move.fromPosition = new Position(2, 2);
        move.toPosition = new Position(2, 9);
        board.setPieceByPosition(move.fromPosition, Piece.WPAO); // Set a piece at the from position
        assertEquals("炮七退七", move.getChineseStyleDescription());

        move.fromPosition = new Position(2, 9);
        move.toPosition = new Position(4, 7);
        board.setPieceByPosition(move.fromPosition, Piece.WXIANG); // Set a piece at the from position
        assertEquals("相七进五", move.getChineseStyleDescription());

        move.fromPosition = new Position(5, 8);
        move.toPosition = new Position(4, 8);
        board.setPieceByPosition(move.fromPosition, Piece.WSHUAI); // Set a piece at the from position
        assertEquals("帅四平五", move.getChineseStyleDescription());

        move.fromPosition = new Position(3, 7);
        move.toPosition = new Position(4, 8);
        board.setPieceByPosition(move.fromPosition, Piece.WSHI); // Set a piece at the from position
        assertEquals("仕六退五", move.getChineseStyleDescription());

        // black pieces

        move.fromPosition = new Position(3, 9);
        move.toPosition = new Position(3, 8) ;
        board.setPieceByPosition(move.fromPosition, Piece.BJU); // Set a piece at the from position
        assertEquals("车四退一", move.getChineseStyleDescription());

        move.fromPosition = new Position(7, 7);
        move.toPosition = new Position(3, 7) ;
        board.setPieceByPosition(move.fromPosition, Piece.BPAO); // Set a piece at the from position
        assertEquals("炮八平四", move.getChineseStyleDescription());

        move.fromPosition = new Position(2, 0);
        move.toPosition = new Position(4, 2) ;
        board.setPieceByPosition(move.fromPosition, Piece.BXIANG); // Set a piece at the from position
        assertEquals("象三进五", move.getChineseStyleDescription());

        move.fromPosition = new Position(2, 2);
        move.toPosition = new Position(1, 4) ;
        board.setPieceByPosition(move.fromPosition, Piece.BMA); // Set a piece at the from position
        assertEquals("马三进二", move.getChineseStyleDescription());
    }
}