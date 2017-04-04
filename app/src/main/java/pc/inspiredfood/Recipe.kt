package pc.inspiredfood

import org.jetbrains.anko.db.update

data class Recipe(val id: Int, var name: String, var category: Int,
                  var popularity: Int) {

//    var instructions = ""
//    var noOfPeople = 0

    // Increase popularity by one and update recipe in the database
    fun updatePopularity() {

        RecipeDBHelper.instance.use {

            update(C.RecipesTable.tableName, C.RecipesTable.popularity to ++popularity)
                    .where("${C.RecipesTable.id} = $id")
                    .exec()
        }
    }
}