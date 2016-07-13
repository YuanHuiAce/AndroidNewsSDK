package com.news.yazhidao.net;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by fengjigang on 15/1/14.
 */
public class NetWorkRequestManager {
    private HashMap<String,ArrayList<NetworkRequest>> mRequestCache;
    private static NetWorkRequestManager instance;
    private NetWorkRequestManager(){
        mRequestCache=new HashMap<String, ArrayList<NetworkRequest>>();
    }
    public static NetWorkRequestManager getInstance(){
        if(instance==null){
            instance=new NetWorkRequestManager();
        }
        return instance;
    }
    public void cancel(String key){
        ArrayList<NetworkRequest> requests = mRequestCache.get(key);
        if(requests!=null&&requests.size()>0){
            for(NetworkRequest request:requests){
                request.cancel(true);
            }
        }
    }
    public void cancelAll(){
        if(mRequestCache.size()>0){
            for(Map.Entry<String,ArrayList<NetworkRequest>> entry:mRequestCache.entrySet()){
                ArrayList<NetworkRequest> requests = entry.getValue();
                if(requests!=null&&requests.size()>0){
                    for(NetworkRequest request:requests){
                        request.cancel(true);
                    }
                }
            }
        }
    }
    public void execute(String key, NetworkRequest request){
        ArrayList<NetworkRequest> requests = mRequestCache.get(key);
        if(requests==null){
            requests=new ArrayList<NetworkRequest>();
            requests.add(request);
            request.execute();
        }
        mRequestCache.put(key,requests);
    }
}
