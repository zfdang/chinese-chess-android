package com.zfdang.chess.gamelogic;

import android.util.Log;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


/**
 * Created by 77304 on 2021/4/4.
 */

public class Rule {
    private static int[][] area = new int[][]{
            {1, 1, 1, 2, 2, 2, 1, 1, 1},
            {1, 1, 1, 2, 2, 2, 1, 1, 1},
            {1, 1, 1, 2, 2, 2, 1, 1, 1},
            {1, 1, 1, 1, 1, 1, 1, 1, 1},
            {1, 1, 1, 1, 1, 1, 1, 1, 1},

            {3, 3, 3, 3, 3, 3, 3, 3, 3},
            {3, 3, 3, 3, 3, 3, 3, 3, 3},
            {3, 3, 3, 4, 4, 4, 3, 3, 3},
            {3, 3, 3, 4, 4, 4, 3, 3, 3},
            {3, 3, 3, 4, 4, 4, 3, 3, 3},
    };
    private static int[][] offsetX = new int[][]{
            {0, 0, 1, -1},             //帅 将
            {1, 1, -1, -1},            //仕 士
            {2, 2, -2, -2},            //相 象
            {1, 1, -1, -1},            //象眼
            {1, 1, -1, -1, 2, 2, -2, -2},  //马
            {0, 0, 0, 0, 1, 1, -1, -1},    //蹩马腿
            {0},                    //卒
            {-1, 0, 1},               //过河卒
            {0},                    //兵
            {-1, 0, 1},               //过河兵
            {1, 1, -1, -1, 1, 1, -1, -1},  //反向蹩马腿
    };
    private static int[][] offsetY = new int[][]{
            {1, -1, 0, 0},             //帅 将
            {1, -1, 1, -1},            //仕 士
            {2, -2, 2, -2},            //相 象
            {1, -1, 1, -1},            //象眼
            {2, -2, 2, -2, 1, -1, 1, -1},  //马
            {1, -1, 1, -1, 0, 0, 0, 0},    //蹩马腿
            {1},                    //卒
            {0, 1, 0},                //过河卒
            {-1},                   //兵
            {0, -1, 0},               //过河兵
            {1, -1, 1, -1, 1, -1, 1, -1},  //反向蹩马腿
    };


    /*
    在棋盘中找到将帅的位置
     */
    public static Position findJiangShuaiPos(int piece, Board board){
        int x = 0, y = 0;
        if(piece == Piece.WSHUAI) {
            for (y = 7; y <= 9; y++) {
                for (x = 3; x <= 5; x++) {
                    if (board.getPieceByPosition(x,y) == Piece.WSHUAI) {
                        return new Position(x, y);
                    }
                }
            }
        } else if (piece == Piece.BJIANG) {
            for (y = 0; y <= 2; y++) {
                for (x = 3; x <= 5; x++) {
                    if (board.getPieceByPosition(x,y) == Piece.BJIANG) {
                        return new Position(x, y);
                    }
                }
            }
        }
        return null;
    }


