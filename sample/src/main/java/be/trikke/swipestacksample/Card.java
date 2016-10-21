package be.trikke.swipestacksample;

import android.content.Context;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.CardView;
import android.util.AttributeSet;
import android.view.ViewGroup;

/**
 * Hi, I'm a File Header. I won't tell you anything.
 */

public class Card extends CardView {

	public Card(Context context) {
		super(context);
		setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
		setRadius(context.getResources().getDimensionPixelSize(R.dimen.padding_normal));
		ViewCompat.setElevation(this, context.getResources().getDimensionPixelSize(R.dimen.padding_normal));
	}

	public Card(Context context, AttributeSet attrs) {
		super(context, attrs);
		setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
		setRadius(context.getResources().getDimensionPixelSize(R.dimen.padding_normal));
		ViewCompat.setElevation(this, context.getResources().getDimensionPixelSize(R.dimen.padding_normal));
	}

	public Card(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
		setRadius(context.getResources().getDimensionPixelSize(R.dimen.padding_normal));
		ViewCompat.setElevation(this, context.getResources().getDimensionPixelSize(R.dimen.padding_normal));
	}

	@Override public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int widthPixels = MeasureSpec.getSize(widthMeasureSpec);
		int widthMode = MeasureSpec.getMode(widthMeasureSpec);
		// dimension ratio is 1:1.1 ( so a card of 100px width has 110px height)
		int newHeightSpec = MeasureSpec.makeMeasureSpec(widthPixels + (widthPixels / 100 * 10), widthMode);
		super.onMeasure(widthMeasureSpec, newHeightSpec);
	}
}
