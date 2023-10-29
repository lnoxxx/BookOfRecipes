package com.example.bookofrecipes.dataBase

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface IngredientDao {
    @Insert
    suspend fun insert(ingredient: Ingredient)

    @Query("SELECT * FROM ingredients")
    suspend fun getAllIngredients(): Array<Ingredient>

    @Query("SELECT * FROM ingredients WHERE name LIKE :searchQuery")
    fun searchEntities(searchQuery: String): Array<Ingredient>

    @Delete
    fun deleteIngredient(ingredient: Ingredient)
}



@Dao
interface CategoryDao {
    @Insert
    suspend fun insert(category: Category)

    @Query("SELECT * FROM categories")
    suspend fun getAllCategories(): Array<Category>

    @Delete
    fun deleteCategory(category: Category)
}

@Dao
interface RecipesDao {
    @Insert
    suspend fun insert(recipes: Recipe): Long

    @Query("SELECT * FROM recipes")
    suspend fun getAllRecipes(): Array<Recipe>
}

@Dao
interface RecipesAndIngredientsDao {
    @Insert
    suspend fun insert(recipesAndIngredients: RecipesAndIngredients)

    @Query("SELECT * FROM recipesAndIngredients")
    suspend fun getAllRecipesAndIngredients(): Array<RecipesAndIngredients>
}