    public static List<Position> PossibleToPositions(int pieceId, int fromX, int fromY, Board board) {
        List<Position> ret = new ArrayList<Position>();
        int num;
        Position flyPos = null;
        switch (pieceId) {
            case Piece.BJIANG: //黑将
                num = 0;
                for (int i = 0; i < offsetX[num].length; i++) {
                    int toX = fromX + offsetX[num][i];
                    int toY = fromY + offsetY[num][i];
                    if (InArea(toX, toY) == 2 && !onSameSide(pieceId, board.getPieceByPosition(toX, toY))) {
                        flyPos = flyKing(Piece.BJIANG, toX, toY, board);
                        if (flyPos == null) {
                            // (toX, toY)不会导致将帅照脸
                            ret.add(new Position(toX, toY));
                        }
                    }
                }
                break;
            case Piece.BSHI: //黑士
                num = 1;
                for (int i = 0; i < offsetX[num].length; i++) {
                    int toX = fromX + offsetX[num][i];
                    int toY = fromY + offsetY[num][i];
                    if (InArea(toX, toY) == 2 && !onSameSide(pieceId, board.getPieceByPosition(toX, toY))) {
                        ret.add(new Position(toX, toY));
                    }
                }
                break;
            case Piece.BXIANG: //黑象
                num = 2;
                for (int i = 0; i < offsetX[num].length; i++) {
                    int toX = fromX + offsetX[num][i];
                    int toY = fromY + offsetY[num][i];
                    int blockX = fromX + offsetX[num + 1][i];
                    int blockY = fromY + offsetY[num + 1][i];
                    if (InArea(toX, toY) >= 1 && InArea(toX, toY) <= 2
                            && !onSameSide(pieceId, board.getPieceByPosition(toX, toY))
                            && !Piece.isValid(board.getPieceByPosition(blockX, blockY))) {
                        ret.add(new Position(toX, toY));
                    }
                }
                break;
            case Piece.BMA: //黑马
            case Piece.WMA: //红马
                num = 4;
                for (int i = 0; i < offsetX[num].length; i++) {
                    int toX = fromX + offsetX[num][i];
                    int toY = fromY + offsetY[num][i];
                    int blockX = fromX + offsetX[num + 1][i];
                    int blockY = fromY + offsetY[num + 1][i];
                    if (InArea(toX, toY) != 0
                            && !onSameSide(pieceId, board.getPieceByPosition(toX, toY))
                            && !Piece.isValid(board.getPieceByPosition(blockX, blockY))) {
                        ret.add(new Position(toX, toY));
                    }
                }
                break;
            case Piece.BJU: //黑车
            case Piece.WJU: //红车
                for (int i = fromY + 1; i < Board.BOARD_PIECE_HEIGHT; i++) { //向下走
                    if (CanMove(Piece.BJU, fromX, fromY, fromX, i, board)) {
                        //可以走时
                        ret.add(new Position(fromX, i));
                    } else {
                        //不可以走时直接 break
                        break;
                    }
                }
                for (int i = fromY - 1; i >= 0; i--) { //向上走
                    if (CanMove(Piece.BJU, fromX, fromY, fromX, i, board)) {
                        //可以走时
                        ret.add(new Position(fromX, i));
                    } else {
                        //不可以走时
                        break;
                    }
                }
                for (int j = fromX - 1; j >= 0; j--) { //向左走
                    if (CanMove(Piece.BJU, fromX, fromY, j, fromY, board)) {
                        //可以走时
                        ret.add(new Position(j, fromY));
                    } else {
                        //不可以走时
                        break;
                    }
                }
                for (int j = fromX + 1; j < Board.BOARD_PIECE_WIDTH; j++) { //向右走
                    if (CanMove(Piece.BJU, fromX, fromY, j, fromY, board)) {
                        //可以走时
                        ret.add(new Position(j, fromY));
                    } else {
                        //不可以走时
                        break;
                    }
                }
                break;
            case Piece.BPAO: //黑炮
            case Piece.WPAO: //红炮
                for (int i = fromY + 1; i < Board.BOARD_PIECE_HEIGHT; i++) { //向下走
                    if (CanMove(Piece.BPAO, fromX, fromY, fromX, i, board)) {
                        //可以走时
                        ret.add(new Position(fromX, i));
                    }
                }
                for (int i = fromY - 1; i >= 0; i--) { //向上走
                    if (CanMove(Piece.BPAO, fromX, fromY, fromX, i, board)) {
                        //可以走时
                        ret.add(new Position(fromX, i));
                    }
                }
                for (int j = fromX - 1; j >= 0; j--) { //向左走
                    if (CanMove(Piece.BPAO, fromX, fromY, j, fromY, board)) {
                        //可以走时
                        ret.add(new Position(j, fromY));
                    }
                }
                for (int j = fromX + 1; j < Board.BOARD_PIECE_WIDTH; j++) { //向右走
                    if (CanMove(Piece.BPAO, fromX, fromY, j, fromY, board)) {
                        //可以走时
                        ret.add(new Position(j, fromY));
                    }
                }
                break;
            case Piece.BZU: //黑卒
                if (InArea(fromX, fromY) == 1) {
                    num = 6;
                    for (int i = 0; i < offsetX[num].length; i++) {
                        int toX = fromX + offsetX[num][i];
                        int toY = fromY + offsetY[num][i];
                        if (InArea(toX, toY) != 0 && !onSameSide(pieceId, board.getPieceByPosition(toX, toY))) {
                            ret.add(new Position(toX, toY));
                        }
                    }
                } else {
                    // 过河卒
                    num = 7;
                    for (int i = 0; i < offsetX[num].length; i++) {
                        int toX = fromX + offsetX[num][i];
                        int toY = fromY + offsetY[num][i];
                        if (InArea(toX, toY) != 0 && !onSameSide(pieceId, board.getPieceByPosition(toX, toY))) {
                            ret.add(new Position(toX, toY));
                        }
                    }
                }
                break;
            case Piece.WSHUAI: //红帅
                num = 0;
                for (int i = 0; i < offsetX[num].length; i++) {
                    int toX = fromX + offsetX[num][i];
                    int toY = fromY + offsetY[num][i];
                    if (InArea(toX, toY) == 4 && !onSameSide(pieceId, board.getPieceByPosition(toX, toY))) {
                        flyPos = flyKing(Piece.WSHUAI, toX, toY, board);
                        if (flyPos == null) {
                            // (toX, toY)不会导致将帅照脸
                            ret.add(new Position(toX, toY));
                        }
                    }
                }
                break;
            case Piece.WSHI: //红士
                num = 1;
                for (int i = 0; i < offsetX[num].length; i++) {
                    int toX = fromX + offsetX[num][i];
                    int toY = fromY + offsetY[num][i];
                    if (InArea(toX, toY) == 4 && !onSameSide(pieceId, board.getPieceByPosition(toX, toY))) {
                        ret.add(new Position(toX, toY));
                    }
                }
                break;
            case Piece.WXIANG: //红象
                num = 2;
                for (int i = 0; i < offsetX[num].length; i++) {
                    int toX = fromX + offsetX[num][i];
                    int toY = fromY + offsetY[num][i];
                    int blockX = fromX + offsetX[num + 1][i];
                    int blockY = fromY + offsetY[num + 1][i];
                    if (InArea(toX, toY) >= 3 && InArea(toX, toY) <= 4
                            && !onSameSide(pieceId, board.getPieceByPosition(toX, toY))
                            && !Piece.isValid(board.getPieceByPosition(blockX, blockY))) {
                        ret.add(new Position(toX, toY));
                    }
                }
                break;
            case Piece.WBING: //红兵
                if (InArea(fromX, fromY) == 3) {
                    num = 8;
                    for (int i = 0; i < offsetX[num].length; i++) {
                        int toX = fromX + offsetX[num][i];
                        int toY = fromY + offsetY[num][i];
                        if (InArea(toX, toY) != 0 && !onSameSide(pieceId, board.getPieceByPosition(toX, toY))) {
                            ret.add(new Position(toX, toY));
                        }
                    }
                } else {
                    // 过河兵
                    num = 9;
                    for (int i = 0; i < offsetX[num].length; i++) {
                        int toX = fromX + offsetX[num][i];
                        int toY = fromY + offsetY[num][i];
                        if (InArea(toX, toY) != 0 && !onSameSide(pieceId, board.getPieceByPosition(toX, toY))) {
                            ret.add(new Position(toX, toY));
                        }
                    }
                }
                break;
            default:
                break;
        }
        return ret;
    }

