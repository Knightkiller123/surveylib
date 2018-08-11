package easygov.saral.harlabh.utils;

import android.util.Log;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.security.MessageDigest;

/**
 * Created by vineetverma on 28/10/17.
 */

public class ResponseManager {

    public List<String> list_of_translation_required = null;
    public String raw_response = null;
    public String raw_translated_response = null;
    public HashMap<String, Object> response_with_keys_changed = null;
    public HashMap<String, String> response_with_translation = null;
    public String language = null;
    public boolean call_translation_api = true;
    final String key_prefix  = "translate_";
    final String Splitter = "____";
    String[] hash_plus_text = new String[2];

    static String[] keys_to_match = new String[]{
            "data:objects:status_name",
            "data:objects:service_category_name",
            "data:objects:service_name",
            "message",
            "data:objects:service:name",
            "data:objects:service:category:name",
            "data:fieldsData:field__display_name",
            "data:fieldsData:display_name",
            "data:fieldsData:val",
            "data:fieldsData:options:value",
            "data:objects:district_name",
            "data:objects:state_name",
            "data:fields:field__display_name",
            "data:fields:docTypes:docs:name",
            "data:fields:docTypes:doc_number_label",
            "data:state",
            "data:dist",
            "data:fields:display_name",
            "data:fields:val",
            "data:fields:options:value",
            "data:qualified_schemes:objects:service__department__name",

            "data:field_data:display_name",
            "data:field_data:value",
            "data:field_data:options:text",
            "data:field_data:options:display_name",
            "data:family_profiles:relationship",
            "data:qualified_schemes:objects:service__name",
            "data:qualified_schemes:objects:benefits",
            "data:qualified_schemes:objects:service__analysis",
            "data:qualified_schemes:objects:service__benefits",
            "data:objects:category:name",
            "data:qualified_schemes:objects:benefits_list:benefit",
            "data:status_data:display_name",
            "data:family_profile:relationship",
            "data:objects:scheme_and_service_category",
            "data:objects:scheme_and_service_name",
            "data:objects:action",
            "data:objects:status",
            "data:qualified_schemes.objects:service__service_category__name"



    };


    static JsonParser parser = new JsonParser();

    public ResponseManager(boolean call_api, String locale){
        this.list_of_translation_required = new ArrayList<String>();
        this.call_translation_api = call_api;
        this.language = locale;
    }

    public boolean isJson(String teststring) {
        try {
            new JSONObject(teststring);
        } catch (JSONException ex_jsonobject) {
            return false;
        }
        return true;
    }



    public HashMap<String, Object> createHashMapFromJsonString(String json, String parentkeys) {

        JsonObject object = (JsonObject) parser.parse(json);
        Set<Map.Entry<String, JsonElement>> set = object.entrySet();
        Iterator<Map.Entry<String, JsonElement>> iterator = set.iterator();
        HashMap<String, Object> map = new HashMap<String, Object>();

        while (iterator.hasNext()) {

            Map.Entry<String, JsonElement> entry = iterator.next();
            String key = entry.getKey();
            JsonElement value = entry.getValue();



            if (null != value) {
                if (!value.isJsonPrimitive()) {
                    if (value.isJsonObject()) {



//                        if(parentkeys.contains("data:")){
                            Log.d("ReposnseManager ",parentkeys);
//                        }

                        map.put(key, createHashMapFromJsonString(value.toString(), parentkeys + key + ":"));
                    } else if (value.isJsonArray() && value.toString().contains(":")) {

                        List<HashMap<String, Object>> list = new ArrayList<>();
                        JsonArray array = value.getAsJsonArray();
                        if (null != array) {

//                            parentkeys += key + ":";
                            boolean was_that_an_array = false;
                            for (JsonElement element : array) {

                                if(isJson(element.toString())) {
                                    list.add(createHashMapFromJsonString(element.toString(), parentkeys + key + ":"));
                                }else{
                                    was_that_an_array = true;
                                    break;
                                }
//                                list.add(createHashMapFromJsonString(element.toString(), parentkeys + key + ":"));
                            }

                            if(was_that_an_array == false) {
                                map.put(key, list);
                            }else{
                                map.put(key, value.getAsJsonArray());
                            }

//                            map.put(key, list);
                        }
                    } else if (value.isJsonArray() && !value.toString().contains(":")) {

                        map.put(key, value.getAsJsonArray());

                    }
                } else {

                    if(this.call_translation_api){

                        StringBuffer sb = new StringBuffer();

                        Log.d("Translation", parentkeys + key + " = " + value);

                        if(Arrays.asList(keys_to_match).contains(parentkeys + key)){

                            try {

                                MessageDigest md = MessageDigest.getInstance("MD5");

                                byte[] value_in_bytes = value.getAsString().getBytes("UTF-8");
                                byte[] value_digest = md.digest(value_in_bytes);

                                for (int i = 0; i < value_digest.length; ++i) {
                                    sb.append(Integer.toHexString((value_digest[i] & 0xFF) | 0x100).substring(1,3));
                                }

                                this.list_of_translation_required.add(sb.toString());



                                map.put(key_prefix + key, sb.toString() + Splitter + value.getAsString());

                                Log.d("Translation", " ======== " + parentkeys + key + " = " + sb.toString() + Splitter + value.getAsString());

                                md = null;
                                value_in_bytes = null;
                                value_digest = null;
                                sb = null;

                            } catch (NoSuchAlgorithmException e) {
                                e.printStackTrace();
                            }catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                            }

                        }else{
                            map.put(key, value.getAsString());
                        }

                    }
                    else{

                        if(key.contains(key_prefix)){
                            Log.d("Translation",value.getAsString());
                            if(value.getAsString().contains(Splitter)) {
                                hash_plus_text = value.getAsString().split(Splitter);
                                if (this.response_with_translation.containsKey(hash_plus_text[0]) && this.response_with_translation.get(hash_plus_text[0]) != null) {
                                    Log.d("Translation", " Hindi ======= " + this.response_with_translation.get(hash_plus_text[0]));

                                    map.put(key.replace(key_prefix, ""), this.response_with_translation.get(hash_plus_text[0]));
                                } else {
                                    if (hash_plus_text.length == 2) {
                                        map.put(key.replace(key_prefix, ""), hash_plus_text[1]);
                                    } else {
                                        map.put(key.replace(key_prefix, ""), "");
                                    }
                                }
                            }else{
                                map.put(key.replace(key_prefix, ""), "");
                            }
                        }else{
                            map.put(key, value);
                        }
                    }
                }
            }
        }
        //Return an object having list of translation required as well as hashmap
        return map;
    }

}
