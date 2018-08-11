package easygov.saral.harlabh.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.List;

import easygov.saral.harlabh.models.responsemodels.categoriesmodel.CategoryDetails;
import easygov.saral.harlabh.models.ServicesDetails;
import easygov.saral.harlabh.models.responsemodels.statesndistricts.StateDetails;

/**
 * Created by apoorv on 22/08/17.
 */

public class DbHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "easygovern.db";
    public static final int DATABASE_VERSION = 7;
    private Context context;


    private static final String SQL_CREATE_serviceCategoriesDetailsDb =
            "CREATE TABLE " + Contract.ServiceCategoriesDetailsDb.TABLE_NAME + " (" +
                    Contract.ServiceCategoriesDetailsDb._ID + " INTEGER PRIMARY KEY," +
                    Contract.ServiceCategoriesDetailsDb.categoryname + " TEXT,"
                    + Contract.ServiceCategoriesDetailsDb.categoryid + " TEXT," +
                    Contract.ServiceCategoriesDetailsDb.sgmid + " UNIQUE,"  +
                    Contract.ServiceCategoriesDetailsDb.isscheme + " TEXT," +
                    Contract.ServiceCategoriesDetailsDb.geoid + " TEXT," +
                    Contract.ServiceCategoriesDetailsDb.servicename + " TEXT," +
                    Contract.ServiceCategoriesDetailsDb.serviceid + " TEXT," +
                    Contract.ServiceCategoriesDetailsDb.servicefee + " TEXT," +
                    Contract.ServiceCategoriesDetailsDb.servicecharge + " TEXT," +
                    Contract.ServiceCategoriesDetailsDb.geodistname + " TEXT,"+
                    Contract.ServiceCategoriesDetailsDb.image + " TEXT)"
            ;



    private static final String SQL_CREATE_StatesDb=
            "CREATE TABLE " + Contract.StatesDb.TABLE_NAME + " (" +
                    Contract.StatesDb._ID + " INTEGER PRIMARY KEY," +
                    Contract.StatesDb.id + " UNIQUE," +Contract.StatesDb.name + " TEXT)";

    private static final String SQL_CREATE_DistrictsDb="CREATE TABLE " + Contract.DistrictsDb.TABLE_NAME + " (" +
            Contract.DistrictsDb._ID + " INTEGER PRIMARY KEY," +
            Contract.DistrictsDb.id + " UNIQUE,"+Contract.DistrictsDb.Identifier + " TEXT," +Contract.DistrictsDb.name + " TEXT)";



    public DbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context=context;
    }

   /* public DbHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context=context;
    }*/

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_serviceCategoriesDetailsDb);
       /* db.execSQL(SQL_CREATE_StatesDb);
        db.execSQL(SQL_CREATE_DistrictsDb);*/
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + Contract.ServiceCategoriesDetailsDb.TABLE_NAME);
        onCreate(db);

    }

    public void clearDb()
    {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DROP TABLE IF EXISTS " + Contract.ServiceCategoriesDetailsDb.TABLE_NAME);
        onCreate(db);
       // db.execSQL("DELETE FROM "+ Contract.ServiceCategoriesDetailsDb.TABLE_NAME);
      //  db.execSQL("DROP TABLE IF EXISTS " + Contract.ServiceCategoriesDetailsDb.TABLE_NAME);
        db.close();
    }


    public void addServiceCategoryDetails(List<CategoryDetails> list)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        for(int i=0;i<list.size();i++)
        {
            ContentValues values = new ContentValues();
            values.put(Contract.ServiceCategoriesDetailsDb.categoryname, list.get(i).category.name);
            values.put(Contract.ServiceCategoriesDetailsDb.categoryid, list.get(i).category.id);

            values.put(Contract.ServiceCategoriesDetailsDb.sgmid, list.get(i).sgm_id);
            if(list.get(i).category.scheme)
            values.put(Contract.ServiceCategoriesDetailsDb.isscheme, 1);

            else {
                values.put(Contract.ServiceCategoriesDetailsDb.isscheme, 0);
            }
            values.put(Contract.ServiceCategoriesDetailsDb.geoid, list.get(i).geography_id);

            values.put(Contract.ServiceCategoriesDetailsDb.servicename, list.get(i).service.name);
            values.put(Contract.ServiceCategoriesDetailsDb.serviceid, list.get(i).service.id.toString());

            values.put(Contract.ServiceCategoriesDetailsDb.servicefee, list.get(i).service_fee);
            values.put(Contract.ServiceCategoriesDetailsDb.servicecharge, list.get(i).service_charge);

            values.put(Contract.ServiceCategoriesDetailsDb.geodistname, list.get(i).geography_district_name);

            values.put(Contract.ServiceCategoriesDetailsDb.image,list.get(i).image);

            db.insert(Contract.ServiceCategoriesDetailsDb.TABLE_NAME, null, values);
        }
        db.close();
    }

    public List<CategoryDetails> getServiceCategoryDetails()
    {
        SQLiteDatabase db = this.getWritableDatabase();
        List<CategoryDetails> list =new ArrayList<>();

        String selectQuery = "SELECT  * FROM " + Contract.ServiceCategoriesDetailsDb.TABLE_NAME+" ORDER BY "+Contract.ServiceCategoriesDetailsDb.categoryid +" ASC";
        Gson gson= new GsonBuilder().create();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                CategoryDetails cat = new CategoryDetails();

                //cat.setAdd_on_service(s1);

                cat.setCategoryName(cursor.getString(1));
                cat.setCategoryId(Integer.parseInt(cursor.getString(2)));
                cat.setSgm_id(Integer.parseInt(cursor.getString(3)));
                cat.setIsScheme(Boolean.parseBoolean(cursor.getString(4)));
                cat.setGeography_id(Integer.parseInt(cursor.getString(5)));
                cat.setServiceName(cursor.getString(6));
                cat.setServiceId(Integer.parseInt(cursor.getString(7)));
               // s1= gson.fromJson(cursor.getString(9), new TypeToken<List<String>>(){}.getType());
               // cat.setAttachment_service(s1);
                cat.setService_fee(Integer.parseInt(cursor.getString(8)));
                cat.setService_charge(Integer.parseInt(cursor.getString(9)));
                cat.setGeography_district_name(cursor.getString(10));

                // Adding contact to list
                list.add(cat);
            } while (cursor.moveToNext());
        }
        db.close();
        return list;
    }


    public List<CategoryDetails> getServiceCategoryIds()
    {
        SQLiteDatabase db = this.getWritableDatabase();
        List<CategoryDetails> list =new ArrayList<>();

        String selectQuery = "SELECT  * FROM " + Contract.ServiceCategoriesDetailsDb.TABLE_NAME+" GROUP BY "+Contract.ServiceCategoriesDetailsDb.categoryid ;
        Gson gson= new GsonBuilder().create();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                CategoryDetails cat = new CategoryDetails();

                //cat.setAdd_on_service(s1);

                cat.setCategoryName(cursor.getString(1));
                cat.setCategoryId(Integer.parseInt(cursor.getString(2)));
                cat.setSgm_id(Integer.parseInt(cursor.getString(3)));
                cat.setIsScheme(Boolean.parseBoolean(cursor.getString(4)));
                cat.setGeography_id(Integer.parseInt(cursor.getString(5)));
                cat.setServiceName(cursor.getString(6));
                cat.setServiceId(Integer.parseInt(cursor.getString(7)));
                // s1= gson.fromJson(cursor.getString(9), new TypeToken<List<String>>(){}.getType());
                // cat.setAttachment_service(s1);
                cat.setService_fee(Integer.parseInt(cursor.getString(8)));
                cat.setService_charge(Integer.parseInt(cursor.getString(9)));
                cat.setGeography_district_name(cursor.getString(10));
                cat.setImage(cursor.getString(11));
                // Adding contact to list
                list.add(cat);
            } while (cursor.moveToNext());
        }
        db.close();
        return list;
    }


    public List<ServicesDetails> getSubCategories(String id)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        List<ServicesDetails> list =new ArrayList<>();

        String selectQuery = "SELECT  * FROM " + Contract.ServiceCategoriesDetailsDb.TABLE_NAME+" WHERE "+Contract.ServiceCategoriesDetailsDb.categoryid +" = " +id;
        Gson gson= new GsonBuilder().create();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                ServicesDetails cat = new ServicesDetails();

                //cat.setAdd_on_service(s1);


                cat.setSgmid(Integer.parseInt(cursor.getString(3)));
                cat.setScheme(Integer.parseInt(cursor.getString(4)));
                cat.setGeographyId(Integer.parseInt(cursor.getString(5)));
                cat.setServiceName(cursor.getString(6));
                cat.setServiceId(Integer.parseInt(cursor.getString(7)));
                // s1= gson.fromJson(cursor.getString(9), new TypeToken<List<String>>(){}.getType());
                // cat.setAttachment_service(s1);
                cat.setServiceFee(Integer.parseInt(cursor.getString(8)));
                cat.setServiceCharge(Integer.parseInt(cursor.getString(9)));
                //cat.setGeography_district_name(cursor.getString(10));

                // Adding contact to list
                list.add(cat);
            } while (cursor.moveToNext());
        }
        db.close();
        return list;
    }


    /*public void addStates(List<StateDetails> list)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        for(int i=0;i<list.size();i++) {
            ContentValues values = new ContentValues();
            values.put(Contract.StatesDb.id, list.get(i).states_id);
            values.put(Contract.StatesDb.name, list.get(i).display_state_name);

            // Inserting Row
            db.insert(Contract.StatesDb.TABLE_NAME, null, values);
        }
        db.close();
    }*/


    public List<StateDetails> getStates() {
        List<StateDetails> list = new ArrayList<>();
        String selectQuery = "SELECT  * FROM " + Contract.StatesDb.TABLE_NAME;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                StateDetails cat = new StateDetails();
                cat.setStates_id(Integer.parseInt(cursor.getString(1)));
                cat.setState_name(cursor.getString(2));
                // Adding contact to list
                list.add(cat);
            } while (cursor.moveToNext());

        }
        db.close();
        return list;
    }
}