    /*
    检查走法是否合法
     */
    public static boolean isValidMove(Move move, Board board) {
        if (move == null) {
            return false;
        }

        int piece = board.getPieceByPosition(move.fromPosition);
        int fromX = move.fromPosition.x;
        int fromY = move.fromPosition.y;

        // 检查是否是合法的走法
        List<Position> positions = PossibleToPositions(piece, fromX, fromY, board);
        Iterator<Position> it = positions.iterator();
        while (it.hasNext()) {
            Position pos = it.next();
            if (pos.equals(move.toPosition)) {
                // toPosition是所有合法的走法之一
                return true;
            }
        }

        return false;
    }

    public static boolean isJiangShuaiInDanger(int piece, Position pos, Board board) {
        // 马的攻击和别腿，红帅和黑将都会用到
        int num = 4;
        int op_block_num = 10; // 反向蹩马腿

        if (piece == Piece.WSHUAI) {
            // 找到红帅的位置
            int x = pos.x, y = pos.y;

            // 被黑马攻击
            for (int i = 0; i < offsetX[num].length; i++) {
                int toX = x + offsetX[num][i];
                int toY = y + offsetY[num][i];
                int blockX = x + offsetX[op_block_num][i];
                int blockY = y + offsetY[op_block_num][i];
                if (InArea(toX, toY) != 0
                        && board.getPieceByPosition(toX, toY) == Piece.BMA
                        && !Piece.isValid(board.getPieceByPosition(blockX, blockY))) {
                    return true;
                }
            }
            // 被黑车攻击
            if(attackableByJuPao(Piece.BJU, x, y, board)){
                return true;
            }
            // 被黑炮攻击
            if(attackableByJuPao(Piece.BPAO, x, y, board)){
                return true;
            }
            // 被黑卒攻击
            if (board.getPieceByPosition(x-1, y) == Piece.BZU
                    || board.getPieceByPosition(x+1, y) == Piece.BZU
                    || board.getPieceByPosition(x, y-1) == Piece.BZU) {
                return true;
            }
        } else if(piece == Piece.BJIANG) {
            // 找到黑将的位置
            int x = pos.x, y = pos.y;

            // 被红马攻击
            for (int i = 0; i < offsetX[num].length; i++) {
                int toX = x + offsetX[num][i];
                int toY = y + offsetY[num][i];
                int blockX = x + offsetX[op_block_num][i];
                int blockY = y + offsetY[op_block_num][i];
                if (InArea(toX, toY) != 0
                        && board.getPieceByPosition(toX, toY) == Piece.WMA
                        && !Piece.isValid(board.getPieceByPosition(blockX, blockY))) {
                    return true;
                }
            }
            // 被红车攻击
            if(attackableByJuPao(Piece.WJU, x, y, board)){
                return true;
            }
            // 被红炮攻击
            if(attackableByJuPao(Piece.WPAO, x, y, board)){
                return true;
            }
            // 被红兵攻击
            if (board.getPieceByPosition(x-1, y) == Piece.WBING
                    || board.getPieceByPosition(x+1, y) == Piece.WBING
                    || board.getPieceByPosition(x, y+1) == Piece.WBING) {
                return true;
            }
        }
        return false;
    }

