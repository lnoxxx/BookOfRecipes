package com.example.bookofrecipes.rcAdapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.bookofrecipes.R
import com.example.bookofrecipes.dataBase.Ingredient
import com.example.bookofrecipes.databinding.IngredientItemBinding

class IngredientRcAdapter(private val listener: Listener,
                          private var dataSet: MutableList<Ingredient>):
    RecyclerView.Adapter<IngredientRcAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view){
        val binding = IngredientItemBinding.bind(view)
        fun bind(ingredient: Ingredient, listener: Listener, position: Int){
            if (position == 0){
                binding.helpIngrTV.visibility = View.VISIBLE
            } else {
                binding.helpIngrTV.visibility = View.GONE
            }
            binding.textView5.text = ingredient.name
            binding.deleteIngredientButton.visibility = View.GONE
            binding.cvItemList.setOnLongClickListener {
                binding.deleteIngredientButton.visibility = View.VISIBLE
                return@setOnLongClickListener true
            }
            binding.deleteIngredientButton.setOnClickListener {
                listener.onDeleteIngredient(ingredient)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).
        inflate(R.layout.ingredient_item,parent,false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(dataSet[position], listener, position)
    }

    override fun getItemCount(): Int {
        return dataSet.size
    }

    fun deleteIngredient(ingredient: Ingredient){
        dataSet.remove(ingredient)
        notifyDataSetChanged()
    }

    fun clearSearch(newList: MutableList<Ingredient>){
        dataSet = newList
        notifyDataSetChanged()
    }

    fun addIngredient(addIngredient: Ingredient){
        dataSet.add(addIngredient)
        notifyDataSetChanged()
    }

    interface Listener{
        fun onDeleteIngredient(ingredient: Ingredient)
    }

}