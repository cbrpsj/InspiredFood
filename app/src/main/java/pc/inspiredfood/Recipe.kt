package pc.inspiredfood

import org.jetbrains.anko.db.update

data class Recipe(val id: Int, var name: String, var category: Int,
                  var popularity: Int) {



    // Increase popularity by one and update recipe in the database
    fun updatePopularity() {

        CRUD.updatePopularity(id, ++popularity)
    }
}