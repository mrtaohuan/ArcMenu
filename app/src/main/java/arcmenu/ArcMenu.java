package arcmenu;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;

import static java.lang.Math.PI;
import static java.lang.Math.cos;
import static java.lang.Math.sin;

/**
 * Created by huan.tao on 2015/3/8.
 */
public class ArcMenu extends ViewGroup implements View.OnClickListener {

    private static final int STATE_OPEN = 1, STATE_CLOSE = 0;

    private int mCurrentState = STATE_CLOSE;
    private int mRadius = 250;

    private int[][] points;
    private RotateAnimation[] mainBtnRotateAnimations = new RotateAnimation[2];
    private ScaleAnimation[] itemScaleAnimations = new ScaleAnimation[2];
    private AnimationSet[][] animations;
    private View[] views;

    private View mMainButton;

    public ArcMenu(Context context) {
        this(context, null);
    }

    public ArcMenu(Context context, AttributeSet attrs) {
        this(context, attrs, -1);
    }

    public ArcMenu(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int count = getChildCount();
        for (int i = 0; i < count; i++) {
            measureChild(getChildAt(i), widthMeasureSpec, heightMeasureSpec);
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if (changed) {
            setOnClickListener(this);
            layoutMainButton();
            layoutItemButton();
            initAnimations(200);
        }
    }

    private void layoutMainButton() {
        mMainButton = getChildAt(0);
        if (mMainButton.getVisibility() != GONE) {
            mMainButton.setOnClickListener(this);
            int width = mMainButton.getMeasuredWidth(), height = mMainButton.getMeasuredHeight();
            int left = getMeasuredWidth() / 2 - width / 2;
            int top = getMeasuredHeight() - height - 20;

            mMainButton.layout(left, top, left + width, top + height);
        }
    }

    private void layoutItemButton() {
        int count = getChildCount();
        int width = getMeasuredWidth(), height = getMeasuredHeight();

        int left = width / 2, top = height - mMainButton.getMeasuredHeight() / 2;

        points = new int[count][2];
        views = new View[count];
        for (int i = 0; i < count + 1; i++) {
            if (i == 0 || i == count) continue;
            View child = getChildAt(i);
            child.setVisibility(GONE);
            child.setFocusable(false);

            int w = child.getMeasuredWidth() / 2, h = child.getMeasuredHeight() / 2;
            int x = (int) (left - mRadius * cos(PI / (count) * i)) - w;
            int y = (int) (top - mRadius * sin(PI / (count) * i)) - h;

            child.layout(x, y, x + child.getMeasuredWidth(), y + child.getMeasuredHeight());
            final int index = i;
            child.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onItemClickListener != null) {
                        onItemClickListener.onItemClick(v, index);
                    }
                }
            });

            points[i][0] = -x + left - w;
            points[i][1] = -y + top - h - 20;
            views[i] = child;
        }
    }

    private void initAnimations(int duration) {
        RotateAnimation rotateAnimIn = new RotateAnimation(0, 360f * 2,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotateAnimIn.setDuration(duration);
        rotateAnimIn.setFillAfter(true);
        rotateAnimIn.setStartOffset(100);

        RotateAnimation rotateAnimOut = new RotateAnimation(360f * 2, 0,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotateAnimOut.setDuration(duration);
        rotateAnimOut.setFillAfter(true);

        AlphaAnimation alphaAnimIn = new AlphaAnimation(0, 1f);
        alphaAnimIn.setDuration(duration);
        alphaAnimIn.setFillAfter(true);

        AlphaAnimation alphaAnimOut = new AlphaAnimation(1f, 0);
        alphaAnimOut.setDuration(duration);
        alphaAnimOut.setFillAfter(true);
        alphaAnimOut.setStartOffset(100);

        ScaleAnimation scaleAnimIn = new ScaleAnimation(0.0f, 1.4f, 0.0f, 1.4f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        scaleAnimIn.setDuration(200);
        itemScaleAnimations[0] = scaleAnimIn;

        ScaleAnimation scaleAnimOut = new ScaleAnimation(1.4f, 0f, 1.4f, 0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        scaleAnimIn.setDuration(200);
        itemScaleAnimations[1] = scaleAnimOut;

        RotateAnimation mainBtnRotateAnimIn = new RotateAnimation(0, 135f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        mainBtnRotateAnimIn.setDuration(duration);
        mainBtnRotateAnimIn.setFillAfter(true);
        mainBtnRotateAnimations[0] = mainBtnRotateAnimIn;

        RotateAnimation mainBtnRotateAnimOut = new RotateAnimation(135, 0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        mainBtnRotateAnimOut.setDuration(duration);
        mainBtnRotateAnimOut.setFillAfter(true);
        mainBtnRotateAnimations[1] = mainBtnRotateAnimOut;

        int count = getChildCount();
        animations = new AnimationSet[count][2];
        for (int i = 1; i < count; i++) {
            TranslateAnimation transAnimIn = new TranslateAnimation(points[i][0], 0, points[i][1], 0);
            transAnimIn.setDuration(duration);
            transAnimIn.setFillAfter(true);

            TranslateAnimation transAnimOut = new TranslateAnimation(0, points[i][0], 0, points[i][1]);
            transAnimOut.setDuration(duration);
            transAnimOut.setFillAfter(true);
            transAnimOut.setStartOffset(100);


            AnimationSet animationSetIn = new AnimationSet(true);
            animationSetIn.addAnimation(rotateAnimIn);
            animationSetIn.addAnimation(transAnimIn);
            animationSetIn.addAnimation(alphaAnimIn);
            animations[i][0] = animationSetIn;

            AnimationSet animationSetOut = new AnimationSet(true);
            animationSetOut.addAnimation(rotateAnimOut);
            animationSetOut.addAnimation(transAnimOut);
            animationSetOut.addAnimation(alphaAnimOut);
            animations[i][1] = animationSetOut;
        }

    }

    @Override
    public void onClick(View v) {
        if (v == mMainButton) {
            toggleMenu();
        } else if (v == this && mCurrentState == STATE_OPEN) {
            toggleMenu();
        }
    }

    private void toggleMenu() {
        final int status = mCurrentState;
        changeStatus();

        if (status == STATE_CLOSE) {
            //rotateMainButton(mMainButton, 0, 135f, duration);
            mMainButton.startAnimation(mainBtnRotateAnimations[0]);
        } else {
            //rotateMainButton(mMainButton, 135f, 0, duration);
            mMainButton.startAnimation(mainBtnRotateAnimations[1]);
        }

        for (int i = 1; i < views.length; i++) {
            final View child = views[i];
            child.setVisibility(VISIBLE);
            child.setFocusable(true);

            AnimationSet anim;
            if (status == STATE_CLOSE) {
                anim = animations[i][0];
            } else {
                anim = animations[i][1];
            }

            anim.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    if (mCurrentState == STATE_CLOSE) {
                        child.setVisibility(GONE);
                        child.setFocusable(false);
                    }
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                }
            });

            child.startAnimation(anim);
        }
    }

    private void changeStatus() {
        mCurrentState = mCurrentState == STATE_OPEN ? STATE_CLOSE : STATE_OPEN;
    }

    private OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public static interface OnItemClickListener {
        void onItemClick(View view, int position);
    }
}
