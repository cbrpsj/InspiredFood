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
            C.RecipesTable.preparation to "Sæt i ovn. Tag den ud.",
            C.RecipesTable.popularity to 0,
            C.RecipesTable.numberOfPeople to 4
    )

    db.insert(tableName,
            C.RecipesTable.recipeName to "Suppe",
            C.RecipesTable.category to 1,
            C.RecipesTable.preparation to "Rør rundt.",
            C.RecipesTable.popularity to 2,
            C.RecipesTable.numberOfPeople to 3
    )

    db.insert(tableName,
            C.RecipesTable.recipeName to "Is",
            C.RecipesTable.category to 3,
            C.RecipesTable.preparation to "Sæt i fryser. \nTag den ud.",
            C.RecipesTable.popularity to 10,
            C.RecipesTable.numberOfPeople to 1
    )

    db.insert(tableName,
            C.RecipesTable.recipeName to "Pommes frites",
            C.RecipesTable.category to 2,
            C.RecipesTable.preparation to "Ind i ovnen med dem.",
            C.RecipesTable.popularity to 7,
            C.RecipesTable.numberOfPeople to 1
    )

    db.insert(tableName,
            C.RecipesTable.recipeName to "Kage",
            C.RecipesTable.category to 3,
            C.RecipesTable.preparation to "Også i ovnen med denne.",
            C.RecipesTable.popularity to 10,
            C.RecipesTable.numberOfPeople to 4
    )

    db.insert(tableName,
            C.RecipesTable.recipeName to "Mørbradgryde",
            C.RecipesTable.category to 2,
            C.RecipesTable.preparation to "Put i gryden og rør rundt.",
            C.RecipesTable.popularity to 8,
            C.RecipesTable.numberOfPeople to 3
    )

    db.insert(tableName,
            C.RecipesTable.recipeName to "Rejecocktail",
            C.RecipesTable.category to 1,
            C.RecipesTable.preparation to "Rejer i et glas.",
            C.RecipesTable.popularity to 2,
            C.RecipesTable.numberOfPeople to 1
    )

    db.insert(tableName,
            C.RecipesTable.recipeName to "Nachos",
            C.RecipesTable.category to 1,
            C.RecipesTable.preparation to "Noget med kylling.",
            C.RecipesTable.popularity to 5,
            C.RecipesTable.numberOfPeople to 4
    )

    db.insert(tableName,
            C.RecipesTable.recipeName to "Pastaret",
            C.RecipesTable.category to 2,
            C.RecipesTable.preparation to "Koges godt og grundigt.",
            C.RecipesTable.popularity to 9,
            C.RecipesTable.numberOfPeople to 2
    )

    db.insert(tableName,
            C.RecipesTable.recipeName to "Pandekager",
            C.RecipesTable.category to 3,
            C.RecipesTable.preparation to "Rør dejen sammen og hæld på pande.",
            C.RecipesTable.popularity to 5,
            C.RecipesTable.numberOfPeople to 2
    )

    db.insert(tableName,
            C.RecipesTable.recipeName to "Æbleskiver",
            C.RecipesTable.category to 3,
            C.RecipesTable.preparation to "Uden æbler. TAK!",
            C.RecipesTable.popularity to 9,
            C.RecipesTable.numberOfPeople to 2
    )

    db.insert(tableName,
            C.RecipesTable.recipeName to "Bruchetta",
            C.RecipesTable.category to 1,
            C.RecipesTable.preparation to "Brød med noget på herunder hvidløg.",
            C.RecipesTable.popularity to 11,
            C.RecipesTable.numberOfPeople to 2
    )

    tableName = C.UnitsTable.tableName
    columnName = C.UnitsTable.unitName

    db.insert(tableName, columnName to "dl")
    db.insert(tableName, columnName to "l")
    db.insert(tableName, columnName to "g")
    db.insert(tableName, columnName to "kg")
    db.insert(tableName, columnName to "stk")
    db.insert(tableName, columnName to "tskf")
    db.insert(tableName, columnName to "spskf")

    tableName = C.IngredientsTable.tableName
    columnName = C.IngredientsTable.ingredientName

    db.insert(tableName, columnName to "Mel")
    db.insert(tableName, columnName to "Sukker")
    db.insert(tableName, columnName to "Æg")
    db.insert(tableName, columnName to "Smør")
    db.insert(tableName, columnName to "Bagepulver")
    db.insert(tableName, columnName to "Mørbradkød")
    db.insert(tableName, columnName to "Bacon")
    db.insert(tableName, columnName to "Flormelis")
    db.insert(tableName, columnName to "Mælk")
    db.insert(tableName, columnName to "Vand")
    db.insert(tableName, columnName to "Kartofler")
    db.insert(tableName, columnName to "Salt")

    tableName = C.IngredientsInRecipesTable.tableName

    db.insert(tableName,
            C.IngredientsInRecipesTable.recipeId to 1,
            C.IngredientsInRecipesTable.ingredientId to 1,
            C.IngredientsInRecipesTable.unitId to 1,
            C.IngredientsInRecipesTable.amount to 200
    )

    db.insert(tableName,
            C.IngredientsInRecipesTable.recipeId to 1,
            C.IngredientsInRecipesTable.ingredientId to 2,
            C.IngredientsInRecipesTable.unitId to 4,
            C.IngredientsInRecipesTable.amount to 1
    )

    db.insert(tableName,
            C.IngredientsInRecipesTable.recipeId to 1,
            C.IngredientsInRecipesTable.ingredientId to 10,
            C.IngredientsInRecipesTable.unitId to 2,
            C.IngredientsInRecipesTable.amount to 1
    )

    db.insert(tableName,
            C.IngredientsInRecipesTable.recipeId to 1,
            C.IngredientsInRecipesTable.ingredientId to 6,
            C.IngredientsInRecipesTable.unitId to 6,
            C.IngredientsInRecipesTable.amount to 3
    )

    db.insert(tableName,
            C.IngredientsInRecipesTable.recipeId to 1,
            C.IngredientsInRecipesTable.ingredientId to 4,
            C.IngredientsInRecipesTable.unitId to 3,
            C.IngredientsInRecipesTable.amount to 100
    )


    db.insert(tableName,
            C.IngredientsInRecipesTable.recipeId to 2,
            C.IngredientsInRecipesTable.ingredientId to 7,
            C.IngredientsInRecipesTable.unitId to 1,
            C.IngredientsInRecipesTable.amount to 200
    )

    db.insert(tableName,
            C.IngredientsInRecipesTable.recipeId to 2,
            C.IngredientsInRecipesTable.ingredientId to 6,
            C.IngredientsInRecipesTable.unitId to 4,
            C.IngredientsInRecipesTable.amount to 1
    )

    db.insert(tableName,
            C.IngredientsInRecipesTable.recipeId to 2,
            C.IngredientsInRecipesTable.ingredientId to 5,
            C.IngredientsInRecipesTable.unitId to 2,
            C.IngredientsInRecipesTable.amount to 1
    )

    db.insert(tableName,
            C.IngredientsInRecipesTable.recipeId to 2,
            C.IngredientsInRecipesTable.ingredientId to 4,
            C.IngredientsInRecipesTable.unitId to 6,
            C.IngredientsInRecipesTable.amount to 3
    )

    db.insert(tableName,
            C.IngredientsInRecipesTable.recipeId to 2,
            C.IngredientsInRecipesTable.ingredientId to 3,
            C.IngredientsInRecipesTable.unitId to 3,
            C.IngredientsInRecipesTable.amount to 100
    )

    db.insert(tableName,
            C.IngredientsInRecipesTable.recipeId to 3,
            C.IngredientsInRecipesTable.ingredientId to 1,
            C.IngredientsInRecipesTable.unitId to 1,
            C.IngredientsInRecipesTable.amount to 200
    )

    db.insert(tableName,
            C.IngredientsInRecipesTable.recipeId to 3,
            C.IngredientsInRecipesTable.ingredientId to 2,
            C.IngredientsInRecipesTable.unitId to 4,
            C.IngredientsInRecipesTable.amount to 1
    )

    db.insert(tableName,
            C.IngredientsInRecipesTable.recipeId to 4,
            C.IngredientsInRecipesTable.ingredientId to 10,
            C.IngredientsInRecipesTable.unitId to 2,
            C.IngredientsInRecipesTable.amount to 1
    )

    db.insert(tableName,
            C.IngredientsInRecipesTable.recipeId to 4,
            C.IngredientsInRecipesTable.ingredientId to 6,
            C.IngredientsInRecipesTable.unitId to 6,
            C.IngredientsInRecipesTable.amount to 3
    )

    db.insert(tableName,
            C.IngredientsInRecipesTable.recipeId to 4,
            C.IngredientsInRecipesTable.ingredientId to 4,
            C.IngredientsInRecipesTable.unitId to 3,
            C.IngredientsInRecipesTable.amount to 100
    )

    db.insert(tableName,
            C.IngredientsInRecipesTable.recipeId to 5,
            C.IngredientsInRecipesTable.ingredientId to 1,
            C.IngredientsInRecipesTable.unitId to 1,
            C.IngredientsInRecipesTable.amount to 200
    )

    db.insert(tableName,
            C.IngredientsInRecipesTable.recipeId to 5,
            C.IngredientsInRecipesTable.ingredientId to 2,
            C.IngredientsInRecipesTable.unitId to 4,
            C.IngredientsInRecipesTable.amount to 1
    )

    db.insert(tableName,
            C.IngredientsInRecipesTable.recipeId to 6,
            C.IngredientsInRecipesTable.ingredientId to 10,
            C.IngredientsInRecipesTable.unitId to 2,
            C.IngredientsInRecipesTable.amount to 1
    )

    db.insert(tableName,
            C.IngredientsInRecipesTable.recipeId to 6,
            C.IngredientsInRecipesTable.ingredientId to 6,
            C.IngredientsInRecipesTable.unitId to 6,
            C.IngredientsInRecipesTable.amount to 3
    )

    db.insert(tableName,
            C.IngredientsInRecipesTable.recipeId to 7,
            C.IngredientsInRecipesTable.ingredientId to 4,
            C.IngredientsInRecipesTable.unitId to 3,
            C.IngredientsInRecipesTable.amount to 100
    )


    db.insert(tableName,
            C.IngredientsInRecipesTable.recipeId to 7,
            C.IngredientsInRecipesTable.ingredientId to 7,
            C.IngredientsInRecipesTable.unitId to 1,
            C.IngredientsInRecipesTable.amount to 200
    )

    db.insert(tableName,
            C.IngredientsInRecipesTable.recipeId to 8,
            C.IngredientsInRecipesTable.ingredientId to 6,
            C.IngredientsInRecipesTable.unitId to 4,
            C.IngredientsInRecipesTable.amount to 1
    )

    db.insert(tableName,
            C.IngredientsInRecipesTable.recipeId to 8,
            C.IngredientsInRecipesTable.ingredientId to 5,
            C.IngredientsInRecipesTable.unitId to 2,
            C.IngredientsInRecipesTable.amount to 1
    )

    db.insert(tableName,
            C.IngredientsInRecipesTable.recipeId to 9,
            C.IngredientsInRecipesTable.ingredientId to 4,
            C.IngredientsInRecipesTable.unitId to 6,
            C.IngredientsInRecipesTable.amount to 3
    )

    db.insert(tableName,
            C.IngredientsInRecipesTable.recipeId to 9,
            C.IngredientsInRecipesTable.ingredientId to 3,
            C.IngredientsInRecipesTable.unitId to 3,
            C.IngredientsInRecipesTable.amount to 100
    )

    db.insert(tableName,
            C.IngredientsInRecipesTable.recipeId to 10,
            C.IngredientsInRecipesTable.ingredientId to 1,
            C.IngredientsInRecipesTable.unitId to 1,
            C.IngredientsInRecipesTable.amount to 200
    )

    db.insert(tableName,
            C.IngredientsInRecipesTable.recipeId to 10,
            C.IngredientsInRecipesTable.ingredientId to 2,
            C.IngredientsInRecipesTable.unitId to 4,
            C.IngredientsInRecipesTable.amount to 1
    )

    db.insert(tableName,
            C.IngredientsInRecipesTable.recipeId to 11,
            C.IngredientsInRecipesTable.ingredientId to 10,
            C.IngredientsInRecipesTable.unitId to 2,
            C.IngredientsInRecipesTable.amount to 1
    )

    db.insert(tableName,
            C.IngredientsInRecipesTable.recipeId to 11,
            C.IngredientsInRecipesTable.ingredientId to 6,
            C.IngredientsInRecipesTable.unitId to 6,
            C.IngredientsInRecipesTable.amount to 3
    )

    db.insert(tableName,
            C.IngredientsInRecipesTable.recipeId to 12,
            C.IngredientsInRecipesTable.ingredientId to 4,
            C.IngredientsInRecipesTable.unitId to 3,
            C.IngredientsInRecipesTable.amount to 100
    )
}