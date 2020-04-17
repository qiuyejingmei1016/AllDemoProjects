package example.com.showdialog;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.customdialog.widget.ActionSheetDialog;
import com.customdialog.widget.AlertDialog;

import java.util.ArrayList;

import example.com.showdialog.utils.ToastUtil;

/**
 * package:example.com.showdialog
 * name:MainActivity.java
 * Write by Jimmy.li
 * Date:2016/4/24 16:39
 */
public class MainActivity extends Activity implements View.OnClickListener {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    private void initView() {
        findViewById(R.id.btn1).setOnClickListener(this);
        findViewById(R.id.btn2).setOnClickListener(this);
        findViewById(R.id.btn3).setOnClickListener(this);
        findViewById(R.id.btn4).setOnClickListener(this);
        findViewById(R.id.btn5).setOnClickListener(this);
        findViewById(R.id.btn6).setOnClickListener(this);
        findViewById(R.id.btn7).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn1:
                new ActionSheetDialog(MainActivity.this)
                        .builder()
                        .setTitle("清空消息列表后，聊天记录依然保留，确定要清空消息列表？")
                        .setCancelable(true)
                        .setCanceledOnTouchOutside(true)
                        .addSheetItem("清空消息列表", ActionSheetDialog.SheetItemColor.Red
                                , new ActionSheetDialog.OnSheetItemClickListener() {
                                    @Override
                                    public void onClick(int which, String content) {
                                        //填写事件
                                    }
                                }).show();
                break;
            case R.id.btn2:
                new ActionSheetDialog(MainActivity.this)
                        .builder()
                        .setCancelable(true)
                        .setCanceledOnTouchOutside(true)
                        .addSheetItem("发送给好友",
                                ActionSheetDialog.SheetItemColor.Blue,
                                new ActionSheetDialog.OnSheetItemClickListener() {
                                    @Override
                                    public void onClick(int which, String content) {
                                        //填写事件
                                    }
                                })
                        .addSheetItem("转载到空间相册",
                                ActionSheetDialog.SheetItemColor.Blue,
                                new ActionSheetDialog.OnSheetItemClickListener() {
                                    @Override
                                    public void onClick(int which, String content) {
                                        //填写事件
                                    }
                                })
                        .addSheetItem("上传到群相册",
                                ActionSheetDialog.SheetItemColor.Blue,
                                new ActionSheetDialog.OnSheetItemClickListener() {
                                    @Override
                                    public void onClick(int which, String content) {
                                        //填写事件
                                    }
                                })
                        .addSheetItem("保存到手机",
                                ActionSheetDialog.SheetItemColor.Blue,
                                new ActionSheetDialog.OnSheetItemClickListener() {
                                    @Override
                                    public void onClick(int which, String content) {
                                        //填写事件
                                    }
                                }).show();
                break;
            case R.id.btn3:
                ArrayList<String> items = new ArrayList<>();
                items.add("==9==");
                items.add("==8==");
                items.add("==7==");
                items.add("==6==");
                items.add("==5==");
                items.add("==4==");
                items.add("==3==");
                items.add("==2==");
                items.add("==1==");
                new ActionSheetDialog(MainActivity.this)
                        .builder()
                        .setTitle("好友列表")
                        .setCancelable(true)
                        .setCanceledOnTouchOutside(true)
                        .addSheetItem(items, ActionSheetDialog.SheetItemColor.Blue
                                , new ActionSheetDialog.OnSheetItemClickListener() {
                                    @Override
                                    public void onClick(int which, String content) {
                                        //填写事件
                                        Log.e("=========which ", which + "");
                                        Toast.makeText(MainActivity.this,
                                                content + "  " + which, Toast.LENGTH_SHORT).show();
                                    }
                                })
                        .addSheetItem("备注", ActionSheetDialog.SheetItemColor.Red
                                , new ActionSheetDialog.OnSheetItemClickListener() {
                                    @Override
                                    public void onClick(int which, String content) {
                                        //填写事件
                                        Log.e("=========备注 ", which + "");
                                        Toast.makeText(MainActivity.this,
                                                content + "  " + which, Toast.LENGTH_SHORT).show();
                                    }
                                }).show();
                break;
            case R.id.btn4:
                new AlertDialog(MainActivity.this)
                        .builder()
                        .setTitle("退出当前帐号")
                        .setMsg("再连续登陆天，就可变身为QQ达人。退出QQ可能会使你现有记录归零，确定退出？")
                        .setPositiveButton("确认退出", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                //填写事件
                            }
                        })
                        .setNegativeButton("取消", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                //填写事件
                            }
                        }).show();
                break;
            case R.id.btn5:
                new AlertDialog(MainActivity.this)
                        .builder()
                        .setTitle("错误信息")
                        .setMsg("你的手机sd卡出现问题，建议删除不需要的文件，否则收不到图片和视频等打文件")
                        .setPositiveButton("确定", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                //填写事件
                            }
                        }).show();
                break;

            case R.id.btn6:
                ToastUtil.showImageToast(this, "带图片的提示");
                break;
            case R.id.btn7:
                ToastUtil.showTextToast(this, "默认提示样式");
                break;
        }
    }
}
