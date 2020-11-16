package com.github.chrisbanes.photoview;

import android.view.MotionEvent;

public interface OnSwipeListener {
    void onDragStart();
    void onDragStop();
    void onDismissed();
    void onDrag(float startY , float y, float rawY, MotionEvent it);
    void onSingleTapUp();
}
