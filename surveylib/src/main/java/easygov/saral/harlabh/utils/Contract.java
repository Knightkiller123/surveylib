package easygov.saral.harlabh.utils;

import android.provider.BaseColumns;

/**
 * Created by apoorv on 22/08/17.
 */

public class Contract  {

    public static class ServiceCategoriesDetailsDb implements BaseColumns
    {
        public static final String TABLE_NAME = "servicecategories";

        public static final String servicecharge="serviceservicecharge";
        public static final String servicefee="servicefee";
        public static final String sgmid="sgmid";

        public static final String categoryname="categoryname";
        public static final String categoryid="cid";

        public static final String servicename="servicename";
        public static final String serviceid="sid";

        public static final String geoid="geoid";
        public static final String isscheme="isscheme";

        public static final String geodistname="geodistname";

        public static final String image="img";



    }

    public static class  ServicesDb implements BaseColumns
    {
        public static final String TABLE_NAME = "servicec";
        public static final String categoryid="cid1";
        public static final String isscheme="isscheme1";
        public static final String geoid="geoid1";
        public static final String geodistname="geodistname1";
        public static final String categoryname="categoryname1";
    }

    public static class ServiceSubCategory implements BaseColumns
    {
        public static final String TABLE_NAME = "servicecat";
        public static final String servicename="servicename1";
        public static final String serviceid="sid1";
        public static final String servicecharge="serviceservicecharge1";
        public static final String servicefee="servicefee1";
        public static final String sgmid="sgmid1";

    }


    public static class StatesDb implements BaseColumns
    {
        public static final String TABLE_NAME = "statestable";
        public static final String id="iddds";
        public static final String name="name";
    }

    public static class DistrictsDb implements BaseColumns
    {
        public static final String TABLE_NAME = "districts";
        public static final String id="iddds";
        public static final String Identifier="identifier";
        public static final String name="name";
    }
}
