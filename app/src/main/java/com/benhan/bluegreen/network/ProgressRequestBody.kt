package com.benhan.bluegreen.network

import android.os.Looper

import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okio.BufferedSink
import java.io.File
import java.io.FileInputStream


class ProgressRequestBody(file: File, val content_type: String, listener: UploadCallbacks): RequestBody() {

    var mFile: File? = null
    var mPath: String? = null
    var mListener: UploadCallbacks? = null


    interface UploadCallbacks {
        fun onProgressUpdate(percentage: Int)
        fun onError()
        fun onFinish()
    }

    init {
        mFile = file
        mListener = listener
    }


    override fun contentType(): MediaType? {
        return ("$content_type/*").toMediaTypeOrNull()
    }

    override fun contentLength(): Long {
        return mFile?.length()!!
    }

    override fun writeTo(sink: BufferedSink) {
        val fileLength = mFile?.length()!!.toLong()
        val buffer = ByteArray(DEFAULT_BUFFER_SIZE)
        val inputStream = FileInputStream(mFile!!)
        var uploaded = 0L
        try {
            var read: Int? = null
            val handler = android.os.Handler(Looper.getMainLooper())
            while (inputStream.read(buffer).also({ read = it }) !== -1) {
                // update progress on UI thread
                handler.post(ProgressUpdater(uploaded, fileLength))
                uploaded += read!!
                sink.write(buffer, 0, read!!)
            }

        } finally {
            inputStream.close()
        }


    }

    inner class ProgressUpdater(uploaded: Long, total: Long): Runnable{

        private var mUploaded: Long? = null
        private var mTotal: Long? = null
        init {
            mUploaded = uploaded
            mTotal = total
        }

        override fun run() {
            mListener!!.onProgressUpdate((100 * mUploaded!! / mTotal!!).toInt())
        }


    }
}
