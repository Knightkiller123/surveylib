package easygov.saral.harlabh.models.irismodel;

import com.google.gson.annotations.SerializedName;

/**
 * Created by apoorv on 15/09/17.
 */

public class Modality {
    public String demographics;

    @SerializedName("fp-image")
    public String fp_image;

    @SerializedName("fp-minutae")
    public String fp_minutae;

    public String iris;
    public String otp;
    public String pin;
}
