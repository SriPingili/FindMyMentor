package sp.android.findmymentor.play.application

import android.app.Application
import android.content.Context

class CustomApplication : Application() {
    companion object {
        var context: Context? = null
    }

    override fun onCreate() {
        super.onCreate()
        context = this;
    }
}