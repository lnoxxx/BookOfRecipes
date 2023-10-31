package com.example.bookofrecipes.rcAdapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.bookofrecipes.R
import com.example.bookofrecipes.dataBase.Category
import com.example.bookofrecipes.databinding.ChoseCategoryItemBinding

class ChoseCategoryRecyclerViewAdapter (private val listener: Listener,
                                        private val categoryList : MutableList<Category>):
    RecyclerView.Adapter<ChoseCategoryRecyclerViewAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view){
        val binding = ChoseCategoryItemBinding.bind(view)
        fun bind(category: Category, listener: Listener){
            binding.textView2.text = category.name
            itemView.setOnClickListener{
                listener.onClick(category)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).
        inflate(R.layout.chose_category_item,parent,false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(categoryList[position], listener)
    }

    override fun getItemCount(): Int {
        return categoryList.size
    }

    interface Listener{
        fun onClick(category: Category)
    }
}