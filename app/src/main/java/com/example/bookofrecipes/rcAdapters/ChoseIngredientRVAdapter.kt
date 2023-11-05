package com.example.bookofrecipes.rcAdapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.bookofrecipes.dataClasses.IngredientCount
import com.example.bookofrecipes.R
import com.example.bookofrecipes.databinding.IngredientCountItemBinding

class ChoseIngredientRVAdapter(private val listener: Listener,
                               private val dataSet: MutableList<IngredientCount>):
    RecyclerView.Adapter<ChoseIngredientRVAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view){
        val binding = IngredientCountItemBinding.bind(view)
        fun bind(ingredientCount: IngredientCount, listener: Listener){
            binding.textView9.text = ingredientCount.name
            binding.textView10.text = ingredientCount.many

            binding.clearCountButton.visibility = View.GONE
            binding.countCV.setOnLongClickListener {
                binding.clearCountButton.visibility = View.VISIBLE
                return@setOnLongClickListener true
            }
            binding.clearCountButton.setOnClickListener {
                listener.onDeleteChosenIngredient(ingredientCount)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).
        inflate(R.layout.ingredient_count_item ,parent,false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(dataSet[position],listener)
    }

    fun clearRecyclerView(){
        dataSet.clear()
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return dataSet.size
    }

    fun deleteCount(ingredientCount: IngredientCount){
        dataSet.remove(ingredientCount)
        notifyDataSetChanged()
    }

    interface Listener{
        fun onDeleteChosenIngredient(ingredientCount: IngredientCount)
    }
}