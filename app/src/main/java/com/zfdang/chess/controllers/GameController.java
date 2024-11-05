package com.zfdang.chess.controllers;

import android.util.Log;

import com.zfdang.chess.gamelogic.Board;
import com.zfdang.chess.gamelogic.Game;
import com.zfdang.chess.gamelogic.GameStatus;
import com.zfdang.chess.gamelogic.Move;
import com.zfdang.chess.gamelogic.Piece;
import com.zfdang.chess.gamelogic.Position;
import com.zfdang.chess.gamelogic.PvInfo;
import com.zfdang.chess.gamelogic.Rule;

import org.jetbrains.annotations.NotNull;
import org.petero.droidfish.player.ComputerPlayer;
import org.petero.droidfish.player.EngineListener;
import org.petero.droidfish.player.SearchListener;
import org.petero.droidfish.player.SearchRequest;

import java.util.ArrayList;

public class GameController implements EngineListener, SearchListener {
    public ComputerPlayer player = null;
    private String engineName = "pikafish";
    public static final int MAX_MOVES = 2048;

    public Game game = null;
    public Game oldGame = null;
    private int searchId;
    private long searchStartTime;
    private long searchEndTime;

    public boolean isComputerPlaying = true;
    public boolean isAutoPlay = true;

    private GameControllerListener gui = null;
    ArrayList<PvInfo> multiPVs = new ArrayList<>();
    boolean multiPVMode = false;

    public boolean isRedTurn;

    public GameController(GameControllerListener cListener) {
        gui = cListener;

        isComputerPlaying = true;
        isAutoPlay = true;
        searchId = 0;

        // Initialize computer player
        if(player == null) {
            player = new ComputerPlayer(this, this);
        }
        player.queueStartEngine(searchId++, engineName);
        newGame();
    }

    public void newGame() {
        isRedTurn = true;
        game = new Game();
        player.uciNewGame();
    }

    public void toggleComputer() {
        isComputerPlaying = !isComputerPlaying;
    }

    public void toggleComputerAutoPlay() {
        isAutoPlay = !isAutoPlay;
    }

    private void toggleTurn() { isRedTurn = !isRedTurn; }

    // this can be called by either GUI or computer
    public void stepBack() {
        if(game.history.size() > 0) {
            Game.HistoryRecord record = game.undoMove();
            isRedTurn = record.isRedMove;
            gui.onGameEvent(GameStatus.MOVE, game.getLastMoveDesc());
        } else {
            gui.onGameEvent(GameStatus.ILLEGAL, "无棋可悔");
        }
    }

    // computer to play his turn
    public void computerForward() {
        if(isRedTurn) {
            // play only plays the black
            gui.onGameEvent(GameStatus.ILLEGAL, "该红方出着");
            return;
        }
        gui.onGameEvent(GameStatus.SELECT, "电脑搜索着法中...");

        // trigger searchrequest, engine will call notifySearchResult for bestmove
        searchStartTime = System.currentTimeMillis();
        Board board = null;
        if (game.history.size() == 0) {
            board = game.currentBoard;
        } else {
            board = game.history.get(0).move.board;
        }
        SearchRequest sr = SearchRequest.searchRequest(
                searchId++,
                board,
                game.getMoveList(),
                new Board(game.currentBoard),
                null,
                false,
                engineName,
                3);
        player.queueAnalyzeRequest(sr);
    }

    public void playerAskForHelp() {
        if(!isRedTurn) {
            // play only plays the black
            gui.onGameEvent(GameStatus.ILLEGAL, "己方出着时方可寻求帮助");
            return;
        }

        // notifiSearchResult will check this
        multiPVMode = true;

        // trigger searchrequest, engine will call notifySearchResult for bestmove
        searchStartTime = System.currentTimeMillis();
        Board board = null;
        if(game.history.size() == 0) {
            board = game.currentBoard;
        } else {
            board = game.history.get(0).move.board;
        }
        SearchRequest sr = SearchRequest.searchRequest(
                searchId++,
                board,
                game.getMoveList(),
                new Board(game.currentBoard),
                null,
                false,
                engineName,
                3);
        player.queueAnalyzeRequest(sr);
    }

    public void touchPosition(@NotNull Position pos) {
        if(game.startPos == null) {
            // start position is empty
            if(Piece.isValid(game.currentBoard.getPieceByPosition(pos))){
                // and the piece is valid
                game.setStartPos(pos);
                gui.onGameEvent(GameStatus.SELECT);
            }
        } else {
            // startPos is not empty
            if(game.startPos.equals(pos)) {
                // click the same position, unselect
                game.clearStartPos();
                gui.onGameEvent(GameStatus.SELECT);
                return;
            }

            // 判断pos是否是合法的move的终点
            Move tempMove = new Move(game.startPos, pos, game.currentBoard);
            boolean valid = Rule.isValidMove(tempMove, game.currentBoard);
            if(valid) {
                game.setEndPos(pos);

                // 手工走棋，确保这时候非电脑控制
                if(!isRedTurn && isComputerPlaying) {
                    Log.d("GameController", "Computer is playing, please wait");
                    gui.onGameEvent(GameStatus.ILLEGAL, "黑方为电脑控制");

                    // reset start/end position
                    game.startPos = null;
                    game.endPos = null;
                    return;
                }

                doMoveAndUpdateStatus();

                if(isAutoPlay && isComputerPlaying && !isRedTurn) {
                    computerForward();
                }
            } else {
                gui.onGameEvent(GameStatus.ILLEGAL);
            }
        }

    }
    public void moveNow() {
        // send "stop" to engine for "bestmove"
        player.stopSearch();
    }

