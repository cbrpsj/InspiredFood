package pc.inspiredfood

import android.app.ListActivity
import android.os.Bundle
import android.view.ContextMenu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.AdapterView.AdapterContextMenuInfo
import android.widget.ListView
import kotlinx.android.synthetic.main.activity_recipe_list.*
import org.jetbrains.anko.contentView
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.toast
import pc.inspiredfood.App.Companion.updateRecipeList
import pc.inspiredfood.CRUD.getCategories
import pc.inspiredfood.CRUD.getIngredients
import pc.inspiredfood.CRUD.getRecipes
import pc.inspiredfood.CRUD.getUnits


class RecipeListActivity : ListActivity() {

    var recipes: List<Recipe> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recipe_list)

        registerForContextMenu(list)

        updateRecipeList = true
        mainButtons.check(all.id)


        getCategories()
    }


    override fun onResume() {

        super.onResume()

        if (updateRecipeList) {

            getIngredients()
            getUnits()

            recipes = getRecipes()
            updateRecipeList = false
        }

        filterRecipes()
    }


    override fun onListItemClick(listView: ListView, view: View, position: Int, id: Long) {

        val recipeId = view.tag

        recipes.find { it.id == recipeId }?.updatePopularity()
        startActivity(intentFor<RecipeActivity>(C.recipeId to recipeId))
    }


    override fun onCreateContextMenu(menu: ContextMenu?, v: View?, menuInfo: ContextMenu.ContextMenuInfo?) {
        super.onCreateContextMenu(menu, v, menuInfo)

        val inflate = menuInflater
        inflate.inflate(R.menu.context_menu, menu)
    }


    override fun onContextItemSelected(item: MenuItem?): Boolean {

        val info = item?.menuInfo as AdapterContextMenuInfo

        val recipe = list.getItemAtPosition(info.position) as Recipe
        toast(recipe.name)

        return true
    }


    fun mainButtonClicked(view: View) {

        filterRecipes()
        listView.invalidateViews()

    }


    fun filterRecipes() {

        val buttonChecked = mainButtons.checkedRadioButtonId
        val tempList: List<Recipe>

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
