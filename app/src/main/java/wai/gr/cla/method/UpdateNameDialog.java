package wai.gr.cla.method;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.annotation.StyleRes;
import android.text.TextUtils;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import wai.gr.cla.R;

/**
 * Created by JX on 2017/5/22.
 * 修改昵称
 */

public class UpdateNameDialog extends Dialog {

    public interface OnPositiveButtonClickListener {
        void onClickListener(String content, int v);
    }

    public UpdateNameDialog(@NonNull Context context) {
        super(context);
    }

    public UpdateNameDialog(@NonNull Context context, @StyleRes int themeResId) {
        super(context, themeResId);
    }

    public static class Builder {
        private Context context;
        private EditText et_name;
        private String name;

        private UpdateNameDialog.OnPositiveButtonClickListener onPositiveButtonClickListener;

        public Builder setOnPositiveButtonClickListener(OnPositiveButtonClickListener onPositiveButtonClickListener) {
            this.onPositiveButtonClickListener = onPositiveButtonClickListener;
            return this;
        }

        public Builder(Context context) {
            this.context = context;
        }

        public Builder setName(String name) {
            this.name = name;
            return this;
        }

        public UpdateNameDialog create() {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            // instantiate the dialog with the custom Theme
            final UpdateNameDialog dialog = new UpdateNameDialog(context, R.style.Update_Dialog);
            View layout = inflater.inflate(R.layout.layout_updatenick_dialog, null);
            et_name = (EditText) layout.findViewById(R.id.et_name);
            dialog.addContentView(layout, new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            // set the confirm button
            if (onPositiveButtonClickListener != null) {
                ((TextView) layout.findViewById(R.id.positiveButton))
                        .setOnClickListener(new View.OnClickListener() {
                            public void onClick(View v) {
                                String content = et_name.getText().toString();
                                if (TextUtils.isEmpty(content)) {
                                    Toast.makeText(context, "昵称不能为空", Toast.LENGTH_SHORT).show();
                                    return;
                                }
                                dialog.dismiss();
                                onPositiveButtonClickListener.onClickListener(content,
                                        DialogInterface.BUTTON_POSITIVE);
                            }
                        });
            }
            ((TextView) layout.findViewById(R.id.negativeButton))
                    .setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });
            //当前昵称
            if (TextUtils.isEmpty(name)) {
                et_name.setHint("昵称");
            } else {
                et_name.setText(name);
            }
            dialog.setContentView(layout);

            Window dialogWindow = dialog.getWindow();
            WindowManager m = ((Activity) context).getWindowManager();
            Display d = m.getDefaultDisplay(); // 获取屏幕宽、高度
            WindowManager.LayoutParams p = dialogWindow.getAttributes(); // 获取对话框当前的参数值
//            p.height = (int) (d.getHeight() * 0.8); // 高度设置为屏幕的0.6，根据实际情况调整
            p.width = (int) (d.getWidth() * 0.8); // 宽度设置为屏幕的0.65，根据实际情况调整
            dialogWindow.setAttributes(p);

            return dialog;
        }

    }
}
