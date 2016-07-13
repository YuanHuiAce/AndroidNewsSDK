package com.news.yazhidao.utils.adapter;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteCursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.SpannableStringBuilder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONObject;

import java.lang.reflect.Field;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;

/**
 * abslistview闁倿鍘ら崳锟�
 *
 * @author Administrator
 */
public class MapAdapter extends BaseAdapter implements ExpandableListAdapter {
    public ContainerInfo linearInfo;
    public List<View> groupViewSet = new ArrayList<View>();
    protected Context context;
    protected List<String> fieldnames;
    protected List<Integer> viewsid;
    private int itemLayout;
    private MapContent itemDataSrc;
    private Set<ActionListener> handlers;
    private Map<Integer, ListenerBox> listenerMaps;
    private Map<Integer, StyleBox> styleMaps;
    private Map<View, Integer> viewContentMap = new HashMap<View, Integer>();
    boolean ischecked;
    public boolean isVisible = true;
    public Map<Integer, Boolean> checkBoxVisibleOptions = new HashMap<Integer, Boolean>();
    public List<Integer> viewInitIndices = new ArrayList<Integer>();
    public int viewChildCount = 0;
    public boolean startChildViewsCount = true;
    protected boolean isCreatedView;
    public int selectedNum;
    public int latestPosition;
    public List<Integer> selected = new ArrayList<Integer>();
    public Class clazz;
    public String checkboxname;
    public List<Integer> selectedbck;
    private String imageField;

    public List<Object> selectedItems = new ArrayList<Object>();

    public void clearStyles() {
        styleMaps.clear();
    }

    public void clearPage() {
        continueRunner.clear();
    }

    public void reversetoGroup(ViewGroup vg, Object data) {
        // int leng = linearInfo.itemsid.size();
        // this.setAdaptInfo(linearInfo.adaptInfo);
        // for (int i = 0; i < leng; i++) {
        // View view = vg.findViewById(linearInfo.itemsid.get(i));
        // if (view == null)
        // return;
        // int idx = i;
        // Object item = JsonUtil.findJsonLink(linearInfo.itemsname.get(idx),
        // data);
        // if ((item instanceof String || item instanceof Float
        // || item instanceof Integer || item instanceof Double || item
        // instanceof Boolean)
        // && linearInfo.adaptInfo.objectFieldList.size() == 0
        // && linearInfo.adaptInfo.objectFields.length == 0) {
        // if (view instanceof ViewGroup
        // && linearInfo.adaptInfo.viewIdList.size() == 1) {
        // view = ((ViewGroup) view)
        // .findViewById((Integer) linearInfo.adaptInfo.viewIdList
        // .get(0));
        // }
        // setView(i, item, item, view, view);
        // } else {
        // getView(item, view, i, vg);
        // }
        // }
    }

    boolean cacheoutofdate = false;

    public boolean isCacheoutofdate() {
        return cacheoutofdate;
    }

    public void setCacheoutofdate(boolean cacheoutofdate) {
        this.cacheoutofdate = cacheoutofdate;
    }

    long timemills = -1L;
    long tmptimemills;

    // public void traceTime() {
    // if (timemills == -1L) {
    // timemills = System.currentTimeMillis();
    // tmptimemills = -1;
    // } else {
    // tmptimemills = System.currentTimeMillis();
    // }
    //
    // if (((Long) (tmptimemills - timemills)) > (ConfigConstant.enddatetime)) {
    // timemills = System.currentTimeMillis();
    // setCacheoutofdate(true);
    // timemills = -1;
    // } else
    // setCacheoutofdate(false);
    // }

    public void setCheckBoxName(String checkboxFieldName) {
        if (this.fieldnames.indexOf(checkboxFieldName) == -1) {
            throw new IllegalStateException("");
        }
        checkboxname = checkboxFieldName;
    }

    public int getTotalSelection() {
        return this.selected.size();
    }

    public void setImageLoadedField(String tosetImageField) {
        this.imageField = tosetImageField;
        this.isSyncInImage = false;
    }

    public void selectAll(boolean shouldSelect) {
        if (shouldSelect) {
            if (this.selectedbck != null) {
                this.selected = new ArrayList(this.selectedbck);
            }
        } else {
            if (this.selected != null) {
                this.selected.clear();
            }
        }
    }

