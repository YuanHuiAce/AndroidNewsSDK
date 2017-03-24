package com.news.yazhidao.widget;

import android.content.Context;
import android.text.ClipboardManager;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.widget.EditText;

import com.news.yazhidao.utils.Logger;
import com.news.yazhidao.utils.ToastUtil;

public class CommentEditText extends EditText {
    /**
     * 粘贴id
     */
    private static final int ID_PASTE = android.R.id.paste;
    private boolean isCopy;

    public CommentEditText(Context context) {
        super(context);
        // TODO Auto-generated constructor stub
    }

    public CommentEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub
    }

    public CommentEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        // TODO Auto-generated constructor stub
    }

    @SuppressWarnings("deprecation")
    @Override
    public boolean onTextContextMenuItem(int id) {
        if (id == ID_PASTE) {
            isCopy = true;
            ClipboardManager clip = (ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
            //改变粘贴版中的内容

            String clipText = clip.getText().toString();
            String oldc = getText().toString();
            oldc = oldc != null ? oldc : "";
            String content = getText().toString() + clipText;
            if (content.length() > 144) {
                clipText = clipText.substring(0, (144 - oldc.length()));
                ToastUtil.toastShort("您的字数超限，成功截取");
            }
            clip.setText(clipText);
        } else {
            isCopy = true;
        }
        return super.onTextContextMenuItem(id);
    }

    public boolean isCopy() {
        return isCopy;
    }

    @Override
    public boolean dispatchKeyEventPreIme(KeyEvent event) {
        if (mOnCancelDialogImp != null) {
            mOnCancelDialogImp.onCancelDialog();
        }
        return super.dispatchKeyEventPreIme(event);
    }

    private OnCancelDialogImp mOnCancelDialogImp;

    public void setOnCancelDialogImp(OnCancelDialogImp onCancelDialogImp) {
        mOnCancelDialogImp = onCancelDialogImp;
    }

    public interface OnCancelDialogImp {
        void onCancelDialog();
    }

}
