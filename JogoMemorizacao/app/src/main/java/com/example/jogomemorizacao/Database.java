package com.example.jogomemorizacao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;


import java.io.Closeable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Database implements Closeable {
    private static final String DATABASE_NAME = "memory_game";
    private static final int DATABASE_ACCESS = 0;
    private static final String TABLE = "scores";

    private static final String SQL_STRUCT = "CREATE TABLE IF NOT EXISTS scores (id INTEGER PRIMARY KEY,time INTEGER NOT NULL,name TEXT NOT NULL,errors INTEGER NOT NULL)";
    private static final String SQL_SELECT_ALL = "SELECT * FROM scores";
    private static final String SQL_SELECT_BY_NAME = "SELECT * FROM scores where name = ?";
    private static final String SQL_CLEAR = "DROP TABLE IF EXISTS scores";

    private SQLiteDatabase database;

    public Database(Context context) {
        database = context.openOrCreateDatabase(DATABASE_NAME, DATABASE_ACCESS, null);
        database.execSQL(SQL_STRUCT);
    }

    public void insertOrUpdate(Score entry) {
        Score score = getScore(entry.getName());

        if (score == null) {
            database.insert(TABLE, null, parseToContentValues(entry));
            return;
        }

        if (entry.getTime() < score.getTime()) {
            score.setTime(entry.getTime());
        }
        if (entry.getErrors() < score.getErrors()) {
            score.setErrors(entry.getErrors());
        }

        database.update(TABLE, parseToContentValues(score), "id = ?", new String[] { String.valueOf(score.getId()) });
    }

    public List<Score> getAll() {
        return get(SQL_SELECT_ALL);
    }

    public Score getScore(String name) {
        Collection<Score> scores = get(SQL_SELECT_BY_NAME, name);

        if (scores.isEmpty())
            return  null;

        return scores.iterator().next();
    }

    public void clear() {
        database.execSQL(SQL_CLEAR);
        database.execSQL(SQL_STRUCT);
    }

    @Override
    public void close()  {
        database.close();
    }

    private ContentValues parseToContentValues(Score score) {
        ContentValues values = new ContentValues();
        values.put("id", score.getId());
        values.put("name", score.getName());
        values.put("time", score.getTime());
        values.put("errors", score.getErrors());
        return values;
    }

    private List<Score> get(String sqlCommand, String... selectionArgs) {
        List<Score> scores = new ArrayList<>();
        Score score;

        Cursor cursor = database.rawQuery(sqlCommand, selectionArgs);
        if (cursor.moveToFirst()) {
            int indexId = cursor.getColumnIndex("id");
            int indexName = cursor.getColumnIndex("name");
            int indexErrors = cursor.getColumnIndex("errors");
            int indexTime = cursor.getColumnIndex("time");

            do {
                score = new Score(
                        cursor.getInt(indexId),
                        cursor.getString(indexName),
                        cursor.getInt(indexErrors),
                        cursor.getInt(indexTime));

                scores.add(score);
            }while (cursor.moveToNext());
        }

        cursor.close();

        return scores;
    }
}
