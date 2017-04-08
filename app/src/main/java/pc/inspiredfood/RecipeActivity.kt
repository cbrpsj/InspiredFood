package pc.inspiredfood

import android.app.Activity
import android.os.Bundle
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
        fetchRecipe(id)
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
                    C.RecipesTable.recipeName, C.CategoriesTable.categoryName, C.RecipesTable.instructions, C.RecipesTable.numberOfPeople)
                    .where("$id = ${C.RecipesTable.tableName}.${C.RecipesTable.id} and " +
                            "${C.RecipesTable.tableName}.${C.RecipesTable.category} = ${C.CategoriesTable.tableName}.${C.CategoriesTable.id}")
                    .parseSingle(rowParser { recipeName: String, categoryName: String, instructions: String, numberOfPeople: Int ->
                        recipe_name.setText(recipeName)

                        var recipeInfo = "$categoryName ${getString(R.string.recipe_info_for)} $numberOfPeople"

                        if (numberOfPeople > 1)
                            recipe_info.setText("$recipeInfo ${getString(R.string.recipe_info_persons)}")
                        else
                            recipe_info.setText("$recipeInfo ${getString(R.string.recipe_info_person)}")


                        recipe_instructions.setText(instructions)
                    })
        }
    }
}
