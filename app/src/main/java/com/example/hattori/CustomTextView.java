package com.example.hattori;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
public class CustomTextView extends androidx.appcompat.widget.AppCompatTextView {

    private Drawable originalBackgroundColor;

    public CustomTextView(Context context) {
        super(context);
        init();
    }

    public CustomTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CustomTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        // 設置長按事件
        this.setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                // 保存原來的背景顏色
                originalBackgroundColor = getBackground();

                // 開啟自訂的 ActionMode
                startActionMode(new ActionMode.Callback() {
                    @Override
                    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                        // 設置背景顏色為灰色
                        setBackgroundColor(Color.parseColor("#dddddd"));
                        // 清除預設的選單項
                        menu.clear();

                        // 添加自訂的選單項
                        menu.add(0, 0, 0, "自訂選項1");
                        menu.add(0, 1, 1, "自訂選項2");

                        return true; // 返回 true 以顯示選單
                    }

                    @Override
                    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                        return false; // 如果不需要更新選單，可以返回 false
                    }

                    @Override
                    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                        switch (item.getItemId()) {
                            case 1:
                                // 處理自訂選項1的點擊事件
                                performCustomAction1();
                                mode.finish(); // 結束 ActionMode
                                return false;
                            case 2:
                                // 處理自訂選項2的點擊事件
                                performCustomAction2();
                                mode.finish(); // 結束 ActionMode
                                return false;
                            default:
                                return false;
                        }
                    }

                    @Override
                    public void onDestroyActionMode(ActionMode mode) {
                        // 當 ActionMode 結束時執行的操作
                        setBackground(originalBackgroundColor);
                    }
                });
                return true; // 返回 true 表示已處理長按事件
            }
        });
    }

    private void performCustomAction1() {
        // 自訂操作1的實現
    }

    private void performCustomAction2() {
        // 自訂操作2的實現
    }

}