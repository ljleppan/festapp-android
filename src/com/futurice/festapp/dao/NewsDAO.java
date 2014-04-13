package com.futurice.festapp.dao;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.futurice.festapp.domain.News;
import com.futurice.festapp.domain.to.HTTPBackendResponse;
import com.futurice.festapp.util.CalendarUtil;
import com.futurice.festapp.util.FestAppConstants;
import com.futurice.festapp.util.HTTPUtil;
import com.futurice.festapp.util.JSONUtil;

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
	private static final String[] columns = {"id", "title", "time", "content"};

	
	public static List<News> getAll(Context context) {
		return readNews(context, -1, null, null);
	}
	
	public static List<News> getLatest(int count, Context context) {
		return readNews(context, -1, null, null);
	}
	
	/**
	 * Function for reading news from local database.
	 * @param limit limit of returned articles. Limit of zero or less means no
	 * limit clause is used.
	 * @param context context for creating databasehelper
	 * @return list of news
	 */
	private static List<News> readNews(Context context, int limit, String selection, String[] selectionArgs){
		List<News> news = new ArrayList<News>();
		SQLiteDatabase db = (new DatabaseHelper(context)).getReadableDatabase();
		
		Cursor cursor = null;
		
		try {
			cursor = db.query("news", columns, null, null, null, null, "time", 
					limit > 0 ? String.valueOf(limit) : null);
			while (cursor.moveToNext()) {
				String id = cursor.getString(0);
		        String title = cursor.getString(1);
		        Date time = CalendarUtil.getDbDate(cursor.getString(2));
		        String content = cursor.getString(3);
		        news.add(new News(id, title, "", "", content, time, ""));
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
	
	public static News findNews(Context context, String id) {
		List<News> news = readNews(context, 1, "id = ?", new String[]{id});
		if (news.isEmpty())
			return null;
		else
			return news.get(0);
	}
	
	public static ContentValues convertNewsToContentValues(News article) {
		ContentValues values = new ContentValues();
		values.put("title", article.getTitle());
		values.put("time", CalendarUtil.dbFormat(article.getTime()));
		values.put("content", article.getContent());
		return values;
	}

	public static List<News> updateNewsOverHttp(Context context) {
		HTTPUtil httpUtil = new HTTPUtil();
		HTTPBackendResponse response = httpUtil.performGet(FestAppConstants.NEWS_JSON_URL);
		String stringContent = null;
		if (!response.isValid() || (stringContent = response.getStringContent()) == null) {
			return null;
		}
		ConfigDAO.setEtagForGigs(context, response.getEtag());
		try {
			List<News> articles = JSONUtil.parseNewsFromJSON(stringContent);
			
			if (articles != null && articles.size() > 0) { // Hackish fail-safe
				SQLiteDatabase db = null;
				List<News> newArticles = new ArrayList<News>();
				int inserted = 0, updated = 0;
				for (News article : articles) {
					News existingArticle = findNews(context, article.getId());
					try{
						db = (new DatabaseHelper(context)).getWritableDatabase();
						db.beginTransaction();
						if (existingArticle != null) {
							db.update("news", convertNewsToContentValues(article), "id = ?", 
									new String[] {article.getId()});
							updated++;
						} else {
							db.insert("news", "content", convertNewsToContentValues(article));
							inserted++;
							newArticles.add(article);
						}
						db.setTransactionSuccessful();
					}
					finally{
						db.endTransaction();
						db.close();
					}
				}
				Log.i(TAG, String.format("Successfully updated NewsArticles via HTTP. " +
						"Result {received: %d, updated: %d, added: %d", articles.size(), updated, inserted));
				return newArticles;
			}
			
		} catch (Exception e) {
			Log.w(TAG, "Received invalid JSON-structure", e);
		}
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