    public void computerMovePiece(String bestmove) {
        // validate the move
        Move move = new Move(game.currentBoard);
        boolean result = move.fromUCCIString(bestmove);

        if(result) {
            Log.d("GameController", "computer move: " + move.getChsString());

            game.setStartPos(move.fromPosition);
            game.setEndPos(move.toPosition);
            doMoveAndUpdateStatus();
        }
    }

    public void processMultiPVInfos(){
        // show multiPV infos
        for(PvInfo pv : multiPVs) {
            Log.d("GameController", "PV: " + pv);
        }
        game.generateSuggestedMoves(multiPVs);

        // notify GUI
        gui.onGameEvent(GameStatus.MULTIPV, "选择编号或直接移动棋子：");
    }

    public void selectMultiPV(int index) {
        multiPVMode = false;
        Move move = game.getSuggestedMove(index);
        game.clearSuggestedMoves();

        if(move != null) {
            game.startPos = move.fromPosition;
            game.endPos = move.toPosition;
            doMoveAndUpdateStatus();

            if(isAutoPlay && isComputerPlaying && !isRedTurn) {
                computerForward();
            }
        }
    }


    public void doMoveAndUpdateStatus(){
        // game.startPos & game.endPos should be ready

        // check piece color to move
        int piece = game.currentBoard.getPieceByPosition(game.startPos);
        if(Piece.isRed(piece) != isRedTurn) {
            Log.e("GameController", "Invalid move, piece color is not match");
            if(isRedTurn) {
                gui.onGameEvent(GameStatus.ILLEGAL, "该红方出着");
            } else {
                gui.onGameEvent(GameStatus.ILLEGAL, "该黑方出着");
            }
            // reset start/end position
            game.startPos = null;
            game.endPos = null;
            return;
        }

        game.movePiece();

        // update controller status
        toggleTurn();

        // update game status after the move
        GameStatus status = game.updateGameStatus();

        // clear multipv status
        multiPVMode = false;
        game.clearSuggestedMoves();

        // send notification to GUI
        if(status == GameStatus.CHECKMATE) {
            gui.onGameEvent(GameStatus.CHECKMATE, "将死！");
        } else if(status == GameStatus.CHECK) {
            gui.onGameEvent(GameStatus.CHECK, "将军！");
        } else {
            gui.onGameEvent(GameStatus.MOVE, game.getLastMoveDesc());
        }

    }

    @Override
    public void reportEngineError(String errMsg) {
        Log.d("GameController", "Engine error: " + errMsg);
    }

    @Override
    public void notifyEngineName(String engineName) {
        Log.d("GameController", "Engine name: " + engineName);
    }

    @Override
    public void notifyDepth(int id, int depth) {
        Log.d("GameController", "Depth: " + depth);
    }

    @Override
    public void notifyCurrMove(int id, Board board, Move m, int moveNr) {
        Log.d("GameController", "Current move: " + m.getUCCIString());
    }

    @Override
    public void notifyPV(int id, Board board, ArrayList<PvInfo> pvInfos, Move ponderMove) {
        // show infos about all pvInfos
        multiPVs.clear();
        for(PvInfo pv : pvInfos) {
            Log.d("GameController", "PV: " + pv);
            multiPVs.add(pv);
        }
    }

    @Override
    public void notifyStats(int id, long nodes, int nps, long tbHits, int hash, int time, int seldepth) {
        Log.d("GameController", "Stats: nodes=" + nodes + ", nps=" + nps + ", tbHits=" + tbHits + ", hash=" + hash + ", time=" + time + ", seldepth=" + seldepth);
    }

    @Override
    public void notifyBookInfo(int id, String bookInfo, ArrayList<Move> moveList, String eco, int distToEcoTree) {
        Log.d("GameController", "Book info: bookInfo=" + bookInfo + ", eco=" + eco + ", distToEcoTree=" + distToEcoTree);
    }

    @Override
    public void notifySearchResult(int searchId, String bestMove, String nextPonderMove) {
        Log.d("GameController", "Search result: bestMove=" + bestMove + ", nextPonderMove=" + nextPonderMove);
        searchEndTime = System.currentTimeMillis();

        // engine返回bestmove, 有两种情况，一种是电脑搜索的结果，一种是红方寻求帮助的结果
        if(multiPVMode) {
            // 红方寻求帮助，或者电脑被强制要求变着
            // 把multiPV的结果显示在界面上，让用户选择
            gui.runOnUIThread(() -> processMultiPVInfos());
        } else {
            // 电脑发起的请求，走下一步棋子
            gui.runOnUIThread(() -> computerMovePiece(bestMove));
        }
    }

    @Override
    public void notifyEngineInitialized() {
        Log.d("GameController", "Engine initialized");
    }

}
