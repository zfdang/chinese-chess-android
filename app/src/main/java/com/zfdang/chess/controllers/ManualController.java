package com.zfdang.chess.controllers;

import android.content.Context;
import android.util.Log;

import com.zfdang.chess.ChessApp;
import com.zfdang.chess.manuals.XQFManual;
import com.zfdang.chess.manuals.XQFParser;

import java.io.IOException;
import java.io.InputStream;

public class ManualController extends GameController{
    public XQFManual manual = null;
    public XQFManual.MoveNode moveNode = null;

    private Context context = null;

    public ManualController(GameControllerListener listener) {
        super(listener);

        this.context = ChessApp.getContext();
    }

    public boolean loadManualFromFile(String filename) {
        if(filename.toLowerCase().endsWith(".xqf")) {
            // load content from file assets/xqf/, and store it into char buffer
            InputStream inputStream = null;
            try {
                inputStream = this.context.getAssets().open(filename);
                byte[] buffer = new byte[inputStream.available()];
                inputStream.read(buffer);
                inputStream.close();

                // use XQFGame to parse the buffer
                XQFManual xqfManual = XQFParser.parse(buffer);
                manual = xqfManual;
                moveNode = manual.getHeadMove();

                // reset game
                game.currentBoard = xqfManual.board;
                game.history.clear();
                game.suggestedMoves.clear();
                game.startPos = null;
                game.endPos = null;

                return true;
            } catch (IOException e) {
                Log.e("ManualController", "Failed to load manual from file: " + filename);
            }

            return false;
        }

        return false;
    }

    public void manualForward() {

    }

    public void manualBack() {

    }

    public void manualFirst() {

    }
}
