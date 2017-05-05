package com.news.sdk.entity;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Administrator on 2016/9/6.
 */
public class AdDetailEntity implements Serializable {

    private int status;
    private int version;
    private String message;
    private Data data;

    public void setStatus(int status) {
        this.status = status;
    }

    public int getStatus() {
        return status;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public int getVersion() {
        return version;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public Data getData() {
        return data;
    }

    public class Event {
        private int event_key;
        private String event_value;

        public void setEventKey(int eventKey) {
            this.event_key = eventKey;
        }

        public int getEventKey() {
            return event_key;
        }

        public void setEventValue(String eventValue) {
            this.event_value = eventValue;
        }

        public String getEventValue() {
            return event_value;
        }

    }

    public class AdNative {

        private int index;
        private String type;
        private int template_id;
        private String index_value;
        private int required_field;
        private int action_type;
        private String required_value;

        public void setIndex(int index) {
            this.index = index;
        }

        public int getIndex() {
            return index;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getType() {
            return type;
        }

        public void setTemplateId(int templateId) {
            this.template_id = templateId;
        }

        public int getTemplateId() {
            return template_id;
        }

        public void setIndexValue(String indexValue) {
            this.index_value = indexValue;
        }

        public String getIndexValue() {
            return index_value;
        }

        public void setRequiredField(int requiredField) {
            this.required_field = requiredField;
        }

        public int getRequiredField() {
            return required_field;
        }

        public void setActionType(int actionType) {
            this.action_type = actionType;
        }

        public int getActionType() {
            return action_type;
        }

        public void setRequiredValue(String requiredValue) {
            this.required_value = requiredValue;
        }

        public String getRequiredValue() {
            return required_value;
        }

    }

    public class Creative {

        private int cid;
        private int index;
        private List<String> impression;
        private List<String> click;
        private List<Event> event;
        private String admark;
        private List<AdNative> ad_native;
        private List<Tracking> tracking;

        public void setCid(int cid) {
            this.cid = cid;
        }

        public int getCid() {
            return cid;
        }

        public void setIndex(int index) {
            this.index = index;
        }

        public int getIndex() {
            return index;
        }

        public void setImpression(List<String> impression) {
            this.impression = impression;
        }

        public List<String> getImpression() {
            return impression;
        }

        public void setClick(List<String> click) {
            this.click = click;
        }

        public List<String> getClick() {
            return click;
        }

        public void setEvent(List<Event> event) {
            this.event = event;
        }

        public List<Event> getEvent() {
            return event;
        }

        public void setAdmark(String admark) {
            this.admark = admark;
        }

        public String getAdmark() {
            return admark;
        }

        public void setAdNative(List<AdNative> adNative) {
            this.ad_native = adNative;
        }

        public void setAd_native(List<AdNative> ad_native) {
            this.ad_native = ad_native;
        }

        public List<Tracking> getTracking() {
            return tracking;
        }

        public void setTracking(List<Tracking> tracking) {
            this.tracking = tracking;
        }
    }

    public class Tracking{
        private String tracking_key;
        private List<String> tracking_value;

        public String getTracking_key() {
            return tracking_key;
        }

        public void setTracking_key(String tracking_key) {
            this.tracking_key = tracking_key;
        }

        public List<String> getTracking_value() {
            return tracking_value;
        }

        public void setTracking_value(List<String> tracking_value) {
            this.tracking_value = tracking_value;
        }
    }

    public class Adspace {

        private int aid;
        private List<Creative> creative;
        private int adformat;

        public void setAid(int aid) {
            this.aid = aid;
        }

        public int getAid() {
            return aid;
        }

        public void setCreative(List<Creative> creative) {
            this.creative = creative;
        }

        public List<Creative> getCreative() {
            return creative;
        }

        public void setAdformat(int adformat) {
            this.adformat = adformat;
        }

        public int getAdformat() {
            return adformat;
        }

    }

    public class Data {
        private List<Adspace> adspace;
        public void setAdspace(List<Adspace> adspace) {
            this.adspace = adspace;
        }

        public List<Adspace> getAdspace() {
            return adspace;
        }

    }

}
