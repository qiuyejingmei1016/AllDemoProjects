package example.com.showdialog.utils;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import example.com.showdialog.R;

/**
 * @describe: 吐丝提示封装类
 * @author: yyh
 * @createTime: 2020/4/17 14:10
 * @className: ToastUtil
 */
public class ToastUtil {

    //显示文本+图片的Toast
    public static void showImageToast(Context context, String message) {
        View view = LayoutInflater.from(context).inflate(R.layout.view_toast, null);
        view.findViewById(R.id.image_view).setVisibility(View.VISIBLE);
        TextView text = view.findViewById(R.id.content_view);
        text.setText(message);    //要提示的文本
        Toast toast = new Toast(context);   //上下文
        toast.setGravity(Gravity.CENTER, 0, 0);   //位置居中
        toast.setView(view);   //把定义好的View布局设置到Toast里面
        toast.setDuration(Toast.LENGTH_SHORT);  //设置短暂提示
        toast.show();
    }

    //显示文本的Toast
    public static void showTextToast(Context context, String message) {
        View toastview = LayoutInflater.from(context).inflate(R.layout.view_toast, null);
        TextView text = toastview.findViewById(R.id.content_view);
        text.setText(message);
        Toast toast = new Toast(context);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(toastview);
        toast.show();
    }
}
