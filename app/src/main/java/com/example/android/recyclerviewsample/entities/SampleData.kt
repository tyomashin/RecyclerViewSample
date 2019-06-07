package com.example.android.recyclerviewsample.entities

enum class RecyclerViewCustomViewType{
    DEFAULT, SUB, SECTION
}

data class SampleData(var title : String = "",
                      var subTitle : String = "",
                      var imageResource : Int = 0,
                      var viewType : RecyclerViewCustomViewType = RecyclerViewCustomViewType.DEFAULT
)