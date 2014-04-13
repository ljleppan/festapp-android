package com.futurice.festapp.util;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.futurice.festapp.domain.News;

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
	
	public static List<News> parseNewsFromJSON(String json) throws JSONException {
		List<News> news = new ArrayList<News>();
		JSONArray list = new JSONArray(json);
		
		for (int i=0; i < list.length(); i++) {
			try {
				JSONObject newsObject = list.getJSONObject(i);
				News article = parseSingleNewsFromJSON(newsObject);
				news.add(article);
			} catch (Exception e) {
				Log.w(TAG, "Received invalid JSON-structure", e);
			}
		}
		
		return news;
	}
	
	private static News parseSingleNewsFromJSON(JSONObject newsObject) throws JSONException{
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
