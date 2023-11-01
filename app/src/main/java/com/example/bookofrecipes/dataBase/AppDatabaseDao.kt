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
    suspend fun getAllIngredients(): MutableList<Ingredient>

    @Query("SELECT * FROM ingredients WHERE name LIKE :searchQuery")
    fun searchEntities(searchQuery: String): MutableList<Ingredient>

    @Query("SELECT * FROM ingredients WHERE id = :id")
    fun getIngredientById(id: Long?): Ingredient

    @Delete
    fun deleteIngredient(ingredient: Ingredient)
}

@Dao
interface CategoryDao {
    @Insert
    suspend fun insert(category: Category)

    @Query("SELECT * FROM categories")
    suspend fun getAllCategories(): MutableList<Category>

    @Query("SELECT * FROM categories WHERE id = :id")
    fun getCategoryById(id: Long?): Category?

    @Delete
    fun deleteCategory(category: Category)
}

@Dao
interface RecipesDao {
    @Insert
    suspend fun insert(recipes: Recipe): Long

    @Query("SELECT * FROM recipes")
    suspend fun getAllRecipes(): MutableList<Recipe>

    @Query("SELECT * FROM recipes WHERE type = :idType")
    fun getRecipeInCategory(idType: Long?): MutableList<Recipe>
}

@Dao
interface RecipesAndIngredientsDao {
    @Insert
    suspend fun insert(recipesAndIngredients: RecipesAndIngredients)

    @Query("SELECT * FROM recipesAndIngredients WHERE recipeId = :recipeId")
    fun getIngredientsForRecipe(recipeId: Long?): MutableList<RecipesAndIngredients>

    @Query("SELECT * FROM recipesAndIngredients WHERE ingredientId = :ingredient")
    fun getWhereIngredientId(ingredient: Long?): MutableList<RecipesAndIngredients>

    @Query("SELECT * FROM recipesAndIngredients")
    suspend fun getAllRecipesAndIngredients(): MutableList<RecipesAndIngredients>

    @Delete
    fun deleteRecipeAndIngredient(recipesAndIngredients: RecipesAndIngredients)
}