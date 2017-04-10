package pc.inspiredfood

import android.app.Activity
import android.os.Bundle
import android.util.TypedValue
import android.view.Gravity
import android.widget.TableRow
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_recipe.*
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


    // Get details about a specific recipe
    fun getRecipeDetails(id: Int) {

        var recipeName: String
        var category: String
        var preparation: String
        var popularity: Int
        var numberOfPeople: Int

        RecipeDBHelper.instance.use {

            // Query db for all recipes, orderBy recipeName and parse result to a list
            select( C.RecipesTable.tableName+","+C.CategoriesTable.tableName,
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


    // Get ingredients for a specific recipe
    fun getIngredientsForRecipe(id: Int) {

        var ingredientsInRecipe = listOf<Triple<String, Double, String>>()

        RecipeDBHelper.instance.use {

            // Query db for all ingredients in a recipe and parse result to list of Triples
            ingredientsInRecipe = select(
                    C.IngredientsInRecipesTable.tableName+","+C.IngredientsTable.tableName+","+ C.UnitsTable.tableName,
                    C.IngredientsTable.ingredientName, C.IngredientsInRecipesTable.amount, C.UnitsTable.unitName)
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
            val layoutParams = TableRow.LayoutParams()
            val textTypeValue = TypedValue.COMPLEX_UNIT_SP
            val textSize = 17f

            // Set padding for table row
            tableRow.setPadding(dpToPixel(5f), dpToPixel(5f), dpToPixel(7f), dpToPixel(5f))

            // Set table row border using drawable shape under resource
            tableRow.background = getDrawable(R.drawable.cell)

            // Set weight (used in ingredient text view). Ensure space between ingredient and amount
            layoutParams.weight = 1f

            // Create text views for table row
            val textViewIngredient = TextView(this)
            val textViewAmount = TextView(this)
            val textViewUnit = TextView(this)

            // Insert text in text views
            textViewIngredient.text = ingredientLine.first
            textViewAmount.text = ingredientLine.second.toString()
            textViewUnit.text =ingredientLine.third

            // Set text size in text views
            textViewIngredient.setTextSize(textTypeValue, textSize)
            textViewAmount.setTextSize(textTypeValue, textSize)
            textViewUnit.setTextSize(textTypeValue, textSize)

            // Set padding (adds padding between the 3 text views)
            textViewAmount.setPadding(dpToPixel(15f), 0, dpToPixel(10f), 0)

            // Apply weight to ingredient text view
            textViewIngredient.layoutParams = layoutParams

            // Align text in amount text view to the right
            textViewAmount.gravity = Gravity.END

            // Add text views to table row, and add table row to table layout
            tableRow.addView(textViewIngredient)
            tableRow.addView(textViewAmount)
            tableRow.addView(textViewUnit)
            ingredients_table.addView(tableRow)
        }
    }


    // Convert DP to Pixel
    fun dpToPixel(dp: Float) = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, resources.displayMetrics).toInt()


    // Get category in the correct language
    fun translateCategory(categoryName: String): String =
            when(categoryName){
                "Starter" -> getString(R.string.starter)
                "Main"    -> getString(R.string.main)
                else      -> getString(R.string.dessert)
            }
}
