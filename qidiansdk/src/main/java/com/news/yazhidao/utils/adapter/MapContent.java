package com.news.yazhidao.utils.adapter;

import android.database.Cursor;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class MapContent {

	Map<Integer, Cursor> tailIdx_Cursor_Map = new TreeMap<Integer, Cursor>(
			new Comparator<Integer>() {

				public int compare(Integer arg0, Integer arg1) {
					return arg0 - arg1;
				}
			});
	Map<Integer, JSONArray> tailIdx_JSONArray_Map = new TreeMap<Integer, JSONArray>(
			new Comparator<Integer>() {

				public int compare(Integer arg0, Integer arg1) {
					return arg0 - arg1;
				}
			});
	List<Integer> posKeys;
	protected Cursor mCurrentCursor;
	public static final int CURSOR_TYPE = 0;
	public static final int LIST_OBJECT_TYPE = 1;
	private static final int JSONARRAY_TYPE = 2;
	private List list;
	private JSONArray jsonArray;
	private int type = -1;
	private Map<String, Class> nameTypePair;
	protected int mCursorGetCount;
	protected int mJSONArrayGetCount;

	public Map<String, Class> getNameTypePair() {
		return nameTypePair;
	}

	public MapContent(Cursor cursor, Map<String, Class> nameTypePair) {
		super();
		this.tailIdx_Cursor_Map.put(cursor.getCount() - 1, cursor);
		Logs.i("put " + (cursor.getCount() - 1) + " " + cursor + " "
				+ cursor.getCount());
		setDataSrc(cursor, nameTypePair);
		mCursorGetCount += cursor.getCount();
		// TODO Auto-generated constructor stub
	}

	public void swapObject(Object objct) {
		if (objct instanceof Cursor) {
			swapCursor((Cursor) objct);
		} else if (objct instanceof JSONArray) {
			swapJSONArray((JSONArray) objct);
		}else if(objct instanceof List){
			swapList((List) objct);
		}
	}

	private void swapList(List objct) {
		// TODO Auto-generated method stub
		list.addAll(objct);
	}

	public void swapCursor(Cursor cursor) {
		setDataSrc(cursor, nameTypePair);
		Integer idxKey = cursor.getCount() == 0 ? -1
				: (Integer) tailIdx_Cursor_Map.keySet().toArray()[tailIdx_Cursor_Map
						.size() - 1] + cursor.getCount();
		tailIdx_Cursor_Map.put(idxKey, cursor);
		mCursorGetCount += cursor.getCount();
	}

	public void swapJSONArray(JSONArray cursor) {
		setDataSrc(cursor);
		Integer idxKey = cursor.length() == 0 ? -1
				: (Integer) tailIdx_JSONArray_Map.keySet().toArray()[tailIdx_JSONArray_Map
						.size() - 1] + cursor.length();
		tailIdx_JSONArray_Map.put(idxKey, cursor);
		mJSONArrayGetCount += cursor.length();
	}

	public MapContent(Object list) {
		super();
		setContent(list);
		// TODO Auto-generated constructor stub
	}

	public MapContent() {
		// TODO Auto-generated constructor stub
	}

	public Object getContent() {
		switch (type) {
		case CURSOR_TYPE:
			return mCurrentCursor;
		case LIST_OBJECT_TYPE:
			return list;
		case JSONARRAY_TYPE:
			return jsonArray;
		default:
			return null;
		}
	}

	public void clear() {
		if (this.type == CURSOR_TYPE) {
			if (mCurrentCursor != null && !mCurrentCursor.isClosed()) {
				this.mCurrentCursor.close();
				this.mCurrentCursor = null;
			}
			for (Cursor cursor : tailIdx_Cursor_Map.values()) {
				if (!cursor.isClosed()) {
					cursor.close();
					cursor = null;
				}
			}
			tailIdx_Cursor_Map.clear();
		} else if (this.type == LIST_OBJECT_TYPE) {
			this.list.clear();
		} else if (this.type == JSONARRAY_TYPE) {
			this.jsonArray = null;
			tailIdx_Cursor_Map.clear();
		}
	}

	public void setDataSrc(Cursor cursor, Map<String, Class> nameTypePair) {
		if (cursor == null) {
			throw new NullPointerException("cursor is null");
		}

		this.mCurrentCursor = cursor;
		type = CURSOR_TYPE;
		this.nameTypePair = nameTypePair;
	}

	public void setDataSrc(JSONArray cursor) {
		if (cursor == null) {
			throw new NullPointerException("cursor is null");
		}

		this.jsonArray = cursor;
		type = JSONARRAY_TYPE;
	}

	public void setContent(Object list) {

		if (list == null) {
			throw new NullPointerException("list is null");
		}
		if (list instanceof List) {
			this.list = (List) list;
			type = LIST_OBJECT_TYPE;
		} else if (list instanceof JSONArray) {
			this.jsonArray = (JSONArray) list;
			type = JSONARRAY_TYPE;
			this.tailIdx_JSONArray_Map.put(jsonArray.length() - 1, jsonArray);
			mJSONArrayGetCount += jsonArray.length();
		}

	}

	public int getType() {
		return type;
	}

	public int getCount() {
		switch (type) {
		case CURSOR_TYPE:
			if (mCurrentCursor == null) {
				return 0;
			}
			return mCursorGetCount;
		case LIST_OBJECT_TYPE:
			if(list==null){
				return 0;
			}
			return list.size();
		case JSONARRAY_TYPE:
			if (jsonArray == null) {
				return 0;
			}
			return mJSONArrayGetCount;
		default:
			return 0;
		}
	}

	public Object getItem(int position) {
		if (position == getCount()) {
			return null;
		}
		switch (type) {
		case CURSOR_TYPE:
			Cursor cursor = null;
			
				posKeys = new ArrayList<Integer>(tailIdx_Cursor_Map.keySet());
			
			int insertionPoint = Collections.binarySearch(posKeys, position);
			insertionPoint = insertionPoint < 0 ? -insertionPoint - 1
					: insertionPoint;
			if (insertionPoint < posKeys.size()) {
				int posLessThankey = posKeys.get(insertionPoint);
				cursor = tailIdx_Cursor_Map.get(posLessThankey);

				// Logs.i("put posLessThankey " + posLessThankey + " " + cursor
				// + " " + cursor.getCount() + " " + tailIdx_Cursor_Map.size());
				int currCount = cursor.getCount();
				if (currCount == 0) {
					return null;
				}
				// Logs.i("pos insertionPoint " + insertionPoint);
				int realPos = insertionPoint == 0 ? position
						: (currCount - 1 - (posLessThankey - position));
				// Logs.i("realPos " + realPos);
				if (cursor != null && cursor.isClosed()) {
					return cursor;
				}
				cursor.moveToPosition(realPos);
			}
			return cursor;
		case LIST_OBJECT_TYPE:
			return position < list.size() ? list.get(position) : null;
		case JSONARRAY_TYPE:

			try {
				int realPos = position;
				JSONArray jsonArray = null;
//				if (posKeys == null) {
					posKeys = new ArrayList<Integer>(
							tailIdx_JSONArray_Map.keySet());
//				}
				int insertionPointJSONArray = Collections.binarySearch(posKeys,
						position);
				insertionPointJSONArray = insertionPointJSONArray < 0 ? -insertionPointJSONArray - 1
						: insertionPointJSONArray;
				if (insertionPointJSONArray < posKeys.size()) {
					int posLessThankeyJSONArray = posKeys
							.get(insertionPointJSONArray);
					jsonArray = tailIdx_JSONArray_Map
							.get(posLessThankeyJSONArray);

					// Logs.i("put posLessThankey " + posLessThankey + " " +
					// cursor
					// + " " + cursor.getCount() + " " +
					// tailIdx_Cursor_Map.size());
					int currCount = jsonArray.length();
					if (currCount == 0) {
						return null;
					}
					// Logs.i("pos insertionPoint " + insertionPoint);
					realPos = insertionPointJSONArray == 0 ? position
							: (currCount - 1 - (posLessThankeyJSONArray - position));
					// Logs.i("realPos " + realPos);

				}
				return jsonArray.get(realPos);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		return null;
	}

	public void nextBatch(Object object) {
		// TODO Auto-generated method stub

	}
}
