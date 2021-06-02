package com.v.commonlib.ui;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import com.v.commonlib.R;


/**
 * Author:v
 * Time:2020/11/25
 */
public class BaseInfoDialog extends Dialog {

    private Context mContext;
    private TextView tvTitle;
    private TextView tvContent;
    private TextView btnConfirm;

    private String title;
    private String content;
    private String btnText;
    private boolean cancelOutside = false;
    private boolean cancelable = true;
    private boolean richContent = false;
    private DialogClickListener clickListener;
    private OnCancelListener cancelListener;


    public BaseInfoDialog(Builder builder) {
        super(builder.mContext, builder.mThemeId);
        this.mContext = builder.mContext;
        this.title = builder.title;
        this.content = builder.content;
        this.btnText = builder.btnText;
        this.cancelOutside = builder.cancelOutside;
        this.clickListener = builder.clickListener;
        this.cancelListener = builder.cancelListener;
        this.cancelable = builder.cancelable;
        this.richContent = builder.richContent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_base_info);
        initViews();
    }


    private void initViews() {
        tvTitle = findViewById(R.id.dbi_tv_title);
        tvContent = findViewById(R.id.dbi_tv_content);
        btnConfirm = findViewById(R.id.dbi_btn_confirm);

        if (!TextUtils.isEmpty(title)) {
            tvTitle.setText(title);
            tvContent.setTextColor(Color.parseColor("#888888"));
        } else {
            tvTitle.setVisibility(View.GONE);
            tvContent.setTextColor(Color.parseColor("#323232"));
        }

        if (!richContent) {
            tvContent.setText(content);
        } else {
            tvContent.setText(Html.fromHtml(content));
        }

        if (!TextUtils.isEmpty(btnText)) {
            btnConfirm.setText(btnText);
        }
        setClick();
        setCancelable(cancelable);
        setOnCancelListener(cancelListener);
        setCanceledOnTouchOutside(cancelOutside);
    }

    private void setClick() {
        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (clickListener != null) {
                    clickListener.onClick(BaseInfoDialog.this);
                }
                dismiss();
            }
        });
    }

    @Override
    public void show() {
        if (isActivityNotExist()) return;
        super.show();
    }

    private boolean isActivityNotExist() {
        if (mContext == null) {
            return true;
        }
        if (mContext instanceof Activity) {
            Activity activity = (Activity) mContext;
            if (activity.isFinishing() || activity.isDestroyed()) {
                return true;
            }
        }
        return false;
    }


    public static final class Builder {
        private final Context mContext;
        private final int mThemeId;
        private int gravity;

        private String title;
        private String content;
        private String btnText;
        private boolean cancelOutside = false;
        private boolean cancelable = false;
        private boolean richContent = false;
        private DialogClickListener clickListener;
        private OnCancelListener cancelListener;


        public Builder(Context context) {
            this(context, R.style.commonDialogTheme);
        }

        public Builder(Context context, int themeResId) {
            this.mContext = context;
            this.mThemeId = themeResId;
            gravity = Gravity.CENTER;
        }

        public Builder content(String contents) {
            this.content = contents;
            return this;
        }

        public Builder title(String title) {
            this.title = title;
            return this;
        }

        public Builder buttonText(String btnText) {
            this.btnText = btnText;
            return this;
        }

        public Builder gravity(int gravity) {
            this.gravity = gravity;
            return this;
        }

        public Builder cancelOutside(boolean cancelOutside) {
            this.cancelOutside = cancelOutside;
            return this;
        }

        public Builder cancelable(boolean cancelable) {
            this.cancelable = cancelable;
            return this;
        }

        public Builder richContent(boolean richContent) {
            this.richContent = richContent;
            return this;
        }

        public Builder buttonClickListener(DialogClickListener dialogClickListener) {
            this.clickListener = dialogClickListener;
            return this;
        }

        public Builder cancelListener(OnCancelListener cancelListener) {
            this.cancelListener = cancelListener;
            return this;
        }

        public BaseInfoDialog build() {
            return new BaseInfoDialog(this);
        }
    }


    public interface DialogClickListener {
        void onClick(Dialog dialog);
    }
}
