package com.news.yazhidao.entity;

/**
 * Created by Administrator on 2016/4/18.
 */
public class LocationEntity {
    private String address;
    private String city;
    private String cityCode;
    private String country;
    private String countryCode;
    private String district;
    private String province;
    private String street;
    private String streetNumber;

    public LocationEntity(String address) {
        this.address = address;
    }

    public LocationEntity(String address, String city, String cityCode, String country, String countryCode, String district, String province, String street, String streetNumber) {
        this.address = address;
        this.city = city;
        this.cityCode = cityCode;
        this.country = country;
        this.countryCode = countryCode;
        this.district = district;
        this.province = province;
        this.street = street;
        this.streetNumber = streetNumber;
    }

    @Override
    public String toString() {
        return "LocationEntity{" +
                "address='" + address + '\'' +
                ", city='" + city + '\'' +
                ", cityCode='" + cityCode + '\'' +
                ", country='" + country + '\'' +
                ", countryCode='" + countryCode + '\'' +
                ", district='" + district + '\'' +
                ", province='" + province + '\'' +
                ", street='" + street + '\'' +
                ", streetNumber='" + streetNumber + '\'' +
                '}';
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCityCode() {
        return cityCode;
    }

    public void setCityCode(String cityCode) {
        this.cityCode = cityCode;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getStreetNumber() {
        return streetNumber;
    }

    public void setStreetNumber(String streetNumber) {
        this.streetNumber = streetNumber;
    }
}

