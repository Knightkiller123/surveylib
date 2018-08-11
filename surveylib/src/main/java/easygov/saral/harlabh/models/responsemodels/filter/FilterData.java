package easygov.saral.harlabh.models.responsemodels.filter;

import java.util.List;

/**
 * Created by apoorv on 26/09/17.
 */

public class FilterData {

    public Integer total_count;
    public String previous_page;
    public List<FilterListData> objects = null;
    public Integer total_pages;
    public String next_page;
}
