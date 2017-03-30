package pc.inspiredfood

import android.app.Activity
import android.app.ListActivity
import android.os.Bundle
import android.view.View
import android.widget.ListView
import kotlinx.android.synthetic.main.activity_recipe_list.*
import org.jetbrains.anko.db.*
import org.jetbrains.anko.toast


class RecipeListActivity : ListActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recipe_list)

        mainButtons.check(all.id)
    }


    override fun onResume() {

        super.onResume()

        RecipeDBHelper.instance.use {

            //val cursor = rawQuery("select * from ${C.RecipesTable.tableName}", null)
            //listAdapter = RecipeCursorAdapter(this@RecipeListActivity, cursor, 0)

            // Create a row parser (parse all fields, and return two of them as a Pair)
            val parser = rowParser {

                id: Int,
                recipeName: String,
                category: Int,
                instr: String,
                popul: Int,
                noOfPeople: Int -> Pair(id, recipeName)
            }

            // Query db for all recipes, orderBy recipeName and parse the result to a list
            val recipes = select(C.RecipesTable.tableName)
                    .orderBy(C.RecipesTable.recipeName)
                    .parseList(parser)

            listAdapter = RecipeAdapter(this@RecipeListActivity, recipes)
        }
    }

    override fun onListItemClick(listView: ListView?, view: View, position: Int, id: Long) {

        val recipeId = view.tag

        if (recipeId is Int)
            toast("RecipeID: $recipeId")
    }

    fun mainButtonClicked(view: View) {

        var str = ""

        when(view.id) {
            starters.id -> str = "Starters"
            mains.id -> str = "Mains"
            desserts.id -> str = "Desserts"
            favourites.id -> str = "Favourites"
            all.id -> str = "All"
        }

        toast(str)
    }
}
