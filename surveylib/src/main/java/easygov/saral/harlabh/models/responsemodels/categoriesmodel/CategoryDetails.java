package easygov.saral.harlabh.models.responsemodels.categoriesmodel;

/**
 * Created by apoorv on 22/08/17.
 */

public class CategoryDetails {

    private String cn;
    private Integer cid;
    private String sn;
    private Integer sid;
    private Boolean bol;
    public Integer service_fee;
    public Integer service_charge;
    public String geography_district_name;
    public CategoryName category;
    public Integer geography_id;
    public ServiceName service;
    public Integer sgm_id;
    public String image;

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }


    public Integer getService_fee() {
        return service_fee;
    }

    public void setService_fee(Integer service_fee) {
        this.service_fee = service_fee;
    }

    public Integer getService_charge() {
        return service_charge;
    }

    public void setService_charge(Integer service_charge) {
        this.service_charge = service_charge;
    }

    public String getGeography_district_name() {
        return geography_district_name;
    }

    public void setGeography_district_name(String geography_district_name) {
        this.geography_district_name = geography_district_name;
    }

    public CategoryName getCategory() {
        return category;
    }

    public void setCategory(CategoryName category) {
        this.category = category;
    }

    public void setCategoryName(String s)
    {
        cn=s;
    }
    public void setCategoryId(Integer id)
    {
        cid=id;



    }
    public void setIsScheme(Boolean bool)
    {
//        this.category.scheme=bool;
        bol=bool;
        CategoryName obj =new CategoryName();
        obj.name=cn;
        obj.id=cid;
        obj.scheme=bol;
        setCategory(obj);

    }


    public Integer getGeography_id() {
        return geography_id;
    }

    public void setGeography_id(Integer geography_id) {
        this.geography_id = geography_id;
    }

    public ServiceName getService() {
        return service;
    }

    public void setService(ServiceName service) {
        this.service = service;
    }
    public void setServiceName(String s)
    {
        sn=s;

    }

    public void setServiceId(Integer idd)
    {
        sid=idd;
        ServiceName obj=new ServiceName();
        obj.id=sid;
        obj.name=sn;
        setService(obj);
    }

    public Integer getSgm_id() {
        return sgm_id;
    }

    public void setSgm_id(Integer sgm_id) {
        this.sgm_id = sgm_id;
    }


}
