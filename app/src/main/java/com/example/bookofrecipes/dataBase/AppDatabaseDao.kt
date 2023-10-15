package com.example.bookofrecipes.dataBase

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface IngredientDao {
    @Insert
    suspend fun insert(ingredient: Ingredient)

    @Query("SELECT * FROM ingredients")
    suspend fun getAllIngredients(): List<Ingredient>
}

@Dao
interface RecipesDao {
    @Insert
    suspend fun insert(recipes: Recipe)

    @Query("SELECT * FROM recipes")
    suspend fun getAllRecipes(): List<Recipe>
}

@Dao
interface RecipesAndIngredientsDao {
    @Insert
    suspend fun insert(recipesAndIngredients: RecipesAndIngredients)

    @Query("SELECT * FROM recipesAndIngredients")
    suspend fun getAllRecipesAndIngredients(): List<RecipesAndIngredients>
}