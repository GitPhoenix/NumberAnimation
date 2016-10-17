# NumberAnimation属性动画——数字动画
######Android提供了几种动画：View Animation 、Drawable Animation 、Property Animation。其中View Animation只支持简单的平移，旋转，缩放，不透明度；Drawable Animation也就是帧动画，通过图片的切换来达到动画的效果，有点像Gif格式的图片。有时候，这些都不能满足我们的需求，在Android3.0的时候系统为我们提供了一种全新的动画模式——属性动画(Property animation)。本文说说属性动画的具体实现，重点讲数字动画；涉及到的相关类及用法请自行Google。

######效果图：

![数字动画.gif](http://upload-images.jianshu.io/upload_images/3066970-1cc90b0cd1305edc.gif?imageMogr2/auto-orient/strip)
######原理：
属性动画要求动画作用的对象提供该属性的get和set方法，属性动画根据你传递的该熟悉的初始值和最终值，以动画的效果多次去调用set方法，每次传递给set方法的值都不一样，确切来说是随着时间的推移，所传递的值越来越接近最终值。总结一下，你对object的属性xxx做动画，如果想让动画生效，要同时满足两个条件：
* object必须要提供setXxx方法，如果动画的时候没有传递初始值，那么还要提供getXxx方法，因为系统要去拿xxx属性的初始值（如果这条不满足，程序直接Crash）。
* object的setXxx对属性xxx所做的改变必须能够通过某种方法反映出来，比如会带来ui的改变啥的（如果这条不满足，动画无效果但不会Crash）。

那如果对象没有此属性，而我们又要实现这样的效果怎么办呢？  就像数字动画，TextView没有为我们提供number属性，正在我愁眉苦脸时，Google给了我们三种解决方案。
######方案一：给你的对象加上get和set方法，如果你有权限的话
如果你有权限的话，加上get和set就搞定了，但是很多时候我们没权限去这么做，无法修改Android SDK内部的实现。这个方法最简单，但是往往是不可行的，那我们在此演示一下Android SDK内部本来就提供了set/get方法的如何去实现。

```
<LinearLayout
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:background="#ffffff"
    android:alpha="0.4" > //不透明度属性
```

一般情况只要对象有此属性就可以实现此属性的动画，比如LinearLayout的属性alpha，就可以实现不透明度动画，同样也可以用background来实现背景的动画。
ObjectAnimator常用方法有：ofFloat()，ofInt()，ofObject()，ofArgb()，ofPropertyValuesHolder()。
```
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
    //监听动画开始
	translationAnimatorY.addListener(new AnimatorListenerAdapter() {
		@Override
		public void onAnimationStart(Animator animation) {
			super.onAnimationStart(animation);
			llSecond.setVisibility(View.VISIBLE);
		}
	});
    //把以上动画效果组合起来
	AnimatorSet animatorSet = new AnimatorSet();
	animatorSet.playTogether(scaleAnimatorX, scaleAnimatorY,
			alphaAnimator,
			translationAnimatorX, translationAnimatorY,
			rotationAnimatorX, rotationAnimatorY);
    //开始动画过程
	animatorSet.start();
}
```
第一个参数表示：此动画的载体，第二个参数表示：要通过什么属性来表现效果

接下来用Button的width属性来讲解方案二和三。view中的layout_width形容的是本view和父容器的关系，比如button和包含它的LinearLayout；而view中的width是用来描述自己本身的，而view的尺寸是由其父控件来决定的，对应父布局的layout_width和layout_height。所以我们对width做属性动画是没有效果的。

######方案二：用一个类来包装原始对象，间接为其提供get和set方法

```
static class ViewWrapper {
	private Button btnTarget;

	public ViewWrapper(Button view) {
		this.btnTarget = view;
	}

  //在set方法中修改Button的width
	public void setWidth(int width) {
		btnTarget.getLayoutParams().width = width;
		btnTarget.requestLayout();
	}

  //如果没有初始值的话还需提供get方法，不然程序直接崩溃
	public int getWidth() {
		return btnTarget.getLayoutParams().width;
	}
}
```
包装类已经有了，那我们就来实现Button的width动画，Button在3秒的时间内由最初的宽变为原来的2倍。
```
ViewWrapper viewWrapper = new ViewWrapper(btnWidthAnimation);
ObjectAnimator widthAnimator = ObjectAnimator.ofInt(viewWrapper, "width",
		btnWidthAnimation.getWidth(), 2 * btnWidthAnimation.getWidth());
widthAnimator.setDuration(3000);
widthAnimator.start()
```
######方案三：采用ValueAnimator，监听动画过程，自己实现属性的改变
```
private void widthAnimation(final View target, final int start, final int end, int duration) {
	ValueAnimator valueAnimator = ValueAnimator.ofInt(1, 100);
	valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
		@Override
		public void onAnimationUpdate(ValueAnimator animation) {
			IntEvaluator intEvaluator = new IntEvaluator();
            //获取当前帧值
			int currentValue = (int) animation.getAnimatedValue();

			//Elapsed/interpolated fraction of the animation.
			float fraction = animation.getAnimatedFraction();

			target.getLayoutParams().width = intEvaluator.evaluate(fraction, start, end);
			target.requestLayout();
		}
	});
	valueAnimator.setDuration(duration).start();
}
```
比较方案二和三可以看出，用一个类来包装原始对象，间接为其提供get和set方法较为简单点。
######数字动画：
自定义TextView为其提供set/get方法，设置最终值，同时传入一个标识量用来区分设置数据格式以及单位，这样整个应用有多处用到数字动画的地方我都可以写在一个类中统一管理，开启动画，系统会以动画的效果多次去调用set方法改变现实效果，在此get方法不会被调用，因为我设置了初始值0。
```
public void animate(float number, int type, int duration) {
	this.type = type;
	//修改number属性，会调用setNumber方法
	ObjectAnimator numberAnimator = ObjectAnimator.ofFloat(this, "number", 0, number);
	numberAnimator.setDuration(duration);
	//加速器，从慢到快到再到慢
	numberAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
	numberAnimator.start();
}

public void setNumber(float number) {
	this.number = number;
	switch (type) {
		case NUMBER_STYLE_COMPLEX:
			float yi = number / 100000000;
			float wan = number % 100000000 / 10000;
			if (yi > 1) {
				setText(String.valueOf((int) yi) + "亿" + String.valueOf((int) wan) + "万元");
				setTextColor(this, getText().toString(), '亿', '万', '元');
			}else if(wan > 1) {
				setText(String.valueOf((int) wan) + "万元");
				setTextColor(this, getText().toString(), '亿', '万', '元');
			}else{
				setText(String.valueOf((int) number) + "元");
				setTextColor(this, getText().toString(), '亿', '万', '元');
			}
			break;

		//带分割逗号,无元标识符
		case NUMBER_STYLE_DIVIDER_WITHOUT_UNIT:
			setText(BigDecimalUtil.formatMoney(new BigDecimal(String.valueOf((int) number))));
			break;
		//带分割逗号,有元标识符
		case NUMBER_STYLE_DIVIDER_WITH_UNIT:
			setText(BigDecimalUtil.formatMoney(new BigDecimal(String.valueOf((int) number))) + "元");
			break;
		//不带分割逗号,元格式
		case NUMBER_STYLE_WITH_UNIT:
			setText(String.valueOf((int) number) + "元");
			break;
		//不带分割逗号,无元格式
		case NUMBER_STYLE_WITHOUT_UNIT:
			setText(String.valueOf((int) number));
			break;
		//保留两位小数，万元单位
		case NUMBER_STYLE_DECIMAL_WITH_UNIT:
			setText(String.format("%.2f", number / 10000) + "万元");
			break;

		default:
			break;
	}
}
```
形如“97亿5678万元”、“456,258,856元”、“2154.02”等格式的数据，我们是不是应该把单位和具体数字的样式区分开来呢!
```
public void setTextColor(TextView tv, String str, char y, char w, char yuan) {
	SpannableString spannableString = new SpannableString(str);
	if (TextUtils.isEmpty(str)) {
		return;
	}
	if(str.indexOf(y) != -1) {
		spannableString.setSpan(new TextAppearanceSpan(tv.getContext(), R.style.Number_animation_TextAppearance), 0, str.indexOf(y), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		spannableString.setSpan(new TextAppearanceSpan(tv.getContext(), R.style.Number_animation_TextAppearance), str.indexOf(y) + 1, str.indexOf(w), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		tv.setText(spannableString, TextView.BufferType.SPANNABLE);
		return;
	}

	if(str.indexOf(w) != -1) {
		spannableString.setSpan(new TextAppearanceSpan(tv.getContext(), R.style.Number_animation_TextAppearance), 0, str.indexOf(w), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		tv.setText(spannableString, TextView.BufferType.SPANNABLE);
		return;
	}

	if(str.indexOf(yuan) != -1) {
		spannableString.setSpan(new TextAppearanceSpan(tv.getContext(), R.style.Number_animation_TextAppearance), 0, str.indexOf(yuan), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		tv.setText(spannableString, TextView.BufferType.SPANNABLE);
		return;
	}
}
```
从数字动画的代码中我们可以看出，此处才用的是方案二实现动画，其实采用方案三也能实现同样的效果，自己可以尝试一下，这里就不贴代码了。
