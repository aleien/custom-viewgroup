package ru.yandex.yamblz.ui.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import ru.yandex.yamblz.R;

/**
 * Created by user on 17.07.16.
 */

public class OnePassHorizontalLayout extends ViewGroup {

    private int mLeftWidth;
    private int mRightWidth;
    private final Rect mTmpContainerRect = new Rect();
    private final Rect mTmpChildRect = new Rect();
    boolean measuredOnce;

    private LayoutParams mLayoutParams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

    public OnePassHorizontalLayout(Context context) {
        super(context);
    }

    public OnePassHorizontalLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public OnePassHorizontalLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int count = getChildCount();

        mLeftWidth = 0;
        mRightWidth = 0;

        int maxHeight = 0;
        int maxWidth = 0;
        int childState = 0;

        int matchParentChildPostition = 0;

        for (int i = 0; i < count; i++) {
            if (measuredOnce) return;
            final View child = getChildAt(i);
            if (child.getVisibility() != GONE) {
                // measure child
                mLayoutParams.width = child.getLayoutParams().width;
                mLayoutParams.height = child.getLayoutParams().height;

                child.setLayoutParams(mLayoutParams);
                measureChildWithMargins(child, MeasureSpec.AT_MOST, 500,
                        heightMeasureSpec, maxHeight);

                int childWidth = 300;
                int childHeight = 200;

                if (maxHeight < childHeight) maxHeight = childHeight;

                maxWidth += childWidth;

            }
        }

        setMeasuredDimension(resolveSizeAndState(maxWidth, widthMeasureSpec, childState),
                resolveSizeAndState(maxHeight, heightMeasureSpec,
                        childState << MEASURED_HEIGHT_STATE_SHIFT));
        measuredOnce = true;


    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        final int count = getChildCount();

        int leftPos = getPaddingLeft();
        int rightPos = right - left - getPaddingRight();

        int childLeft = 0;

        for (int i = 0; i < count; i++) {
            final View child = getChildAt(i);
            if (child.getVisibility() != GONE) {
                // get child layout params
                final LayoutParams lp =
                        (LayoutParams) child.getLayoutParams();


                // get child measured width /height
                final int childWidth = 300;
                final int childHeight = 200;

                // find some prerequsites


                //call child.layout
                child.layout(childLeft, top,
                        childWidth, childHeight);
                childLeft += childWidth;

            }
        }
    }

    public static class LayoutParams extends MarginLayoutParams {
        /**
         * The gravity to apply with the View to which these layout parameters
         * are associated.
         */
        public int gravity = Gravity.TOP | Gravity.START;

        public static int POSITION_MIDDLE = 0;
        public static int POSITION_LEFT = 1;
        public static int POSITION_RIGHT = 2;

        public int position = POSITION_MIDDLE;

        public LayoutParams(Context c, AttributeSet attrs) {
            super(c, attrs);
        }

        public LayoutParams(int width, int height) {
            super(width, height);
        }

        public LayoutParams(ViewGroup.LayoutParams source) {
            super(source);
        }

    }
}
