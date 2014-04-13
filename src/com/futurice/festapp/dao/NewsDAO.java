package com.futurice.festapp.dao;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.futurice.festapp.domain.News;
import com.futurice.festapp.domain.NewsArticle;
import com.futurice.festapp.util.CalendarUtil;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/**
 * DAO for News-articles.
 * 
 * @author Pyry-Samuli Lahti / Futurice
 */
public class NewsDAO {
	
	private static final String TAG = NewsDAO.class.getSimpleName();
	private static DatabaseHelper dbHelper;

	
	public static List<News> getAll(Context context) {
		List<News> articles = new ArrayList<News>();
		Cursor cursor = null;
		if (dbHelper == null){
			dbHelper = new DatabaseHelper(context);
		}
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		try {
			cursor = db.rawQuery("SELECT title, time, content FROM news ORDER BY time DESC", new String[]{});
			while (cursor.moveToNext()) {
		        String title = cursor.getString(0);
		        Date time = CalendarUtil.getDbDate(cursor.getString(1));
		        String content = cursor.getString(2);
		        articles.add(new News());
			}
		} catch (ParseException e) {
			Log.e(TAG, e.getLocalizedMessage());
		} finally {
			closeDb(db, cursor);
		}
		return articles;
	}
	
	public static List<News> getLatest(int count, Context context) {
		List<News> news = new ArrayList<News>();
		if (dbHelper == null){
			dbHelper = new DatabaseHelper(context);
		}
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		
		Cursor cursor = null;
		
		try {
			cursor = db.rawQuery("SELECT title, time, content FROM news ORDER BY time DESC", new String[]{});
			while (cursor.moveToNext() && count > 0) {
		        String title = cursor.getString(1);
		        Date date = CalendarUtil.getDbDate(cursor.getString(2));
		        String content = cursor.getString(3);
		        news.add(new News(title, "", "", content, date, ""));
		        count--;
			}
		}
		catch (ParseException e){
			Log.e(TAG, e.getLocalizedMessage());
		}
		finally {
			closeDb(db, cursor);
		}
		return news;
	}
	
	public static NewsArticle findNewsArticle(Context context, String id) {
		return null;
	}
	
	public static ContentValues convertNewsToContentValues(News article) {
		ContentValues values = new ContentValues();
		values.put("title", article.getTitle());
		values.put("time", CalendarUtil.dbFormat(article.getTime()));
		values.put("content", article.getContent());
		return values;
	}

	public static List<NewsArticle> updateNewsOverHttp(Context context) {
		/*
		HTTPUtil httpUtil = new HTTPUtil();
		HTTPBackendResponse response = httpUtil.performGet(FestAppConstants.NEWS_JSON_URL);
		if (!response.isValid() || response.getStringContent() == null) {
			return null;
		}
		ConfigDAO.setEtagForGigs(context, response.getEtag());
		try {
			List<NewsArticle> articles = parseFromJson(response.getStringContent());
			
			if (articles != null && articles.size() > 0) { // Hackish fail-safe
				SQLiteDatabase db = null;
				Cursor cursor = null;
				List<NewsArticle> newArticles = new ArrayList<NewsArticle>();
				try {
					db = (new DatabaseHelper(context)).getWritableDatabase();
					db.beginTransaction();
					
					int inserted = 0, updated = 0;
					for (NewsArticle article : articles) {
						NewsArticle existingArticle = findNewsArticle(db, article.getUrl());
						if (existingArticle != null) {
							db.update("news", convertNewsArticleToContentValues(article), "url = ?", new String[] {article.getUrl()});
							updated++;
						} else {
							db.insert("news", "content", convertNewsArticleToContentValues(article));
							inserted++;
							newArticles.add(article);
						}
					}
					db.setTransactionSuccessful();
					Log.i(TAG, String.format("Successfully updated NewsArticles via HTTP. Result {received: %d, updated: %d, added: %d", articles.size(), updated, inserted));
				} finally {
					db.endTransaction();
					closeDb(db, cursor);
				}
				return newArticles;
			}
			
		} catch (Exception e) {
			Log.w(TAG, "Received invalid JSON-structure", e);
		}
		*/
		return null;
	}

	private static void closeDb(SQLiteDatabase db, Cursor cursor) {
		if (db != null) {
			db.close();
		}
		if (cursor != null) {
			cursor.close();
		}
	}

}
