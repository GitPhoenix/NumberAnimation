package com.github.phoenix.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.IntEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.phoenix.R;
import com.github.phoenix.base.BaseActivity;
import com.github.phoenix.widget.NumberAnimationText;

public class MainActivity extends BaseActivity {
    private static final int NUMBER_ANIMATION_TIME = 1500;

    private NumberAnimationText numAnim01;
    private NumberAnimationText numAnim02;
    private NumberAnimationText numAnim03;
    private Button btnWidthAnimation;
    private LinearLayout llFirst;
    private LinearLayout llSecond;
    private TextView tvOpen, tvClose;

    private int firstViewHeight;
    private int secondViewHeight;

    @Override
    protected void initView() {
        numAnim01 = (NumberAnimationText) findViewById(R.id.anim_text_01);
        numAnim02 = (NumberAnimationText) findViewById(R.id.anim_text_02);
        numAnim03 = (NumberAnimationText) findViewById(R.id.anim_text_03);
        numAnim03 = (NumberAnimationText) findViewById(R.id.anim_text_03);

        btnWidthAnimation = (Button) findViewById(R.id.btn_width_animation);
        llFirst = (LinearLayout) findViewById(R.id.ll_property_animation_01);
        llSecond = (LinearLayout) findViewById(R.id.ll_property_animation_02);

        tvOpen = (TextView) findViewById(R.id.tv_open);
        tvClose = (TextView) findViewById(R.id.tv_close);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void setSubView() {
        float a1 = 9748695570L;
        float a2 = 358694837L;
        float a3 = 218656L;

        //把数据传递到控件，并设置数据显示格式
        numAnim01.animate(a1, NumberAnimationText.NUMBER_STYLE_COMPLEX, NUMBER_ANIMATION_TIME);
        numAnim02.animate(a2, NumberAnimationText.NUMBER_STYLE_COMPLEX, NUMBER_ANIMATION_TIME);
        numAnim03.animate(a3, NumberAnimationText.NUMBER_STYLE_COMPLEX, NUMBER_ANIMATION_TIME);

        llFirst.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                firstViewHeight = llFirst.getHeight();
                llFirst.getViewTreeObserver().removeOnPreDrawListener(this);

                return true;
            }
        });

        llSecond.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                secondViewHeight = llSecond.getHeight();
                llSecond.getViewTreeObserver().removeOnPreDrawListener(this);

                return true;
            }
        });

        //同上面效果一样，只是要求minSDKVersion > 16
        /*llFirst.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
	            @Override
	            public void onGlobalLayout() {

	            	fHeight = firstView.getHeight();
	            	firstView.getViewTreeObserver()
	                        .removeOnGlobalLayoutListener(this);
	            }
		});

		llSecond.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
	            @Override
	            public void onGlobalLayout() {

	            	sHeight = secondView.getHeight();
	            	secondView.getViewTreeObserver()
	                        .removeOnGlobalLayoutListener(this);
	            }
		});*/
    }

    @Override
    protected void initEvent() {
        btnWidthAnimation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*ViewWrapper viewWrapper = new ViewWrapper(btnWidthAnimation);
                ObjectAnimator widthAnimator = ObjectAnimator.ofInt(viewWrapper, "width",
                        btnWidthAnimation.getWidth(), 2 * btnWidthAnimation.getWidth());
                widthAnimator.setDuration(3000);
                widthAnimator.start();*/

                //两种实现方式效果一样
                widthAnimation(btnWidthAnimation, btnWidthAnimation.getWidth(), 1000, 3000);
            }
        });

        tvOpen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayAnimation();
            }
        });

        tvClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                close();
            }
        });
    }

    /**
     * 用一个类专门包装View，间接为其属性提供get/set方法
     */
    static class ViewWrapper {
        private Button btnTarget;

        public ViewWrapper(Button view) {
            this.btnTarget = view;
        }

        public void setWidth(int width) {
            btnTarget.getLayoutParams().width = width;
            btnTarget.requestLayout();
        }

        public int getWidth() {
            return btnTarget.getLayoutParams().width;
        }

    }

    /**
     * 采用ValueAnimator，监听动画过程，自己实现属性的改变
     *
     * @param target   Target View Object
     * @param start    The start value
     * @param end      The end value
     * @param duration The time of the animation.
     */
    private void widthAnimation(final View target, final int start, final int end, int duration) {
        ValueAnimator valueAnimator = ValueAnimator.ofInt(1, 100);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                //持有一个IntEvaluator对象，方便下面估值的时候使用
                IntEvaluator intEvaluator = new IntEvaluator();
                //获取当前帧值
                int currentValue = (int) animation.getAnimatedValue();

                //Elapsed/interpolated fraction of the animation.
                float fraction = animation.getAnimatedFraction();

                //直接调用整型估值器通过比例计算出宽度
                target.getLayoutParams().width = intEvaluator.evaluate(fraction, start, end);
                target.requestLayout();
            }
        });
        valueAnimator.setDuration(duration).start();
    }

    /**
     * 弹出界面
     */
    private void displayAnimation() {
        ObjectAnimator scaleAnimatorX = ObjectAnimator.ofFloat(llFirst, "scaleX", 1.0f, 0.8f);
        scaleAnimatorX.setDuration(350);
        ObjectAnimator scaleAnimatorY = ObjectAnimator.ofFloat(llFirst, "scaleY", 1.0f, 0.8f);
        scaleAnimatorY.setDuration(350);
        ObjectAnimator alphaAnimator = ObjectAnimator.ofFloat(llFirst, "alpha", 1.0f, 0.5f);
        alphaAnimator.setDuration(350);
        ObjectAnimator rotationAnimatorX = ObjectAnimator.ofFloat(llFirst, "rotationX", 0f, 10f);
        rotationAnimatorX.setDuration(200);
        ObjectAnimator rotationAnimatorY = ObjectAnimator.ofFloat(llFirst, "rotationX", 10f, 0f);
        rotationAnimatorY.setDuration(150);
        rotationAnimatorY.setStartDelay(200);
        ObjectAnimator translationAnimatorX = ObjectAnimator.ofFloat(llFirst, "translationY", 0, -0.1f * firstViewHeight);
        translationAnimatorX.setDuration(350);
        ObjectAnimator translationAnimatorY = ObjectAnimator.ofFloat(llSecond, "translationY", secondViewHeight, 0);
        translationAnimatorY.setDuration(350);

        translationAnimatorY.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                llSecond.setVisibility(View.VISIBLE);
            }
        });

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(scaleAnimatorX, scaleAnimatorY,
                alphaAnimator,
                translationAnimatorX, translationAnimatorY,
                rotationAnimatorX, rotationAnimatorY);
        animatorSet.start();
    }

    /**
     * 关闭界面
     */
    private void close() {
        ObjectAnimator scaleAnimatorX = ObjectAnimator.ofFloat(llFirst, "scaleX", 0.8f, 1.0f);
        scaleAnimatorX.setDuration(350);
        ObjectAnimator scaleAnimatorY = ObjectAnimator.ofFloat(llFirst, "scaleY", 0.8f, 1.0f);
        scaleAnimatorY.setDuration(350);
        ObjectAnimator alphaAnimator = ObjectAnimator.ofFloat(llFirst, "alpha", 0.5f, 1.0f);
        alphaAnimator.setDuration(350);
        ObjectAnimator rotationAnimatorX = ObjectAnimator.ofFloat(llFirst, "rotationX", 0f, 10f);
        rotationAnimatorX.setDuration(200);
        ObjectAnimator rotationAnimatorY = ObjectAnimator.ofFloat(llFirst, "rotationX", 10f, 0f);
        rotationAnimatorY.setDuration(150);
        rotationAnimatorY.setStartDelay(200);
        ObjectAnimator translationAnimatorX = ObjectAnimator.ofFloat(llFirst, "translationY", -0.1f * firstViewHeight, 0);
        translationAnimatorX.setDuration(350);
        ObjectAnimator translationAnimatorY = ObjectAnimator.ofFloat(llSecond, "translationY", 0, secondViewHeight);
        translationAnimatorY.setDuration(350);

        translationAnimatorY.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationStart(animation);
                llSecond.clearAnimation();
                llSecond.setVisibility(View.INVISIBLE);
            }
        });

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(scaleAnimatorX, scaleAnimatorY,
                alphaAnimator,
                translationAnimatorX, translationAnimatorY,
                rotationAnimatorX, rotationAnimatorY);
        animatorSet.start();
    }
}
