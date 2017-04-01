package pc.inspiredfood

import android.database.sqlite.SQLiteDatabase
import org.jetbrains.anko.db.insert

fun defaultData(db: SQLiteDatabase) {

    var tableName = C.CategoriesTable.tableName
    var columnName = C.CategoriesTable.categoryName

    db.insert(tableName, columnName to "Starter")
    db.insert(tableName, columnName to "Main")
    db.insert(tableName, columnName to "Dessert")

    tableName = C.RecipesTable.tableName

    db.insert(tableName,
            C.RecipesTable.recipeName to "Flæskesteg",
            C.RecipesTable.category to 2,
            C.RecipesTable.instructions to "Sæt i ovn. Tag den ud.",
            C.RecipesTable.popularity to 0,
            C.RecipesTable.numberOfPeople to 4
    )

    db.insert(tableName,
            C.RecipesTable.recipeName to "Suppe",
            C.RecipesTable.category to 1,
            C.RecipesTable.instructions to "Rør rundt.",
            C.RecipesTable.popularity to 2,
            C.RecipesTable.numberOfPeople to 3
    )

    db.insert(tableName,
            C.RecipesTable.recipeName to "Is",
            C.RecipesTable.category to 3,
            C.RecipesTable.instructions to "Sæt i fryser. Tag den ud.",
            C.RecipesTable.popularity to 10,
            C.RecipesTable.numberOfPeople to 1
    )

    db.insert(tableName,
            C.RecipesTable.recipeName to "Pommes frites",
            C.RecipesTable.category to 2,
            C.RecipesTable.instructions to "Ind i ovnen med dem.",
            C.RecipesTable.popularity to 7,
            C.RecipesTable.numberOfPeople to 1
    )

    db.insert(tableName,
            C.RecipesTable.recipeName to "Kage",
            C.RecipesTable.category to 3,
            C.RecipesTable.instructions to "Også i ovnen med denne.",
            C.RecipesTable.popularity to 10,
            C.RecipesTable.numberOfPeople to 4
    )

    db.insert(tableName,
            C.RecipesTable.recipeName to "Mørbradgryde",
            C.RecipesTable.category to 2,
            C.RecipesTable.instructions to "Put i gryden og rør rundt",
            C.RecipesTable.popularity to 8,
            C.RecipesTable.numberOfPeople to 3
    )

    db.insert(tableName,
            C.RecipesTable.recipeName to "Rejecocktail",
            C.RecipesTable.category to 1,
            C.RecipesTable.instructions to "Rejer i et glas.",
            C.RecipesTable.popularity to 2,
            C.RecipesTable.numberOfPeople to 1
    )

    db.insert(tableName,
            C.RecipesTable.recipeName to "Nachos",
            C.RecipesTable.category to 1,
            C.RecipesTable.instructions to "Noget med kylling",
            C.RecipesTable.popularity to 5,
            C.RecipesTable.numberOfPeople to 4
    )

    db.insert(tableName,
            C.RecipesTable.recipeName to "Pastaret",
            C.RecipesTable.category to 2,
            C.RecipesTable.instructions to "Koges godt og grundigt",
            C.RecipesTable.popularity to 9,
            C.RecipesTable.numberOfPeople to 2
    )

    db.insert(tableName,
            C.RecipesTable.recipeName to "Pandekager",
            C.RecipesTable.category to 3,
            C.RecipesTable.instructions to "Rør dejen sammen og hæld på pande.",
            C.RecipesTable.popularity to 5,
            C.RecipesTable.numberOfPeople to 2
    )

    db.insert(tableName,
            C.RecipesTable.recipeName to "Æbleskiver",
            C.RecipesTable.category to 3,
            C.RecipesTable.instructions to "Uden æbler. TAK!",
            C.RecipesTable.popularity to 9,
            C.RecipesTable.numberOfPeople to 2
    )

    db.insert(tableName,
            C.RecipesTable.recipeName to "Bruchetta",
            C.RecipesTable.category to 1,
            C.RecipesTable.instructions to "Brød med noget på herunder hvidløg.",
            C.RecipesTable.popularity to 11,
            C.RecipesTable.numberOfPeople to 2
    )
}