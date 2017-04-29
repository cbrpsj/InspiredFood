package pc.inspiredfood

import android.app.ListActivity
import android.os.Bundle
import android.util.Log
import android.view.ContextMenu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.AdapterView.AdapterContextMenuInfo
import android.widget.ListView
import kotlinx.android.synthetic.main.activity_recipe_list.*
import org.jetbrains.anko.contentView
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.onClick
import org.jetbrains.anko.toast
import pc.inspiredfood.App.Companion.updateRecipeList
import pc.inspiredfood.CRUD.deleteRecipe
import pc.inspiredfood.CRUD.getCategories
import pc.inspiredfood.CRUD.getIngredients
import pc.inspiredfood.CRUD.getRecipes
import pc.inspiredfood.CRUD.getUnits


class RecipeListActivity : ListActivity() {

    var recipes = mutableListOf<Recipe>()

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recipe_list)

        // Enables menu on long press
        registerForContextMenu(list)

        updateRecipeList = true
        mainButtons.check(all.id)

        getCategories()

        button_add.onClick { addRecipe() }
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


    // Event handler for click on recipe
    override fun onListItemClick(listView: ListView, view: View, position: Int, id: Long) {

        val recipeId = view.tag

        // Update popularity counter for the chosen recipe
        recipes.find { it.id == recipeId }?.updatePopularity()

        // Setup bundle with array of longs containing only recipe id
        val bundle = Bundle()
        val arrayWithRecipeId = longArrayOf(recipeId.toString().toLong())
        bundle.putLongArray("InterActivityData", arrayWithRecipeId)

        // Go to recipe details page while transferring bundle
        startActivity(intentFor<RecipeActivity>("RecipeDetails" to bundle))
    }


    // Create custom context menu when recipe is long pressed
    override fun onCreateContextMenu(menu: ContextMenu?, v: View?, menuInfo: ContextMenu.ContextMenuInfo?) {

        val inflate = menuInflater
        inflate.inflate(R.menu.context_menu, menu)
    }


    // Event handler for long press on custom context menu
    override fun onContextItemSelected(item: MenuItem?): Boolean {

        // Find recipe that was long pressed
        val info = item?.menuInfo as AdapterContextMenuInfo
        val recipe = list.getItemAtPosition(info.position) as Recipe

        // Remove recipe from local list and db
        recipes.remove(recipe)
        deleteRecipe(recipe.id)

        // Filter recipes based on selected radio button and update list view
        filterRecipes()

        return true
    }


    // Radio button event handler
    fun mainButtonClicked(view: View) {

        // Filter recipes based on selected radio button and update list view
        filterRecipes()
    }


    // Add new recipe
    fun addRecipe() {

        // Setup bundle with array of longs containing -1 to indicate new recipe
        val bundle = Bundle()
        val arrayWithoutRecipeId = longArrayOf(-1)
        bundle.putLongArray("InterActivityData", arrayWithoutRecipeId)

        // Go to recipe details page while transferring bundle
        startActivity(intentFor<RecipeActivity>("RecipeDetails" to bundle))
    }


    // Filter recipes (sorted alphabetically) based on selected radio button and update list view
    fun filterRecipes() {

        val buttonChecked = mainButtons.checkedRadioButtonId
        recipes.sortBy { it.name }

        val tempList: List<Recipe>

        when(buttonChecked) {
            starters.id -> tempList = recipes.filter { it.category == 1 }
            mains.id -> tempList = recipes.filter { it.category == 2 }
            desserts.id -> tempList = recipes.filter { it.category == 3 }
            favourites.id -> tempList = recipes.sortedByDescending { it.popularity }
            else -> tempList = recipes
        }

        // Map data from tempList to list view
        listAdapter = RecipeAdapter(this@RecipeListActivity, tempList)
    }
}
