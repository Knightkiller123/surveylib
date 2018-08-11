package easygov.saral.harlabh.utils;

/**
 * Created by vineetverma on 07/09/17.
 */

import java.io.IOException;
import java.lang.annotation.Annotation;

import easygov.saral.harlabh.models.responsemodels.generalmodel.GeneralModel;
import retrofit2.Response;
import retrofit2.Converter;
import okhttp3.ResponseBody;

public class ErrorUtils {

    public static GeneralModel parseError(Response<GeneralModel> response) {
        Converter<ResponseBody, GeneralModel> converter =
                RestClient.retrofit().responseBodyConverter(GeneralModel.class, new Annotation[0]);

        GeneralModel error;

        try {
            error = converter.convert(response.errorBody());
        } catch (IOException e) {
            return new GeneralModel();
        }

        return error;
    }

}

