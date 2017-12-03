package io.github.feelfreelinux.wykopmobilny

import android.app.Application
import android.content.Context
import com.bugsnag.android.Bugsnag
import com.evernote.android.job.JobManager
import com.squareup.leakcanary.LeakCanary
import com.squareup.leakcanary.RefWatcher
import io.github.feelfreelinux.wykopmobilny.di.AppModule
import io.github.feelfreelinux.wykopmobilny.di.components.DaggerInjector
import io.github.feelfreelinux.wykopmobilny.di.components.Injector
import io.github.feelfreelinux.wykopmobilny.di.modules.NetworkModule
import io.github.feelfreelinux.wykopmobilny.di.modules.PresentersModule
import io.github.feelfreelinux.wykopmobilny.di.modules.RepositoryModule
import io.github.feelfreelinux.wykopmobilny.di.modules.ViewPresentersModule
import io.github.feelfreelinux.wykopmobilny.ui.modules.notifications.notificationsservice.WykopNotificationJobCreator


class WykopApp : Application() {
    companion object {
        lateinit var uiInjector: Injector
        val WYKOP_API_URL = "https://a2.wykop.pl"
    }

    private var refWatcher: RefWatcher? = null

    fun getRefWatcher(context: Context): RefWatcher {
        val application = context.applicationContext as WykopApp
        return application.refWatcher!!
    }
    override fun onCreate() {
        super.onCreate()
        refWatcher = LeakCanary.install(this)
        JobManager.create(this).addJobCreator(WykopNotificationJobCreator())
        if (!BuildConfig.DEBUG) {
            Bugsnag.init(this, "3b54bcbf963f13ae9aa8f8376ea7c1cc")
            Bugsnag.setReleaseStage(BuildConfig.BUILD_TYPE)
            Bugsnag.setProjectPackages("io.github.feelfreelinux.wykopmobilny")
        }


        uiInjector = DaggerInjector.builder()
                .appModule(AppModule(this))
                .networkModule(NetworkModule(WYKOP_API_URL))
                .repositoryModule(RepositoryModule())
                .presentersModule(PresentersModule())
                .viewPresentersModule(ViewPresentersModule())
                .build()
    }
}
