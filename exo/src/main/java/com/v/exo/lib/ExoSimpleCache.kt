package com.v.exo.lib

import android.content.Context
import androidx.annotation.NonNull
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.database.ExoDatabaseProvider
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.upstream.cache.CacheDataSink
import com.google.android.exoplayer2.upstream.cache.CacheDataSource
import com.google.android.exoplayer2.upstream.cache.NoOpCacheEvictor
import com.google.android.exoplayer2.upstream.cache.SimpleCache
import com.v.commonlib.util.FileUtil


/**
 * Author:v
 * Time:2021/4/15
 */
object ExoSimpleCache {
    private var cacheDataSourceFactory: CacheDataSource.Factory? = null


    fun buildDataSourceFactory(@NonNull context: Context): CacheDataSource.Factory {
        if (cacheDataSourceFactory == null) {
            val upstreamFactory = DefaultDataSourceFactory(context)
            val cache = SimpleCache(
                FileUtil.getAppVideoCacheDir(context),
                NoOpCacheEvictor(),
                ExoDatabaseProvider(context)
            )
            val dataSinkFactory = CacheDataSink.Factory().setCache(cache)
            cacheDataSourceFactory = CacheDataSource.Factory()
                .setCache(cache)
                .setUpstreamDataSourceFactory(upstreamFactory)
                .setCacheWriteDataSinkFactory(dataSinkFactory)
                .setFlags(CacheDataSource.FLAG_BLOCK_ON_CACHE or CacheDataSource.FLAG_IGNORE_CACHE_ON_ERROR)

        }

        return cacheDataSourceFactory!!
    }

    fun createMediaSource(@NonNull url: String, context: Context): ProgressiveMediaSource {
        return ProgressiveMediaSource.Factory(buildDataSourceFactory(context))
            .createMediaSource(MediaItem.fromUri(url))
    }
}