package com.example.bookofrecipes.rcAdapters

import android.animation.LayoutTransition
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.bookofrecipes.R
import com.example.bookofrecipes.dataBase.Recipe
import com.example.bookofrecipes.databinding.RecipeItemBinding

class RecipeRecyclerViewAdapter(private val listener: Listener,
                                private var recipeList: MutableList<Recipe>,
                                private val menuOpen: Boolean = true):
    RecyclerView.Adapter<RecipeRecyclerViewAdapter.ViewHolder>() {

    class ViewHolder(view: View): RecyclerView.ViewHolder(view){
        val binding = RecipeItemBinding.bind(view)
        fun bind(recipe: Recipe,listener: Listener,menuOpen: Boolean, position: Int){
            binding.recipeCardView.layoutTransition.enableTransitionType(LayoutTransition.CHANGING)
            if (recipe.isFavorite){
                binding.unsetFavorite.visibility = View.VISIBLE
                binding.setFavorite.visibility = View.GONE
                binding.favIndCardVIew.visibility = View.VISIBLE
            } else{
                binding.unsetFavorite.visibility = View.GONE
                binding.setFavorite.visibility = View.VISIBLE
                binding.favIndCardVIew.visibility = View.GONE
            }
            binding.textView11.text = recipe.name
            binding.recipeSettingsMenu.visibility = View.GONE
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
            binding.helpingTv.visibility = View.GONE
            if (menuOpen){
                if (position == 0){
                    binding.helpingTv.visibility = View.VISIBLE
                } else {
                    binding.helpingTv.visibility = View.GONE
                }
                binding.recipeCardView.setOnLongClickListener {
                    binding.helpingTv.visibility = View.GONE
                    binding.recipeSettingsMenu.visibility = View.VISIBLE
                    return@setOnLongClickListener true
                }
            }
            binding.setFavorite.setOnClickListener {
                binding.recipeSettingsMenu.visibility = View.GONE
                binding.unsetFavorite.visibility = View.VISIBLE
                binding.setFavorite.visibility = View.GONE
                binding.favIndCardVIew.visibility = View.VISIBLE
                listener.onMakeFavorite(recipe)
            }
            binding.unsetFavorite.setOnClickListener {
                binding.recipeSettingsMenu.visibility = View.GONE
                binding.unsetFavorite.visibility = View.GONE
                binding.setFavorite.visibility = View.VISIBLE
                binding.favIndCardVIew.visibility = View.GONE
                listener.onDeleteInFavorite(recipe)
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
        holder.bind(recipeList[position],listener, menuOpen, position)
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
        fun onMakeFavorite(recipe: Recipe)
        fun onDeleteInFavorite(recipe: Recipe)
    }
}