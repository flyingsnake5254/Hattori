package com.example.hattori;

import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import androidx.appcompat.widget.AppCompatEditText;

public class CustomEditText extends AppCompatEditText {

    private boolean allowKeyboard = false;
    private OnCustomOptionClickListener customOptionClickListener;

    public interface OnCustomOptionClickListener {
        void onCustomOptionClick(int optionId);
    }

    public void setOnCustomOptionClickListener(OnCustomOptionClickListener listener) {
        this.customOptionClickListener = listener;
    }

    public CustomEditText(Context context) {
        super(context);
        init();
    }

    public CustomEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CustomEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            setShowSoftInputOnFocus(allowKeyboard);
        }

        this.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (!allowKeyboard) {
                    if (event.getAction() == MotionEvent.ACTION_UP) {
                        InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                        if (imm != null) {
                            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                        }
                        v.requestFocus();
                    }
                }
                return false;
            }
        });

        this.setCustomSelectionActionModeCallback(new ActionMode.Callback() {
            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                menu.clear();
                menu.add(0, 1, 0, "單字");
                menu.add(0, 2, 1, "KK音標");
                menu.add(0, 3, 2, "單字中文");
                menu.add(0, 4, 3, "例句");
                menu.add(0, 5, 4, "翻譯");

                post(() -> selectAll());

                return true;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                int optionId = item.getItemId();
                System.out.println(optionId);

                if (customOptionClickListener != null) {
                    customOptionClickListener.onCustomOptionClick(optionId);
                }
                mode.finish();
                return true;
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {
            }
        });
    }

    public void setAllowKeyboard(boolean allow) {
        this.allowKeyboard = allow;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            setShowSoftInputOnFocus(allow);
        }
    }
}
