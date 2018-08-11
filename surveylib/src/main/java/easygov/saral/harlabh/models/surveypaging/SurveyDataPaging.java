package easygov.saral.harlabh.models.surveypaging;

import java.util.List;

/**
 * Created by apoorv on 31/08/17.
 */

public class SurveyDataPaging {

    public Boolean has_survey;
    public List<Integer> current_scheme_list = null;
    public String rule_type;
    public List<Integer> existing_field_list = null;
    public List<Field_datumPaging> field_data = null;
    public Integer survey_id;

    public QualifiedSchemePaging qualified_schemes = null;
}
