package com.simplemobiletools.gallery.asynctasks

import android.content.Context
import android.os.AsyncTask
import com.simplemobiletools.commons.helpers.SORT_BY_DATE_TAKEN
import com.simplemobiletools.commons.models.FileDirItem.Companion.sorting
import com.simplemobiletools.gallery.extensions.config
import com.simplemobiletools.gallery.helpers.MediaFetcher
import com.simplemobiletools.gallery.models.Medium
import java.util.*

class GetMediaAsynctask(val context: Context, val mPath: String, val isPickImage: Boolean = false, val isPickVideo: Boolean = false,
                        val showAll: Boolean, val callback: (media: ArrayList<Medium>) -> Unit) :
        AsyncTask<Void, Void, ArrayList<Medium>>() {
    private val mediaFetcher = MediaFetcher(context)

    override fun doInBackground(vararg params: Void): ArrayList<Medium> {
        val getProperDateTaken = sorting and SORT_BY_DATE_TAKEN != 0
        return if (showAll) {
            val foldersToScan = mediaFetcher.getFoldersToScan()
            val media = ArrayList<Medium>()
            foldersToScan.forEach {
                val newMedia = mediaFetcher.getFilesFrom(it, isPickImage, isPickVideo, getProperDateTaken)
                media.addAll(newMedia)
            }

            MediaFetcher(context).sortMedia(media, context.config.getFileSorting(""))
            media
        } else {
            mediaFetcher.getFilesFrom(mPath, isPickImage, isPickVideo, getProperDateTaken)
        }
    }

    override fun onPostExecute(media: ArrayList<Medium>) {
        super.onPostExecute(media)
        callback(media)
    }

    fun stopFetching() {
        mediaFetcher.shouldStop = true
        cancel(true)
    }
}
