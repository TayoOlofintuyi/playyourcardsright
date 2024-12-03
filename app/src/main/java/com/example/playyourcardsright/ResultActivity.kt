package com.example.playyourcardsright

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.playyourcardsright.databinding.ActivityMainBinding
import com.example.playyourcardsright.databinding.ActivityResultBinding

class ResultActivity : AppCompatActivity(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityResultBinding.inflate(layoutInflater)
        setContentView(binding.root)


        //retrieve the list of result passed via intent


    }
}