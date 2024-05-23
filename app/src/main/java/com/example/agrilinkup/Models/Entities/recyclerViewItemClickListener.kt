package com.example.agrilinkup.Models.Entities

import android.content.Context
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.widget.AdapterView.OnItemClickListener
import androidx.recyclerview.widget.RecyclerView
import java.text.FieldPosition

class recyclerViewItemClickListener(context: Context,recyclerView: RecyclerView, private var mListener:OnItemClickListener?):RecyclerView.OnItemTouchListener {

    private var mGestureDetector:GestureDetector

    interface OnItemClickListener{
        fun onItemClick(view: View,position: Int)
        fun onItemLongClick(view: View?,position: Int)
    }
    init {
        mGestureDetector= GestureDetector(context,object :GestureDetector.SimpleOnGestureListener(){
            override fun onSingleTapUp(e: MotionEvent): Boolean {
                return true
            }

            override fun onLongPress(e: MotionEvent) {
                val childView=recyclerView.findChildViewUnder(e.x,e.y)
                if (childView != null && mListener != null){
                    mListener!!.onItemLongClick(childView,recyclerView.getChildAdapterPosition(childView))
                }
            }
        })
    }

    override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {
        val childView=rv.findChildViewUnder(e.x, e.y)
        if (childView != null && mListener != null && mGestureDetector.onTouchEvent(e)) {
            mListener!!.onItemClick(childView, rv.getChildAdapterPosition(childView))
        }
        return false
    }

    override fun onTouchEvent(rv: RecyclerView, e: MotionEvent) {
        TODO("Not yet implemented")
    }

    override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {
        TODO("Not yet implemented")
    }


}