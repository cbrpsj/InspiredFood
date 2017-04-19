package pc.inspiredfood

import kotlinx.android.synthetic.main.activity_recipe.*
import org.jetbrains.anko.db.rowParser
import org.jetbrains.anko.db.select
import org.jetbrains.anko.db.*
import pc.inspiredfood.App.Companion.categories
import pc.inspiredfood.App.Companion.ingredients
import pc.inspiredfood.App.Companion.units



object CRUD {

    /********* CREATE Operations: ********/


    // CREATE Single ingredient
    fun createIngredient(ingredient: String) {

        // When ingredient already exist, return
        if (ingredients.contains(ingredient))
            return

        // Else add ingredient to DB
        RecipeDBHelper.instance.use {

            insert( C.IngredientsTable.tableName,
                    C.IngredientsTable.ingredientName to ingredient)
        }

        // Add new ingredient to hashSet
        ingredients.add(ingredient)
    }


    // CREATE Single unit
    fun createUnit(unit: String) {

        // When unit already exist, return
        if (units.contains(unit))
            return

        // Else add unit to DB
        RecipeDBHelper.instance.use {

            insert( C.UnitsTable.tableName,
                    C.UnitsTable.unitName to unit)
        }

        // Add new unit to hashSet
        units.add(unit)
    }


    // CREATE Several ingredients in a specific recipe
    fun createIngredientsInRecipe(recipeId: Int, ingredients: List<Triple<Int, Double, Int>>) {

        RecipeDBHelper.instance.use {

            for(ingredient in ingredients) {

                insert( C.IngredientsInRecipesTable.tableName,
                        C.IngredientsInRecipesTable.recipeId to recipeId,
                        C.IngredientsInRecipesTable.ingredientId to ingredient.first,
                        C.IngredientsInRecipesTable.amount to ingredient.second,
                        C.IngredientsInRecipesTable.unitId to ingredient.third)
            }
        }
    }



    /********* READ Operations: *********/


    // READ single ingredient id
    fun getIngredientId(ingredient: String): Int {

        var ingredientId = 0

        RecipeDBHelper.instance.use {

            select( C.IngredientsTable.tableName, C.IngredientsTable.id)
                    .where("'$ingredient' = ${C.IngredientsTable.tableName}.${C.IngredientsTable.ingredientName}")
                    .parseSingle( rowParser { id: Int -> ingredientId = id })
        }

        return ingredientId
    }


    // READ Single unit id
    fun getUnitId(unit: String): Int {

        var unitId = 0

        RecipeDBHelper.instance.use {

            select( C.UnitsTable.tableName, C.UnitsTable.id)
                    .where("'$unit' = ${C.UnitsTable.tableName}.${C.UnitsTable.unitName}")
                    .parseSingle( rowParser { id: Int -> unitId = id })
        }

        return unitId
    }


    // READ Single recipe name
    fun getRecipeName(recipeId: Int): String {

        var recipeName = ""

        RecipeDBHelper.instance.use {

            select(C.RecipesTable.tableName, C.RecipesTable.recipeName)
                    .where( "$recipeId = ${C.RecipesTable.tableName}.${C.RecipesTable.id}")
                    .parseSingle( rowParser { name: String -> recipeName = name })
        }

        return recipeName
    }


    // READ Single preparation
    fun getPreparation(recipeId: Int): String {

        var preparation = ""

        RecipeDBHelper.instance.use {

            select(C.RecipesTable.tableName, C.RecipesTable.preparation)
                    .where( "$recipeId = ${C.RecipesTable.tableName}.${C.RecipesTable.id}")
                    .parseSingle( rowParser { prep: String -> preparation = prep })
        }

        return preparation
    }


    // READ Single number of people
    fun getNoOfPeople(recipeId: Int): Int {

        var noOfPeople = 0

        RecipeDBHelper.instance.use {

            select(C.RecipesTable.tableName, C.RecipesTable.numberOfPeople)
                    .where( "$recipeId = ${C.RecipesTable.tableName}.${C.RecipesTable.id}")
                    .parseSingle( rowParser { no: Int -> noOfPeople = no })
        }

        return noOfPeople
    }


