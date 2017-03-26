package pc.inspiredfood

import android.content.Context
import android.database.Cursor
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CursorAdapter
import kotlinx.android.synthetic.main.item_recipe.view.*


class RecipeAdapter(context: Context, cursor: Cursor, flags: Int) : CursorAdapter(context, cursor, flags) {

    override fun newView(context: Context, cursor: Cursor, parent: ViewGroup): View {

        return LayoutInflater
                .from(context)
                .inflate(R.layout.item_recipe, parent, false)
    }

    override fun bindView(view: View, context: Context, cursor: Cursor) {

        view.tag = cursor.getColumnIndex(C.RecipesTable.id)

        view.textRecipeMain.setText(cursor.getString(cursor.getColumnIndex(C.RecipesTable.recipeName)))
    }
}