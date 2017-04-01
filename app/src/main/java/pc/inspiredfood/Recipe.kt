package pc.inspiredfood

import org.jetbrains.anko.db.update

data class Recipe(val id: Int, var name: String, var category: Int, var instructions: String,
                  var popularity: Int, var noOfPeople: Int) {

    fun updatePopularity() {

        //popularity++

        RecipeDBHelper.instance.use {

            update(C.RecipesTable.tableName, C.RecipesTable.popularity to ++popularity)
                    .where("${C.RecipesTable.id} = $id")
                    .exec()
        }
    }
}