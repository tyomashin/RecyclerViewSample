package com.example.android.recyclerviewsample.entities

import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.example.android.recyclerviewsample.R
import com.example.android.recyclerviewsample.activities.MainActivity

/*

* RecyclerView に渡す Adapter
*
* ・Adapter ：View とデータセットを紐づける役割を担うクラス。
*
* ・ViewHolder : View 一行ごとの参照を保持する
*
* */

class SampleAdapter (var myDataset : ArrayList<SampleData>, var activity: MainActivity) :
    RecyclerView.Adapter<SampleAdapter.SampleViewHolder>(){

    /*
     * 行と紐づくViewHolder.
     * viewType ごとに 3 クラスに分かれている。
     * */
    open class SampleViewHolder(v : View) : RecyclerView.ViewHolder(v) {
        var titleTextView : TextView = v.findViewById(R.id.title_text_view)
    }

    open class DefaultSampleViewHolder(v : View, activity: MainActivity) : SampleViewHolder(v) {

        var thumbnailImageView : ImageView = v.findViewById(R.id.thumbnail_image_view)
        var hamburgerImageView : ImageView = v.findViewById(R.id.hamburger_image_view)
        init {

            v.setOnLongClickListener{
                Log.d("longClick", "now")
                v.isPressed = false
                true
            }
            v.setOnClickListener {
                activity.closeRecyclerViewLayout()
                Log.d("click", "now")
            }

            hamburgerImageView.setOnTouchListener { view, event ->
                if (event.actionMasked == MotionEvent.ACTION_DOWN) {
                    activity.startDragging(this)
                }else if(event.actionMasked == MotionEvent.ACTION_UP){
                    activity.endDragging(this)
                }
                true
            }
        }
    }

    class SubSampleViewHolder(v : View, activity: MainActivity) : DefaultSampleViewHolder(v, activity){
        var subTitleTextView : TextView = v.findViewById(R.id.subtitle_text_view)
    }

    //ViewType に応じてレイアウトを決定し、View（ViewHolder）を作成する。
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): SampleViewHolder {

        //viewType に応じてレイアウトを変更している。
        return when (viewType){

            RecyclerViewCustomViewType.SECTION.ordinal -> {
                val v = LayoutInflater.from(viewGroup.context)
                    .inflate(R.layout.sample_recyclerview_row_view_section, viewGroup, false)

                SampleViewHolder(v)
            }

            RecyclerViewCustomViewType.DEFAULT.ordinal -> {

                val v = LayoutInflater.from(viewGroup.context)
                    .inflate(R.layout.sample_recyclerview_row_view_default, viewGroup, false)

                DefaultSampleViewHolder(v, activity)
            }
            else -> {

                val v = LayoutInflater.from(viewGroup.context)
                    .inflate(R.layout.sample_recyclerview_row_view_sub, viewGroup, false)

                SubSampleViewHolder(v, activity)
            }
        }

    }

    //表示するView（ViewHolder）に、データを紐づける。
    override fun onBindViewHolder(holder : SampleViewHolder, position: Int){

        holder.titleTextView.text = myDataset[position].title

        if (holder is DefaultSampleViewHolder) holder.thumbnailImageView.setImageResource(myDataset[position].imageResource)
        if (holder is SubSampleViewHolder) holder.subTitleTextView.text = myDataset[position].subTitle

    }

    override fun getItemCount(): Int = myDataset.size

    override fun getItemViewType(position: Int): Int {

        return when(myDataset[position].viewType){
            RecyclerViewCustomViewType.SECTION -> RecyclerViewCustomViewType.SECTION.ordinal
            RecyclerViewCustomViewType.SUB -> RecyclerViewCustomViewType.SUB.ordinal
            else -> RecyclerViewCustomViewType.DEFAULT.ordinal
        }
    }



}