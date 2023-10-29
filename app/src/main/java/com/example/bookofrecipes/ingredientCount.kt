package com.example.bookofrecipes

import java.io.Serializable

data class IngredientCount(val id: Int,
                           val many: String?,
                           val name: String?
) : Serializable
