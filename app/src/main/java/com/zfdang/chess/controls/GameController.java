package com.zfdang.chess.controls;

import com.zfdang.chess.gamelogic.Game;

import org.petero.droidfish.engine.EngineOptions;
import org.petero.droidfish.player.ComputerPlayer;
import org.petero.droidfish.player.EngineListener;
import org.petero.droidfish.player.SearchListener;

public class GameController{
    public static final int MAX_MOVES = 2048;
    public ComputerPlayer player;
    public Game game = null;
    public Game oldGame = null;
    private int requestId;
    public boolean isComputerPlaying = true;
    public boolean isAutoPlay = true;

    private EngineListener engineListener = null;
    private SearchListener searchListener = null;
    private GameControllerListener controllerListener = null;
    public GameController(EngineListener eListener, SearchListener sListener, GameControllerListener cListener) {
        engineListener = eListener;
        searchListener = sListener;
        controllerListener = cListener;

        isComputerPlaying = true;
        isAutoPlay = true;
        requestId = 0;
    }

    public void newGame() {
        game = new Game();

        // Initialize computer player
        if(player == null) {
            player = new ComputerPlayer(engineListener, searchListener);
            player.setEngineOptions(new EngineOptions());
        }
        player.queueStartEngine(requestId++,"pikafish");
        player.uciNewGame();
    }

    public void togglePlayer() {
        isComputerPlaying = !isComputerPlaying;
    }

    public void toggleAutoPlay() {
        isAutoPlay = !isAutoPlay;
    }
    public void nextMove(){
        player.sendToEngine("position fen rnbakabnr/9/1c5c1/p1p1p1p1p/9/9/P1P1P1P1P/1C5C1/9/RNBAKABNR w - - 0 1 moves h2e2 h9g7 h0g2 g6g5");
        player.sendToEngine("go depth 5");
    }

    public void playerBack() {

    }

    public void playerForward() {

    }

}
