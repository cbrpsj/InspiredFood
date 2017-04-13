package pc.inspiredfood

import android.app.Activity
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.util.TypedValue
import android.view.*
import android.widget.EditText
import android.widget.TableRow
import kotlinx.android.synthetic.main.activity_recipe.*
import org.jetbrains.anko.*
import org.jetbrains.anko.db.rowParser
import org.jetbrains.anko.db.select
import org.jetbrains.anko.db.*
import pc.inspiredfood.App.Companion.updateRecipeList

class RecipeActivity : Activity() {

    var id = 0
    var editModeEnabled = false


    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recipe)

        id = intent.getIntExtra(C.recipeId, -1)
        getRecipeDetails(id)
        getIngredientsForRecipe(id)

        makeViewsUneditable()
        button_edit_save.onClick { toggleEditMode() }
    }


    override fun onResume() {

        super.onResume()
    }


    // Get details about a specific recipe
    fun getRecipeDetails(id: Int) {

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

                        recipe_name.setText(recipeName)
                        recipe_info.text = recipeInfo
                        recipe_preparation.setText(preparation)
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

        // Create a table row for each element in the list and display ingredient info
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

            // Create EditText view for table row
            val editTextViewIngredient = EditText(this)
            val editTextViewAmount = EditText(this)
            val editTextViewUnit = EditText(this)

            // Remove underlining in EditText views
            editTextViewIngredient.background = null
            editTextViewAmount.background = null
            editTextViewUnit.background = null

            // Set padding for EditText views
            editTextViewIngredient.setPadding(0, 0, 0, 0)
            editTextViewAmount.setPadding(0, 0, 0, 0)
            editTextViewUnit.setPadding(0, 0, 0, 0)

            editTextViewIngredient.textColor = R.color.cellTextColor
            editTextViewAmount.textColor = R.color.cellTextColor
            editTextViewUnit.textColor = R.color.cellTextColor

            // Insert text in EditText views
            editTextViewIngredient.setText(ingredientLine.first)
            editTextViewAmount.setText(ingredientLine.second.toString())
            editTextViewUnit.setText(ingredientLine.third)

            // Set text size in EditText views
            editTextViewIngredient.setTextSize(textTypeValue, textSize)
            editTextViewAmount.setTextSize(textTypeValue, textSize)
            editTextViewUnit.setTextSize(textTypeValue, textSize)

            // Set padding (adds padding between the 3 EditText views)
            editTextViewAmount.setPadding(dpToPixel(15f), 0, dpToPixel(10f), 0)

            // Apply weight to ingredient EditText view
            editTextViewIngredient.layoutParams = layoutParams

            // Align text in amount EditText view to the right
            editTextViewAmount.gravity = Gravity.END

            // Set EditText views to uneditable state
//            makeViewUneditable(editTextViewIngredient)
//            makeViewUneditable(editTextViewAmount)
//            makeViewUneditable(editTextViewUnit)

            // Add EditText views to table row, and add table row to table layout
            tableRow.addView(editTextViewIngredient)
            tableRow.addView(editTextViewAmount)
            tableRow.addView(editTextViewUnit)
            ingredients_table.addView(tableRow)
        }
    }



    fun toggleEditMode() {

        if(!editModeEnabled) enterEditMode()
        else exitEditModeAndSave()
    }

    // Enables recipe editing
    fun enterEditMode() {

        editModeEnabled = true
        button_edit_save.setText("${getString(R.string.save)}")
        recipe_detail.backgroundColor = resources.getColor(R.color.backgroundEditMode, null)
        makeViewsEditable()
    }

    fun exitEditModeAndSave() {

        editModeEnabled = false
        button_edit_save.setText("${getString(R.string.edit)}")
        recipe_detail.backgroundColor = resources.getColor(R.color.backgroundWhite, null)
        makeViewsUneditable()
        inputMethodManager.hideSoftInputFromWindow(recipe_detail.windowToken, 0)
        saveRecipe()
    }

    fun makeViewsEditable() {

        makeViewEditable(recipe_name)
        makeViewEditable(recipe_preparation)

        val tableRows = ingredients_table.childrenSequence()

        for(tableRow in tableRows) {
            makeViewEditable((tableRow as TableRow).getChildAt(0) as EditText)
            makeViewEditable(tableRow.getChildAt(1) as EditText)
            makeViewEditable(tableRow.getChildAt(2) as EditText)
        }
    }

    fun makeViewsUneditable() {

        makeViewUneditable(recipe_name)
        makeViewUneditable(recipe_preparation)

        val tableRows = ingredients_table.childrenSequence()

        for(tableRow in tableRows) {
            makeViewUneditable((tableRow as TableRow).getChildAt(0) as EditText)
            makeViewUneditable(tableRow.getChildAt(1) as EditText)
            makeViewUneditable(tableRow.getChildAt(2) as EditText)
        }
    }

    fun makeViewEditable(view: EditText) {

        view.isCursorVisible = true
        view.isClickable = true
        view.isFocusable = true
        view.isFocusableInTouchMode = true
    }

    fun makeViewUneditable(view: EditText) {

        view.isCursorVisible = false
        view.isClickable = false
        view.isFocusable = false
        view.isFocusableInTouchMode = false
    }

    fun saveRecipe() {

        RecipeDBHelper.instance.use {

            update( C.RecipesTable.tableName, 
                    C.RecipesTable.recipeName to recipe_name.text.toString(),
                    C.RecipesTable.preparation to recipe_preparation.text.toString())
                    .where("$id = ${C.RecipesTable.tableName}.${C.RecipesTable.id}")
                    .exec()
        }

        updateRecipeList = true
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
