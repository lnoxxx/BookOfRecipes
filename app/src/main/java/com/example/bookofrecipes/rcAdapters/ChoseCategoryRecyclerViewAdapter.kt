package com.example.bookofrecipes.rcAdapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.bookofrecipes.R
import com.example.bookofrecipes.dataBase.Category
import com.example.bookofrecipes.databinding.CategoryItemBinding
import com.example.bookofrecipes.databinding.ChoseCategoryItemBinding
import java.security.AccessController.getContext

class ChoseCategoryRecyclerViewAdapter
    (val listener: Listener, private val dataSet : Array<Category>):
    RecyclerView.Adapter<ChoseCategoryRecyclerViewAdapter.ViewHolder>() {

    private var chosenId: Int = 1

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view){
        val binding = ChoseCategoryItemBinding.bind(view)
        fun bind(category: Category, chosenId: Int, listener: Listener){
            binding.textView2.text = category.name
            if (category.id.toInt() == chosenId){
                itemView.alpha = 1f
            }
            itemView.setOnClickListener{
                listener.onClick(category)
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ChoseCategoryRecyclerViewAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context).
        inflate(R.layout.chose_category_item,parent,false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ChoseCategoryRecyclerViewAdapter.ViewHolder, position: Int) {
        holder.bind(dataSet[position], chosenId, listener)
    }

    override fun getItemCount(): Int {
        return dataSet.size
    }


    public fun changeChose(id:Int){
        chosenId = id
        notifyDataSetChanged()
    }

    interface Listener{
        fun onClick(category: Category)
    }
}