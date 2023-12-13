package com.example.bookofrecipes.rcAdapters

import android.animation.LayoutTransition
import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.RecyclerView
import com.example.bookofrecipes.R
import com.example.bookofrecipes.dataBase.Recipe
import com.example.bookofrecipes.databinding.RecipeItemBinding
import com.squareup.picasso.Picasso
import java.io.File

class RecipeRecyclerViewAdapter(private val listener: Listener,
                                private var recipeList: MutableList<Recipe>,
                                private val menuOpen: Boolean = true,
                                val context: Context):
    RecyclerView.Adapter<RecipeRecyclerViewAdapter.ViewHolder>() {


    inner class ViewHolder(view: View): RecyclerView.ViewHolder(view){
        val binding = RecipeItemBinding.bind(view)
        fun bind(recipe: Recipe,listener: Listener,menuOpen: Boolean, position: Int){
            binding.recipeMenuLinearLayout.visibility = View.GONE
            binding.textView11.text = recipe.name
            if(recipe.photoId != null){
                binding.recipeImageInList.visibility = View.VISIBLE
                val uri = getFileUri(context,recipe.photoId)
                Picasso.get().load(uri).into(binding.recipeImageInList)
            }else{
                binding.recipeImageInList.visibility = View.GONE
            }
            if (menuOpen){
                if (recipe.isFavorite){
                    changeFavVisibility(true)
                } else{
                    changeFavVisibility(false)
                }
                binding.unfavImage.setOnClickListener {
                    listener.onMakeFavorite(recipe)
                    changeFavVisibility(true)
                }
                binding.favImage.setOnClickListener {
                    listener.onDeleteInFavorite(recipe)
                    changeFavVisibility(false)
                }
                binding.recipeCardView.setOnLongClickListener {
                    binding.recipeMenuLinearLayout.visibility = View.VISIBLE
                    binding.deleteButton.setOnClickListener {
                        listener.onDelete(recipe)
                    }
                    return@setOnLongClickListener true
                }
            }
            binding.recipeCardView.setOnClickListener{
                listener.onClick(recipe)
            }
        }
        private fun changeFavVisibility(favorite: Boolean){
            if (favorite){
                binding.favImage.visibility = View.VISIBLE
                binding.unfavImage.visibility = View.GONE
            } else{
                binding.favImage.visibility = View.GONE
                binding.unfavImage.visibility = View.VISIBLE
            }
        }
        private fun getFileUri(context: Context, filePath: String): Uri {
            val file = File(filePath)
            return FileProvider.getUriForFile(
                context,
                context.packageName + ".provider",
                file
            )
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