package com.example.android.recyclerviewsample.activities

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.app.Activity
import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.util.Log
import android.widget.*
import com.example.android.recyclerviewsample.R
import com.example.android.recyclerviewsample.entities.RecyclerViewCustomViewType
import com.example.android.recyclerviewsample.entities.SampleAdapter
import com.example.android.recyclerviewsample.entities.SampleData
import com.example.android.recyclerviewsample.usecases.TargetSizeChecker

/*
*
* RecyclerView のサンプル
*
* ・画面下部の「タップしてリスト表示」エリア (listHeaderLayout) をタップすることで、
* 　開閉アニメーションが実行されて RecyclerView が出現する。
*
* ・RecyclerView はドラッグでアイテムの移動、スワイプで削除が可能。
*
* */

class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView : RecyclerView
    private lateinit var viewAdapter : RecyclerView.Adapter<*>
    private lateinit var viewManager : RecyclerView.LayoutManager
    private lateinit var itemTouchHelper : ItemTouchHelper
    private lateinit var myDataSet : ArrayList<SampleData>

    private lateinit var recyclerViewLayout : LinearLayout
    private lateinit var listHeaderLayout : RelativeLayout
    private lateinit var arrowImageView : ImageView

    private lateinit var sampleButton : Button

    private var animFlag = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportActionBar?.hide()

        // テストデータの作成
        myDataSet = createDataSet()

        // RecyclerView の設定
        viewManager = LinearLayoutManager(this)
        viewAdapter = SampleAdapter(myDataSet, this)

        recyclerView = findViewById<RecyclerView>(R.id.recycler_view).apply{
            setHasFixedSize(true)
            layoutManager = viewManager
            adapter = viewAdapter
        }

        // RecyclerView のドラッグ、スワイプ操作に関する設定
        itemTouchHelper = ItemTouchHelper(getRecyclerViewSimpleCallBack())
        itemTouchHelper.attachToRecyclerView(recyclerView)

        // RecyclerView を表示するレイアウトを画面下から出現させるアニメーション設定。
        arrowImageView = findViewById(R.id.arrow_image_view)
        recyclerViewLayout = findViewById(R.id.recycler_view_layout)
        listHeaderLayout = findViewById<RelativeLayout>(R.id.list_header_view).apply {
            setOnClickListener{
                //RecyclerView レイアウトの開閉アニメーションを実行
                setAnimation()
            }
        }

        // 画面サイズに合わせてレイアウトサイズを決定する。
        setLayoutSize(this)

        // テスト用のボタン
        sampleButton = findViewById<Button>(R.id.sample_button).apply {
            setOnClickListener{
                Log.d("click_button","now")
            }
        }
    }

    // RecyclerView などのサイズを設定する。
    private fun setLayoutSize(activity: Activity){
        // 画面サイズを取得
        var screenSize = TargetSizeChecker.getDisplaySize(activity)

        // 対象 View のサイズに関する値
        var recyclerViewLayoutHeight = (screenSize.y * 0.75).toInt()
        var listHeaderLayoutHeight = (screenSize.y * 0.1).toInt()
        var recyclerViewHeight = recyclerViewLayoutHeight - listHeaderLayoutHeight
        Log.d("heights", recyclerViewHeight.toString())

        // 対象 View にサイズを挿入。
        var recyclerViewLayoutParams = recyclerViewLayout.layoutParams
        recyclerViewLayoutParams.height = recyclerViewLayoutHeight
        recyclerViewLayout.layoutParams = recyclerViewLayoutParams

        var listHeaderLayoutParams = listHeaderLayout.layoutParams as LinearLayout.LayoutParams
        listHeaderLayoutParams.height = listHeaderLayoutHeight
        listHeaderLayoutParams.setMargins(0, recyclerViewHeight, 0,0)
        listHeaderLayout.layoutParams = listHeaderLayoutParams

        var recyclerViewParams = recyclerView.layoutParams
        recyclerViewParams.height = recyclerViewHeight
        recyclerView.layoutParams = recyclerViewParams
    }

    /*
    * RecyclerView の開閉アニメーション
    * ２つのアニメーションを同時に実行している。
    * ・矢印アイコンの向きを変える
    * ・RecyclerView のサイズを変更
    * */
    private fun setAnimation(){

        // 複数の Animator を格納するリスト
        val animatorList = ArrayList<Animator>()

        // 矢印アイコンの回転アニメーション
        var fromRotation = 0f
        var toRotation = 180f

        var recyclerViewSize = recyclerView.layoutParams.height

        var fromTransitionY = 0f
        var toTransitionY = -1f * recyclerViewSize

        if (animFlag) {
            fromRotation = 180f
            toRotation = 360f

            fromTransitionY = -1f * recyclerViewSize
            toTransitionY = 0f

            animFlag = false
        }else{
            animFlag = true
        }

        val objectRotateAnimatar = ObjectAnimator.ofFloat(arrowImageView, "rotation", fromRotation, toRotation)
        objectRotateAnimatar.duration = 500
        animatorList.add(objectRotateAnimatar)

        val objectTransitionYAnimatorForRecyclerView = ObjectAnimator.ofFloat(recyclerView, "translationY", fromTransitionY, toTransitionY)
        objectTransitionYAnimatorForRecyclerView.duration = 500
        animatorList.add(objectTransitionYAnimatorForRecyclerView)

        val objectTransitionYAnimatorForHeaderLayout = ObjectAnimator.ofFloat(listHeaderLayout, "translationY", fromTransitionY, toTransitionY)
        objectTransitionYAnimatorForHeaderLayout.duration = 500
        animatorList.add(objectTransitionYAnimatorForHeaderLayout)

        val animatorSet = AnimatorSet()
        animatorSet.playTogether(animatorList)
        animatorSet.start()

    }

    private fun createDataSet() : ArrayList<SampleData>{

        val tmpArray = ArrayList<SampleData>()

        for (i in 0 .. 10) {

            when(i){

                0 -> tmpArray.add(
                    SampleData(
                        title = "ViewType 1",
                        imageResource = R.mipmap.ic_launcher,
                        viewType = RecyclerViewCustomViewType.SECTION
                    )
                )
                in 1..4 -> tmpArray.add(
                    SampleData(
                        title = "hoge $i",
                        subTitle = "hoge_sub $i",
                        imageResource = R.mipmap.ic_launcher,
                        viewType = RecyclerViewCustomViewType.SUB
                    )
                )
                5 -> tmpArray.add(
                    SampleData(
                        title = "ViewType 2",
                        imageResource = R.mipmap.ic_launcher,
                        viewType = RecyclerViewCustomViewType.SECTION
                    )
                )
                in 6..10 -> tmpArray.add(
                    SampleData(
                        title = "hoge $i",
                        imageResource = R.mipmap.ic_launcher
                    )
                )

            }

        }

        return tmpArray
    }

    //recyclerView をドラッグ、スワイプした時に呼び出されるコールバック関数
    private fun getRecyclerViewSimpleCallBack() =
        // 引数で、上下のドラッグ、および左方向のスワイプを有効にしている。
        object : ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP or ItemTouchHelper.DOWN,
            ItemTouchHelper.LEFT) {

            // ドラッグしたとき
            override fun onMove(p0: RecyclerView, p1: RecyclerView.ViewHolder, p2: RecyclerView.ViewHolder): Boolean {

                val fromPosition = p1.adapterPosition
                val toPosition = p2.adapterPosition

                /*
                * ドラッグ時、viewType が異なるアイテムを超えるときに、
                * notifyItemMoved を呼び出すと、ドラッグ操作がキャンセルされてしまう。
                * （ドラッグは同じviewTypeを持つアイテム間で行う必要がある模様）
                *
                * 同じ ViewType アイテムを超える時だけ notifyItemMoved を呼び出す。
                * */
                if (p1.itemViewType == p2.itemViewType) {
                    myDataSet.add(toPosition, myDataSet.removeAt(fromPosition))
                    viewAdapter.notifyItemMoved(fromPosition, toPosition)
                }

                return true
            }

            override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
                super.clearView(recyclerView, viewHolder)
                endDragging(viewHolder)
            }

            //左にスワイプしたとき
            override fun onSwiped(p0: RecyclerView.ViewHolder, p1: Int) {
                p0.let{
                    myDataSet.removeAt(p0.adapterPosition)
                    viewAdapter.notifyItemRemoved(p0.adapterPosition)
                }
            }

            /*
            * 一部リストアイテム（セクション）はドラッグ・スワイプさせたくないため、以下で制御。
            * https://kotaeta.com/61339696
            * */
            override fun getMovementFlags(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder): Int {

                if (viewHolder.itemViewType == RecyclerViewCustomViewType.SECTION.ordinal)
                    return 0
                return super.getMovementFlags(recyclerView, viewHolder)
            }
        }

    // リスト画像をタップすると即座にドラッグ開始
    fun startDragging(viewHolder : RecyclerView.ViewHolder){
        itemTouchHelper.startDrag(viewHolder)
        viewHolder.itemView.isPressed = true
        /*
        // ドラッグ開始通知のためにバイブさせる場合。API レベルによって使用できるメソッドが異なるので分岐
        val vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val vibrationEffect = VibrationEffect.createOneShot(300, VibrationEffect.DEFAULT_AMPLITUDE)
            vibrator.vibrate(vibrationEffect)
        } else {
            vibrator.vibrate(300)
        }
        */
    }

    // imageView から指を離した時
    fun endDragging(viewHolder : RecyclerView.ViewHolder){
        Log.d("endDragging", "now")
        viewHolder.itemView.isPressed = false
    }

    // RecyclerView 画面を閉じる
    fun closeRecyclerViewLayout(){
        listHeaderLayout.callOnClick()
    }

    //　テスト用
    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        Log.d("recyclerViewSize", TargetSizeChecker.getViewSize(recyclerViewLayout).toString())
        Log.d("recyclerViewLayoutSize", TargetSizeChecker.getViewSize(recyclerViewLayout).toString())
        Log.d("listHeaderLayoutSize", TargetSizeChecker.getViewSize(listHeaderLayout).toString())
    }


}
