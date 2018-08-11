package easygov.saral.harlabh.models.responsemodels.categoriesmodel;

import java.util.List;

/**
 * Created by apoorv on 22/08/17.
 */

public class PagingCategories {
   /* public int total_pages;
    public int total_count;
    public List<CategoryDetails> objects ;
    public int next_page;
    public String previous_page;*/

    public String previous_page;
    public String next_page;
    public List<CategoryDetails> objects = null;
    public Integer total_count;
    public Integer total_pages;

}
