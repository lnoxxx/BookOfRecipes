package com.example.bookofrecipes.dataClasses

import java.io.Serializable

data class IngredientCount(val id: Long,
                           val many: String?,
                           val name: String?
) : Serializable
