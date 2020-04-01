package com.bairuitech.anychat.recruitment.utils;

import android.content.Context;
import android.text.InputFilter;
import android.text.Spanned;
import android.widget.Toast;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @describe: 文件聊天输入框过滤器
 * @author: yyh
 * @createTime: 2019/8/28 11:42
 * @className: RecruitCustomInputFilter
 */
public class RecruitCustomInputFilter implements InputFilter {

    private Context mContext;

    //表情匹配
    private Pattern mEmojiPattern =
            Pattern.compile("[\ud83c\udc00-\ud83c\udfff]|[\ud83d\udc00-\ud83d\udfff]|[\u2600-\u27ff]",
                    Pattern.UNICODE_CASE | Pattern.CASE_INSENSITIVE);

    //特殊字符匹配
    private Pattern mInvalidPattern =
            Pattern.compile("[^a-zA-Z0-9\\u4E00-\\u9FA5_,.?!:;…~_\\-\"\"/@*+'()<>{}/[/]()<>{}\\[\\]=%&$|\\/♀♂#¥£¢€\"^` ，。？！：；……～“”、“（）”、（——）‘’＠‘·’＆＊＃《》￥《〈〉》〈＄〉［］￡［］｛｝｛｝￠【】【】％〖〗〖〗／〔〕〔〕＼『』『』＾「」「」｜﹁﹂｀．]");

    public RecruitCustomInputFilter(Context context) {
        this.mContext = context;
    }

    @Override
    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
        Matcher emojiMatcher = mEmojiPattern.matcher(source);
        Matcher invalidMatcher = mInvalidPattern.matcher(source);
        if (emojiMatcher.find()) {
            UIAction.makeToast(mContext, "不支持输入表情", Toast.LENGTH_LONG).show();
            return "";
        } else if (invalidMatcher.find()) {
            UIAction.makeToast(mContext, "不支持输入特殊字符", Toast.LENGTH_LONG).show();
            return "";
        }
        return null;
    }
}