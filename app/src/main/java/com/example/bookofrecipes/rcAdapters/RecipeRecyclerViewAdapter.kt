package com.example.bookofrecipes.rcAdapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.bookofrecipes.R
import com.example.bookofrecipes.dataBase.Recipe
import com.example.bookofrecipes.databinding.RecipeItemBinding

class RecipeRecyclerViewAdapter(private val listener: Listener, private var recipeList: MutableList<Recipe>):
    RecyclerView.Adapter<RecipeRecyclerViewAdapter.ViewHolder>() {

    class ViewHolder(view: View): RecyclerView.ViewHolder(view){
        val binding = RecipeItemBinding.bind(view)
        fun bind(recipe: Recipe,listener: Listener){
            binding.textView11.text = recipe.name
            binding.deleteRecipeButton.visibility = View.GONE
            var recipeText = recipe.recipeText
            if (recipeText.length > 40){
                recipeText = recipeText.take(40) + "..."
            }
            binding.textView4.text = recipeText
            binding.recipeCardView.setOnClickListener{
                listener.onClick(recipe)
            }
            binding.deleteRecipeButton.setOnClickListener {
                listener.onDelete(recipe)
            }
            binding.recipeCardView.setOnLongClickListener {
                binding.deleteRecipeButton.visibility = View.VISIBLE
                return@setOnLongClickListener true
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

    fun clearSearch(newList: MutableList<Recipe>){
        recipeList = newList
        notifyDataSetChanged()
    }

    fun deleteRecipe(recipe: Recipe){
        recipeList.remove(recipe)
        notifyDataSetChanged()
    }

    interface Listener{
        fun onClick(recipe: Recipe)
        fun onDelete(recipe: Recipe)
    }
}