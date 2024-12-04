package com.example.playyourcardsright

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.playyourcardsright.databinding.ListGameResultBinding


class ResultHolder(private val binding: ListGameResultBinding): RecyclerView.ViewHolder(binding.root){

    fun bind(result:String){
        binding.gameResultText.text = result

        binding.gameResultText.setOnClickListener {
            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT,result)
                putExtra(Intent.EXTRA_SUBJECT, "WAR RESULT!!!")
            }

            val chooserIntent = Intent.createChooser(
                shareIntent,
                "Game Result"
            )
            binding.root.context.startActivity(chooserIntent)
        }

    }
}



class ResultListAdapter(private val results: List<String>): RecyclerView.Adapter<ResultHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ResultHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ListGameResultBinding.inflate(inflater, parent, false)
        return ResultHolder(binding)
    }


    override fun onBindViewHolder(holder: ResultHolder, position: Int) {
        holder.bind(results[position])
    }

    override fun getItemCount(): Int {
        return results.size
    }
}