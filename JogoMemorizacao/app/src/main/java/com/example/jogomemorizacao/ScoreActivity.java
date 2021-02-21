package com.example.jogomemorizacao;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.tabs.TabLayout;

import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class ScoreActivity extends AppCompatActivity {
    private List<Score> _scores;
    private TableLayout _table;

    private Database _database;
    private int _currentTabIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score);

        Intent intent = getIntent();
        String userName = intent.getStringExtra("user");
        int errorCount = intent.getIntExtra("score", 0);
        int elapsedTime = intent.getIntExtra("time", 0);

        _database = new Database(this);
        _database.insertOrUpdate(new Score(null, userName, errorCount, elapsedTime));

        _table = findViewById(R.id.rankingTable);
        _scores = _database.getAll();

        Collections.sort(_scores,(r1, r2) -> r1.getTime().compareTo(r2.getTime()));

        fetchTableData(_currentTabIndex);

        TabLayout tabLayout = findViewById(R.id.tabs);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                onTabSelect(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    public void fetchTableData(int type) {
        TableRow.LayoutParams param = new TableRow.LayoutParams(
                TableRow.LayoutParams.MATCH_PARENT,
                TableRow.LayoutParams.MATCH_PARENT,
                1.0f
        );
        int i = 1;

        for (Score score :
                _scores) {
            TableRow row = new TableRow(this);

            TextView txtPos = new TextView(this);
            txtPos.setText(String.format(Locale.getDefault(), "%dº", i++));
            txtPos.setTypeface(null, Typeface.BOLD);
            txtPos.setTextSize(18);
            txtPos.setGravity(Gravity.CENTER);
            txtPos.setLayoutParams(param);
            row.addView(txtPos);

            TextView txtName = new TextView(this);
            txtName.setText(score.getName());
            txtName.setLayoutParams(param);
            txtName.setGravity(Gravity.CENTER);
            txtName.setTextSize(18);
            row.addView(txtName);

            TextView txtTime = new TextView(this);

            if (type == 0) {
                txtTime.setText(String.format(Locale.getDefault(), "%02d:%02d",
                        TimeUnit.MILLISECONDS.toMinutes(score.getTime()),
                        TimeUnit.MILLISECONDS.toSeconds(score.getTime()) -
                                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(score.getTime()))
                ));
            }
            else {
                txtTime.setText(String.valueOf(score.getErrors()));
            }
            txtTime.setTextSize(18);
            txtTime.setGravity(Gravity.CENTER);
            txtTime.setLayoutParams(param);
            row.addView(txtTime);

            _table.addView(row);
        }
    }

    public void onTabSelect(int index) {
        _table.removeViews(2, _scores.size());

        if (index == _currentTabIndex) {
            return;
        }

        _currentTabIndex = index;

        if (_currentTabIndex == 0) {
            ((TextView)findViewById(R.id.txtRankingMode)).setText(R.string.record);
            Collections.sort(_scores,(r1, r2) -> r1.getTime().compareTo(r2.getTime()));
        }
        else {
            ((TextView)findViewById(R.id.txtRankingMode)).setText(R.string.errors_count);
            Collections.sort(_scores,(r1, r2) -> r1.getErrors().compareTo(r2.getErrors()));
        }

        fetchTableData(index);
    }

    public void onClearClick(View v) {
        _database.clear();
        _table.removeViews(2, _scores.size());
        _scores.clear();
    }


}
