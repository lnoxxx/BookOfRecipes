package com.example.bookofrecipes.rcAdapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.bookofrecipes.IngredientCount
import com.example.bookofrecipes.R
import com.example.bookofrecipes.dataBase.Category
import com.example.bookofrecipes.databinding.CategoryItemBinding
import com.example.bookofrecipes.databinding.IngredientCountItemBinding

class ChoseIngradientRVadapter(private val dataSet: ArrayList<IngredientCount>):
    RecyclerView.Adapter<ChoseIngradientRVadapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view){
        val binding = IngredientCountItemBinding.bind(view)
        fun bind(ingredientCount: IngredientCount){
            binding.textView9.text = ingredientCount.name
            binding.textView10.text = ingredientCount.many
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ChoseIngradientRVadapter.ViewHolder {
        val view = LayoutInflater.from(parent.context).
        inflate(R.layout.ingredient_count_item ,parent,false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ChoseIngradientRVadapter.ViewHolder, position: Int) {
        holder.bind(dataSet[position])
    }

    override fun getItemCount(): Int {
        return dataSet.size
    }

}