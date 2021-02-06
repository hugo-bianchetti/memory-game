package com.example.jogomemorizacao;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import androidx.core.content.ContextCompat;

import android.content.Context;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.ProgressBar;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private final int GAME_CARDS_COUNT = 6;

    private View _screen;
    private View _resultScreen;


    private List<Button> _cards;
    private ProgressBar _progressBar;
    private boolean _blockUserTouch;
    private int _attempts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        _resultScreen = findViewById(R.id.result_screen);
        _screen = findViewById(R.id.screen);
        _progressBar = (ProgressBar)findViewById(R.id.progress_bar);
        loadCards();

    }

    public void loadCards(){
        _cards = new ArrayList<>();
        for (int i = 1; i <= GAME_CARDS_COUNT; i++){
            _cards.add((Button)findViewById(getResources().getIdentifier(
                    "btn" + i,
                    "id",
                    getPackageName()
            )));
        }
    }

    public void onCardClick(View clickedCard) {
        if(_blockUserTouch) return;
        Button card = (Button)clickedCard;

        if(_cards.indexOf(card) == _attempts++){

            int clickedButtonColor = ContextCompat.getColor(
                    card.getContext(),
                    getResources().getIdentifier(
                            "btn_color_" + card.getText(),
                            "color", getPackageName())
            );

            card.setVisibility(View.INVISIBLE);
            _progressBar.setProgress(_attempts);
            _screen.setBackgroundColor(clickedButtonColor);

            if (_attempts == 6)
                showResultScreen();

        }else resetCards(clickedCard.getContext());
    }

    public void onRestartButtonClick(View v) {
        _blockUserTouch = true;
        _resultScreen.setVisibility(View.GONE);
        resetCards(v.getContext());
        Collections.shuffle(_cards);

        new Thread() {
            @Override
            public void run() {
                ScaleAnimation animation = new ScaleAnimation(0F, 1F, 0F, 1F,
                        Animation.RELATIVE_TO_SELF, 0.5f,
                        Animation.RELATIVE_TO_SELF, 0.5f
                );
                animation.setDuration(200);
                try {
                    for (Button card: _cards) {
                        Thread.sleep(500);
                        card.startAnimation(animation);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                _blockUserTouch = false;
            }
        }.start();
    }

    private void resetCards(Context context) {

        _progressBar.setProgress(0);
        _attempts = 0;
        _screen.setBackgroundColor(ContextCompat.getColor(context, R.color.white));

        for (Button card: _cards)
            card.setVisibility(View.VISIBLE);
    }

    private void showResultScreen() {
        _screen.setBackgroundColor(0xffffffff);
        _resultScreen.setBackgroundColor(0xffffffff);
        _resultScreen.setVisibility(View.VISIBLE);
    }
}