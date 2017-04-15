package pc.inspiredfood

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import org.jetbrains.anko.db.*

class RecipeDBHelper(context: Context = App.instance, version: Int = 17) :
        ManagedSQLiteOpenHelper(context, C.dbName, null, version) {

    companion object {
        val instance by lazy { RecipeDBHelper() }
    }

    override fun onCreate(db: SQLiteDatabase) {

        db.createTable(C.CategoriesTable.tableName, true,
                C.CategoriesTable.id to INTEGER + PRIMARY_KEY + UNIQUE,
                C.CategoriesTable.categoryName to TEXT + UNIQUE
        )

        db.createTable(C.RecipesTable.tableName, true,
                C.RecipesTable.id to INTEGER + PRIMARY_KEY + UNIQUE,
                C.RecipesTable.recipeName to TEXT,
                C.RecipesTable.category to INTEGER,
                C.RecipesTable.preparation to TEXT,
                C.RecipesTable.popularity to INTEGER,
                C.RecipesTable.numberOfPeople to INTEGER,
                "" to FOREIGN_KEY(C.RecipesTable.category, C.CategoriesTable.tableName, C.CategoriesTable.id)
        )

        db.createTable(C.TimersTable.tableName, true,
                C.TimersTable.id to INTEGER + PRIMARY_KEY + UNIQUE,
                C.TimersTable.minutes to INTEGER,
                C.TimersTable.recipeId to INTEGER,
                "" to FOREIGN_KEY(C.TimersTable.recipeId, C.RecipesTable.tableName, C.RecipesTable.id)
        )

        db.createTable(C.IngredientsTable.tableName, true,
                C.IngredientsTable.id to INTEGER + PRIMARY_KEY + UNIQUE,
                C.IngredientsTable.ingredientName to TEXT + UNIQUE
        )

        db.createTable(C.UnitsTable.tableName, true,
                C.UnitsTable.id to INTEGER + PRIMARY_KEY + UNIQUE,
                C.UnitsTable.unitName to TEXT + UNIQUE
        )

        db.createTable(C.IngredientsInRecipesTable.tableName, true,
                C.IngredientsInRecipesTable.id to INTEGER + PRIMARY_KEY + UNIQUE,
                C.IngredientsInRecipesTable.recipeId to INTEGER,
                C.IngredientsInRecipesTable.ingredientId to INTEGER,
                C.IngredientsInRecipesTable.unitId to INTEGER,
                C.IngredientsInRecipesTable.amount to REAL,
                "" to FOREIGN_KEY(C.IngredientsInRecipesTable.recipeId, C.RecipesTable.tableName, C.RecipesTable.id),
                "" to FOREIGN_KEY(C.IngredientsInRecipesTable.ingredientId, C.IngredientsTable.tableName, C.IngredientsTable.id),
                "" to FOREIGN_KEY(C.IngredientsInRecipesTable.unitId, C.UnitsTable.tableName, C.UnitsTable.id)
        )

        defaultData(db)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {

        db.dropTable(C.CategoriesTable.tableName)
        db.dropTable(C.RecipesTable.tableName)
        db.dropTable(C.TimersTable.tableName)
        db.dropTable(C.IngredientsTable.tableName)
        db.dropTable(C.UnitsTable.tableName)
        db.dropTable(C.IngredientsInRecipesTable.tableName)

        onCreate(db)
    }
}