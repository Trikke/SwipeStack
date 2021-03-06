/*
 * Copyright (C) 2016 Frederik Schweiger
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package be.trikke.swipestack;

import android.animation.Animator;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.view.animation.OvershootInterpolator;
import be.trikke.swipestack.util.AnimationUtils;

public class SwipeHelper implements View.OnTouchListener {

	private final SwipeStack mSwipeStack;
	private View mObservedView;

	private boolean mListenForTouchEvents;
	private float mDownX;
	private float mDownY;
	private float mInitialX;
	private float mInitialY;
	private int mPointerId;

	private float mRotateDegrees = SwipeStack.DEFAULT_SWIPE_ROTATION;
	private float mOpacityEnd = SwipeStack.DEFAULT_SWIPE_OPACITY;
	private int mAnimationDuration = SwipeStack.DEFAULT_ANIMATION_DURATION;

	private GestureDetector gestureDetector;

	public SwipeHelper(SwipeStack swipeStack) {
		mSwipeStack = swipeStack;

		gestureDetector = new GestureDetector(mSwipeStack.getContext(), new GestureDetector.SimpleOnGestureListener() {
			@Override public boolean onSingleTapConfirmed(MotionEvent e) {
				mSwipeStack.onViewTapped();
				return super.onSingleTapConfirmed(e);
			}
		});
	}

	@Override public boolean onTouch(View v, MotionEvent event) {
		gestureDetector.onTouchEvent(event);
		switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				if (!mListenForTouchEvents || !mSwipeStack.isEnabled()) {
					return false;
				}
				mListenForTouchEvents = false;
				v.getParent().requestDisallowInterceptTouchEvent(true);
				mSwipeStack.onSwipeStart();
				mPointerId = (event.getAction() & MotionEvent.ACTION_POINTER_INDEX_MASK) >> MotionEvent.ACTION_POINTER_INDEX_SHIFT;
				mDownX = event.getX(mPointerId);
				mDownY = event.getY(mPointerId);
				return true;

			case MotionEvent.ACTION_MOVE:
				int pointerIndex = event.findPointerIndex(mPointerId);
				if (pointerIndex < 0) return false;

				float dx = event.getX(pointerIndex) - mDownX;
				float dy = event.getY(pointerIndex) - mDownY;

				float newX = mObservedView.getX() + dx;
				float newY = mObservedView.getY() + dy;

				mObservedView.setX(newX);
				mObservedView.setY(newY);

				float viewCenterHorizontal = newX + (mObservedView.getWidth() / 2) - (mSwipeStack.getWidth() / 2);
				float swipeProgress = Math.min(Math.max(viewCenterHorizontal / (mSwipeStack.getWidth()) * 2, -1), 1);
				mSwipeStack.onSwipeProgress(swipeProgress);

				if (mRotateDegrees > 0) {
					mObservedView.setRotation(newX / 60);
				}

				if (mOpacityEnd < 1f) {
					mObservedView.setAlpha(1 - Math.min(Math.abs(swipeProgress * 2), 1));
				}

				return true;

			case MotionEvent.ACTION_UP:
				mListenForTouchEvents = true;
				v.getParent().requestDisallowInterceptTouchEvent(false);
				checkViewPosition();

				return true;
		}

		return false;
	}

	private void checkViewPosition() {
		if (!mSwipeStack.isEnabled()) {
			mSwipeStack.onSwipeEnd(false);
			resetViewPosition();
			return;
		}

		float viewCenterHorizontal = mObservedView.getX() + (mObservedView.getWidth() / 2);

		if (viewCenterHorizontal < 0 && mSwipeStack.getAllowedSwipeDirections() != SwipeStack.SWIPE_DIRECTION_ONLY_RIGHT) {
			mSwipeStack.onSwipeEnd(true);
			swipeViewToLeft();
		} else if (viewCenterHorizontal > mSwipeStack.getWidth() && mSwipeStack.getAllowedSwipeDirections() != SwipeStack.SWIPE_DIRECTION_ONLY_LEFT) {
			mSwipeStack.onSwipeEnd(true);
			swipeViewToRight();
		} else {
			mSwipeStack.onSwipeEnd(false);
			resetViewPosition();
		}
	}

	public void resetTopViewToPosition() {
		resetViewPosition();
		mListenForTouchEvents = true;
	}

	private void resetViewPosition() {
		mObservedView.animate().setListener(null).cancel();
		mObservedView.clearAnimation();
		mObservedView.animate()
		             .x(mInitialX)
		             .y(mInitialY)
		             .rotation(0)
		             .alpha(1)
		             .setDuration(mAnimationDuration)
		             .setInterpolator(new OvershootInterpolator(1.4f))
		             .setListener(null);
	}

	void swipeViewToLeft() {
		if (!mListenForTouchEvents) return;
		mListenForTouchEvents = false;
		mObservedView.animate().setListener(null).cancel();
		mObservedView.clearAnimation();
		mObservedView.animate()
		             .x(-mSwipeStack.getWidth() + mObservedView.getX())
		             .rotation(-mRotateDegrees)
		             .alpha(0f)
		             .setDuration(mAnimationDuration)
		             .setInterpolator(new LinearInterpolator())
		             .setListener(new AnimationUtils.AnimationEndListener() {
			             private boolean ended;

			             @Override public void onAnimationEnd(Animator animation) {
				             if (ended) return;

				             ended = true;
				             mSwipeStack.onViewSwipedToLeft();
			             }
		             });
	}

	void swipeViewToRight() {
		if (!mListenForTouchEvents) return;
		mListenForTouchEvents = false;
		mObservedView.animate().setListener(null).cancel();
		mObservedView.clearAnimation();
		mObservedView.animate()
		             .x(mSwipeStack.getWidth() + mObservedView.getX())
		             .rotation(mRotateDegrees)
		             .alpha(0f)
		             .setDuration(mAnimationDuration)
		             .setInterpolator(new LinearInterpolator())
		             .setListener(new AnimationUtils.AnimationEndListener() {
			             private boolean ended;

			             @Override public void onAnimationEnd(Animator animation) {
				             if (ended) return;

				             ended = true;
				             mSwipeStack.onViewSwipedToRight();
			             }
		             });
	}

	public void registerObservedView(View view, float initialX, float initialY) {
		if (view == null) return;
		mObservedView = view;
		mObservedView.setOnTouchListener(this);
		mInitialX = initialX;
		mInitialY = initialY;
		mListenForTouchEvents = true;
	}

	public void unregisterObservedView() {
		if (mObservedView != null) {
			mObservedView.setOnTouchListener(null);
		}
		mObservedView = null;
		mListenForTouchEvents = false;
	}

	public void setAnimationDuration(int duration) {
		mAnimationDuration = duration;
	}

	public void setRotation(float rotation) {
		mRotateDegrees = rotation;
	}

	public void setOpacityEnd(float alpha) {
		mOpacityEnd = alpha;
	}

	public float getInitialX() {
		return mInitialX;
	}

	public float getInitialY() {
		return mInitialY;
	}
}