    /*
       判断将帅是否被将死
       判断逻辑比较简单：将自己一方的所有棋子的所有可能移动的位置都走一遍，如果将帅还在被将军的状态，那么就返回true；否则返回false。
     */
    public static boolean isJiangShuaiDead(int piece, Position bosspos, Board b) {
        Board board = new Board(b);
        if (piece == Piece.WSHUAI) {
            // 遍历所有的棋子，找到红方的所有棋子
            for (int y = 0; y < Board.BOARD_PIECE_HEIGHT; y++) {
                for (int x = 0; x < Board.BOARD_PIECE_WIDTH; x++) {
                    int pieceid = board.getPieceByPosition(x, y);
                    if (Piece.isRed(pieceid)) {
                        // 是红方的棋子
                        List<Position> positions = PossibleToPositions(pieceid, x, y, board);
                        Iterator<Position> it = positions.iterator();
                        while (it.hasNext()) {
                            Position pos = it.next();
                            int tempPieceId = board.getPieceByPosition(pos);

                            // move piece
                            board.setPieceByPosition(x, y, Piece.EMPTY);
                            board.setPieceByPosition(pos, pieceid);

                            boolean result;
                            if(pieceid == Piece.WSHUAI){
                                // 将帅移动了位置
                                result = isJiangShuaiInDanger(Piece.WSHUAI, pos, board);
                            } else {
                                result = isJiangShuaiInDanger(Piece.WSHUAI, bosspos, board);
                            }

                            if (!result) {
                                // 移动了某个棋子后，红帅不再被将军，则返回false
                                return false;
                            }

                            // revert
                            board.setPieceByPosition(x, y, pieceid);
                            board.setPieceByPosition(pos, tempPieceId);
                        }
                    }
                }
            }
        } else if(piece == Piece.BJIANG) {
            // 遍历所有的棋子，找到黑方的所有棋子
            for (int y = 0; y < Board.BOARD_PIECE_HEIGHT; y++) {
                for (int x = 0; x < Board.BOARD_PIECE_WIDTH; x++) {
                    int pieceid = board.getPieceByPosition(x, y);
                    if (Piece.isBlack(pieceid)) {
                        // 是黑方的棋子
                        List<Position> positions = PossibleToPositions(pieceid, x, y, board);
                        Iterator<Position> it = positions.iterator();
                        while (it.hasNext()) {
                            Position pos = it.next();
                            int tempPieceId = board.getPieceByPosition(pos);

                            // move piece
                            board.setPieceByPosition(x, y, Piece.EMPTY);
                            board.setPieceByPosition(pos, pieceid);

                            boolean result;
                            if(pieceid == Piece.BJIANG){
                                // 将帅移动了位置
                                result = isJiangShuaiInDanger(Piece.BJIANG, pos, board);
                            } else {
                                result = isJiangShuaiInDanger(Piece.BJIANG, bosspos, board);
                            }

                            if (!result) {
                                // 移动了某个棋子后，黑将不再被将军，则返回false
                                return false;
                            }

                            // revert
                            board.setPieceByPosition(x, y, pieceid);
                            board.setPieceByPosition(pos, tempPieceId);
                        }
                    }
                }
            }
        }
        return true;
    }

