package io.github.feelfreelinux.wykopmobilny.ui.modules.embedview

import io.reactivex.Single
import io.github.feelfreelinux.wykopmobilny.api.embed.EmbedApi
import io.github.feelfreelinux.wykopmobilny.base.BasePresenter
import io.github.feelfreelinux.wykopmobilny.base.Schedulers
import io.github.feelfreelinux.wykopmobilny.utils.printout
import java.net.URI
import java.net.URLDecoder
import javax.inject.Inject


class EmbedLinkPresenter (val embedApi: EmbedApi, val schedulers: Schedulers) : BasePresenter<EmbedView>() {
    companion object {
        val GFYCAT_MATCHER = "gfycat.com"
        val STREAMABLE_MATCHER = "streamable.com"
        val YOUTUBE_MATCHER = "youtube.com"
        val SIMPLE_YOUTUBE_MATCHER = "youtu.be"
    }

    lateinit var linkDomain : String

    fun playUrl(url : String) {
        linkDomain = getDomainName(url)
        when (linkDomain) {
            GFYCAT_MATCHER -> {
                val id = url.removeSuffix("/").substringAfterLast("/")
                embedApi.getGfycatWebmUrl(id)
                        .subscribeOn(schedulers.backgroundThread())
                        .observeOn(schedulers.mainThread())
                        .subscribe({ view?.playUrl(it) }, { view?.showErrorDialog(it) })
            }

            STREAMABLE_MATCHER -> {
                val id = url.removeSuffix("/").substringAfterLast("/")
                embedApi.getStreamableUrl(id)
                        .subscribeOn(schedulers.backgroundThread())
                        .observeOn(schedulers.mainThread())
                        .subscribe({
                            view?.playUrl("https://$it")
                        }, { view?.showErrorDialog(it) })
            }

            SIMPLE_YOUTUBE_MATCHER, YOUTUBE_MATCHER -> view?.exitAndOpenYoutubeActivity()
            else -> Single.just(url)
        }

    }

    fun getDomainName(url: String): String {
        val uri = URI(url)
        val domain = uri.host
        return if (domain.startsWith("www.")) domain.substring(4) else domain
    }
}