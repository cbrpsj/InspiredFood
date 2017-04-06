package pc.inspiredfood

import android.app.Application

class App : Application() {

    companion object {

        lateinit var instance: App
            private set

        var updateRecipeList = true
    }


    override fun onCreate() {

        super.onCreate()
        instance = this
    }
}