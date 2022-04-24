package com.example.customviewstest

import android.content.Context
import android.util.Log
import android.view.MotionEvent
import android.view.SurfaceHolder
import android.view.SurfaceView

class CustomViewSurfaceViewTest(context: Context) : SurfaceView(context), SurfaceHolder.Callback {

    private val surfaceDrawingThread = SurfaceDrawingThread(holder)

    init {
        holder.addCallback(this)
    }

    override fun surfaceCreated(p0: SurfaceHolder) {
        surfaceDrawingThread.run()
        Log.d("testtest", "surfaceCreated:")
    }

    override fun surfaceChanged(p0: SurfaceHolder, p1: Int, p2: Int, p3: Int) {
        Log.d("testtest", "surfaceChanged:")
    }

    override fun surfaceDestroyed(p0: SurfaceHolder) {
        surfaceDrawingThread.runThread = false

        Log.d("testtest", "SurfaceHolder:")
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        var result = false
        if (event?.action == MotionEvent.ACTION_DOWN) {
            surfaceDrawingThread.join()
            surfaceDrawingThread.start()
            result = true
        }
        return result
    }
}

