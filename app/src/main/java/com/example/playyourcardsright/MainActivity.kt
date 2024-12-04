package com.example.playyourcardsright
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.playyourcardsright.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.warButton.setOnClickListener {
            val intent = Intent(this, War::class.java)
            startActivity(intent)
        }
        binding.blackjackButton.setOnClickListener {
            val intent = Intent(this, BlackJack::class.java)
            startActivity(intent)
        }
    }
}
