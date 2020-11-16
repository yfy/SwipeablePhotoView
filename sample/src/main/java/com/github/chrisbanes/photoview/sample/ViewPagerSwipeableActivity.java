/*
 Copyright 2011, 2012 Chris Banes.

 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

 http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
 */
package com.github.chrisbanes.photoview.sample;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;

import com.github.chrisbanes.photoview.OnSwipeListener;
import com.github.chrisbanes.photoview.PhotoView;

import androidx.annotation.MainThread;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

public class ViewPagerSwipeableActivity extends AppCompatActivity  {
    SwipeUpListener swipeUpListener;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.TransparentSwipeUpTheme);
        setContentView(R.layout.activity_view_pager);
//        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(ContextCompat.getColor(this, R.color.gray95_transparent50)));
        //tWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.gray95_transparent50));
        //setStatusBarColor(ContextCompat.getColor(this, R.color.gray95_transparent80));

        ViewPager viewPager = findViewById(R.id.view_pager);
        View background = findViewById(R.id.single_image_to_back);
        viewPager.setAdapter(new SamplePagerAdapter());
        setSwipeUpListener(new SwipeUpListener() {
            @Override
            public void OnImageDismiss() {
                supportFinishAfterTransition();
            }

            @Override
            public void OnImageDrag(float startY, float y, float rawY, MotionEvent it) {
                float totalDrag = startY - rawY;
                float value = Math.min(1f, Math.abs(totalDrag) / 500f);
                background.setAlpha(1f-value);
            }

            @Override
            public void OnImageDragCancel() {
                background.setAlpha(1f);
            }
        });


    }

    static class SamplePagerAdapter extends PagerAdapter {
        private SwipeUpListener swipeUpListener;
        private static final int[] sDrawables = {R.drawable.wallpaper, R.drawable.wallpaper, R.drawable.wallpaper,
                R.drawable.wallpaper, R.drawable.wallpaper, R.drawable.wallpaper};

        @Override
        public int getCount() {
            return sDrawables.length;
        }

        @Override
        public View instantiateItem(ViewGroup container, int position) {

            PhotoView photoView = new PhotoView(container.getContext());
            photoView.setImageResource(sDrawables[position]);
            photoView.setSwipeable(true);
            photoView.getAttacher().setSwipeListener(new OnSwipeListener() {
                @Override
                public void onDragStart() {

                }

                @Override
                public void onDragStop() {
                    if (swipeUpListener != null) swipeUpListener.OnImageDragCancel();
                }

                @Override
                public void onDismissed() {
                    if (swipeUpListener != null) swipeUpListener.OnImageDismiss();
                }

                @Override
                public void onDrag(float startY, float y, float rawY, MotionEvent it) {
                    if (swipeUpListener != null) swipeUpListener.OnImageDrag(startY,y,rawY,it);
                }

                @Override
                public void onSingleTapUp() {

                }
            });

            // Now just add PhotoView to ViewPager and return it
            container.addView(photoView, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
            return photoView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }


    }

    @MainThread
    public void setSwipeUpListener (SwipeUpListener listener) {
        this.swipeUpListener = listener;
    }
    public interface SwipeUpListener {
        void OnImageDismiss();
        void OnImageDrag(float startY, float y, float rawY, MotionEvent it);
        void OnImageDragCancel();
    }
}

