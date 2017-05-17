package andrewxiao.com.customflowlayout;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

public class FlowLayout extends ViewGroup {

    public FlowLayout(Context context) {
        super(context);
    }

    public FlowLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new MarginLayoutParams(getContext(), attrs);
    }

    /**
     * 参数int widthMeasureSpec, int heightMeasureSpec就是FlowLayout在Parent给出其指定的大小和模式限制下，测量自身的大小
     * @param widthMeasureSpec
     * @param heightMeasureSpec
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int widthMeasureMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthMeasureSize = MeasureSpec.getSize(widthMeasureSpec);

        int heightMeasureMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightMeasureSize = MeasureSpec.getSize(heightMeasureSpec);

        int paddingLeft = getPaddingLeft();
        int paddingRight = getPaddingRight();
        int paddingTop = getPaddingTop();
        int paddingBottom = getPaddingBottom();

        int childCount = getChildCount();
        int widthUsed = paddingLeft + paddingRight;
        int heightUsed = paddingTop + paddingBottom;

        int flowLayoutWidth = widthUsed;
        int totalChildUsedHeight = heightUsed;
        int maxLineHeight = 0;

        for(int i = 0; i < childCount; i++){
            View child = getChildAt(i);
            measureChild(child, widthMeasureSpec, heightMeasureSpec);

            MarginLayoutParams layoutParams = (MarginLayoutParams) child.getLayoutParams();
            int childMarginLeft = layoutParams.leftMargin;
            int childMarginRigth = layoutParams.rightMargin;
            int childMargintTop = layoutParams.topMargin;
            int childMarginBottom = layoutParams.bottomMargin;

            int childWidthUsed = child.getMeasuredWidth() + childMarginLeft + childMarginRigth;
            int childHeightUsed = child.getMeasuredHeight() + childMargintTop + childMarginBottom;

            if(widthUsed + childWidthUsed < widthMeasureSize){ //继续放
                widthUsed += childWidthUsed;
                maxLineHeight = (childHeightUsed > maxLineHeight ? childHeightUsed : maxLineHeight);
            }else{ //放不下，换行
                flowLayoutWidth = (widthUsed > flowLayoutWidth ? widthUsed : flowLayoutWidth);
                widthUsed = paddingLeft + paddingRight; // 初始化 widthUsed

                totalChildUsedHeight += maxLineHeight; // 计算
            }
        }


        setMeasuredDimension(widthMeasureMode == MeasureSpec.EXACTLY ? widthMeasureSize : flowLayoutWidth,
                heightMeasureMode == MeasureSpec.EXACTLY ? heightMeasureSize : totalChildUsedHeight);
    }



    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int paddingLeft = getPaddingLeft();
        int paddingRight = getPaddingRight();
        int paddingTop = getPaddingTop();

        int childCount = getChildCount();
        int widthUsed = paddingLeft + paddingRight;

        int totalChildUsedWidth = 0;
        int totalChildUsedHeight = 0;
        int maxLineHeight = 0;

        for(int i = 0; i < childCount; i++){
            View child = getChildAt(i);

            MarginLayoutParams layoutParams = (MarginLayoutParams) child.getLayoutParams();
            int childMarginLeft = layoutParams.leftMargin;
            int childMarginRigth = layoutParams.rightMargin;
            int childMargintTop = layoutParams.topMargin;
            int childMarginBottom = layoutParams.bottomMargin;

            int childWidthUsed = child.getMeasuredWidth() + childMarginLeft + childMarginRigth;
            int childHeightUsed = child.getMeasuredHeight() + childMargintTop + childMarginBottom;

            if(l + widthUsed + totalChildUsedWidth + childWidthUsed < r){
                int left = l + paddingLeft + childMarginLeft + totalChildUsedWidth;
                int top = t + paddingTop + childMargintTop + totalChildUsedHeight;

                Log.i("TAG", (i+1) + ", left = " + left + ", right = " + (left + child.getMeasuredWidth()));

                child.layout(left, top, left + child.getMeasuredWidth(), top + child.getMeasuredHeight());
                totalChildUsedWidth += childWidthUsed;

                maxLineHeight = (childHeightUsed > maxLineHeight ? childHeightUsed : maxLineHeight);
            }else{
                totalChildUsedWidth = childWidthUsed;
                totalChildUsedHeight += maxLineHeight; // 计算

                int left = l + paddingLeft + childMarginLeft;
                int top = t + paddingTop + childMargintTop + totalChildUsedHeight;

                Log.i("TAG", (i+1) + ", left = " + left + ", right = " + (left + child.getMeasuredWidth()));
                child.layout(left, top, left + child.getMeasuredWidth(), top + child.getMeasuredHeight());
            }
        }
    }
}
