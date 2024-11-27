package com.zfdang.chess

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.zfdang.chess.manuals.XQFGame
import com.zfdang.chess.views.WebviewActivity
import java.io.BufferedReader
import java.io.File
import java.io.InputStream
import java.io.InputStreamReader

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Bind buttons
        val buttonPlay: Button = findViewById(R.id.button_play)
        val buttonLearn: Button = findViewById(R.id.button_learn)
        val buttonHelp: Button = findViewById(R.id.button_help)
        val buttonAbout: Button = findViewById(R.id.button_about)

        // Set click listeners
        buttonPlay.setOnClickListener {
            val intent = Intent(this, GameActivity::class.java)
            startActivity(intent)
        }

        buttonLearn.setOnClickListener {
            processAssetPath("XQF")
        }

        buttonHelp.setOnClickListener {
            // Handle button setting click
            // launch promptactivity
            val intent = Intent(this, WebviewActivity::class.java).apply {
                putExtra("url", "https://fish.zfdang.com/help.html")
            }
            startActivity(intent)
        }

        buttonAbout.setOnClickListener {
            // launch webview activity
            val intent = Intent(this, WebviewActivity::class.java).apply {
                putExtra("url", "https://fish.zfdang.com/")
            }
            startActivity(intent)
        }
    }

    private fun processAssetPath(path: String) {
        val files = assets.list(path)
        for (file in files!!) {
            if(file.endsWith(".xqf")) {
                readXQFFile(path, file)
            } else {
                processAssetPath(path + "/" + file)
            }
        }
    }

    private fun readXQFFile(path: String, file: String) {
        val xqfFile = path + "/" + file
        if(xqfFile.endsWith(".xqf")) {
            Log.d("MainActivity", "Found XQF file: $xqfFile")

            // load content from file assets/xqf/, and store it into char buffer
            val inputStream = assets.open(xqfFile)
            val buffer = inputStream.readBytes()
            inputStream.close()

            Log.d("MainActivity", "Read ${buffer.size} bytes from ${xqfFile}")

            // use XQFGame to parse the buffer
            val xqfGame = XQFGame.parse(buffer)
            val result = xqfGame.validateMoves()
            if (!result) {
                Log.e("MainActivity", "Failed to validate moves")
            }
            Log.d("MainActivity", "Parsed XQF game: " + xqfGame.title + " with result: " + xqfGame.result)
        } else {
            Log.d("MainActivity", "Not a XQF file: $xqfFile")
        }
    }
}