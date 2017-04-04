package pc.inspiredfood

import android.app.ListActivity
import android.os.Bundle
import android.view.View
import android.widget.ListView
import kotlinx.android.synthetic.main.activity_recipe_list.*
import org.jetbrains.anko.db.*
import org.jetbrains.anko.intentFor
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
                popularity: Int -> Recipe(id, name, category, popularity)
            }

            // Query db for all recipes, orderBy recipeName and parse the result to a list
            recipes = select(C.RecipesTable.tableName, C.RecipesTable.id, C.RecipesTable.recipeName,
                    C.RecipesTable.category, C.RecipesTable.popularity)
                    .orderBy(C.RecipesTable.recipeName)
                    .parseList(parser)
        }
    }


    override fun onResume() {

        super.onResume()
        filterRecipes()
    }


    override fun onListItemClick(listView: ListView, view: View, position: Int, id: Long) {

        val id = view.tag

        toast("RecipeID: $id")

        recipes.find { it.id == id }?.updatePopularity()
        startActivity(intentFor<RecipeActivity>(C.recipeId to id))
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
            favourites.id -> tempList = recipes.sortedByDescending { it.popularity }
            else -> tempList = recipes.sortedBy { it.name }
        }

        listAdapter = RecipeAdapter(this@RecipeListActivity, tempList)
    }
}
