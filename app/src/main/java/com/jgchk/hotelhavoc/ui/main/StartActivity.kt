package com.jgchk.hotelhavoc.ui.main


import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.RecyclerView
import android.widget.Button
import com.jgchk.hotelhavoc.R

class StartActivity : AppCompatActivity() {



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start)

        val startButton = findViewById<Button>(R.id.startgame_button)

        startButton.setOnClickListener {
            val intent = Intent(this, CarryActivity::class.java)
            startActivity(intent)
        }
    }


}
