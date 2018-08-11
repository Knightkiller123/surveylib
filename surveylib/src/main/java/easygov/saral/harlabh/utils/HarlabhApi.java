package easygov.saral.harlabh.utils;

import java.util.List;
import java.util.Map;

import easygov.saral.harlabh.models.applicationstatus.ApplicationStatus;
import easygov.saral.harlabh.models.documents.PojoDocument;
import easygov.saral.harlabh.models.responsemodels.aadhaarwebviewmodels.AadhaarWebviewModel;
import easygov.saral.harlabh.models.responsemodels.authoritymodel.AuthorityModel;
import easygov.saral.harlabh.models.responsemodels.beneficiarymodel.BenificiaryModell;
import easygov.saral.harlabh.models.responsemodels.categoriesmodel.Categories;
import easygov.saral.harlabh.models.responsemodels.couponmodel.CouponModel;
import easygov.saral.harlabh.models.responsemodels.documentsubtype.SubType;
import easygov.saral.harlabh.models.responsemodels.esignformmodel.EsignFormModel;
import easygov.saral.harlabh.models.responsemodels.familymodel.FamilyModel;
import easygov.saral.harlabh.models.responsemodels.filefieldsupdated.FileFieldsUpdate;
import easygov.saral.harlabh.models.responsemodels.filterstatus.FilterResponse;
import easygov.saral.harlabh.models.responsemodels.generalmodel.GeneralModel;
import easygov.saral.harlabh.models.responsemodels.getkyc.GetKycModel;
import easygov.saral.harlabh.models.responsemodels.kycmodel.KycModel;
import easygov.saral.harlabh.models.responsemodels.mandatoryfields.MandatoryFields;
import easygov.saral.harlabh.models.responsemodels.paymentinternal.InternalPayment;
import easygov.saral.harlabh.models.responsemodels.savemandatoryfields.SaveMandatoryFields;
import easygov.saral.harlabh.models.responsemodels.servicepickup.ServicePickUp;
import easygov.saral.harlabh.models.responsemodels.statesndistricts.DistrictsModel;
import easygov.saral.harlabh.models.responsemodels.statesndistricts.StatesModel;
import easygov.saral.harlabh.models.responsemodels.statusmodel.StatusResponse;
import easygov.saral.harlabh.models.responsemodels.usidmodel.UsidModel;
import easygov.saral.harlabh.models.responsemodels.filter.FilterApplicationModel;
import easygov.saral.harlabh.models.responsemodels.paymentmodel.PaymentModel;
import easygov.saral.harlabh.models.surveypaging.StartSurveyPaging;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.PartMap;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by apoorv on 08/08/17.
 */

public interface HarlabhApi {

    @FormUrlEncoded
    @POST("signup/creation/")
    Call<GeneralModel> signup(@Field("mobile") String test);

    @FormUrlEncoded
    @POST("signup/creation/")
    Call<AadhaarWebviewModel> signUpAadhaar(@Field("aadhaar_id") String test);

    @FormUrlEncoded
    @POST("signup/getaadhaarotp/")
    Call<AadhaarWebviewModel> getAadhaarOtp(@Field("aadhaar_id") String test);

    @FormUrlEncoded
    @POST("signup/getaadhaarotp/")
    Call<GeneralModel> getAadhaarOtpforkyc(@Field("aadhaar_id") String test);

    @FormUrlEncoded
    @POST("signup/otpverification/")
    Call<GeneralModel> mobileVerification(@Field("imei")String imei,@Field("deviceid") String deviceId,@Field("mobile") String mob ,@Field("otp") String otp,@Field("reset_password") String rp,@Field("client") String client);

    @FormUrlEncoded
    @POST("signup/otpverification/")
    Call<GeneralModel> aadhaarOtpVerify(@Field("imei")String imei,@Field("deviceid") String deviceId,@Field("uuid") String uuid,@Field("requestId") String reqid,@Field("aadhaar_id") String aaid,@Field("client") String client);

    /*@FormUrlEncoded
    @POST("signup/otpverification/")
    Call<GeneralModel> fingerCAptureApi(@Field("aadhaar_id")String adh,@Field("via")String fmr,@Field("enc_pid") String enc);*/

    @FormUrlEncoded
    @POST("signup/otpverification/")
    Call<GeneralModel> aadhaarworkaround(@Field("aadhaar_id")String aadhaar,
                                         @Field("via") String via,@Field("enc_pid") String encpid,
                                         @Field("transaction_id") String transid,@Field("otp") String otp);



    @FormUrlEncoded
    @POST("signup/resetpassword/")
    Call<GeneralModel> reset(@Field("aadhaar_id")String aadhaar,
                                         @Field("via") String via,@Field("enc_pid") String encpid,
                                         @Field("transaction_id") String transid,@Field("otp") String otp);
    @FormUrlEncoded
    @POST("signup/setphoneemail/")
    Call<GeneralModel> setPhoneEmail(@Field("phone") String phone, @Field("email") String email);

