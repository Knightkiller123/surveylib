package easygov.saral.harlabh.models.responsemodels.generalmodel;

import java.util.List;

import easygov.saral.harlabh.models.responsemodels.kycmodel.Kyc;
import easygov.saral.harlabh.models.responsemodels.mandatoryfields.FieldsOptions;
import easygov.saral.harlabh.models.responsemodels.paymentmodel.ServiceInvoiceObj;

/**
 * Created by apoorv on 21/08/17.
 */

public class Datum {
    public String token;
    public String message;
    public String next_link;
    public String result;
    public String serviceInvoiceId;
    public String survey_id;
    public ServiceInvoiceObj payment_detail;
    public Integer  is_aadhaar;
    public String is_scheme_enabled;
    public String current_version;
    public String enforce_version;
    public String is_aadhaar_enabled;
    public String  transaction_id;
    public Integer is_email_mandatory;
    public String service_category_image_url;
    public String enable_apply_non_aadhaar;
    public String phone;
    public String email;
    public String is_app_maintenance_on;
    public String getting_aadhaar_kyc;
    public Kyc kyc;

    public String enable_signup_aadhaar;
    public List<FieldsOptions> options = null;
}
