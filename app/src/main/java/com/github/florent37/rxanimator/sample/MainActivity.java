package com.github.florent37.rxanimator.sample;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.github.florent37.rxanimator.RxAnimator;
import com.jakewharton.rxbinding2.view.RxView;

import io.reactivex.Observable;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final View view = findViewById(R.id.text);

        //run sequencially
        RxView.clicks(view)
                .flatMap(o ->
                        RxAnimator.ofFloat(view, "translationY", 0, 300).animationDuration(500)
                )
                .flatMap(o ->
                        RxAnimator.ofFloat(view, "translationX", 0, 300)
                );

        //run in parallel
        RxView.longClicks(view)
                .flatMap(animation ->
                        Observable.zip(
                                RxAnimator.ofFloat(view, "scaleX", 5),
                                RxAnimator.ofFloat(view, "scaleY", 5),
                                RxAnimator.ofFloat(view, "translationY", 300),
                                RxAnimator.ofFloat(view, "translationX", 200),
                                (a, a2, a3, a4) -> a
                        )
                )
                .flatMap(animation ->
                        Observable.zip(
                                RxAnimator.ofFloat(view, "scaleX", 1),
                                RxAnimator.ofFloat(view, "scaleY", 1),
                                RxAnimator.ofFloat(view, "translationX", 0),
                                RxAnimator.ofFloat(view, "translationY", 0),
                                (a, a2, a3, a4) -> a
                        )
                )
                .subscribe(animation -> {});
    }
}
