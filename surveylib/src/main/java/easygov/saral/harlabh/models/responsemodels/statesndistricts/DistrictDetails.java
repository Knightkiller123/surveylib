package easygov.saral.harlabh.models.responsemodels.statesndistricts;

/**
 * Created by apoorv on 17/08/17.
 */

public class DistrictDetails {

    public String district_name;
    public String state_code;
    public Integer state_id;
    public String district_code;
    public String state_name;
    public Integer districts_id;
    public Integer geography_id;

    public String getDistrict_name() {
        return district_name;
    }

    public void setDistrict_name(String district_name) {
        this.district_name = district_name;
    }

    public Integer getDistricts_id() {
        return districts_id;
    }

    public void setDistricts_id(Integer districts_id) {
        this.districts_id = districts_id;
    }

    public Integer getGeography_id() {
        return geography_id;
    }

    public void setGeography_id(Integer geography_id) {
        this.geography_id = geography_id;
    }
}
