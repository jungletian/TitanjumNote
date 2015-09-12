package io.github.jungletian.titanjumnote.view;

import android.content.Context;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

/**
 * Create by JungleTian on 15-9-13 01:20.
 * Email：tjsummery@gmail.com
 */
public class SwipeBackFrameLayout extends FrameLayout {

  private ViewDragHelper mDragHelper;

  private int mDividerWidth = 100;

  private int mLastdx;

  public SwipeBackFrameLayout(Context context) {

    super(context);

    initView();

  }

  public SwipeBackFrameLayout(Context context, AttributeSet attrs) {

    super(context, attrs);

    initView();

  }

  public SwipeBackFrameLayout(Context context, AttributeSet attrs, int defStyleAttr) {

    super(context, attrs, defStyleAttr);

    initView();

  }

  @Override

  protected void onLayout(boolean changed, int left, int top, int right, int bottom) {

    super.onLayout(changed, left, top, right, bottom);

    mDividerWidth = mDividerView.getWidth();

  }

  //Notice view 刚初始化的时候就会被调用一次

  @Override

  public void computeScroll() {

    super.computeScroll();

    if (mDragHelper.continueSettling(true)) {

      invalidate();

    }

  }

  private void initView() {

    // 1f代表灵敏度

    mDragHelper = ViewDragHelper.create(this, 1f, new ViewDragHelper.Callback() {

      @Override

      public void onViewDragStateChanged(int state) {

        super.onViewDragStateChanged(state);

        //滑动停止,并且到达了滑动的判断条件 则回调关闭

        if (mDragHelper.getViewDragState() == ViewDragHelper.STATE_IDLE && mcCallBack != null && mDividerWidth == mContentView.getLeft() && mLastdx > 0) {

          mcCallBack.onShouldFinish();

        }

      }

      @Override

      public void onEdgeTouched(int edgeFlags, int pointerId) {

        super.onEdgeTouched(edgeFlags, pointerId);

        // 触摸到边界的时候, 我们capture注mContentView

        mDragHelper.captureChildView(mContentView, pointerId);

      }

      @Override

      public int getViewHorizontalDragRange(View child) {

        return 1;

      }

      @Override

      public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {

        super.onViewPositionChanged(changedView, left, top, dx, dy);

        Log.d("ZTJ",
            "onViewPositionChanged() called with left = [" + left + "], top = [" + top + "], dx = ["
                + dx + "], dy = [" + dy + "]");

        //0.0 - 1.0

        //Notice 这边可以给个接口回调出去,就可以做各种炫酷的效果了

        float alpha = (float) (left * 1.0 / mDividerWidth);

        mDividerView.setAlpha(alpha);

      }

      @Override

      public int clampViewPositionHorizontal(View child, int left, int dx) {

        // 计算left 我们的目标范围是0-dividerwidth的宽度

        mLastdx = dx;

        int newLeft = Math.min(mDividerWidth, Math.max(left, 0));

        return newLeft;

      }

      @Override

      public void onViewReleased(View releasedChild, float xvel, float yvel) {

        //>0代表用户想关闭

        if (mLastdx > 0) {

          // 还不到关闭条件,我们让view滑动过去,再关闭

          if (mDividerWidth != releasedChild.getLeft()) {

            mDragHelper.settleCapturedViewAt(mDividerWidth, releasedChild.getTop());

            invalidate();

          } else {

            if (mcCallBack != null) {

              mcCallBack.onShouldFinish();

            }

          }

        } else {

          //用户不想关闭 ,则滑动到最左边

          if (mDividerWidth != 0) {

            mDragHelper.settleCapturedViewAt(0, releasedChild.getTop());

            invalidate();

          }

        }

      }

      @Override

      public boolean tryCaptureView(View child, int pointerId) {

        return false;

      }

    });

    // 滑动范围

    mDragHelper.setEdgeTrackingEnabled(ViewDragHelper.EDGE_LEFT);

  }

  // 右侧要出现的View

  private View mDividerView;

  // 内容

  private View mContentView;

  @Override

  protected void onFinishInflate() {

    super.onFinishInflate();

    mDividerView = getChildAt(0);

    mDividerView.setAlpha(0f);

    mContentView = getChildAt(1);

  }

  // 设置finish 回调

  public void setCallBack(CallBack mCallBack) {

    this.mcCallBack = mCallBack;

  }

  private CallBack mcCallBack;

  public interface CallBack {

    void onShouldFinish();

  }

  // 让ViewDragHelper来处理Touch事件

  @Override

  public boolean onInterceptTouchEvent(MotionEvent ev) {

    return mDragHelper.shouldInterceptTouchEvent(ev);

  }

  @Override

  public boolean onTouchEvent(MotionEvent event) {

    mDragHelper.processTouchEvent(event);

    return true;

  }

}