    /*
     * 判断(x,y)是否会被piece攻击，piece只能是车或者炮。
     * 假如（x,y)是车或者炮，检查它的攻击范围内是否有车或者炮，如果有那么反过来也会被攻击
     */
    private static boolean attackableByJuPao(int piece, int x, int y, Board board){
        if(!(piece == Piece.BJU || piece == Piece.BPAO || piece == Piece.WJU || piece == Piece.WPAO)){
            return false;
        }
        List<Position> positions = PossibleToPositions(piece, x, y, board);
        Iterator<Position> it = positions.iterator();
        while (it.hasNext()) {
            Position pos = it.next();
            if (board.getPieceByPosition(pos) == piece) {
                return true;
            }
        }
        return false;
    }



    private static int InArea(int x, int y) { // 0 棋盘外 1 黑盘 2 黑十字 3 红盘 4 红十字
        if (x < 0 || x >= Board.BOARD_PIECE_WIDTH || y < 0 || y >= Board.BOARD_PIECE_HEIGHT) {
            return 0;
        }
        return area[y][x];
    }

    private static boolean onSameSide(int fromID, int toID) {
        if(!Piece.isValid(toID) || !Piece.isValid(fromID)) {
            return false;
        }
        return Piece.isRed(fromID) == Piece.isRed(toID);
    }

    /*
    * 飞将
    * 检查将帅是否在同一列，且中间没有其他棋子遮挡。如果是，返回对方的将的位置，否则返回null
     */
    private static Position flyKing(int id, int fromX, int fromY, Board board) {
        int i;
        if (id == Piece.BJIANG) {  //将
            for (i = fromY + 1; i < Board.BOARD_PIECE_HEIGHT; i++) {
                int pieceid = board.getPieceByPosition(fromX, i);
                if (Piece.isValid(pieceid)) {
                    // 是合法的棋子
                    if(pieceid == Piece.WSHUAI) {
                        // 是对方的帅
                        return new Position(fromX, i);
                    } else {
                        // 不是对方的帅
                        return null;
                    }
                }
            }
        } else if(id == Piece.WSHUAI) {       //帅
            for (i = fromY - 1; i >= 0; i--) {
                int pieceid = board.getPieceByPosition(fromX, i);
                if (Piece.isValid(pieceid)) {
                    // 是合法的棋子
                    if(pieceid == Piece.BJIANG) {
                        // 是对方的将
                        return new Position(fromX, i);
                    } else {
                        // 不是对方的将
                        return null;
                    }
                }
            }
        }
        return null;
    }

