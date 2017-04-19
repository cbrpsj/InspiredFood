package pc.inspiredfood

import android.app.Application

class App : Application() {

    companion object {

        lateinit var instance: App
            private set

        var updateRecipeList = true

        var units = hashSetOf<String>()
        var ingredients = hashSetOf<String>()
        var categories = listOf<String>()
    }


    override fun onCreate() {

        super.onCreate()
        instance = this
    }
}