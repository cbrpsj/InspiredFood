package pc.inspiredfood

import android.app.Activity
import android.os.Bundle
import android.os.Handler
import org.jetbrains.anko.intentFor

class SplashActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        Handler().postDelayed({
            startActivity(intentFor<RecipeListActivity>())
            finish()
        }, 1500)
    }
}
