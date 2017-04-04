package pc.inspiredfood

import android.app.Activity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_recipe.*

class RecipeActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recipe)

        val id = intent.getIntExtra(C.recipeId, -1)

        recipename.setText(id.toString())
    }
}
