package easygov.saral.harlabh.models.responsemodels.statusmodel;

import java.util.List;

/**
 * Created by apoorv on 11/12/17.
 */

public class StatusData {
    public String next_page;
    public Integer total_count;
    public Integer total_pages;
    public String previous_page;
    public List<StatusList> objects = null;
}
