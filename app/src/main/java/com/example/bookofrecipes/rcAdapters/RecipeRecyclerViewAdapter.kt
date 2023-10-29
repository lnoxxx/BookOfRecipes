package com.example.bookofrecipes.rcAdapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.bookofrecipes.R
import com.example.bookofrecipes.dataBase.Recipe
import com.example.bookofrecipes.databinding.RecipeItemBinding

class RecipeRecyclerViewAdapter(val listener: Listener,private val recipeList: Array<Recipe>):
    RecyclerView.Adapter<RecipeRecyclerViewAdapter.ViewHolder>() {

    class ViewHolder(view: View): RecyclerView.ViewHolder(view){
        val binding = RecipeItemBinding.bind(view)
        fun bind(recipe: Recipe,listener: Listener){
            binding.textView11.text = recipe.name
            binding.recipeCardView.setOnClickListener{
                listener.onClick(recipe)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recipe_item,parent,false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return recipeList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(recipeList[position],listener)
    }

    interface Listener{
        fun onClick(recipe: Recipe)
    }
}