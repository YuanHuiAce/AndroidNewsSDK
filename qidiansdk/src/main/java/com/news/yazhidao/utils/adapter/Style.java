package com.news.yazhidao.utils.adapter;


public class Style {
	public static final int TEXT_COLOR = 0;
	public static final int BACKGROUND_COLOR = 1;
	public static final int VISIBLE = 2;
	public static final int STRIKE_THRU_TEXT_FLAG = 3;
	public int viewid;
	public int styleitem;
	public Object value;
	public boolean selectedItem;

	@Override
	public String toString() {
		return "Style [viewid=" + viewid + "]";
	}

	public Style(int viewid, int style, Object value) {
		super();
		this.viewid = viewid;
		this.styleitem = style;
		this.value = value;
	}

	public Style(boolean selectedItem, int viewid, int styleitem,
			Object value) {
		this(viewid, styleitem, value);
		this.selectedItem = selectedItem;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Style other = (Style) obj;
		if (viewid != other.viewid)
			return false;
		return true;
	}

}
