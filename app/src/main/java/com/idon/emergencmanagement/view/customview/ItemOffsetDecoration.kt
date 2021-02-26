package com.shin.tmsuser.view.customview

import android.content.Context
import android.graphics.Rect
import android.view.View
import androidx.annotation.DimenRes
import androidx.annotation.NonNull
import androidx.recyclerview.widget.RecyclerView

class ItemOffsetDecoration : RecyclerView.ItemDecoration  {

    // amount to add to padding
    private val _itemOffset: Int

    constructor(itemOffset: Int) {
        _itemOffset = itemOffset
    }

    constructor(@NonNull context: Context, @DimenRes itemOffsetId: Int){
        _itemOffset = context.resources.getDimensionPixelSize(itemOffsetId)
    }

    /**
     * Applies padding to all sides of the [Rect], which is the container for the view
     */
    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        super.getItemOffsets(outRect, view, parent, state)
        outRect.set(_itemOffset, _itemOffset, _itemOffset, _itemOffset)
    }
}