    private static boolean CanMove(int id, int fromX, int fromY, int toX, int toY, Board board) {
        // validate fromx, fromy, tox, toy
        if (fromX < 0 || fromX >= Board.BOARD_PIECE_WIDTH || fromY < 0 || fromY >= Board.BOARD_PIECE_HEIGHT
                || toX < 0 || toX >= Board.BOARD_PIECE_WIDTH || toY < 0 || toY >= Board.BOARD_PIECE_HEIGHT) {
//            Log.e("Rule", "CanMove: 位置不合法" + fromX + " " + fromY + " " + toX + " " + toY);
            return false;
        }

        if(fromX == toX && fromY == toY){
//            Log.d("Rule", "CanMove: 位置相同" + fromX + " " + fromY + " " + toX + " " + toY);
            return false;
        }

        if (onSameSide(board.getPieceByPosition(fromX, fromY), board.getPieceByPosition(toX, toY))) {
//            Log.d("Rule", "CanMove: 不能吃自己的棋子");
            return false;
        }

        if (id == Piece.BJU || id == Piece.WJU) {  //车
            // 已验证终点和自己非同一颜色，验证中间无子即可
            int start, finish;
            if (fromX == toX) { // 进退
                if (fromY < toY) {
                    start = fromY + 1;
                    finish = toY;
                } else {
                    start = toY + 1;
                    finish = fromY;
                }
                for (int i = start; i < finish; i++) {
                    if (Piece.isValid(board.getPieceByPosition(fromX, i))) {
                        return false;
                    }
                }
            } else { // 平移
                if (fromX < toX) {
                    start = fromX + 1;
                    finish = toX;
                } else {
                    start = toX + 1;
                    finish = fromX;
                }
                for (int i = start; i < finish; i++) {
                    if (Piece.isValid(board.getPieceByPosition(i, fromY))) {
                        return false;
                    }
                }
            }
        } else if(id == Piece.BPAO || id == Piece.WPAO){   //炮
            if (!Piece.isValid(board.getPieceByPosition(toX, toY))) {
                // 终点无子，属于移动，验证中间无子即可
                int start, finish;
                if (fromX == toX) {
                    if (fromY < toY) {
                        start = fromY + 1;
                        finish = toY;
                    } else {
                        start = toY + 1;
                        finish = fromY;
                    }
                    for (int i = start; i < finish; i++) {
                        if (Piece.isValid(board.getPieceByPosition(fromX, i))) {
                            return false;
                        }
                    }
                } else {
                    if (fromX < toX) {
                        start = fromX + 1;
                        finish = toX;
                    } else {
                        start = toX + 1;
                        finish = fromX;
                    }
                    for (int i = start; i < finish; i++) {
                        if (Piece.isValid(board.getPieceByPosition(i, fromY))) {
                            return false;
                        }
                    }
                }
            } else {
                // 终点有子，属于吃子,验证中间只有一子
                int start, finish;
                int count = 0;
                if (fromX == toX) {
                    if (fromY < toY) {
                        start = fromY + 1;
                        finish = toY;

                    } else {
                        start = toY + 1;
                        finish = fromY;
                    }
                    for (int i = start; i < finish; i++) {
                        if (Piece.isValid(board.getPieceByPosition(fromX, i))) {
                            count++;
                        }
                    }
                } else {
                    if (fromX < toX) {
                        start = fromX + 1;
                        finish = toX;
                    } else {
                        start = toX + 1;
                        finish = fromX;
                    }
                    for (int i = start; i < finish; i++) {
                        if (Piece.isValid(board.getPieceByPosition(i, fromY))) {
                            count++;
                        }
                    }
                }
                if (count != 1) {
                    return false;
                }
            }
        }
        return true;
    }
}
