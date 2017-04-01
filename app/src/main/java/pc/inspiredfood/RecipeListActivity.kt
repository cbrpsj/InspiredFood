package pc.inspiredfood

import android.app.ListActivity
import android.os.Bundle
import android.view.View
import android.widget.ListView
import kotlinx.android.synthetic.main.activity_recipe_list.*
import org.jetbrains.anko.db.*
import org.jetbrains.anko.toast


class RecipeListActivity : ListActivity() {

    var recipes: List<Recipe> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recipe_list)

        mainButtons.check(all.id)

        RecipeDBHelper.instance.use {

            // Create a row parser (parse all fields, and return two of them as a Pair)
            val parser = rowParser {

                id: Int,
                name: String,
                category: Int,
                instructions: String,
                popularity: Int,
                noOfPeople: Int -> Recipe(id, name, category, instructions, popularity, noOfPeople)
            }

            // Query db for all recipes, orderBy recipeName and parse the result to a list
            recipes = select(C.RecipesTable.tableName)
                    .orderBy(C.RecipesTable.recipeName)
                    .parseList(parser)

        }
    }


    override fun onResume() {

        super.onResume()
        filterRecipes()
    }


    override fun onListItemClick(listView: ListView?, view: View, position: Int, id: Long) {

        val recipeId = view.tag

        if (recipeId is Int)
            toast("RecipeID: $recipeId")
    }


    fun mainButtonClicked(view: View) {

        filterRecipes()
        listView.invalidateViews()
    }


    fun filterRecipes() {

        var buttonChecked = mainButtons.checkedRadioButtonId
        var tempList: List<Recipe>

        when(buttonChecked) {
            starters.id -> tempList = recipes.filter { it.category == 1 }
            mains.id -> tempList = recipes.filter { it.category == 2 }
            desserts.id -> tempList = recipes.filter { it.category == 3 }
            favourites.id -> tempList = recipes.sortedBy { it.popularity }
            else -> tempList = recipes.sortedBy { it.name }
        }

        listAdapter = RecipeAdapter(this@RecipeListActivity, tempList)
    }
}
