package easygov.saral.harlabh.models.irismodel;

import com.google.gson.annotations.SerializedName;

/**
 * Created by apoorv on 15/09/17.
 */

public class IrisModel {
    public String consent;
    public Modality modality;
    public Pid pid;

    @SerializedName("aadhaar-id")
    public String aadhaar_id;

    public String hmac;

    @SerializedName("session-key")
    public Session_key session_key;  //done

    @SerializedName("unique-device-code")
    public String unique_device_code;

    public String dpId;
    public String rdsId;
    public String rdsVer;
    public String dc;
    public String mi;
    public String mc;
}
