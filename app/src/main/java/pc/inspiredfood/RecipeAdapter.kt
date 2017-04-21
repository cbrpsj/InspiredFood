package pc.inspiredfood

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import kotlinx.android.synthetic.main.item_recipe.view.*


// Map data from a collection to the elements in the listView
class RecipeAdapter(context: Context, val recipes: List<Recipe>):
        ArrayAdapter<Recipe>(context, 0, recipes) {

    // For each element getView is called
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

        // Find the next recipe based on the position
        val recipe = recipes[position]

        // The first time, convertView is null and is created
        val view = convertView ?: LayoutInflater
                .from(context)
                .inflate(R.layout.item_recipe, parent, false)

        // Set tag to id (first var in Pair), and display the recipeName in view (second var in Pair)
        view.tag = recipe.id
        view.text_recipe_main.text = recipe.name

        // return the view
        return view
    }
}
