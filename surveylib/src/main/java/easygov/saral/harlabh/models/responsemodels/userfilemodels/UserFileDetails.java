package easygov.saral.harlabh.models.responsemodels.userfilemodels;

import java.util.List;

/**
 * Created by apoorv on 28/08/17.
 */

public class UserFileDetails {
    public Boolean field__is_master_table;
    public String field__display_name;
    public Boolean is_label;
    public String rule_type;
    public Integer id;
    public Object field__field_group__system_name;
    public Integer field__id;
    public Object field__field_group__name;
    public String field__system_name;
    public Object field__master_table_name;
    public Boolean validate;
    public String display_name;
    public String field__field_type;
    public String field__table_name;
    public Object custom_css;
    public String val;
    public Object field__field_group__id;
    public List<FileOption> options = null;


    public UserFileDetails(String field__field_group__name,Integer field__id, Integer id, String field__system_name, String field__field_group__system_name,  String display_name,String field__display_name,  String field__field_group__id,  String field__field_type, List<FileOption> options) {
        this.field__field_group__name=field__field_group__name;
        this.field__id = field__id;
        this.id = id;
        this.field__system_name = field__system_name;
        this.field__field_group__system_name = field__field_group__system_name;
        this.display_name = display_name;
        this.field__display_name = field__display_name;
        this.field__field_group__id = field__field_group__id;
        this.field__field_type = field__field_type;
        this.options = options;
    }

}
