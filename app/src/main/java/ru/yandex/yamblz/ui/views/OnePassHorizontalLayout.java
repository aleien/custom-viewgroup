package ru.yandex.yamblz.ui.views;

import android.content.Context;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;

/**
 * Created by user on 17.07.16.
 */

public class OnePassHorizontalLayout extends ViewGroup {

    private int mLeftWidth;
    private int mRightWidth;
    private final Rect mTmpContainerRect = new Rect();
    private final Rect mTmpChildRect = new Rect();
    private int matchParentPosition = -1;
    private int remainingSpace;

    private LayoutParams mLayoutParams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    private int deviceWidth;

    public OnePassHorizontalLayout(Context context) {
        super(context);
        init();
    }

    public OnePassHorizontalLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }


    @Override
    protected MarginLayoutParams generateLayoutParams(LayoutParams p) {
        return new MarginLayoutParams(super.generateLayoutParams(p));
    }

    @Override
    protected MarginLayoutParams generateDefaultLayoutParams() {
        return new MarginLayoutParams(super.generateDefaultLayoutParams());
    }

    private void init() {
        final Display display = ((WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        Point deviceDisplay = new Point();
        display.getSize(deviceDisplay);
        deviceWidth = deviceDisplay.x;

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int count = getChildCount();

        int maxHeight = 0;
        int maxWidth = 0;
        int childState = 0;

        for (int i = 0; i < count; i++) {
            final View child = getChildAt(i);

            if (child.getVisibility() == GONE)
                continue;

            LayoutParams layoutParams = child.getLayoutParams();
            if (layoutParams.width == LayoutParams.MATCH_PARENT) {
//                if (matchParentPosition != -1) throw new IllegalStateException("One pass horizontal layout was destined to MATCH_PARENT only one view! \nShame on you!");
                matchParentPosition = i;
            } else {
                measureChild(child, widthMeasureSpec, heightMeasureSpec);
                maxWidth += Math.max(maxWidth, child.getMeasuredWidth());
            }


            maxHeight = Math.max(maxHeight, child.getMeasuredHeight());
            childState = combineMeasuredStates(childState, child.getMeasuredState());

        }

        remainingSpace = deviceWidth - maxWidth;

        maxHeight = Math.max(maxHeight, getSuggestedMinimumHeight());
        maxWidth = Math.max(maxWidth, getSuggestedMinimumWidth());

        setMeasuredDimension(resolveSizeAndState(maxWidth, widthMeasureSpec, childState),
                resolveSizeAndState(maxHeight, heightMeasureSpec, childState << MEASURED_HEIGHT_STATE_SHIFT));
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        final int count = getChildCount();
        int curWidth, curHeight, curLeft, maxHeight;

        final int childLeft = this.getPaddingLeft();
        final int childTop = this.getPaddingTop();
        final int childRight = this.getMeasuredWidth() - this.getPaddingRight();
        final int childBottom = this.getMeasuredHeight() - this.getPaddingBottom();
        final int childWidth = childRight - childLeft;
        final int childHeight = childBottom - childTop;

        maxHeight = 0;
        curLeft = childLeft;

        for (int i = 0; i < count; i++) {
            View child = getChildAt(i);

            if (child.getVisibility() == GONE)
                return;

            LayoutParams layoutParams = child.getLayoutParams();

            if (layoutParams.width == LayoutParams.MATCH_PARENT) {
                child.measure(MeasureSpec.makeMeasureSpec(childWidth, MeasureSpec.UNSPECIFIED), MeasureSpec.makeMeasureSpec(childHeight, MeasureSpec.UNSPECIFIED));
                curWidth = remainingSpace;
                curHeight = child.getMeasuredHeight();
            } else {
                curWidth = child.getMeasuredWidth();
                curHeight = child.getMeasuredHeight();
            }

            child.layout(curLeft, top, curLeft + curWidth, top + curHeight);

            if (maxHeight < curHeight)
                maxHeight = curHeight;
            curLeft += curWidth;
        }
    }

}
