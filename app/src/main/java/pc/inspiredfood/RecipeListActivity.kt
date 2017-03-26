package pc.inspiredfood

import android.app.Activity
import android.app.ListActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_recipe_list.*

class RecipeListActivity : ListActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recipe_list)

        mainButtons.check(all.id)
    }

    override fun onResume() {

        super.onResume()

        RecipeDBHelper.instance.use {

            val cursor = rawQuery("select * from ${C.RecipesTable.tableName}", null)
            listAdapter = RecipeAdapter(this@RecipeListActivity, cursor, 0)

        }
    }
}
