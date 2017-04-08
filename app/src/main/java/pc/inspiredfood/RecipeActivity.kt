package pc.inspiredfood

import android.app.Activity
import android.os.Bundle
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
        fetchRecipe(id)
        createTableRows(id)
    }

    fun fetchRecipe(id: Int) {

        var recipeName: String
        var category: String
        var instructions: String
        var popularity: Int
        var numberOfPeople: Int

        RecipeDBHelper.instance.use {

            // Query db for all recipes, orderBy recipeName and parse the result to a list
            val recipe = select(C.RecipesTable.tableName+","+C.CategoriesTable.tableName,
                    C.RecipesTable.recipeName, C.CategoriesTable.categoryName, C.RecipesTable.preparation, C.RecipesTable.numberOfPeople)
                    .where("$id = ${C.RecipesTable.tableName}.${C.RecipesTable.id} and " +
                            "${C.RecipesTable.tableName}.${C.RecipesTable.category} = ${C.CategoriesTable.tableName}.${C.CategoriesTable.id}")
                    .parseSingle(rowParser { recipeName: String, categoryName: String, instructions: String, numberOfPeople: Int ->
                        recipe_name.setText(recipeName)

                        var recipeInfo = "${translateCategory(categoryName)} ${getString(R.string.recipe_info_for)} $numberOfPeople"

                        if (numberOfPeople > 1)
                            recipe_info.setText("$recipeInfo ${getString(R.string.recipe_info_persons)}")
                        else
                            recipe_info.setText("$recipeInfo ${getString(R.string.recipe_info_person)}")

                        recipe_instructions.setText(instructions)
                    })
        }
    }

    fun translateCategory(categoryName: String): String =
            when(categoryName){
                "Starter" -> getString(R.string.starter)
                "Main"    -> getString(R.string.main)
                else      -> getString(R.string.dessert)
            }

    fun createTableRows(id: Int) {

        RecipeDBHelper.instance.use {

            val ingredientsInRecipe = select(C.IngredientsInRecipesTable.tableName+","+C.IngredientsTable.tableName+","+
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

            for(ingredientLine in ingredientsInRecipe) {
                val tableRow = TableRow(this@RecipeActivity)
                val textViewIngredient = TextView(this@RecipeActivity)
                val textViewAmount = TextView(this@RecipeActivity)
                val textViewUnit = TextView(this@RecipeActivity)

                textViewIngredient.setText(ingredientLine.first)
                textViewAmount.setText(ingredientLine.second.toString())
                textViewUnit.setText(ingredientLine.third)

                tableRow.addView(textViewIngredient)
                tableRow.addView(textViewAmount)
                tableRow.addView(textViewUnit)
                ingredients_table.addView(tableRow)
            }
        }




//        textViewIngredient.setPadding(20, 10, 20, 10)




    }
}
