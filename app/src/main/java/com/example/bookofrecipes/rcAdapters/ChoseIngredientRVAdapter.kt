package com.example.bookofrecipes.rcAdapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.bookofrecipes.dataClasses.IngredientCount
import com.example.bookofrecipes.R
import com.example.bookofrecipes.databinding.IngredientCountItemBinding

class ChoseIngredientRVAdapter(private val dataSet: MutableList<IngredientCount>):
    RecyclerView.Adapter<ChoseIngredientRVAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view){
        val binding = IngredientCountItemBinding.bind(view)
        fun bind(ingredientCount: IngredientCount){
            binding.textView9.text = ingredientCount.name
            binding.textView10.text = ingredientCount.many
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).
        inflate(R.layout.ingredient_count_item ,parent,false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(dataSet[position])
    }

    override fun getItemCount(): Int {
        return dataSet.size
    }

}