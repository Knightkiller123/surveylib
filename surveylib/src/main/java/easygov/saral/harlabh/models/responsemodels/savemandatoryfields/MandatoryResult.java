package easygov.saral.harlabh.models.responsemodels.savemandatoryfields;

import easygov.saral.harlabh.models.responsemodels.paymentmodel.ServiceInvoiceObj;

/**
 * Created by apoorv on 05/09/17.
 */

public class MandatoryResult {
    public String next_link;
    public String result;
    public String message = null;
    public ServiceInvoiceObj payment_detail;
}
