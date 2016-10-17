package com.github.phoenix.widget;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.TextAppearanceSpan;
import android.util.AttributeSet;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.TextView;

import com.github.phoenix.R;
import com.github.phoenix.utils.BigDecimalUtil;

import java.math.BigDecimal;


/**
 * 自定义TextView 提供set/get方法的数字动画控件
 *
 * @author Phoenix
 * @date 2016-6-13 16:09
 */
public class NumberAnimationText extends TextView {
    /**
     * 复杂数据格式
     */
    public static final int NUMBER_STYLE_COMPLEX = 1;
    public static final int NUMBER_STYLE_DIVIDER_WITHOUT_UNIT = 2;
    public static final int NUMBER_STYLE_DIVIDER_WITH_UNIT = 3;
    public static final int NUMBER_STYLE_WITH_UNIT = 4;
    public static final int NUMBER_STYLE_WITHOUT_UNIT = 5;
    public static final int NUMBER_STYLE_DECIMAL_WITH_UNIT = 6;

    private float number;
    private int type;

    public NumberAnimationText(Context context) {
        this(context, null);
    }

    public NumberAnimationText(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public NumberAnimationText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /**
     * 开始执行动画
     *
     * @param number 要显示的数据
     * @param type  数字格式,单位标识量
     *              type = 1 表示【亿万单位格式】<br/>
     *              type = 2 表示【带分割逗号,无元标识符】<br/>
     *              type = 3 表示【带分割逗号,有元标识符】<br/>
     *              type = 4 表示【不带分割逗号,元格式】<br/>
     *              type = 5 表示【不带分割逗号,无元格式】<br/>
     *              type = 6 表示【保留两位小数，万元单位】<br/>
     *
     * @param duration  执行动画的时间
     */
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

    public float getNumber() {
        return number;
    }

    /**
     * 动态设置数字颜色和单位区分开
     *
     * @param tv  控件
     * @param str 显示字符串
     * @param y  亿
     * @param w  万
     * @param yuan  元
     */
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
}
