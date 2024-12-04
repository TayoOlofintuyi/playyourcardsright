package com.example.playyourcardsright

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.playyourcardsright.databinding.ActivityMainBinding
import com.example.playyourcardsright.databinding.ActivityResultBinding

class ResultActivity : AppCompatActivity(){

    private val gameResults = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityResultBinding.inflate(layoutInflater)
        setContentView(binding.root)


        //retrieve the list of result passed via intent
        val gameResult = intent.getStringExtra("gameResult")
        gameResult?.let{
            gameResults.add(it)
        }

        binding.resultRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.resultRecyclerView.adapter = ResultListAdapter(gameResults)



    }
}