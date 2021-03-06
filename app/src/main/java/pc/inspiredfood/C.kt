package pc.inspiredfood

object C {

    val recipeId = "recipeId"

    val timerRunning = "timerRunning"
    val timerStopped = "timerStopped"
    val recipeDataArray = "recipeDataArray"
    val recipeDataBundle = "recipeDataBundle"

    val dbName = "Recipes"

    object RecipesTable {
        val tableName = "RECIPES"
        val id = "_id"
        val recipeName = "recipe_name"
        val category = "category_id"
        val preparation = "preparation"
        val popularity = "popularity"
        val numberOfPeople = "number_of_people"
    }

    object CategoriesTable {
        val tableName = "CATEGORIES"
        val id = "_id"
        val categoryName = "category_name"
    }

    object TimersTable {
        val tableName = "TIMERS"
        val id = "_id"
        val timerName = "timer_name"
        val minutes = "minutes"
        val recipeId = "recipe_id"
    }

    object IngredientsTable {
        val tableName = "INGREDIENTS"
        val id = "_id"
        val ingredientName = "ingredient_name"
    }

    object UnitsTable {
        val tableName = "UNITS"
        val id = "_id"
        val unitName = "unit_name"
    }

    object IngredientsInRecipesTable {
        val tableName = "INGREDIENTSINRECIPES"
        val id = "_id"
        val recipeId = "_recipe_id"
        val ingredientId = "_ingredient_id"
        val unitId = "unit_id"
        val amount = "amount"
    }
}