    // READ List of recipes
    fun getRecipes(): List<Recipe> {

        var recipes: List<Recipe> = mutableListOf()

        RecipeDBHelper.instance.use {

            // Create a row parser (parse all fields, and return two of them as a Pair)
            val parser = rowParser {
                id: Int,
                name: String,
                category: Int,
                popularity: Int -> Recipe(id, name, category, popularity)
            }

            // Query db for all recipes, orderBy recipeName and parse the result to a list
            recipes = select(C.RecipesTable.tableName,
                    C.RecipesTable.id, C.RecipesTable.recipeName,
                    C.RecipesTable.category, C.RecipesTable.popularity)
                    .orderBy(C.RecipesTable.recipeName)
                    .parseList(parser)
        }

        return recipes
    }


    // READ List of categories
    fun getCategories() {

        RecipeDBHelper.instance.use {

            categories = select(
                    C.CategoriesTable.tableName,
                    C.CategoriesTable.categoryName)
                    .parseList(rowParser { category: String -> category })
        }
    }


    // READ List of ingredients
    fun getIngredients() {

        var tmpIngredients: List<String> = mutableListOf()

        RecipeDBHelper.instance.use {

            tmpIngredients = select(
                    C.IngredientsTable.tableName,
                    C.IngredientsTable.ingredientName)
                    .parseList(rowParser { ingredient: String -> ingredient })
        }

        ingredients = tmpIngredients.toHashSet()
    }


    // READ List of units
    fun getUnits() {

        var tmpUnits: List<String> = mutableListOf()

        RecipeDBHelper.instance.use {

            tmpUnits = select(
                    C.UnitsTable.tableName,
                    C.UnitsTable.unitName)
                    .parseList(rowParser { unit: String -> unit })
        }

        units = tmpUnits.toHashSet()
    }


    // READ List of all ingredients in a specific recipe
    fun getIngredientsInRecipe(recipeId: Int): List<Triple<String, Double, String>> {

        var ingredientsInRecipe: List<Triple<String, Double, String>> = mutableListOf()

        RecipeDBHelper.instance.use {

            // Query db for all ingredients in a recipe and parse result to list of Triples
            ingredientsInRecipe = select(
                    C.IngredientsInRecipesTable.tableName+","+C.IngredientsTable.tableName+","+ C.UnitsTable.tableName,
                    C.IngredientsTable.ingredientName, C.IngredientsInRecipesTable.amount, C.UnitsTable.unitName)
                    .where( "$recipeId = " +
                            "${C.IngredientsInRecipesTable.tableName}.${C.IngredientsInRecipesTable.recipeId} and " +
                            "${C.IngredientsInRecipesTable.tableName}.${C.IngredientsInRecipesTable.ingredientId} =" +
                            "${C.IngredientsTable.tableName}.${C.IngredientsTable.id} and " +
                            "${C.IngredientsInRecipesTable.tableName}.${C.IngredientsInRecipesTable.unitId} =" +
                            "${C.UnitsTable.tableName}.${C.UnitsTable.id}")
                    .parseList(rowParser {
                        ingredientName: String, amount: Double, unit: String ->
                        Triple(ingredientName, amount, unit)
                    })
        }

        return ingredientsInRecipe
    }



    /********* UPDATE Operations: *********/


    // UPDATE Single recipe popularity
    fun updatePopularity(recipeId: Int, popularity: Int) {

        RecipeDBHelper.instance.use {

            update(C.RecipesTable.tableName, C.RecipesTable.popularity to popularity)
                    .where("${C.RecipesTable.id} = $recipeId")
                    .exec()
        }
    }


    // UPDATE Single recipe name
    fun updateRecipeName(recipeId: Int, recipeName: String) {

        RecipeDBHelper.instance.use {

            update( C.RecipesTable.tableName, C.RecipesTable.recipeName to recipeName)
                    .where("$recipeId = ${C.RecipesTable.tableName}.${C.RecipesTable.id}")
                    .exec()
        }
    }


    // UPDATE Single recipe preparation
    fun updatePreparation(recipeId: Int, preparation: String) {

        RecipeDBHelper.instance.use {

            update( C.RecipesTable.tableName, C.RecipesTable.preparation to preparation)
                    .where("$recipeId = ${C.RecipesTable.tableName}.${C.RecipesTable.id}")
                    .exec()
        }
    }



    /******** DELETE Operations: *********/


    // DELETE all ingredients in a specific recipe
    fun deleteIngredientsInRecipe(recipeId: Int) {

        RecipeDBHelper.instance.use {

            delete( C.IngredientsInRecipesTable.tableName,
                    "$recipeId = ${C.IngredientsInRecipesTable.tableName}.${C.IngredientsInRecipesTable.recipeId}")
        }
    }
}