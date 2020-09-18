package com.example.myanimation;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    //定义图片控件
    private ImageView ivPic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//
//        //获取到activity_main.xml文件中的图片控件
        ivPic = (ImageView) findViewById(R.id.ivPic);
    }


    /**
     * 淡入淡出动画方法
     *
     * @param v
     */
    public void alpha(View v) {
        // 创建透明度动画，第一个参数是开始的透明度，第二个参数是要转换到的透明度
        AlphaAnimation alphaAni = new AlphaAnimation(0.2f, 1);

        //设置动画执行的时间，单位是毫秒
        alphaAni.setDuration(1000);

        // 设置动画结束后停止在哪个状态（true表示动画完成后的状态）
        // alphaAni.setFillAfter(true);

        // true动画结束后回到开始状态
        // alphaAni.setFillBefore(true);

        // 设置动画重复次数
        // -1或者Animation.INFINITE表示无限重复，正数表示重复次数，0表示不重复只播放一次
        alphaAni.setRepeatCount(10);

        // 设置动画模式（Animation.REVERSE设置循环反转播放动画,Animation.RESTART每次都从头开始）
        alphaAni.setRepeatMode(Animation.REVERSE);

        // 启动动画
        ivPic.startAnimation(alphaAni);
    }


    /**
     * 缩放动画
     *
     * @param v
     */
    public void Scale(View v) {
        //参数1：x轴的初始值
        //参数2：x轴收缩后的值
        //参数3：y轴的初始值
        //参数4：y轴收缩后的值
        //参数5：确定x轴坐标的类型
        //参数6：x轴的值，0.5f表明是以自身这个控件的一半长度为x轴
        //参数7：确定y轴坐标的类型
        //参数8：y轴的值，0.5f表明是以自身这个控件的一半长度为x轴
        // Animation.RELATIVE_TO_SELF, 0.5f表示绕着自己的中心点进行动画
        ScaleAnimation scaleAni = new ScaleAnimation(0.2f, 3.0f, 0.2f, 3.0f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
                0.5f);

        //设置动画执行的时间，单位是毫秒
        scaleAni.setDuration(100);

        // 设置动画重复次数
        // -1或者Animation.INFINITE表示无限重复，正数表示重复次数，0表示不重复只播放一次
        scaleAni.setRepeatCount(10);

        // 设置动画模式（Animation.REVERSE设置循环反转播放动画,Animation.RESTART每次都从头开始）
        scaleAni.setRepeatMode(Animation.REVERSE);

        // 启动动画
        ivPic.startAnimation(scaleAni);
    }


    /**
     * 旋转动画
     *
     * @param v
     */
    public void Rotate(View v) {
        //参数1：从哪个旋转角度开始,0表示从0度开始
        //参数2：转到什么角度,360表示旋转360度
        //后4个参数用于设置围绕着旋转的圆的圆心在哪里
        //参数3：确定x轴坐标的类型，有ABSOLUT绝对坐标、RELATIVE_TO_SELF相对于自身坐标、RELATIVE_TO_PARENT相对于父控件的坐标
        // Animation.RELATIVE_TO_SELF, 0.5f表示绕着自己的中心点进行动画
        //参数4：x轴的值，0.5f表明是以自身这个控件的一半长度为x轴
        //参数5：确定y轴坐标的类型
        //参数6：y轴的值，0.5f表明是以自身这个控件的一半长度为x轴
        RotateAnimation rotateAni = new RotateAnimation(0, 360,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
                0.5f);
        //设置动画执行的时间，单位是毫秒
        rotateAni.setDuration(1000);
        // 设置动画重复次数
        // -1或者Animation.INFINITE表示无限重复，正数表示重复次数，0表示不重复只播放一次
        rotateAni.setRepeatCount(10);
        // 设置动画模式（Animation.REVERSE设置循环反转播放动画,Animation.RESTART每次都从头开始）
        rotateAni.setRepeatMode(Animation.REVERSE);

        // 启动动画
        ivPic.startAnimation(rotateAni);
    }


    /**
     * 动画平移
     *
     * @param v
     */
    public void Translate(View v) {
        /*
         * TranslateAnimation translateAni = new TranslateAnimation(fromXType,
         * fromXValue, toXType, toXValue, fromYType, fromYValue, toYType,
         * toYValue);
         */
        //参数1～2：x轴的开始位置
        //参数3～4：y轴的开始位置
        //参数5～6：x轴的结束位置
        //参数7～8：x轴的结束位置
//        TranslateAnimation translateAni = new TranslateAnimation(
//                Animation.RELATIVE_TO_PARENT, 0,
//                Animation.RELATIVE_TO_PARENT, 0.3f,
//                Animation.RELATIVE_TO_PARENT, 0,
//                Animation.RELATIVE_TO_PARENT, 0.3f);
//        TranslateAnimation translateAni = new TranslateAnimation(
//                Animation.RELATIVE_TO_PARENT, 0,
//                Animation.RELATIVE_TO_PARENT, 0.5f,
//                Animation.RELATIVE_TO_PARENT, 0,
//                Animation.RELATIVE_TO_PARENT, 0);
        //　fromXDelta:这个参数表示动画开始的点离当前View X坐标上的差值；
        //  toXDelta,这个参数表示动画结束的点离当前View X坐标上的差值；
        //　fromYDelta,这个参数表示动画开始的点离当前View Y坐标上的差值；
        //　toYDelta)这个参数表示动画开始的点离当前View Y坐标上的差值；
        TranslateAnimation translateAni = new TranslateAnimation(0, 200, 0, 0);

        //设置动画执行的时间，单位是毫秒
        translateAni.setDuration(1000);
        //使其可以填充效果从而不回到原地
        translateAni.setFillEnabled(true);
        //不回到起始位置
        translateAni.setFillAfter(true);
        // 设置动画重复次数
        // -1或者Animation.INFINITE表示无限重复，正数表示重复次数，0表示不重复只播放一次
        translateAni.setRepeatCount(0);
        // 设置动画模式（Animation.REVERSE设置循环反转播放动画,Animation.RESTART每次都从头开始）
        translateAni.setRepeatMode(Animation.REVERSE);


//        ScaleAnimation scaleAni = new ScaleAnimation(
//                0.2f, 3.0f,
//                0.2f, 3.0f,
//                Animation.RELATIVE_TO_SELF, 0.5f,
//                Animation.RELATIVE_TO_SELF, 0.5f);
        //float fromX 动画起始时 X坐标上的伸缩尺寸
        //float toX 动画结束时 X坐标上的伸缩尺寸

        //float fromY 动画起始时Y坐标上的伸缩尺寸
        //float toY 动画结束时Y坐标上的伸缩尺寸

        //int pivotXType 动画在X轴相对于物件位置类型
        //float pivotXValue 动画相对于物件的X坐标的开始位置

        //int pivotYType 动画在Y轴相对于物件位置类型
        //float pivotYValue 动画相对于物件的Y坐标的开始位置
        //(float fromX, float toX,
        // float fromY, float toY,
        // int pivotXType, float pivotXValue,
        // int pivotYType, float pivotYValue)
//        ScaleAnimation scaleAni = new ScaleAnimation(
//                Animation.RELATIVE_TO_SELF, 0.5f,
//                Animation.RELATIVE_TO_SELF, 0.5f,
//                Animation.RELATIVE_TO_SELF, Animation.RELATIVE_TO_SELF,
//                Animation.RELATIVE_TO_SELF, Animation.RELATIVE_TO_SELF);

        ScaleAnimation scaleAni = new ScaleAnimation(
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, Animation.RELATIVE_TO_SELF,
                Animation.RELATIVE_TO_SELF, 0);

        //设置动画执行的时间，单位是毫秒
        scaleAni.setDuration(1000);
        //使其可以填充效果从而不回到原地
        scaleAni.setFillEnabled(true);
        //不回到起始位置
        scaleAni.setFillAfter(true);
        // 设置动画重复次数
        // -1或者Animation.INFINITE表示无限重复，正数表示重复次数，0表示不重复只播放一次
        scaleAni.setRepeatCount(0);
        // 设置动画模式（Animation.REVERSE设置循环反转播放动画,Animation.RESTART每次都从头开始）
        scaleAni.setRepeatMode(Animation.REVERSE);


        // 将缩放动画和旋转动画放到动画插值器
        AnimationSet animationSet = new AnimationSet(false);
        animationSet.addAnimation(scaleAni);
//        animationSet.addAnimation(translateAni);
        animationSet.setFillAfter(true);
        // 启动动画
        ivPic.startAnimation(animationSet);
    }


    public void Reverse(View v) {
        //　fromXDelta:这个参数表示动画开始的点离当前View X坐标上的差值；
        //  toXDelta,这个参数表示动画结束的点离当前View X坐标上的差值；
        //　fromYDelta,这个参数表示动画开始的点离当前View Y坐标上的差值；
        //　toYDelta)这个参数表示动画开始的点离当前View Y坐标上的差值；
        TranslateAnimation translateAni = new TranslateAnimation(200, 0, 0, 0);
        //设置动画执行的时间，单位是毫秒
        translateAni.setDuration(1000);
        //使其可以填充效果从而不回到原地
        translateAni.setFillEnabled(true);
        //不回到起始位置
        translateAni.setFillAfter(true);
        // 设置动画重复次数
        // -1或者Animation.INFINITE表示无限重复，正数表示重复次数，0表示不重复只播放一次
        translateAni.setRepeatCount(0);
        // 设置动画模式（Animation.REVERSE设置循环反转播放动画,Animation.RESTART每次都从头开始）
        translateAni.setRepeatMode(Animation.REVERSE);


        //float fromX 动画起始时 X坐标上的伸缩尺寸
        //float toX 动画结束时 X坐标上的伸缩尺寸

        //float fromY 动画起始时Y坐标上的伸缩尺寸
        //float toY 动画结束时Y坐标上的伸缩尺寸

        //int pivotXType 动画在X轴相对于物件位置类型
        //float pivotXValue 动画相对于物件的X坐标的开始位置

        //int pivotYType 动画在Y轴相对于物件位置类型
        //float pivotYValue 动画相对于物件的Y坐标的开始位置
//        ScaleAnimation scaleAni = new ScaleAnimation(
//                0.5f, Animation.RELATIVE_TO_SELF,
//                0.5f, Animation.RELATIVE_TO_SELF,
//                Animation.RELATIVE_TO_SELF, Animation.RELATIVE_TO_SELF,
//                Animation.RELATIVE_TO_SELF, Animation.RELATIVE_TO_SELF);

        ScaleAnimation scaleAni = new ScaleAnimation(
                0.5f, Animation.RELATIVE_TO_SELF,
                0.5f, Animation.RELATIVE_TO_SELF,
                Animation.RELATIVE_TO_SELF, Animation.RELATIVE_TO_SELF,
                0, Animation.RELATIVE_TO_SELF);
        //设置动画执行的时间，单位是毫秒
        scaleAni.setDuration(1000);
        //使其可以填充效果从而不回到原地
        scaleAni.setFillEnabled(true);
        //不回到起始位置
        scaleAni.setFillAfter(true);
        // 设置动画重复次数
        // -1或者Animation.INFINITE表示无限重复，正数表示重复次数，0表示不重复只播放一次
        scaleAni.setRepeatCount(0);
        // 设置动画模式（Animation.REVERSE设置循环反转播放动画,Animation.RESTART每次都从头开始）
        scaleAni.setRepeatMode(Animation.REVERSE);


        // 将缩放动画和旋转动画放到动画插值器
        AnimationSet animationSet = new AnimationSet(false);
        animationSet.addAnimation(scaleAni);
//        animationSet.addAnimation(translateAni);
        animationSet.setFillAfter(true);
        // 启动动画
        ivPic.startAnimation(animationSet);
    }


    /**
     * 组合动画（缩放和旋转组合）
     *
     * @param v
     */
    public void Combo(View v) {
        // Animation.RELATIVE_TO_SELF, 0.5f表示绕着自己的中心点进行动画
        ScaleAnimation scaleAni = new ScaleAnimation(0.2f, 3.0f, 0.2f, 3.0f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
                0.5f);

        //设置动画执行的时间，单位是毫秒
        scaleAni.setDuration(100);

        // 设置动画重复次数
        // -1或者Animation.INFINITE表示无限重复，正数表示重复次数，0表示不重复只播放一次
        scaleAni.setRepeatCount(-1);

        // 设置动画模式（Animation.REVERSE设置循环反转播放动画,Animation.RESTART每次都从头开始）
        scaleAni.setRepeatMode(Animation.REVERSE);

        // 0表示从0度开始,360表示旋转360度
        // Animation.RELATIVE_TO_SELF, 0.5f表示绕着自己的中心点进行动画
        RotateAnimation rotateAni = new RotateAnimation(0, 360,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
                0.5f);

        // 设置动画完成的时间（速度），单位是毫秒，1000是1秒内完成动画
        rotateAni.setDuration(1000);

        // 设置动画重复次数
        // -1或者Animation.INFINITE表示无限重复，正数表示重复次数，0表示不重复只播放一次
        rotateAni.setRepeatCount(-1);

        // 设置动画模式（Animation.REVERSE设置循环反转播放动画,Animation.RESTART每次都从头开始）
        rotateAni.setRepeatMode(Animation.REVERSE);

        // 将缩放动画和旋转动画放到动画插值器
        AnimationSet as = new AnimationSet(false);
        as.addAnimation(scaleAni);
        as.addAnimation(rotateAni);

        // 启动动画
        ivPic.startAnimation(as);
    }

    public void test(View v) {
        Intent intent = new Intent(this, TestAddActivity.class);
        startActivity(intent);
    }
}
