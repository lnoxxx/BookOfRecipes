package com.example.bookofrecipes.rcAdapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.bookofrecipes.R
import com.example.bookofrecipes.dataBase.Ingredient
import com.example.bookofrecipes.databinding.IngredientItemBinding

class IngredientRcAdapter(private val dataSet: MutableList<Ingredient>):
    RecyclerView.Adapter<IngredientRcAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view){
        val binding = IngredientItemBinding.bind(view)
        fun bind(ingredient: Ingredient){
            binding.textView5.text = ingredient.name
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).
        inflate(R.layout.ingredient_item,parent,false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(dataSet[position])
    }


    override fun getItemCount(): Int {
        return dataSet.size
    }

    fun deleteIngredient(ingredient: Ingredient){
        dataSet.remove(ingredient)
        notifyDataSetChanged()
    }

    fun update(){
        notifyDataSetChanged()
    }

    fun addIngredient(addIngredient: Ingredient){
        dataSet.add(addIngredient)
        notifyDataSetChanged()
    }

}