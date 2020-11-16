package com.github.chrisbanes.photoview.sample;

import android.graphics.Matrix;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.chrisbanes.photoview.OnMatrixChangedListener;
import com.github.chrisbanes.photoview.OnPhotoTapListener;
import com.github.chrisbanes.photoview.OnSingleFlingListener;
import com.github.chrisbanes.photoview.OnSwipeListener;
import com.github.chrisbanes.photoview.PhotoView;

import java.util.Random;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

public class SimpleSwipeableSampleActivity extends AppCompatActivity {

    static final String PHOTO_TAP_TOAST_STRING = "Photo Tap! X: %.2f %% Y:%.2f %% ID: %d";
    static final String SCALE_TOAST_STRING = "Scaled to: %.2ff";
    static final String FLING_LOG_STRING = "Fling velocityX: %.2f, velocityY: %.2f";

    private PhotoView mPhotoView;
    private TextView mCurrMatrixTv;
    private View      background;

    private Toast mCurrentToast;

    private Matrix mCurrentDisplayMatrix = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simple_sample);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Simple Swipe Sample");
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        toolbar.inflateMenu(R.menu.main_menu);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.menu_swipeable_toggle:
                        mPhotoView.setSwipeable(!mPhotoView.isSwipeable());
                        item.setTitle(mPhotoView.isSwipeable() ? R.string.menu_swipe_disable : R.string.menu_swipe_enable);
                        return true;

                    case R.id.menu_zoom_toggle:
                        mPhotoView.setZoomable(!mPhotoView.isZoomable());
                        item.setTitle(mPhotoView.isZoomable() ? R.string.menu_zoom_disable : R.string.menu_zoom_enable);
                        return true;

                    case R.id.menu_scale_fit_center:
                        mPhotoView.setScaleType(ImageView.ScaleType.CENTER);
                        return true;

                    case R.id.menu_scale_fit_start:
                        mPhotoView.setScaleType(ImageView.ScaleType.FIT_START);
                        return true;

                    case R.id.menu_scale_fit_end:
                        mPhotoView.setScaleType(ImageView.ScaleType.FIT_END);
                        return true;

                    case R.id.menu_scale_fit_xy:
                        mPhotoView.setScaleType(ImageView.ScaleType.FIT_XY);
                        return true;

                    case R.id.menu_scale_scale_center:
                        mPhotoView.setScaleType(ImageView.ScaleType.CENTER);
                        return true;

                    case R.id.menu_scale_scale_center_crop:
                        mPhotoView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                        return true;

                    case R.id.menu_scale_scale_center_inside:
                        mPhotoView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                        return true;

                    case R.id.menu_scale_random_animate:
                    case R.id.menu_scale_random:
                        Random r = new Random();

                        float minScale = mPhotoView.getMinimumScale();
                        float maxScale = mPhotoView.getMaximumScale();
                        float randomScale = minScale + (r.nextFloat() * (maxScale - minScale));
                        mPhotoView.setScale(randomScale, item.getItemId() == R.id.menu_scale_random_animate);

                        showToast(String.format(SCALE_TOAST_STRING, randomScale));

                        return true;
                    case R.id.menu_matrix_restore:
                        if (mCurrentDisplayMatrix == null)
                            showToast("You need to capture display matrix first");
                        else
                            mPhotoView.setDisplayMatrix(mCurrentDisplayMatrix);
                        return true;
                    case R.id.menu_matrix_capture:
                        mCurrentDisplayMatrix = new Matrix();
                        mPhotoView.getDisplayMatrix(mCurrentDisplayMatrix);
                        return true;
                }
                return false;
            }
        });
        mPhotoView = findViewById(R.id.iv_photo);
        mCurrMatrixTv = findViewById(R.id.tv_current_matrix);
        background = findViewById(R.id.single_image_to_back);

        Drawable bitmap = ContextCompat.getDrawable(this, R.drawable.wallpaper);
        mPhotoView.setImageDrawable(bitmap);
        mPhotoView.setSwipeable(true);

        // Lets attach some listeners, not required though!
        mPhotoView.setOnMatrixChangeListener(new MatrixChangeListener());
        mPhotoView.setOnPhotoTapListener(new PhotoTapListener());
        mPhotoView.setOnSingleFlingListener(new SingleFlingListener());
        mPhotoView.getAttacher().setSwipeListener(new OnSwipeListener() {
            @Override
            public void onDragStart() {}

            @Override
            public void onDragStop() {
                background.setAlpha(1f);
            }

            @Override
            public void onDismissed() {
                supportFinishAfterTransition();
            }

            @Override
            public void onDrag(float startY, float y, float rawY, MotionEvent it) {
                float totalDrag = startY - rawY;
                float value = Math.min(1f, Math.abs(totalDrag) / 500f);
                background.setAlpha(1f-value);
            }

            @Override
            public void onSingleTapUp() {}
        });
    }

    private class PhotoTapListener implements OnPhotoTapListener {

        @Override
        public void onPhotoTap(ImageView view, float x, float y) {
            float xPercentage = x * 100f;
            float yPercentage = y * 100f;

            showToast(String.format(PHOTO_TAP_TOAST_STRING, xPercentage, yPercentage, view == null ? 0 : view.getId()));
        }
    }

    private void showToast(CharSequence text) {
        if (mCurrentToast != null) {
            mCurrentToast.cancel();
        }

        mCurrentToast = Toast.makeText(SimpleSwipeableSampleActivity.this, text, Toast.LENGTH_SHORT);
        mCurrentToast.show();
    }

    private class MatrixChangeListener implements OnMatrixChangedListener {

        @Override
        public void onMatrixChanged(RectF rect) {
            mCurrMatrixTv.setText(rect.toString());
        }
    }

    private class SingleFlingListener implements OnSingleFlingListener {

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            Log.d("PhotoView", String.format(FLING_LOG_STRING, velocityX, velocityY));
            return true;
        }
    }
}
