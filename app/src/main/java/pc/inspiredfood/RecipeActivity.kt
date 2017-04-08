package pc.inspiredfood

import android.app.Activity
import android.os.Bundle
import android.view.Gravity
import android.widget.TableRow
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_recipe.*
import org.jetbrains.anko.db.MapRowParser
import org.jetbrains.anko.db.RowParser
import org.jetbrains.anko.db.rowParser
import org.jetbrains.anko.db.select

class RecipeActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recipe)
    }


    override fun onResume() {

        super.onResume()
        val id = intent.getIntExtra(C.recipeId, -1)
        getRecipeDetails(id)
        getIngredientsForRecipe(id)
    }


    fun getRecipeDetails(id: Int) {

        var recipeName: String
        var category: String
        var preparation: String
        var popularity: Int
        var numberOfPeople: Int

        RecipeDBHelper.instance.use {

            // Query db for all recipes, orderBy recipeName and parse result to a list
            val recipe = select(
                    C.RecipesTable.tableName+","+C.CategoriesTable.tableName,
                    C.RecipesTable.recipeName, C.CategoriesTable.categoryName, C.RecipesTable.preparation, C.RecipesTable.numberOfPeople)
                    .where("$id = " +
                            "${C.RecipesTable.tableName}.${C.RecipesTable.id} and " +
                            "${C.RecipesTable.tableName}.${C.RecipesTable.category} = " +
                            "${C.CategoriesTable.tableName}.${C.CategoriesTable.id}")
                    .parseSingle(rowParser {
                        recipeName: String, categoryName: String, preparation: String, numberOfPeople: Int ->

                        var recipeInfo = "${translateCategory(categoryName)} ${getString(R.string.recipe_info_for)} $numberOfPeople"
                        if (numberOfPeople > 1) recipeInfo += " ${getString(R.string.recipe_info_persons)}"
                        else recipeInfo += " ${getString(R.string.recipe_info_person)}"

                        recipe_name.text = recipeName
                        recipe_info.text = recipeInfo
                        recipe_preparation.text = preparation
                    })
        }
    }


    fun translateCategory(categoryName: String): String =
            when(categoryName){
                "Starter" -> getString(R.string.starter)
                "Main"    -> getString(R.string.main)
                else      -> getString(R.string.dessert)
            }


    // Get ingredients for a specific recipe
    fun getIngredientsForRecipe(id: Int) {

        var ingredientsInRecipe = listOf<Triple<String, Double, String>>()

        RecipeDBHelper.instance.use {

            // Query db for all ingredients in a recipe and parse result to list of Triples
            ingredientsInRecipe = select(
                    C.IngredientsInRecipesTable.tableName+","+C.IngredientsTable.tableName+","+
                            C.UnitsTable.tableName, C.IngredientsTable.ingredientName, C.IngredientsInRecipesTable.amount, C.UnitsTable.unitName)
                    .where("$id = " +
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

        createTableRows(ingredientsInRecipe)
    }


    // Create table rows and insert data in rows
    fun createTableRows(ingredientsInRecipe: List<Triple<String, Double, String>>) {

        // Create a table row foreach element in the list and display ingredient info
        for(ingredientLine in ingredientsInRecipe) {

            // Create table row
            val tableRow = TableRow(this)
            val lp = TableRow.LayoutParams()
            lp.weight = 8f

            // Create text views for table row
            val textViewIngredient = TextView(this)
            val textViewAmount = TextView(this)
            val textViewUnit = TextView(this)

            // Insert text in text views
            textViewIngredient.text = ingredientLine.first
            textViewAmount.text = ingredientLine.second.toString()
            textViewUnit.text =ingredientLine.third

            // Set padding, text alignment and weight for text views
            textViewIngredient.layoutParams = lp
            textViewIngredient.setPadding(0, 0, 40, 0)
            textViewAmount.gravity = Gravity.END
            textViewUnit.setPadding(50, 0, 10, 0)

            // Add text views to table row, and add table row to table layout
            tableRow.addView(textViewIngredient)
            tableRow.addView(textViewAmount)
            tableRow.addView(textViewUnit)
            ingredients_table.addView(tableRow)
        }
    }
}
