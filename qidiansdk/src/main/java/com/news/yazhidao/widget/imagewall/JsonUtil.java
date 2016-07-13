package com.news.yazhidao.widget.imagewall;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/***
 * json工具类
 * 
 * @author Administrator
 *
 */
public class JsonUtil {
	public List<Object> fromJsonArray(JSONArray arrayObject) {
		List<Object> arrayvalues = null;

		try {
			arrayvalues = new ArrayList<Object>();
			JSONArray array = arrayObject;
			for (int i = 0; i < array.length(); i++) {
				String item = array.get(i).toString();
				arrayvalues.add(getObject(extractJsonRightValue(item)));

			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return arrayvalues;

	}

	public Object getObject(Object extractJsonRightValue) {
		// TODO Auto-generated method stub
		return extractJsonRightValue;
	}

	public List<Object> fromJsonArray(String content) {
		try {
			return fromJsonArray(new JSONArray(content));
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public Map<String, Object> fromJsonObject(String jsonObjectValue) {
		try {
			return fromJsonObject(new JSONObject(jsonObjectValue));
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public Map<String, Object> fromJsonObject(JSONObject jsonObjectValue) {
		// TODO Auto-generated method stub
		List<Object> arrayvalues = null;
		Map<String, Object> map = null;
		try {
			arrayvalues = new ArrayList<Object>();
			JSONObject jsonobject = jsonObjectValue;
			map = new HashMap<String, Object>();
			String name;
			String strvalue;
			Object extractedValue;
			for (int i = 0; i < jsonobject.length(); i++) {
				name = jsonobject.names().getString(i);
				strvalue = jsonobject.getString(name);
				extractedValue = extractJsonRightValue(strvalue);
				map.put(name, extractedValue);
			}

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return map;
	}

	public Object extractJsonRightValue(String value) {
		Object jsonvalue;
		if (value.startsWith("[")) {
			jsonvalue = fromJsonArray(value);
		} else if (value.startsWith("{")) {
			jsonvalue = fromJsonObject(value);
		} else {
			jsonvalue = value;
		}
		return jsonvalue;
	}

	public Object findJsonNode(String exactname, JSONArray rootObject) {
		if (rootObject instanceof JSONArray) {
			JSONArray jsonobject = (JSONArray) rootObject;
			for (int i = 0; i < jsonobject.length(); i++) {
				try {
					return findJsonNode(exactname, jsonobject.getString(i));
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return rootObject;
	}

	public Object findJsonNode(String exactname, JSONObject rootObject) {
		try {
			String name;
			String strvalue;
			if (rootObject instanceof JSONObject) {
				JSONObject jsonobject = (JSONObject) rootObject;
				for (int i = 0; i < jsonobject.length(); i++) {
					name = jsonobject.names().getString(i);
					strvalue = jsonobject.getString(name);
					if (name.equalsIgnoreCase(exactname)) {
						return strvalue;
					} else {
						Object node = findJsonNode(exactname, strvalue);
						if (node != null && !node.toString().equals(strvalue)) {
							return node.toString();
						}
					}

				}
			}

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public Object findJsonNode(String exactname, String strvalue) {
		try {
			if (strvalue.startsWith("[")) {

				return findJsonNode(exactname, new JSONArray(strvalue));

			} else if (strvalue.startsWith("{")) {
				return findJsonNode(exactname, new JSONObject(strvalue));
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return strvalue;
	}

	public Object findJsonLink(String nodelink, String rootObject) {
		try {
			return findJsonLink(nodelink, new JSONObject(rootObject));
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			try {
				return findJsonLink(nodelink, new JSONArray(rootObject).get(0));
			} catch (JSONException e1) {
				// TODO Auto-generated catch block
				return "";
			}

		}
	}

	public Object findJsonLink(String nodelink, Object rootObject) {
		if (rootObject instanceof JSONObject) {
			return findJsonLink(nodelink, (JSONObject) rootObject);
		} else if (rootObject instanceof JSONArray) {
			return findJsonLink(nodelink, (JSONArray) rootObject);
		}
		return rootObject;
	}

	public Object findJsonLink(String nodelink, JSONArray rootObject) {
		Object node = "";
		for (int i = 0; i < rootObject.length(); i++) {
			try {
				node = findJsonLink(nodelink, rootObject.get(i));
				if (node.equals("")) {
					continue;
				} else {
					return node;
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return rootObject;

	}

	public Object findJsonLink(String nodelink, JSONObject rootObject) {
		Object jsonobject = null;
		if (nodelink.equals("")) {
			return rootObject;
		}
		try {
			String[] nodes = nodelink.split("-");
			String data = nodes[0];

			jsonobject = getNode(rootObject, data);
			if (jsonobject == null || jsonobject.equals("")) {
				return jsonobject;
			}
			for (int j = 1; j < nodes.length; j++) {

				if (jsonobject instanceof JSONObject) {
					jsonobject = getNode((JSONObject) jsonobject, nodes[j]);
				} else if (jsonobject instanceof JSONArray) {
					jsonobject = ((JSONArray) jsonobject).get(0);
				} else {
					return jsonobject;
				}

			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return jsonobject;
	}

	private Object getNode(JSONObject rootObject, String data)
			throws JSONException {
		Object jsonobject;
		String[] ns = data.split(":");
		String node = ns[0];
		String index = "";

		if (!rootObject.has(node)) {
			return "";
		}
		jsonobject = rootObject.get(node);
		if (jsonobject == null) {
			return "";
		}
		if (ns.length > 1) {
			index = ns[1];
			Integer i = Integer.parseInt(index);
			if (((JSONArray) jsonobject).length() + 1 >= i) {
				jsonobject = ((JSONArray) jsonobject).get(Integer
						.parseInt(index));
			}
		}
		return jsonobject;
	}
}