    @FormUrlEncoded
    @POST("signin/")
    Call<GeneralModel> signIn(@Field("imei")String imei,@Field("deviceid") String deviceId,@Field("number") String number,@Field("password") String pass,@Field("client") String client);

    @FormUrlEncoded
    @POST("signup/setpassword/")
    Call<GeneralModel> setPassword(@Field("password") String otp);

    @FormUrlEncoded
    @POST("district/")
    Call<DistrictsModel> getAllDistricts(@Field("size") String size);

    @FormUrlEncoded
    @POST("district/")
    Call<DistrictsModel> getSelectedDistricts(@Field("state_id") String sId,@Field("size") String strng);


    //Todo: remove this and use only districts
    @FormUrlEncoded
    @POST("state/getall/")
    Call<StatesModel> getAllStates(@Field("size") String sizes);

    @FormUrlEncoded
    @POST("signup/resetpassword/")
    Call<GeneralModel> resetPassword(@FieldMap Map<String,String> map);


    @FormUrlEncoded
    @POST("signup/profile/")
    Call<ResponseBody> signupProfile(@Field("mobile") String asd,@Field("first_name") String fn,@Field("middle_name") String mn,@Field("last_name") String ln,@Field("username") String un,@Field("email") String email
                                        ,@Field("profile_pic") String dp,@Field("date_of_birth") String dob,@Field("children_count") String cc,@Field("gender") String gender
                                 , @Field("martial_status") String ms,@Field("temporary_address") String ta,@Field("permanent_address") String pa,@Field("user_type") String user,@Field("date_of_marriage") String dom);


    @POST("logout/")
    Call<GeneralModel> logOut();

    @FormUrlEncoded
    @POST("getservices/")
    Call<Categories> getServices(@FieldMap Map<String,String> map);

   /* @FormUrlEncoded
    @POST("signup/creation/")
    Call<SignUpModel> signUpAadhaar(@Field("aadhaar_id") String id,@Field("request_type") String rt);*/

   //todo: readmandatoryfilefieldsforuser
    @FormUrlEncoded
    @POST("service/readmandatoryfilefieldsforuser/")
    Call<FileFieldsUpdate> readFileFields(@Field("service_id") String sid, @Field("survey_id") String surveyId, @Field("userServiceId") String usid, @Field("geo_id") String geoId);

    @Multipart
    @POST("service/savedocsforuser/")
    Call<UsidModel> saveDocGetId(@PartMap Map<String,RequestBody> map, @Part List<MultipartBody.Part> file );


    //NOT in USE
    @FormUrlEncoded
    @POST("service/readdocumentsubtypes/")
    Call<SubType> readSubDocumnets(@Field("document_type_id") String dti);

    @FormUrlEncoded
    @POST("survey/start/")
    Call<StartSurveyPaging> startSurvey(@Field("is_scheme")String isscheme, @Field("is_category")String isCat, @Field("selected_id") String sid, @Field("geography_id") String geoId,
    @Field("survey_id") String surveyid,@Field("profile_id") String pid,@Field("relation_type")String relation);

    @FormUrlEncoded
    @POST("survey/match/")
    Call<StartSurveyPaging> setEnt(@FieldMap Map<String ,String> map);

    @FormUrlEncoded
    @POST("service/readmandatoryfieldswithsurveydetailsforuser/")
    Call<MandatoryFields> readMandatoryFields(@Field("userServiceId") String usid,
                                              @Field("serviceId") String sid, @Field("surveyId")String id);

    @FormUrlEncoded
    @POST("service/savemandatoryfieldsforuser/")
    Call<SaveMandatoryFields> saveMandatoryFields(@FieldMap Map<String , String> map);

    @FormUrlEncoded
    @POST("service/readservicepickupaddress/")
    Call<ServicePickUp> pickUpAddress(@Field("userServiceId") String usid);

    @FormUrlEncoded
    @POST("service/savebillingaddress/")
    Call<GeneralModel> saveBillAddress(@FieldMap Map<String ,String> maps);

    @FormUrlEncoded
    @POST("service/readpaymentdetails/")
    Call<PaymentModel> payment(@Field("serviceInvoiceId") String asd);

    @FormUrlEncoded
    @POST("service/verifycoupon/")
    Call<CouponModel> applyCoupon(@Field("coupon_code") String cpnCode, @Field("userServiceId") String usid, @Field("merchantTxnId") String mcid );

    @FormUrlEncoded
    @POST("service/applycoupon/")
    Call<CouponModel> verifyCoupon(@Field("coupon_code") String cpnCode, @Field("merchantTxnId") String mcid , @Field("userServiceId") String usid);

    @FormUrlEncoded
    @POST("savekyc/")
    Call<GeneralModel> savekycData(@FieldMap Map<String,String> map);

    @FormUrlEncoded
    @POST("initiatekycforothers/")
    Call<GeneralModel> kycForOthers(@Field("aadhaar_id") String aid,@Field("survey_id") String sid);

