package com.futurice.festapp.util;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.futurice.festapp.domain.News;
import com.futurice.festapp.domain.NewsArticle;

public class JSONUtil {
	
	private static final String TAG = "JSONParser";
	
	public static String getString(JSONObject j, String name) throws JSONException {
		if (!j.has(name) || j.isNull(name)) {
			return null;
		}
		return j.getString(name);
	}
	
	public static Long getLong(JSONObject j, String name) throws JSONException {
		if(!j.has(name) || j.isNull(name)) {
			return null;
		}
		return j.getLong(name);
	}
	
	/**
	 * Parses a JSON string to a List<News>. Un-parseable news articles will not be put into the returned list.
	 * @param json				The JSON string to parse
	 * @return					List<News> parsed from the JSON
	 * @throws JSONException	Thrown if unable to build a JSONArray from the input
	 */
	public static List<News> parseNewsFromJSON(String json) throws JSONException {
		List<News> articles = new ArrayList<News>();
		JSONArray list = new JSONArray(json);
		
		for (int i=0; i < list.length(); i++) {
			try {
				JSONObject newsObject = list.getJSONObject(i);
				News article = parseSingleNewsArticleFromJSON(newsObject);
				if (article != null){
					articles.add(article);
				}
			} catch (Exception e) {
				Log.w(TAG, "Received invalid JSON-structure", e);
			}
		}
		
		return articles;
	}
	
	/**
	 * Parses a single JSONObject to a News object.
	 * @param newsObject		The object that should be parsed
	 * @return					A News object if parsing is succesfull, null otherwise.
	 */
	private static News parseSingleNewsArticleFromJSON(JSONObject newsObject){
		try {
			String title = JSONUtil.getString(newsObject, "title");
			String teaserText = JSONUtil.getString(newsObject, "teaser_text");
			String content = JSONUtil.getString(newsObject, "content");
			Date time = CalendarUtil.parseDateFromString(JSONUtil.getString(newsObject, "time"));
			
			News article = new News();
			article.setTitle(title);
			article.setTeaserText(teaserText);
			article.setContent(content);
			article.setTime(time);
			
			return article;
		} catch (Exception e) {
			Log.w(TAG, "Received invalid JSON-structure", e);
			return null;
		}
	}

}
