package easygov.saral.harlabh.models.surveypaging;

import java.util.List;

/**
 * Created by apoorv on 14/11/17.
 */

public class SurveyPagingData {
    public String service_charge;
    public String service__name;
    public String service__department__name;
    public String service__special_condition;
    public Integer service__service_level;
    public String govt_fee;
    public List<String> benefits =null;
    public String service__analysis;
    public List<Object> attachment_list = null;
    public String service__benefits;
    public String service__service_category__name;
    public Integer id;
    public Boolean has_elgibility_survey;
    public String service__monetary_benefits;
    public String service__benefit_unit;
    public Boolean status;
    public List<CustomBenefits> benefits_list;
    public boolean enable_apply;
}
