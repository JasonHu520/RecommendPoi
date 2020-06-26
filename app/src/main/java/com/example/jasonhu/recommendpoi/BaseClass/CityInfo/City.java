package com.example.jasonhu.recommendpoi.BaseClass.CityInfo;

public class City {
    //省份名
    private String CityName;
    //省份ID
    private String CityId;

    private String ProvinceId;

    public String getCityId() {
        return CityId;
    }

    public String getCityName() {
        return CityName;
    }

    public String getProvinceId() {
        return ProvinceId;
    }

    public void setCityId(String CityId) {
        this.CityId = CityId;
    }

    public void setCityName(String CityName) {
        this.CityName = CityName;
    }

    public void setProvinceId(String provinceId) {
        this.ProvinceId = provinceId;
    }
}
