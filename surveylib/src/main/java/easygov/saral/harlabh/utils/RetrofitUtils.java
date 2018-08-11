package easygov.saral.harlabh.utils;





import java.io.File;

import okhttp3.MediaType;
import okhttp3.RequestBody;


/**
 * Created by cbll88 on 6/7/16.
 */
public class RetrofitUtils {

    public static RequestBody StringtoRequestBody(String string){
        return RequestBody.create(MediaType.parse(Constants.MIME_TYPE_TEXT), string);
    }




    public static RequestBody ImagetoRequestBody(File file){
        return RequestBody.create(MediaType.parse("multipart/form-data"), file);
    }

    public static RequestBody PDFtoRequestBody(File file){
        return RequestBody.create(MediaType.parse("application/pdf"), file);
    }

    public static RequestBody VideotoRequestBody(File file){
        return RequestBody.create(MediaType.parse(Constants.MIME_TYPE_VIDEO), file);
    }

    public static RequestBody AudiotoRequestBody(File file){
        return RequestBody.create(MediaType.parse(Constants.MIME_TYPE_AUDIO), file);
    }

    public static String CreateFileName(File file){
        if (file != null) {
            return "\";filename=\"" +file.getName();
        }
        return "\";filename=\"image.jpg";
    }
}