    @FormUrlEncoded
    @POST("initiatekycforothers/")
    Call<GeneralModel> surveyInitiateKyc(@Field("uuid") String uuid,@Field("requestId") String reqid,@Field("aadhaar_id") String aid,@Field("survey_id") String sid);

    @FormUrlEncoded
    @POST("initiatekycforothers/")
    Call<KycModel> authenticateUser(@Field("aadhaar_id") String adhaar, @Field("via") String via,
                                    @Field("otp") String otp, @Field("transaction_id") String trans_id,
                                    @Field("enc_pid") String enc_pid,@Field("survey_id")String surveyid,
                                    @Field("relation_type") String relation,@Field("profile_id") String pfId);

    //Todo: check hindi
    @FormUrlEncoded
    @POST("service/servicedetails/")
    Call<EsignFormModel> getEsignForm(@Field("usid") String usid);

    @FormUrlEncoded
    @POST("service/updateandconfirmservicedetails/")
    Call<GeneralModel> esignSave(@FieldMap Map<String ,String> map);

    @FormUrlEncoded
    @POST("dashboard/filterapplication/")
    Call<FilterApplicationModel> getFilterServicesList(@Field("size") String size);

    @FormUrlEncoded
    @POST("dashboard/filterapplication/")
    Call<FilterApplicationModel> getFilterServicesListDate(@Field("size") String size,@Field("from_date") String from,
                                                           @Field("to_date") String to,
                                                           @Field("status_id") String statusid);


    @FormUrlEncoded
    @POST("dashboard/searchapplication/")
    Call<FilterApplicationModel> searchApplication(@Field("size") String size,@Field("search_for") String searchfor,
                                                   @Field("search_option")String searchoption);



    @FormUrlEncoded
    @POST("dashboard/applicationcountbystatus/")
    Call<ApplicationStatus> applicationStatuswithDate(@Field("from_date") String from,@Field("to_date") String todate);


    @POST("dashboard/applicationcountbystatus/")
    Call<ApplicationStatus> applicationStatus();

    @FormUrlEncoded
    @POST("survey/generate/")
    Call<GeneralModel> getSurveyId(@Field("myself") String myself,@Field(Constants.PARAM_BENEFICIARY_TYPE_ID )String beneficiaryId);


    @POST("get_settings/")
    Call<GeneralModel> checkAadhaarEnabled();

    @GET("for_kyc.html")
    Call<ResponseBody> irisTest(@FieldMap Map<String ,String> map);


    @FormUrlEncoded
    @POST("dashboard/get_service_status_mappings/")
    Call<StatusResponse> getstatus(@Field("user_service_id") String userserviceid);


    @FormUrlEncoded
    @POST("getsitecontent/")
    Call<FilterResponse> getABoutUsData(@Field("category") String category);

    @FormUrlEncoded
    @POST("changepassword/")
    Call<GeneralModel> changePass(@Field("oldpassword") String oldPass,@Field("newpassword") String newPass,
                                  @Field("confirmpassword") String cnfrmPass);


    @POST("dashboard/payment/")
    Call<InternalPayment> getPaymentDetails ();

    @POST("statusdata/")
    Call<FilterResponse> getStatus();

    @FormUrlEncoded
    @POST("get_authorities/")
    Call<AuthorityModel> getAuthorities(@Field("district_id") String distId,@Field("authority_type") String authType);


    @POST("getbeneficiaries/")
    Call<BenificiaryModell> getBeneficiary();

    @GET("userfamilyprofile/")
    Call<FamilyModel> familyModel(@Query("someoneelse") String selectedType);


    @DELETE("userfamilyprofile/delete/{userfamilyprofile_id}")
    Call<GeneralModel> getdele(@Path("userfamilyprofile_id") String distId);

    @FormUrlEncoded
    @POST("userfamilyprofile/")
    Call<GeneralModel> addRelative(@Field("name") String name , @Field("relationship") String relation);

    @POST("gender/")
    Call<GeneralModel> getGenderId();




    @GET("getkycufp/")
    Call<GetKycModel> getKyc(@Query("profile_id") String profileid,@Query("relation_type")String relType);


    @GET("getuserservicedocsdetails/")
    Call<PojoDocument> readDocsForUserByFieldID( @Query("field_id") String fieldId,
                                                 @Query("document_type_id") String document_type_id);

    @GET("getfiles/")
    Call<PojoDocument> getFiles(
            @Query("via") String via,
            @Query("user_service_id") String user_service_id,
            @Query("field_id") String field_id,
            @Query("document_type_id") String document_type_id);



    @DELETE("getfiles/")
    Call<GeneralModel> deleteFiles(
            @Query("user_service_id") String user_service_id,
            @Query("field_id") String field_id,
            @Query("document_type_id") String document_type_id);


    @FormUrlEncoded
    @POST("userservicefeedback/")
    Call<GeneralModel> userServiceFeedback(
            @Field("userserviceid") String user_service_id,
            @Field("rating") String rating,
            @Field("feedback_message") String feedback_message);




}
