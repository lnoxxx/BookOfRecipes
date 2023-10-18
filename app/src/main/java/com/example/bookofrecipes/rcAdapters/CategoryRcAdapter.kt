package com.example.bookofrecipes.rcAdapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.bookofrecipes.R
import com.example.bookofrecipes.dataBase.Category
import com.example.bookofrecipes.databinding.CategoryItemBinding

class CategoryRcAdapter(private val dataSet: Array<Category>):
    RecyclerView.Adapter<CategoryRcAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view){
        val binding = CategoryItemBinding.bind(view)
        fun bind(category: Category){
            binding.textView3.text = category.name
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CategoryRcAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context).
        inflate(R.layout.category_item,parent,false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: CategoryRcAdapter.ViewHolder, position: Int) {
        holder.bind(dataSet[position])
    }

    override fun getItemCount(): Int {
        return dataSet.size
    }

}