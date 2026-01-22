package evan.takehome

import android.app.Application
import evan.takehome.di.AppContainer
import evan.takehome.di.DefaultAppContainer

class TakeHomeApplication : Application() {

    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = DefaultAppContainer()
    }
}
