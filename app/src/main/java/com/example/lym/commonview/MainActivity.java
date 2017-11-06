package com.example.lym.commonview;

import android.content.Context;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * 1.设置点击事件 弹出软键盘
 * 2.监听Activity dispatchTouchEvent 隐藏软键盘
 * 3.设置getViewTreeObserver().addOnGlobalLayoutListener 监听布局变化 根据已经选择项底部坐标
 * 和 输入框顶部坐标差值 滑动RecyclerView 使item显示内容在输入框之上
 * 4. 还可以重写最外层布局的onSizeChanged 来获取点击软键盘上隐藏软键盘键后的操作
 */
public class MainActivity extends AppCompatActivity {

    RecyclerView mRecyclerView;
    TextView mTvAnswer;
    LinearLayout mLltReply;
    View mSelectItemView;

    ArrayList<String> mList;


    private InputMethodManager iManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        iManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        mRecyclerView = findViewById(R.id.recycleview);
        mTvAnswer = findViewById(R.id.answer_text);
        mLltReply = findViewById(R.id.reply);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        initData();
        mRecyclerView.setAdapter(new TextAdapter());

        initListener();
    }

    private void initListener() {
        mRecyclerView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                //软键盘已经弹出
                if(mSelectItemView!=null){
                    //获取输入框最顶部相对于最外层布局top 坐标
                    int top=mLltReply.getTop();
                    //选择Item底部相对于RecyclerView bottom坐标
                    int bottom=mSelectItemView.getBottom();
                    //计算item需要在Y轴上移动多少距离 到达输入框顶部
                    int distance=bottom-top;
                    if(Math.abs(distance)>0){
                        mRecyclerView.smoothScrollBy(0,distance);
                    }

                }
            }
        });

    }

    private void initData() {
        mList = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            String str;
            if (i % 2 == 0) {
                str = "看见没有，如上图中id为content的内容就是整个View树的结构，所以对每个具体View对象的操作，其实就是个递归的实现";
            } else {
                str = "Figures out the measure spec for the root view in a window based on it's\n" +
                        "     * layout params";
            }
            mList.add(str);
        }
    }



    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        View view=getCurrentFocus();
        if(view!=null) {
            //监听触摸事件 如果有选择ITEM说明对话框已经打开 此时需要关闭软键盘
            if (mSelectItemView!=null) {
                hideKeyboard(view.getWindowToken());
                return true;
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    /**
     * 影藏软键盘
     *
     * @param iBinder
     */
    private void hideKeyboard(IBinder iBinder) {
        if (iBinder != null) {
            InputMethodManager manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            manager.hideSoftInputFromWindow(iBinder, InputMethodManager.HIDE_NOT_ALWAYS);
            mSelectItemView=null;
        }
    }

    class TextAdapter extends RecyclerView.Adapter<TextAdapter.TextHolder> {

        @Override
        public TextHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new TextHolder(LayoutInflater.from(MainActivity.this).inflate(R.layout.item_layout, parent, false));
        }

        @Override
        public void onBindViewHolder(final TextHolder holder, int position) {
            holder.textView.setText(mList.get(position));
            holder.textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //记录点击的Item
                    mSelectItemView=holder.itemView;
                    //设置焦点 弹出对话框
                    mTvAnswer.setFocusable(true);
                    mTvAnswer.requestFocus();
                    iManager.showSoftInput(mTvAnswer, InputMethodManager.HIDE_IMPLICIT_ONLY);
                }
            });
        }

        @Override
        public int getItemCount() {
            return mList.size();
        }

        class TextHolder extends RecyclerView.ViewHolder {
            TextView textView;

            public TextHolder(View itemView) {
                super(itemView);
                textView = itemView.findViewById(R.id.text);
            }
        }
    }
}
