package com.example.bookofrecipes.rcAdapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.bookofrecipes.R
import com.example.bookofrecipes.dataBase.Ingredient
import com.example.bookofrecipes.databinding.SearchIngredientItemBinding

class SearchIngredientRVAdapter(private val listener: Listener, private var dataset: MutableList<Ingredient>):
    RecyclerView.Adapter<SearchIngredientRVAdapter.ViewHolder>() {

    class ViewHolder (view: View): RecyclerView.ViewHolder(view){
        val binding = SearchIngredientItemBinding.bind(view)
        fun bind (ingredient: Ingredient, listener: Listener){
            binding.textView8.text = ingredient.name
            itemView.setOnClickListener {
                listener.onClick(ingredient)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.search_ingredient_item,parent,false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return dataset.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(dataset[position], listener)
    }

    fun clearSearch(newList: MutableList<Ingredient>){
        dataset = newList
        notifyDataSetChanged()
    }

    interface Listener{
        fun onClick(ingredient: Ingredient)
    }

}