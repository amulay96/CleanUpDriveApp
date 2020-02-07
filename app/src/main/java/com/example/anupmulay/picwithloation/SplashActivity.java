package com.example.anupmulay.picwithloation;

import android.content.Intent;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

public class SplashActivity extends AppCompatActivity {

    ImageView ivLogo;
    MediaPlayer mp;

    Animation animation1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        ivLogo= (ImageView) findViewById(R.id.ivLogo);

        animation1= AnimationUtils.loadAnimation(getApplicationContext(),R.anim.alpha);
        ivLogo.startAnimation(animation1);

        mp=MediaPlayer.create(SplashActivity.this,R.raw.modiji);

        mp.start();

        new Thread(new Runnable() {
            @Override
            public void run() {

                try {
                    Thread.sleep(7000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                Intent intent=new Intent(getApplicationContext(),MainActivity.class);
                startActivity(intent);
                finish();
            }
        }).start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mp.pause();
    }
}
