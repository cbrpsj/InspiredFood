package pc.inspiredfood

import android.database.sqlite.SQLiteDatabase
import org.jetbrains.anko.db.insert

fun defaultData(db: SQLiteDatabase) {

    var tableName = C.CategoriesTable.tableName
    var columnName = C.CategoriesTable.categoryName

    db.insert(tableName, C.CategoriesTable.id to 1, columnName to "Starter")
    db.insert(tableName, C.CategoriesTable.id to 2, columnName to "Main")
    db.insert(tableName, C.CategoriesTable.id to 3, columnName to "Dessert")

    tableName = C.RecipesTable.tableName

    db.insert(tableName,
            C.RecipesTable.id to 1,
            C.RecipesTable.recipeName to "Flæskesteg",
            C.RecipesTable.category to 2,
            C.RecipesTable.instructions to "Sæt i ovn. Tag den ud.",
            C.RecipesTable.popularity to 0,
            C.RecipesTable.numberOfPeople to 4
    )

    db.insert(tableName,
            C.RecipesTable.id to 2,
            C.RecipesTable.recipeName to "Suppe",
            C.RecipesTable.category to 1,
            C.RecipesTable.instructions to "Rør rundt.",
            C.RecipesTable.popularity to 2,
            C.RecipesTable.numberOfPeople to 3
    )

    db.insert(tableName,
            C.RecipesTable.id to 3,
            C.RecipesTable.recipeName to "Is",
            C.RecipesTable.category to 3,
            C.RecipesTable.instructions to "Sæt i fryser. Tag den ud.",
            C.RecipesTable.popularity to 10,
            C.RecipesTable.numberOfPeople to 1
    )
}