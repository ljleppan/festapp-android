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
	private static DatabaseHelper dbHelper;

	
	public static List<News> getAll(Context context) {
		return readNews(-1, context);
	}
	
	public static List<News> getLatest(int count, Context context) {
		return readNews(count, context);
	}
	
	/**
	 * Function for reading news from local database.
	 * @param limit limit of returned articles. Limit of -1 means no
	 * limit clause is used.
	 * @param context context for creating databasehelper
	 * @return list of news
	 */
	private static List<News> readNews(int limit, Context context){
		List<News> news = new ArrayList<News>();
		if (dbHelper == null){
			dbHelper = new DatabaseHelper(context);
		}
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		
		Cursor cursor = null;
		
		try {
			// TODO this kind of query creating is brutal and bad. It should be
			// changed in the future
			String sql = "SELECT title, time, content FROM news ORDER BY time DESC";
			if (limit > -1){
				sql += " LIMIT " + limit;
			}
			cursor = db.rawQuery(sql, new String[]{});
			while (cursor.moveToNext()) {
		        String title = cursor.getString(0);
		        Date date = CalendarUtil.getDbDate(cursor.getString(1));
		        String content = cursor.getString(2);
		        news.add(new News(title, "", "", content, date, ""));
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
		// TODO not implemented
		return null;
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
				Cursor cursor = null;
				List<News> newArticles = new ArrayList<News>();
				try {
					if (dbHelper == null){
						dbHelper = new DatabaseHelper(context);
					}
					db = dbHelper.getWritableDatabase();
					db.beginTransaction();
					
					int inserted = 0, updated = 0;
					for (News article : articles) {
						News existingArticle = findNews(context, article.getId());
						if (existingArticle != null) {
							db.update("news", convertNewsToContentValues(article), "url = ?", 
									new String[] {article.getId()});
							updated++;
						} else {
							db.insert("news", "content", convertNewsToContentValues(article));
							inserted++;
							newArticles.add(article);
						}
					}
					db.setTransactionSuccessful();
					Log.i(TAG, String.format("Successfully updated NewsArticles via HTTP. " +
							"Result {received: %d, updated: %d, added: %d", articles.size(), updated, inserted));
				} finally {
					db.endTransaction();
					closeDb(db, cursor);
				}
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
