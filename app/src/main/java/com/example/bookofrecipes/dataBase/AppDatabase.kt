package com.example.bookofrecipes.dataBase

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities =
[Ingredient::class, Recipe::class, RecipesAndIngredients::class, Category::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun ingredientDao(): IngredientDao
    abstract fun recipeDao(): RecipesDao
    abstract fun recipesAndIngredientsDao(): RecipesAndIngredientsDao
    abstract fun CategoryDao(): CategoryDao
}
