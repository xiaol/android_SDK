package demo.com.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.news.yazhidao.utils.manager.SharedPreManager;

public class MainActivity extends AppCompatActivity {
    RelativeLayout newsLayout;
    MainView mainView;
    private TextView mFirstAndTop;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        SharedPreManager.mInstance(this).save("flag","text1","一天天的测试！");
        //activity 跳转
        TextView tv = (TextView) findViewById(R.id.tv);
        tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int size = SharedPreManager.mInstance(MainActivity.this).getInt("showflag", "textSize");
                if(size == MainView.FONTSIZE.TEXT_SIZE_BIG.getfontsize()){
                    mainView.setTextSize(MainView.FONTSIZE.TEXT_SIZE_NORMAL);
                }else{
                    mainView.setTextSize(MainView.FONTSIZE.TEXT_SIZE_BIG);
                }
            }
        });
        mFirstAndTop = (TextView)findViewById(R.id.mFirstAndTop);
        mFirstAndTop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainView.backFirstItemAndRefreshData();
            }
        });
        //添加View
        newsLayout = (RelativeLayout)findViewById(R.id.newsLayout);
        mainView = new MainView(this); //传入的activity是FragmentActivity
        mainView.setTextSize(MainView.FONTSIZE.TEXT_SIZE_NORMAL);
        newsLayout.addView(mainView.getNewsView());

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //设置频道的回调
        mainView.onActivityResult(requestCode, resultCode, data);
    }
    //梁帅: 点击返回如果不喜欢窗口是显示的，隐藏它；
    //如果是不显示的直接退出
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (mainView != null && mainView.closePopWindow()) {
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }
}