    public boolean isAdaptEmpty() {
        return this.fieldnames == null || this.fieldnames.size() == 0
                || this.adaptInfo == null;
    }

    public boolean isEmpty() {
        return getItemDataSrc() == null || getItemDataSrc().getCount() == 0;
    }

    Integer pageSize = -1;

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public boolean addSelected(int pos, boolean isAdd) {
        if (isAdd) {
            selected.add(pos);
            selectedItems.add(getItem(pos));
        } else {
            int i = selected.lastIndexOf(pos);
            if (i >= 0) {
                selected.remove(i);
            }
            selectedItems.remove(getItem(pos));
        }
        return isAdd;
    }

    public boolean toggleSelected(int pos) {
        return addSelected(pos, !selected.contains(pos));
    }

    public boolean rmSelectedCollection() {
        if (getItemDataSrc().getContent() instanceof List) {
            ((List) getItemDataSrc().getContent()).removeAll(selectedItems);
            notifyDataSetChanged();
            return true;
        }
        return false;
    }

    Map<Integer, Bitmap> noStyncImageMap = new HashMap<Integer, Bitmap>();
    private List<Style> styles = new ArrayList<Style>();

    public void reinitSelectedAllBck(int count) {
        selected = new ArrayList<Integer>(count);
        selectedbck = new ArrayList<Integer>(count);
        fill(selectedbck, count);
        viewContentMap.clear();
        groupViewSet.clear();
    }

