package com.news.yazhidao.widget.imagewall;

public class ViewWall {
//    public static final int STYLE_7_232 = R.layout.wall_7_232;
//    public static final int STYLE_9 = R.layout.wall_9;
//    public int layoutid;
//
//    /**
//     * @param ctx
//     * @param layoutid : walllayout
//     * @param urls     : net source
//     */
//
//    public ViewWall(Context ctx) {
//        super();
//        this.ctx = ctx;
//    }
//
//    public Context ctx;
//
//    public int getId() {
//        return layoutid;
//    }
//
//    public void setLayoutId(int id) {
//        switch (id) {
//            case STYLE_7_232:
//                ids = new int[]{R.id.col11, R.id.col12, R.id.col21, R.id.col22,
//                        R.id.col23, R.id.col31, R.id.col32};
//            case STYLE_9:
//                ids = new int[]{R.id.col11, R.id.col12, R.id.col13, R.id.col21,
//                        R.id.col22, R.id.col23, R.id.col31, R.id.col32, R.id.col33};
//        }
//        this.layoutid = id;
//    }
//
//    public View inflate() {
//        if (layoutid == 0) {
//            throw new RuntimeException("inner toFlate Id is zero");
//        }
//        int theid = layoutid;
//        return inflate(theid);
//    }
//
//    public View inflate(int theid) {
//        view = LayoutInflater.from(ctx).inflate(theid, null);
//        return view;
//    }
//
//    public void setData() {
//        if (urls == null) {
//            throw new RuntimeException("urls is not setted");
//        }
//        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
//                ctx).threadPriority(Thread.NORM_PRIORITY - 2)
//                .denyCacheImageMultipleSizesInMemory()
//                .diskCacheFileNameGenerator(new Md5FileNameGenerator())
//                .diskCacheSize(50 * 1024 * 1024)
//                        // 50 Mb
//                .tasksProcessingOrder(QueueProcessingType.LIFO)
//                .writeDebugLogs() // Remove for release app
//                .build();
//        // Initialize ImageLoader with configuration.
//        ImageLoader.getInstance().init(config);
//
//
//        int i;int bal = 0;
//        for (i = 0; i < urls.size(); i++) {
//
//            if (ids.length >= i + 1) {
//                View picwallview = ((ImageView) view.findViewById(ids[i+bal]));
//                if (picwallview != null) {
//                    picwallview.setVisibility(View.VISIBLE);
//                } else {
//                    i--;
//                    bal++;
//                    continue;
//                }
//
//            }
//            if(urls.get(i).containsKey("scaledh")) {
//                ((View) ((ImageView) view.findViewById(ids[i + bal])).getParent()).getLayoutParams().height = Integer.parseInt(urls.get(i).get("scaledh"));
//                ((ImageView) view.findViewById(ids[i + bal])).getLayoutParams().height = Integer.parseInt(urls.get(i).get("scaledh"));
//                ((ImageView) view.findViewById(ids[i + bal])).getLayoutParams().width = Integer.parseInt(urls.get(i).get("scaledw"));
//                ((ImageView) view.findViewById(ids[i + bal])).invalidate();
//            }
//            final int j = i;
//            ImageLoader.getInstance().displayImage(urls.get(i).get("img"),
//                    ((ImageView) view.findViewById(ids[i+bal])),
//                    new ImageLoadingListener() {
//
//                        @Override
//                        public void onLoadingStarted(String arg0, View arg1) {
//                            // TODO Auto-generated method stub
//
//                        }
//
//                        @Override
//                        public void onLoadingFailed(String arg0, View arg1,
//                                                    FailReason arg2) {
//                            // TODO Auto-generated method stub
//
//                        }
//
//                        @Override
//                        public void onLoadingComplete(String arg0, View arg1,
//                                                      Bitmap arg2) {
//                            // TODO Auto-generated method stub
//                            // BitmapFactory.Options options = new
//                            // BitmapFactory.Options();
//                            //
//                            // options.inJustDecodeBounds = true;
//                            // BitmapFactory.decodeFile();
//
//                            WallActivity.url_height.put(urls.get(j).get("img").toString(),
//                                    arg2.getHeight());
//                        }
//
//                        @Override
//                        public void onLoadingCancelled(String arg0, View arg1) {
//                            // TODO Auto-generated method stub
//
//                        }
//                    });
//
//            ((ImageView) view.findViewById(ids[i+bal]))
//                    .setOnClickListener(new OnClickListener() {
//
//                        @Override
//                        public void onClick(View v) {
//                            // TODO Auto-generated method stub
////                            for (Map m : urls) {
////                                if (m == null) {
////                                    continue;
////                                }
////                                String strurl = m.get("img").toString();
////                                BitmapFactory.Options op = null;
////                                while (op == null)
////                                    try {
////                                        op = BitmapUtil.getBitmapFactoryOptions(strurl);
////                                    } catch (Exception e) {
////                                        // TODO Auto-generated catch block
////                                        continue;
////                                    }
////
////                                float thumbnailHeight = op.outHeight
////                                        * (GlobalParams.screenWidth / 2f)
////                                        / op.outWidth;
////                                BitmapInfo bitmapInfo = new BitmapInfo((int) (GlobalParams.screenWidth / 2f),
////                                        (int) thumbnailHeight);
////                                bitmapinfos.put(strurl, bitmapInfo);
////                            }
//
//
//                            int end = urls.size() > 9 ? 9 : urls.size();
//                            WallActivity.browsedata = urls.subList(0, end);
//
//                            ImageLoader.getInstance().getDiskCache()
//                                    .get(urls.get(j).get("img"));
//                            v.getContext().startActivity(
//                                    new Intent(v.getContext(),
//                                            WallActivity.class).putExtra("page", j));
//                        }
//                    });
////            Map m = urls.get(i);
////            if (m == null) {
////                continue;
////            }
////            String strurl = m.get("img").toString();
////            BitmapFactory.Options op = null;
////            while (op == null)
////                try {
////                    op = BitmapUtil.getBitmapFactoryOptions(strurl);
////                } catch (Exception e) {
////                    // TODO Auto-generated catch block
////                    continue;
////                }
////            new Random().nextInt(5);
////            float thumbnailHeight = op.outHeight
////                    * (GlobalParams.screenWidth / 2f)
////                    / op.outWidth;
////            BitmapInfo bitmapInfo = new BitmapInfo((int) (GlobalParams.screenWidth / 2f),
////                    (int) thumbnailHeight);
////            bitmapinfos.put(strurl, bitmapInfo);
//
//
//        }
//        if (i - 1 <= 3) {
//
//            view.findViewById(R.id.row3).setVisibility(View.GONE);
//        }
//        if (i - 1 <= 1) {
//            view.findViewById(R.id.row2).setVisibility(View.GONE);
//        }
//    }
//
//    Map<String, BitmapInfo> bitmapinfos = new HashMap<String, BitmapInfo>();
//
//    public class BitmapInfo {
//        public int width;
//        public int height;
//
//        public BitmapInfo(int width, int height) {
//            this.width = width;
//            this.height = height;
//        }
//    }
//
//    public View view;
//
//    int[] ids;
//    String[] localpath;
//
//    private List<HashMap<String, String>> urls;
//
//    public List<HashMap<String, String>> getUrls() {
//        return urls;
//    }
//
//    public void setUrls(List<HashMap<String, String>> urls) {
//        this.urls = urls;
//    }
//
//    public void setUrls(String jsonArrayRightValue) {
//        this.urls = ((List) (new JsonUtil().fromJsonArray(jsonArrayRightValue)));
//    }
//
//    public static void add(ImageWallView parent, Object source, int layoutid) {
//
//        ViewWall wall = new ViewWall(parent.getContext());
//        wall.setLayoutId(layoutid);
//        if (source instanceof String) {
//            wall.setUrls(source.toString());
//        } else if (source instanceof List) {
//            wall.setUrls(((List) (source)));
//        }
//        ((ImageWallView) parent).addView(wall.inflate());
//        wall.setData();
//    }

}
