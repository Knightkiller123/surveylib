package easygov.saral.harlabh.models;

/**
 * Created by apoorv on 22/08/17.
 */

public class ServicesDetails {
    public Integer sgmid;
    public String serviceName;
    public Integer serviceId;
    public Integer serviceFee;
    public Integer serviceCharge;
    public Integer scheme;

    public Integer getGeographyId() {
        return geographyId;
    }

    public void setGeographyId(Integer geographyId) {
        this.geographyId = geographyId;
    }

    public Integer geographyId;

    public Integer getScheme() {
        return scheme;
    }

    public void setScheme(Integer scheme) {
        this.scheme = scheme;
    }



    public Integer getSgmid() {
        return sgmid;
    }

    public void setSgmid(Integer sgmid) {
        this.sgmid = sgmid;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public Integer getServiceId() {
        return serviceId;
    }

    public void setServiceId(Integer serviceId) {
        this.serviceId = serviceId;
    }

    public Integer getServiceFee() {
        return serviceFee;
    }

    public void setServiceFee(Integer serviceFee) {
        this.serviceFee = serviceFee;
    }

    public Integer getServiceCharge() {
        return serviceCharge;
    }

    public void setServiceCharge(Integer serviceCharge) {
        this.serviceCharge = serviceCharge;
    }
}
