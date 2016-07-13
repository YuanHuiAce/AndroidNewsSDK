package com.news.yazhidao.net;

/**
 * Created by fengjigang on 15/1/6.
 */
public class TextUtils {
    public static boolean isValidate(String text){
        if(text!=null&&!"".equals(text.trim())){
            return true;
        }
            return false;
    }
}