    public void treatCursor(Object item, View convertView, int position) {
        Object value = null;
        String name;
        Cursor cur = (Cursor) item;
        Class type;
        if (cur.getColumnCount() > 0) {
            while (cur.moveToNext()) {
                for (int i = 0; i < cur.getColumnCount(); i++) {
                    name = cur.getColumnName(i);
                    type = itemDataSrc.getNameTypePair().get(name);
                    if (type == null) {
                        continue;
                    }
                    try {
                        if (type == Integer.class) {
                            value = cur.getInt(i);
                        } else if (type == Long.class) {
                            value = cur.getLong(i);
                        } else if (type == Double.class) {
                            value = cur.getDouble(i);
                        } else if (type == String.class) {
                            value = cur.getString(i);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    findAndBindView(convertView, position, item, name, value);
                }
            }
        }
    }

    public void treatObject(Object item, View convertView, int position)
            throws SecurityException {
        Object value = null;
        String name;
        boolean isAccessible;
        clazz = item.getClass();
        Field[] fields = clazz.getFields();
        for (Field field : fields) {
            isAccessible = field.isAccessible();
            field.setAccessible(true);
            if (this.fieldnames.contains(field.getName())) {
                name = field.getName();
                try {
                    value = field.get(item);
                    // value = value == null ? "" : value;
                } catch (IllegalArgumentException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                findAndBindView(convertView, position, item, name, value);
            }
            field.setAccessible(isAccessible);
        }
    }

    public void treatMap(Object item, View convertView, int position) {
        String name;
        Object value;
        Map<String, Object> items = (Map<String, Object>) item;
        for (int i = 0; i < this.fieldnames.size(); i++) {
            name = this.fieldnames.get(i);
            if (items.containsKey(name)) {
                value = items.get(name);

                findAndBindView(convertView, position, item, name, value);

            }
        }
    }

    private void fill(List<Integer> boolList, int count) {
        boolList.removeAll(boolList);
        for (int i = 0; i < count; i++) {
            boolList.add(i);
        }
    }

    public boolean isCreatedView() {
        return isCreatedView;
    }

    public void setCreatedView(boolean isCreateView) {
        this.isCreatedView = isCreateView;
    }

    public Map<View, Integer> getViewContentMap() {
        return viewContentMap;

    }

    public void setStartChildViewsCount(boolean flag) {
        startChildViewsCount = flag;
        viewChildCount = 0;
    }

    public void setItemDataSrc(MapContent itemDataSrc) {
        this.itemDataSrc = itemDataSrc;
        visibleALlCheckBox();
    }

    public void addItemDataSrcList(List<? extends Object> objs) {
        if (this.getItemDataSrc() == null
                || this.getItemDataSrc().getContent() == null) {
            this.setItemDataSrc(new MapContent(objs));
        } else {
            ((List) this.getItemDataSrc().getContent()).addAll(objs);
        }
        visibleALlCheckBox();
    }

    public void clearSelectOption() {
    }

    public void clearDataSrc() {
        if (this.getItemDataSrc() != null) {
            this.getItemDataSrc().clear();
        }
        continues.clear();
        noStyncImageMap.clear();
        notifyDataSetChanged();
    }

    public void addAdaptInfo(AdaptInfo adaptInfo) {
        addMapInfo(adaptInfo, false);
    }

    public void addMapInfo(AdaptInfo adaptInfo, boolean idDefault) {

        if (adaptInfo.adaptviewid != -1) {
            this.id_adaptinfo.put(adaptInfo.adaptviewid, adaptInfo);
        }
        if (idDefault) {
            setAdaptInfo(adaptInfo);
        }
        // TODO Auto-generated constructor stub
    }

    public MapAdapter(Context context, AdaptInfo adaptInfo) {
        init(context);
        addMapInfo(adaptInfo, true);
        // TODO Auto-generated constructor stub
    }

    private void init(Context context) {
        this.context = context;
        if (styleMaps == null) {
            styleMaps = new HashMap<Integer, StyleBox>();
        }
    }

    private void visibleALlCheckBox() {
        if (this.itemDataSrc == null) {
            return;
        }
        this.markVisible(true);
    }

    public MapContent getItemDataSrc() {
        return itemDataSrc;
    }

    public MapAdapter(Context context) {
        init(context);
    }

    AdaptInfo adaptInfo;
    private View footerView;

    public void setAdaptInfo(AdaptInfo adaptInfo) {
        this.fieldnames = adaptInfo.objectFieldList.size() > 0 ? adaptInfo.objectFieldList
                : Arrays.asList(adaptInfo.objectFields);

        this.viewsid = adaptInfo.viewIdList.size() > 0 ? adaptInfo.viewIdList
                : Arrays.asList(adaptInfo.viewIds);
        this.itemLayout = adaptInfo.listviewItemLayoutId;
        this.handlers = adaptInfo.actionListeners;
        this.styles.clear();
        this.styles.addAll(adaptInfo.styles);

        this.pageSize = adaptInfo.pageSize;
        this.adaptInfo = adaptInfo;
        if (this.adaptInfo.pagefooterId != 0) {
            this.footerView = LayoutInflater.from(context).inflate(
                    this.adaptInfo.pagefooterId, null);
        }
        this.continueRunner = adaptInfo.continueRunner;
        if (this.continueRunner != null) {
            this.continueRunner.setMapAdapter(this);
        }
        deployListeners(handlers);

    }

    int scrollpos;

    public void deployAdapter(int id) {
        AdaptInfo adaptInfo = id_adaptinfo.get(id);
        if (adaptInfo != null) {
            setAdaptInfo(adaptInfo);

            ((AbsListView) ((Activity) context).findViewById(id))
                    .setAdapter(this);
            ((AbsListView) ((Activity) context).findViewById(id))
                    .setSelection(scrollpos);
            notifyDataSetChanged();

        }
    }

    public void deployListeners(Set<ActionListener> actionListeners) {
        if (listenerMaps == null) {
            listenerMaps = new HashMap<Integer, ListenerBox>();
        }
        if (actionListeners == null || actionListeners.size() == 0) {
            return;
        }
        for (ActionListener listener : actionListeners) {
            deployListener(listener);
        }
    }

    public void deployListener(ActionListener listener) {
        listener.setBaseAdapter(this);
        if (!listenerMaps.containsKey(listener.getResrcId())) {
            listenerMaps.put(listener.getResrcId(), new ListenerBox(this,
                    listener).setBasicAdapter(this));
        } else {
            listenerMaps.get(listener.getResrcId()).addActionListener(listener);
        }
    }

    public void deployStyleBoxes(Style[] styls) {
        if (styleMaps == null) {
            styleMaps = new HashMap<Integer, StyleBox>();
        }
        List<Style> styles = Arrays.asList(styls);
        if (styles == null || styles.size() == 0) {
            return;
        }
        for (Style style : styles) {
            deployStyleBox(style);
        }
    }

    public void deployStyleBoxes(Collection<Style> styles) {
        if (styleMaps == null) {
            styleMaps = new HashMap<Integer, StyleBox>();
        }
        if (styles == null || styles.size() == 0) {
            return;
        }
        for (Style style : styles) {
            deployStyleBox(style);
        }
    }

    public void deployStyleBox(Style style) {
        if (!styleMaps.containsKey(style.viewid)) {
            styleMaps.put(style.viewid, new StyleBox(style));
        } else {
            styleMaps.get(style.viewid).addStyle(style);
        }
    }

    private void addListener(Integer resid, ListenerBox listener) {
        if (this.listenerMaps == null) {
            this.listenerMaps = new HashMap<Integer, ListenerBox>();
        }
        this.listenerMaps.put(resid, listener);
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        int count = itemDataSrc == null ? 0 : itemDataSrc.getCount();
        if (isPaged()) {
            if ((count >= this.adaptInfo.pageSize && (count
                    % this.adaptInfo.pageSize == 0))) {
                inpage = true;
                count += 1;
            } else {
                inpage = false;
            }

        }
        return count;
    }

    private boolean isPaged() {
        if (adaptInfo != null && this.adaptInfo.pagefooterId != 0
                && this.adaptInfo.pageSize != -1) {
            return true;
        }
        return false;
    }

    private boolean inpage;

    public boolean isInpage() {
        return inpage;
    }

    public void setInpage(boolean inpage) {
        this.inpage = inpage;
    }

    public static class ContainerInfo {
        public AdaptInfo adaptInfo = new AdaptInfo();
        public List<Integer> itemsid = new ArrayList<Integer>();
        public List<String> itemsname = new ArrayList<String>();
        public List<String> lineardatas = new ArrayList<String>();
        public Object containerId;
        public boolean simple;

        public List<String> getLinearnames() {
            return itemsname;
        }

        public void setLinearnames(String[] linearnames) {
            this.itemsname = Arrays.asList(linearnames);
        }

        public void setLinearids(Integer[] linearids) {
            this.itemsid = Arrays.asList(linearids);
        }

        public void setLineardatas(String[] lineardatas) {
            this.lineardatas = Arrays.asList(lineardatas);
        }

        public AdaptInfo getAdaptInfo() {
            return adaptInfo;
        }

        public void setAdaptInfo(AdaptInfo adaptInfo) {
            this.adaptInfo = adaptInfo;
        }

        public void setContainerId(Object id) {
            // TODO Auto-generated method stub
            this.containerId = id;
        }

    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        // dangerous itemDataSrc.getCount() <= position
        try {
            if (itemDataSrc == null || itemDataSrc.getCount() < position) {
                return null;
            } else

                return itemDataSrc.getItem(position);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public long getItemId(int arg0) {
        return 0;
    }

    private class ViewHolder {

        View[] viewCaches;
    }

    int forceHeight = -1;

    public View createItemView() {
        // TODO Auto-generated method stub
        View itemView = LayoutInflater.from(this.context).inflate(itemLayout,
                null);

        return itemView;
    }

    public Set<Integer> continues = new HashSet<Integer>();
    public boolean loadExclude;
    private boolean isSyncInImage = true;

    protected void getViewInDetail(Object item, int position, View convertView) {
        if (item == null) {
            return;
        }
        if (item instanceof Cursor || item instanceof SQLiteCursor) {
            treatCursor(item, convertView, position);
        } else if (item instanceof Map) {
            treatMap(item, convertView, position);
        }
        // else if (item instanceof Entity) {
        // treatMap(((Entity) item).fieldContents, convertView, position);
        // }
        else if (item instanceof JSONObject) {
            treatJSONArray(item, convertView, position);
        } else {
            treatObject(item, convertView, position);
        }

    }

    private void treatJSONArray(Object item, View convertView, int position) {
        // TODO Auto-generated method stub
        // String exactname = null;
        // Object value = null;
        // JSONObject jsonobject = (JSONObject) item;
        // for (int i = 0; i < this.fieldnames.size(); i++) {
        // exactname = this.fieldnames.get(i);
        // if (jsonobject.has(exactname)) {
        // try {
        // value = jsonobject.get(exactname).toString();
        // } catch (JSONException e) {
        // // TODO Auto-generated catch block
        // e.printStackTrace();
        // }
        // value = JsonUtil.findJsonNode(exactname, value.toString());
        // if (value != null) {
        // findAndBindView(convertView, position, item, exactname,
        // value.toString());
        // }
        // }
        // }

    }

    Exception imagexcepion;

    public int clickedItem = -1;

    public int getClickedPos() {
        return clickedItem;
    }

    public void setClickedItem(int clickedItem) {
        this.clickedItem = clickedItem;
    }

    protected boolean findAndBindView(View convertView, int pos, Object item,
                                      String name, Object value) {

        int theViewId = this.fieldnames.indexOf(name);
        View theView = convertView.findViewById(this.viewsid.get(theViewId));
        return setView(pos, item, value, convertView, theView);

    }

    public View currView;

    // FileIconHelper fileIconHelper;
    public int bannerpos;
    public int banneroffset;

    protected boolean setView(int pos, Object item, Object value,
                              View convertView, View theView) {
        if (theView == null) {
            return false;
        }
        theView.setVisibility(View.VISIBLE);
        StyleBox styleBox = null;
        if (value.toString().startsWith("http")) {
            if (value instanceof String) {

                // }
                // if (fileIconHelper == null) {
                // fileIconHelper = new FileIconHelper(context);
                // }
                int visible = -1;
                Object url;
                String strurl = null;
                // Logs.e(" --------------------------- imgurl " + value
                // + " item " + item);
                if (value != null && !value.equals("")
                        && !value.equals("anyType{}")) {
                    try {
                        strurl = value.toString();

                        visible = View.VISIBLE;
                        url = new URL(strurl);
                        ((View) theView.getParent()).setVisibility(visible);
                        loadImage(pos, item, value, convertView, theView,
                                strurl);
                        // helper.setIcon(new FileRequest(new URL(strurl)),
                        // (ImageView) theView);

                    } catch (MalformedURLException e) {
                        // TODO Auto-generated catch block
                        url = strurl;
                    }
                    // fileIconHelper.setIcon(new FileRequest(url),
                    // (ImageView) theView);
                } else {
                    ((View) theView.getParent()).setVisibility(View.GONE);

                }

            }

        } else if (theView instanceof ImageView) {

            if (value == null) {
                return false;
            }

            if (value instanceof Integer) {
                ((ImageView) theView).setImageResource(Integer.parseInt(value
                        .toString()));
            } else if (value.getClass() == BitmapDrawable.class) {
                ((ImageView) theView).setImageDrawable((BitmapDrawable) value);
            } else if (value instanceof Drawable) {
                ((ImageView) theView).setImageDrawable((Drawable) value);
            }
            return true;
        } else {
            if (theView instanceof CheckBox) {
                ((CheckBox) theView).setChecked(Boolean.parseBoolean(value
                        .toString()));
                return true;
            } else if (theView instanceof TextView) {

                ((TextView) theView)
                        .setText(value instanceof SpannableStringBuilder ? (SpannableStringBuilder) value
                                : value.toString());
                return true;
            }
            // else if (theView instanceof NumberBox) {
            //
            // ((NumberBox) theView).setNum(value.toString(), true);
            // return true;
            // } else if (theView instanceof CheckLayout) {
            // //
            // // ((CheckLayout) theView).
            // }
        }
        return false;

    }

    public void loadImage(int pos, Object item, Object value, View convertView,
                          View theView, String strurl) {
        // TODO Auto-generated method stub

    }

    public int getWidth(Object item) {
        // TODO Auto-generated method stub
        return (int) (MobUtil.instance.screenWidth / 2);
    }

    public void callbackImageLoadingDone(Exception lastimageException) {
        // TODO Auto-generated method stub

    }

    public Object getClickedItem() {
        // TODO Auto-generated method stub
        if (clickedItem == -1) {
            return null;
        }
        return getItem(clickedItem);
    }

    public void markVisible(boolean isVisible) {
        this.isVisible = isVisible;
    }

    public int getVisibleOption(boolean isVisible) {
        if (isVisible) {
            return View.VISIBLE;
        } else {
            return View.GONE;
        }
    }

    public boolean hasSelected() {
        return this.selected.size() > 0;
    }

    public boolean isSelectAll() {

        return this.selected.size() == this.getCount();
    }

    public View getView(Object item, View convertView, int position,
                        ViewGroup parent) {

        try {

            ListenerBox listener;
            viewContentMap.put(convertView, position);
            if (listenerMaps != null) {
                for (Entry en : listenerMaps.entrySet()) {

                    Integer resourceid = ((Integer) en.getKey());
                    listener = ((ListenerBox) en.getValue());
                    listener.setPos(position);
                    View view = ViewUtil.findViewById(convertView, resourceid);
                    if (view == null) {
                        continue;
                    }
                    for (Entry<Integer, ActionListener> e : listener.handlers
                            .entrySet()) {
                        int actionType = e.getKey().intValue();
                        ListenerBox instance = new ListenerBox(listener,
                                actionType);
                        switch (actionType) {
                            case ActionListener.OnClick:
                                view.setOnClickListener(instance);
                                break;
                            case ActionListener.OnLongClick:
                                view.setOnLongClickListener(instance);
                                break;
                            case ActionListener.OnTouch:
                                view.setOnTouchListener(instance);
                                break;
                            case ActionListener.OnCheckChanged:
                                if (view instanceof CheckBox) {
                                    ((CheckBox) view)
                                            .setOnCheckedChangeListener(instance);
                                }
                                break;
                        }
                    }
                }
            }

            addStyles(position, convertView);
            // if (convertView instanceof ExpandableView) {
            // if (this.clickedItem != -1 && clickedItem == position) {
            // ((ExpandableView) convertView).expand();
            // } else {
            // ((ExpandableView) convertView).collapse();
            // }
            // }
            getViewInDetail(item, position, convertView);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return convertView;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (isPaged()) {
            if (inpage && (position == getCount() - 1)) {
                return this.footerView;
            }
            if (convertView == this.footerView) {
                convertView = null;
            }
        }
        if (parent != null && parent instanceof AbsListView) {
            this.scrollpos = ((AbsListView) parent).getFirstVisiblePosition();
        }
        this.latestPosition = position;
        Object item = getItem(position);
        // Logs.i("item " + item + " position " + position);
        setCreatedView(false);
        if (null == convertView) {
            setCreatedView(true);
            convertView = createItemView();
        }

        if (this.adaptInfo.scaledImageId != 0) {
            View view = parent;
            // if (view instanceof GridView) {
            // convertView.findViewById(this.adaptInfo.scaledImageId)
            // .getLayoutParams().height = this.itemHeight = ((GridView) view)
            // .getWidth() / ((GridView) view).getNumColumns() - 20;
            // }
        }
        ListenerBox listener;
        viewContentMap.put(convertView, position);
        if (listenerMaps != null) {
            for (Entry en : listenerMaps.entrySet()) {

                Integer resourceid = ((Integer) en.getKey());
                listener = ((ListenerBox) en.getValue());
                listener.setPos(position);
                View view = ViewUtil.findViewById(convertView, resourceid);
                if (view == null) {
                    continue;
                }
                for (Entry<Integer, ActionListener> e : listener.handlers
                        .entrySet()) {
                    int actionType = e.getKey().intValue();
                    ListenerBox instance = new ListenerBox(listener, actionType);
                    switch (actionType) {
                        case ActionListener.OnClick:
                            view.setOnClickListener(instance);
                            break;
                        case ActionListener.OnLongClick:
                            view.setOnLongClickListener(instance);
                            break;
                        case ActionListener.OnTouch:
                            view.setOnTouchListener(instance);
                            break;
                        case ActionListener.OnCheckChanged:
                            if (view instanceof CheckBox) {
                                ((CheckBox) view)
                                        .setOnCheckedChangeListener(instance);
                            }
                            break;
                    }
                }
            }
        }

        addStyles(position, convertView);
        // if (convertView instanceof ExpandableView) {
        // if (this.clickedItem != -1 && clickedItem == position) {
        // ((ExpandableView) convertView).expand();
        // } else {
        // ((ExpandableView) convertView).collapse();
        // }
        // }

        getViewInDetail(item, position, convertView);
        int getcount = inpage ? this.getItemDataSrc().getCount() - 1 : this
                .getCount() - 1;
        if (position == getcount) {
            if (continueRunner != null) {

                continueRunner.run();
            }

        }
        if (bannerpos != -1) {

        }
        if (bannerpos + 1 == position) {

            bannerpos = -1;
        }
        currView = convertView;
        return convertView;
    }

    boolean allowLoadMore;

    private void addStyles(int position, View convertView) {
        for (Entry<Integer, StyleBox> en : styleMaps.entrySet()) {

            View view = convertView.findViewById(en.getKey());
            if (view == null) {
                continue;
            }
            StyleBox stylebox = styleMaps.get(en.getKey());
            Map<Integer, Style> styles = (position == clickedItem ? stylebox.selectedStyle
                    : stylebox.handlers);
            for (Style style : styles.values()) {
                switch (style.styleitem) {
                    case Style.BACKGROUND_COLOR:
                        if (style.value instanceof Integer) {
                            view.setBackgroundColor(context.getResources()
                                    .getColor((Integer) style.value));

                        } else if (style.value instanceof String) {
                            view.setBackgroundColor(Color
                                    .parseColor((String) style.value));
                        }
                        break;
                    case Style.STRIKE_THRU_TEXT_FLAG:
                        if (view instanceof TextView) {
                            ((TextView) view).getPaint().setFlags(
                                    Paint.STRIKE_THRU_TEXT_FLAG);
                        }
                        break;
                    case Style.TEXT_COLOR:
                        if (view instanceof TextView) {

                            if (style.value instanceof Integer) {
                                ((TextView) view).setTextColor(context
                                        .getResources().getColor(
                                                (Integer) style.value));
                            } else if (style.value instanceof String) {
                                ((TextView) view).setTextColor(Color
                                        .parseColor((String) style.value));
                            }
                        } else {
                            try {
                                throw new Exception(
                                        "TEXT_COLOR's view is not isntanceof TextView but "
                                                + view.getClass());
                            } catch (Exception e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }
                        }

                        break;
                    case Style.VISIBLE:
                        view.setVisibility((Integer) style.value);
                        break;
                }
            }
        }
    }

    public static abstract class ActionListener {

        public static final int OnClick = 0;
        public static final int OnLongClick = 1;
        public static final int OnTouch = 2;
        public static final int OnCheckChanged = 3;
        public int listenerType = -1;
        private MapAdapter baseAdapter;
        public int resrcId;
        public View itemView;
        private ListenerBox listener;

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + listenerType;
            result = prime * result + resrcId;
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            ActionListener other = (ActionListener) obj;
            if (listenerType != other.listenerType)
                return false;
            if (resrcId != other.resrcId)
                return false;
            return true;
        }

        public int getListenerType() {
            return listenerType;
        }

        public int getResrcId() {
            return resrcId;
        }

        public ActionListener(int resrcId, int listenerType) {
            this.listenerType = listenerType;
            this.resrcId = resrcId;
        }

        public abstract void handle(MapAdapter mapAdapter, View view, int pos,
                                    ListenerBox listenerBox);

        public void invokeHandle(View view, ListenerBox listener) {
            this.listener = listener;
            handle(getBaseAdapter(), view, listener.pos, listener);
        }

        public int findViewIndex(View view) {
            View vg = (View) view.getParent();
            if (vg.getTag() == null) {
                return findViewIndex(vg);
            }
            return (Integer) vg.getTag();
        }

        public void setBaseAdapter(MapAdapter baseAdapter) {
            this.baseAdapter = baseAdapter;
        }

        public MapAdapter getBaseAdapter() {
            return baseAdapter;
        }
    }

    private int itemHeight = -1;

    public int getItemHeight() {
        return itemHeight;
    }

    public void setItemHeight(int itemHeight) {
        this.itemHeight = itemHeight;
    }

    public static class AdaptInfo {
        public static AdaptInfo generate() {
            return new AdaptInfo();
        }

        public int scaledImageId = -1;

        public int getScaledImageId() {
            return scaledImageId;
        }

        public void setScaledImageId(int scaledImageId) {
            this.scaledImageId = scaledImageId;
        }

        public ContinueRunner continueRunner;
        public MapContent listviewItemData;// actual data-carried object
        /**
         * data fields in
         */
        public String[] objectFields = new String[]{};
        // sequence which map to
        // view ids
        public Integer[] viewIds = new Integer[0]; // id array in listview item
        /**
         * layout id for each item in listview
         */
        public int listviewItemLayoutId;//
        public Set<ActionListener> actionListeners = new HashSet<ActionListener>(
                0);// varied action listeners
        public int pageSize = -1;
        public int adaptviewid = -1;
        public List<String> objectFieldList = new ArrayList<String>(0);
        public List viewIdList = new ArrayList(0);
        public Set<Style> styles = new HashSet<Style>();
        public int pagefooterId = 0;

        public ContinueRunner getContinueRunner() {
            return continueRunner;
        }

        public AdaptInfo addContinueRunner(ContinueRunner continueRunner) {
            this.continueRunner = continueRunner;
            return this;
        }

        public MapContent getListviewItemData() {
            return listviewItemData;
        }

        public AdaptInfo addListviewItemData(MapContent listviewItemData) {
            this.listviewItemData = listviewItemData;
            return this;
        }

        public String[] getObjectFields() {
            return objectFields;
        }

        public AdaptInfo addObjectFields(String[] objectFields) {
            this.objectFields = objectFields;
            return this;
        }

        public Integer[] getViewIds() {
            return viewIds;
        }

        public AdaptInfo addViewIds(Integer[] viewIds) {
            this.viewIds = viewIds;
            return this;
        }

        public int getListviewItemLayoutId() {
            return listviewItemLayoutId;
        }

        public AdaptInfo addListviewItemLayoutId(int listviewItemLayoutId) {
            this.listviewItemLayoutId = listviewItemLayoutId;
            return this;
        }

        public Set<ActionListener> getActionListeners() {
            return actionListeners;
        }

        public AdaptInfo addActionListeners(Set<ActionListener> actionListeners) {
            this.actionListeners = actionListeners;
            return this;
        }

        public int getAdaptviewid() {
            return adaptviewid;
        }

        public AdaptInfo addAdaptviewid(int adaptviewid) {
            this.adaptviewid = adaptviewid;
            return this;
        }

        public void setUnpaged() {
            pageSize = -1;
            pagefooterId = 0;
        }

        public float heightRatio;

        // in
        // which all events would be
        // received and have been done
        // handling.
        public AdaptInfo addActionListener(ActionListener actionListener) {
            actionListeners.add(actionListener);
            return this;
        }

        public AdaptInfo addStyle(Style style) {
            styles.add(style);
            return this;
        }

        public void appendResCntPair(int id, String item) {
            if (objectFieldList.size() == 0) {
                objectFieldList.addAll(Arrays.<String>asList(objectFields));
            }
            if (viewIdList.size() == 0) {
                viewIdList.addAll(Arrays.asList(viewIds));
            }
            objectFieldList.add(item);
            viewIdList.add(id);
        }

    }

    public ContinueRunner continueRunner;

    public ContinueRunner getContinueRunner() {
        return continueRunner;
    }

    public void setContinueRunner(ContinueRunner continueRunner) {
        this.continueRunner = continueRunner;
    }

    public static abstract class ContinueRunner {
        public Object lastItm;
        public String obj;
        public MapAdapter mapAdapter;

        Set<Integer> lastno = new TreeSet<Integer>();

        public abstract void run(MapAdapter mapAdapter, Object param,
                                 Object lastItm);

        public void clear() {
            lastno.clear();
        }

        public void run() {
            obj = getParam();

            if (!lastno.contains(mapAdapter.latestPosition)) {
                lastno.add(mapAdapter.latestPosition);
                run(mapAdapter, obj, lastItm);
            }

        }

        public MapAdapter getMapAdapter() {
            return mapAdapter;
        }

        public void setMapAdapter(MapAdapter mapAdapter) {
            this.mapAdapter = mapAdapter;
        }

        public abstract String getParam();

    }

    Map<Integer, AdaptInfo> id_adaptinfo = new HashMap<Integer, AdaptInfo>();

    public void notifyDataSetChanged(int id) {
        // TODO Auto-generated method stub

        if (id_adaptinfo.containsKey(id)) {

            deployAdapter(id);
        } else
            super.notifyDataSetChanged();
    }

    @Override
    public void notifyDataSetChanged() {
        // TODO Auto-generated method stub
        deployListeners(handlers);
        deployStyleBoxes(styles);

        super.notifyDataSetChanged();
    }

    public void addStyle(Style style) {
        this.styles.remove(style);
        this.styles.add(style);
    }

    public MapAdapter(Context context, ContainerInfo linearInfo) {
        init(context);
        addMapInfo(linearInfo.adaptInfo, true);
        this.linearInfo = linearInfo;

        // TODO Auto-generated constructor stub
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public long getCombinedChildId(long groupId, long childId) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public long getCombinedGroupId(long groupId) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public Object getGroup(int groupPosition) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public int getGroupCount() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public long getGroupId(int groupPosition) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void onGroupCollapsed(int groupPosition) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onGroupExpanded(int groupPosition) {
        // TODO Auto-generated method stub

    }
}
