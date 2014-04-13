package com.futurice.festapp.dao;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.futurice.festapp.domain.News;
import com.futurice.festapp.domain.NewsArticle;
import com.futurice.festapp.domain.to.HTTPBackendResponse;
import com.futurice.festapp.util.HTTPUtil;
import com.futurice.festapp.util.JSONUtil;
import com.futurice.festapp.util.FestAppConstants;

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
	
	private static DateFormat RSS_DATE_FORMATTER = new SimpleDateFormat("yyyy-MM-dd HH:mm"); //new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss Z", Locale.US);
	private static final DateFormat DB_DATE_FORMATTER = new SimpleDateFormat("yyyy-MM-dd HH:mm");
	private static final String TAG = NewsDAO.class.getSimpleName();

	
	public static List<News> getAll(Context context) {
		List<News> articles = new ArrayList<News>();
		SQLiteDatabase db = null;
		Cursor cursor = null;
		try {
			db = (new DatabaseHelper(context)).getWritableDatabase();
			cursor = db.rawQuery("SELECT title, time, content FROM news ORDER BY time DESC", new String[]{});
			while (cursor.moveToNext()) {
		        String title = cursor.getString(0);
		        Date date = getDate(cursor.getString(1));
		        String content = cursor.getString(2);
		        articles.add(new News());
			}
		} finally {
			closeDb(db, cursor);
		}
		return articles;
	}
	
	public static List<News> getLatest(int count, Context context) {
		List<News> news = new ArrayList<News>();
		
		SQLiteDatabase db = null;
		Cursor cursor = null;
		
		try {
			db = (new DatabaseHelper(context)).getReadableDatabase();
			cursor = db.rawQuery("SELECT title, time, content FROM news ORDER BY time DESC", new String[]{});
			while (cursor.moveToNext() && count > 0) {
		        String title = cursor.getString(1);
		        Date date = getDate(cursor.getString(2));
		        String content = cursor.getString(3);
		        news.add(new News(title, "", "", content, date, ""));
		        count--;
			}
		} finally {
			closeDb(db, cursor);
		}
		return news;
	}
	
	public static NewsArticle findNewsArticle(Context context, String id) {
		return null;
	}
	
	public static ContentValues convertNewsArticleToContentValues(NewsArticle article) {
		ContentValues values = new ContentValues();
		values.put("url", article.getUrl());
		values.put("title", article.getTitle());
		values.put("newsDate", DB_DATE_FORMATTER.format(article.getDate()));
		values.put("content", article.getContent());
		return values;
	}
	
	private static Date getDate(String date) {
		try {
			return DB_DATE_FORMATTER.parse(date);
		} catch (Exception e) {
			Log.e(TAG, "Unable to parse date from " + date);
		}
		return null;
	}

	public static List<NewsArticle> updateNewsOverHttp(Context context) {
		/*
		HTTPUtil httpUtil = new HTTPUtil();
		HTTPBackendResponse response = httpUtil.performGet(FestAppConstants.NEWS_JSON_URL);
		if (!response.isValid() || response.getContent() == null) {
			return null;
		}
		ConfigDAO.setEtagForGigs(context, response.getEtag());
		try {
			List<NewsArticle> articles = parseFromJson(response.getContent());
			
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

	
	public static List<NewsArticle> parseFromJson(String json) throws Exception {
		List<NewsArticle> articles = new ArrayList<NewsArticle>();
		JSONArray list = new JSONArray(json);
		for (int i=0; i < list.length(); i++) {
			try {
				JSONObject newsObject = list.getJSONObject(i);
				Date date = new Date(Long.valueOf(JSONUtil.getString(newsObject, "time")) * 1000);
				String titleString = JSONUtil.getString(newsObject, "title");
				String dateString = RSS_DATE_FORMATTER.format(date);
				NewsArticle article = new NewsArticle(titleString + dateString, titleString, date, "<p>" + JSONUtil.getString(newsObject, "content").replace("  ", "<br /><br />") + "</ p>");
				articles.add(article);
			} catch (Exception e) {
				Log.w(TAG, "Received invalid JSON-structure", e);
			}
		}
		
		return articles;
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
