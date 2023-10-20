package com.example.bookofrecipes.fragmenst

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.example.bookofrecipes.R
import com.example.bookofrecipes.dataBase.AppDatabase
import com.example.bookofrecipes.dataBase.Category
import com.example.bookofrecipes.databinding.FragmentAddBinding
import com.example.bookofrecipes.rcAdapters.CategoryRcAdapter
import com.example.bookofrecipes.rcAdapters.ChoseCategoryRecyclerViewAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AddFragment : Fragment(), ChoseCategoryRecyclerViewAdapter.Listener {

    private lateinit var bindingAdd: FragmentAddBinding

    private var chosenId: Int = 1

    lateinit var adapter:ChoseCategoryRecyclerViewAdapter

    var menuOpen = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        bindingAdd = FragmentAddBinding.inflate(inflater)

        val database = Room.databaseBuilder(
            requireContext(),
            AppDatabase::class.java,
            "my_database"
        ).build()

        bindingAdd.choseCategoryRV.layoutManager =
            LinearLayoutManager(context,LinearLayoutManager.HORIZONTAL,false)

        CoroutineScope(Dispatchers.IO).launch {
            val categoryList = database.CategoryDao().getAllCategories()
            withContext(Dispatchers.Main) {
                adapter = ChoseCategoryRecyclerViewAdapter(this@AddFragment,categoryList)
                bindingAdd.choseCategoryRV.adapter = adapter
            }
        }


        bindingAdd.ingredientCV.setOnClickListener{
            it.findNavController().navigate(R.id.choseIngredientFragment)
        }


        bindingAdd.categoryCardView.setOnClickListener {
            if (menuOpen){
                bindingAdd.choseCategoryRV.visibility = View.GONE
                menuOpen = false
            } else{
                bindingAdd.choseCategoryRV.visibility = View.VISIBLE
                menuOpen = true
            }
        }

        return bindingAdd.root
    }

    override fun onClick(category: Category) {
        chosenId = category.id.toInt()
        bindingAdd.chosenCategoryTV.text = category.name
        adapter.changeChose(category.id.toInt())
        bindingAdd.choseCategoryRV.visibility = View.GONE
        menuOpen = false

    }


}