package pc.inspiredfood

import android.app.Activity
import android.os.Bundle
import android.os.Handler
import org.jetbrains.anko.intentFor

// Main/launcher activity
class SplashActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        // Finish splash activity and start next activity after specified amount of milliseconds
        Handler().postDelayed({
            startActivity(intentFor<RecipeListActivity>())
            finish()
        }, 1500)
    }
}
