package com.example.jasonhu.recommendpoi.BaseClass.CityInfo;

public class County {
    //省份名
    private String CountyName;
    //省份ID
    private String CountyId;

    private String CityId;

    public String getCountyId() {
        return CountyId;
    }

    public String getCountyName() {
        return CountyName;
    }

    public String getCityId() {
        return CityId;
    }

    public void setCountyId(String CountyId) {
        this.CountyId = CountyId;
    }

    public void setCountyName(String CountyName) {
        this.CountyName = CountyName;
    }
    public void setCityId(String CityId){
        this.CityId=CityId;
    }
}
