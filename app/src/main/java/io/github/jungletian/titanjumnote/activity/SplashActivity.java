package io.github.jungletian.titanjumnote.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import io.github.jungletian.titanjumnote.R;

public class SplashActivity extends AppCompatActivity {

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_splash);

    new Handler().postDelayed(new Runnable() {
      // 为了减少代码使用匿名Handler创建一个延时的调用
      public void run() {
        Intent i = new Intent(SplashActivity.this, HomeActivity.class);
        //通过Intent打开最终真正的主界面Main这个Activity
        SplashActivity.this.startActivity(i);    //启动Main界面
        SplashActivity.this.finish();    //关闭自己这个开场屏
      }
    }, 1000);
  }
}
