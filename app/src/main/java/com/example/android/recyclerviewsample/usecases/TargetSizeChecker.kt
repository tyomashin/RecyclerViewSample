package com.example.android.recyclerviewsample.usecases

import android.app.Activity
import android.graphics.Point
import android.view.View

/*
* 画面サイズや View のサイズを取得するメソッドをまとめたクラス。
* */

class TargetSizeChecker {

    companion object {
        fun getDisplaySize(activity: Activity): Point {
            val display = activity.windowManager.defaultDisplay
            var point = Point()
            display.getSize(point)

            return point
        }

        fun getViewSize(view : View) : Point{
            var point = Point()
            point.set(view.width, view.height)

            return point
        }
    }

}