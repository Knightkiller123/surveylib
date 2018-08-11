package easygov.saral.harlabh.adapters;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.google.gson.Gson;
import com.tsongkha.spinnerdatepicker.SpinnerDatePickerDialogBuilder;
import com.wefika.flowlayout.FlowLayout;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import easygov.saral.harlabh.activity.SecondSchemesActivity;
import easygov.saral.harlabh.fragments.survey.SurveyFragment;
import easygov.saral.harlabh.models.header.Value;

import easygov.saral.harlabh.models.surveypaging.StartSurveyPaging;
import easygov.saral.harlabh.models.surveypaging.ValuesPaging;
import easygov.saral.harlabh.utils.MyApplication;
import easygov.saral.harlabh.utils.Prefs;
import easygov.saral.harlabh.utils.RestClient;
import easygov.saral.harlabh.R;
import easygov.saral.harlabh.activity.FirstSurveyActivity;
import easygov.saral.harlabh.models.header.FieldListClass;

import easygov.saral.harlabh.utils.Constants;
import easygov.saral.harlabh.utils.GeneralFunctions;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static easygov.saral.harlabh.fragments.storyboardfrags.YourStoryDetailsFragment.count;

/**
 * Created by apoorv on 31/08/17.
 */

public class SurveyAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>  {

    private Context context;
    private StartSurveyPaging entList=new StartSurveyPaging();
    Map<String ,String> map;
    Value object ;
    private String vals[];
    private List<ValuesPaging> values =new ArrayList();
    public static List<FieldListClass> list=new ArrayList<>();

    private  FieldListClass obj=new FieldListClass();
    private Prefs mPrefs;
    private String nonString="";
    private SurveyFragment fragment;

    public SurveyAdapter(Context context, StartSurveyPaging list, SurveyFragment frag)
    {
        this.context=context;
        mPrefs=Prefs.with(context);
        entList=new StartSurveyPaging();
        entList=list;
        fragment=frag;
        vals= new String[entList.data.field_data.size()];
        for(int i=0;i <entList.data.field_data.size();i++)
        {
            if(entList.data.field_data.get(i).value!=null)
                vals[i]=entList.data.field_data.get(i).value;
            else
                vals[i]="";
        }

        object =new Value();
        map=new HashMap<>();



    }
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        View v=null;
        switch (viewType) {
            case 0:
                v = inflater.inflate(R.layout.nonlist, parent, false);
                viewHolder = new ViewHolder1(v);
                break;

           /*case 1:
                v = inflater.inflate(R.layout.spinner_survey, parent, false);
                viewHolder = new ViewHolder2(v);
                break;*/

            case 1:
                v = inflater.inflate(R.layout.aytocmplt_srvey, parent, false);
                viewHolder = new AutoViewHolder(v);
                break;

            case 2:
                v = inflater.inflate(R.layout.bool, parent, false);
                viewHolder = new ViewHolder3(v);
                break;

            case 3:
                v = inflater.inflate(R.layout.edittext_survey, parent, false);
                viewHolder = new ViewHolder4(v);
                break;

            case 4:
                v = inflater.inflate(R.layout.datetime_user_survey, parent, false);
                viewHolder = new ViewHolder5(v);
                break;

            case 5:
                v = inflater.inflate(R.layout.non_list_vertical, parent, false);
                viewHolder = new ViewHolder6(v);
                break;

        }
        return viewHolder;

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        switch (holder.getItemViewType()) {
            case 0:
                ViewHolder1 vh1 = (ViewHolder1) holder;
                configureViewHolder1(vh1, position);
                break;


            case 1:
                AutoViewHolder vh2 = (AutoViewHolder) holder;
                newConfigureViewHolder(vh2,position);
                // cconfigueAutoViewHolder2(vh2, position);
                break;

            /*case 1:
                ViewHolder2 vh6 = (ViewHolder2) holder;
                configureViewHolder2(vh6, position);
                break;*/

            case 2:
                ViewHolder3 vh3 = (ViewHolder3) holder;
                configureViewHolder3(vh3, position);
                break;

            case 3:
                ViewHolder4 vh4 = (ViewHolder4) holder;
                configureViewHolder4(vh4, position);
                break;
            case 4:
                ViewHolder5 vh5 = (ViewHolder5) holder;
                //holder.itemView.setBackgroundColor(Color.TRANSPARENT);
                configureViewHolder5(vh5, position);
                break;

            case 5:
                ViewHolder6 vh6 = (ViewHolder6) holder;
                configureViewHolder6(vh6, position);
                break;
        }

    }

    private class ViewHolder6 extends RecyclerView.ViewHolder {
        private FlowLayout flLayout;
        private TextView tvNonListTitle;
        public ViewHolder6(View v) {
            super(v);
            tvNonListTitle= v.findViewById(R.id.tvNonListTitle);
            flLayout= v.findViewById(R.id.flLayout);
        }
    }

    private void configureViewHolder6(final ViewHolder6 vh6, final int position) {
        vh6.tvNonListTitle.setText(entList.data.field_data.get(position).display_name);

        if(entList.data.field_data.get(position).options.size()==3) {
            setFlowLayout(entList.data.field_data.get(position).options,vh6,position);
            }
    }

    private void setFlowLayout(final List<ValuesPaging> options, final ViewHolder6 vh6, final int pos) {
        vh6.flLayout.removeAllViews();
        for (int i = 0; i < options.size(); i++) {
            final TextView textView = new TextView(context);
            textView.setText(options.get(i).text);
            textView.setBackgroundResource(R.drawable.white_curved);
            textView.setTextColor(ContextCompat.getColor(context, R.color.black));
            FlowLayout.LayoutParams layoutParams =
                    new FlowLayout.LayoutParams(FlowLayout.LayoutParams.WRAP_CONTENT,
                            FlowLayout.LayoutParams.WRAP_CONTENT);
            layoutParams.setMargins(8, 10, 8, 10);
            textView.setPadding(32, 12, 32, 12);
            textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP,13);
            if (!options.get(i).isSelected) {
                textView.setBackgroundResource(R.drawable.white_curved);
                textView.setTextColor(ContextCompat.getColor(context,
                        R.color.black));
                options.get(i).isSelected = false;
                // nonString = "";

            } else {
                textView.setTextColor(ContextCompat.getColor(context, R.color.white));
                textView.setBackgroundResource(R.drawable.green_curved);
                options.get(i).isSelected = true;
                nonString = String.valueOf(options.get(i).value);


            }

            textView.setLayoutParams(layoutParams);
            final int finalI = i;
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    for (int j = 0; j < options.size(); j++) {
                        if (j != finalI) {
                            options.get(j).isSelected = false;

                        }
                    }
                    if (!options.get(finalI).isSelected) {
                        options.get(finalI).isSelected = true;
                        nonString = String.valueOf(options.get(finalI).value);
                    }

                    setFlowLayout(options,vh6, pos);
                    setTextValues(nonString,pos,entList.data.field_data.get(pos).options.get(0).text);

                }

            });
            vh6.flLayout.addView(textView);
        }
    }


    private void newConfigureViewHolder(final AutoViewHolder vh2, final int position) {
        vh2.tvSpinnerTitle.setText(entList.data.field_data.get(position).display_name);
        if (entList.data.field_data.get(position).value!=null && entList.data.field_data.get(position).value.isEmpty())
            vh2.tvSearch.setText(R.string.select);
        else {

            //  vh2.tvSearch.setText(entList.data.field_data.get(position).value);
            if(vals[position]!=null && !vals[position].isEmpty())
            {


                String selected_value = vals[position];

                Integer selected_id = -1;
                String txt="";

                for (ValuesPaging vp : entList.data.field_data.get(position).options) {
                    if (String.valueOf(vp.value).equals(selected_value)) {
                        selected_id = vp.value;
                        txt=vp.text;
                    }
                }




                if(selected_id>=0) {
                    object = new Value();
                    object.display_value = txt;
                    object.value = selected_id.toString();

                    vh2.tvSearch.setText(txt);
                    obj = new FieldListClass();
                    obj.fieldId = String.valueOf(entList.data.field_data.get(position).field_id);
                    obj.value = object;

                    int chk=0;
                    for (int i = 0; i < list.size(); i++) {
                        if (list.get(i).fieldId.equals(entList.data.field_data.get(position).field_id.toString())) {
                            list.set(i, obj);
                            chk++;
                        }
                    }

                    if (chk == 0)
                        list.add(obj);
                }

                else if(entList.data.field_data.get(position).field_id.equals(536))
                {
                    vh2.tvSearch.setText(entList.data.field_data.get(position).value);
                    int po=0;
                    object = new Value();
                    object.display_value = entList.data.field_data.get(position).value;
                    for(int i =0;i<entList.data.field_data.get(position).options.size();i++)
                    {
                        if(entList.data.field_data.get(position).options.get(i).text.equals(vals[position]))
                        {
                            po=i;
                        }
                    }
                    object.value = entList.data.field_data.get(position).options.get(po).value.toString();
                    obj = new FieldListClass();
                    obj.fieldId = String.valueOf(entList.data.field_data.get(position).field_id);
                    obj.value = object;
                    int chk=0;
                    for (int i = 0; i < list.size(); i++) {
                        if (list.get(i).fieldId.equals(entList.data.field_data.get(position).field_id.toString())) {
                            list.set(i, obj);
                            chk++;
                        }
                    }

                    if (chk == 0)
                        list.add(obj);
                }
                else
                    vh2.tvSearch.setText("");
            }
        }


        vh2.tvSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              /*  if(entList.data.field_data.get(position).field_id.equals(536))
                Toast.makeText(context, context.getResources().getString(R.string.dataaltered), Toast.LENGTH_SHORT).show();

                else
              */  showDialog(vh2,position);
            }
        });
    }
    private ImageView ivSurveyCross;
    private  RelativeLayout rlSurveyNoData;
    private void showDialog(final AutoViewHolder vh2, final int position) {

        LayoutInflater factory = LayoutInflater.from(context);
        final View deleteDialogView = factory.inflate(R.layout.dialog_select_option, null);
        final AlertDialog deleteDialog = new AlertDialog.Builder
                (context,android.R.style.Theme_DeviceDefault_Light_NoActionBar_Fullscreen).create();
        deleteDialog.setView(deleteDialogView);
        final EditText etSearch=deleteDialogView.findViewById(R.id.etSearchKey);
        ivSurveyCross = deleteDialogView.findViewById(R.id.ivSurveyCross);
        rlSurveyNoData = deleteDialogView.findViewById(R.id.rlSurveyNoData);
        ImageView ivCross=deleteDialogView.findViewById(R.id.ivCross);
        ivCross.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteDialog.dismiss();
            }
        });
        final RecyclerView rvData=deleteDialogView.findViewById(R.id.rvData);
        final List<String> stat = new ArrayList<>();
        final List<String> adapterData = new ArrayList<>();
        rvData.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                ((InputMethodManager) context.getSystemService(
                        Activity.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(rvData.getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);
                return false;
            }
        });

        final SearchAdapter searchAdapter=new SearchAdapter(context,adapterData, new SearchAdapter.OnItemClickListener() {
            @Override
            public void onItemClick( Integer pos, String val) {


                vh2.tvSearch.setText(val);
                pos =stat.indexOf(val);
                int chk = 0;
                object = new Value();

                obj = new FieldListClass();

                object.display_value = entList.data.field_data.get(position).options.get(pos).text;
                object.value = String.valueOf(entList.data.field_data.get(position).options.get(pos).value);
                obj.fieldId = String.valueOf(entList.data.field_data.get(position).field_id);
                obj.value = object;

                for (int i = 0; i < list.size(); i++) {
                    if (list.get(i).fieldId.equals(entList.data.field_data.get(position).field_id.toString())) {
                        list.set(i, obj);
                        chk++;
                    }
                }

                if (chk == 0) list.add(obj);
                GeneralFunctions.hideSoftKeyboard((FirstSurveyActivity)context);
                deleteDialog.dismiss();
            }
        });

        rvData.setLayoutManager(new LinearLayoutManager(context));
        rvData.setAdapter(searchAdapter);

        values = entList.data.field_data.get(position).options;
        for (int i = 0; i < values.size(); i++) {
            stat.add(values.get(i).text);
            adapterData.add(values.get(i).text);
        }

        searchAdapter.notifyDataSetChanged();


        if (!vals[position].isEmpty()) {
            if (vals[position].length() > 1) {

                for (int i = 0; i < values.size(); i++) {
                    if (values.get(i).value.equals(vals[position])) {
                        vh2.tvSearch.setText(values.get(i).text);
                        int chk = 0;
                        object = new Value();
                        obj = new FieldListClass();

                        object.display_value = entList.data.field_data.get(position).options.get(i).text;
                        object.value = String.valueOf(entList.data.field_data.get(position).options.get(i).value);
                        obj.fieldId = String.valueOf(entList.data.field_data.get(position).field_id);
                        obj.value = object;


                        for (int i1 = 0; i1 < list.size(); i1++) {
                            if (list.get(i1).fieldId.equals(entList.data.field_data.get(position).field_id.toString())) {
                                list.set(i1, obj);
                                chk++;
                            }
                        }


                        if (chk == 0) list.add(obj);
                    }
                }

            }
        }

        ivSurveyCross.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((InputMethodManager) context.getSystemService(
                        Activity.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(rvData.getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);
                ivSurveyCross.setVisibility(View.GONE);
                etSearch.setText("");
              /*  adapterData.addAll(stat);
                searchAdapter.notifyDataSetChanged();*/
            }
        });

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapterData.clear();

                if(s.length()!=0)
                {
                    ivSurveyCross.setVisibility(View.VISIBLE);
                    for( int i =0;i< stat.size();i++)
                    {

                        if(stat.get(i).toLowerCase().contains(s.toString().toLowerCase()))
                            adapterData.add(stat.get(i));

                    }


                    //   rvData.setAdapter(searchAdapter);
                }

                else
                {
                    ivSurveyCross.setVisibility(View.GONE);
                    adapterData.addAll(stat);
                }

                if(adapterData.size()>0)
                {
                    rlSurveyNoData.setVisibility(View.GONE);
                    rvData.setVisibility(View.VISIBLE);
                }
                else {
                    rlSurveyNoData.setVisibility(View.VISIBLE);
                    rvData.setVisibility(View.GONE);
                }
                searchAdapter.notifyDataSetChanged();

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        deleteDialog.show();
    }


    ViewHolder5 dateObj;
    private int datePosition;
    private void configureViewHolder5(final ViewHolder5 vh5, final int position) {
        //setDate();
        //fragment.setNextVisible();
        dateObj = vh5;
        datePosition = position;

        setCustomDate();
        vh5.tvDobQues.setText(entList.data.field_data.get(position).display_name);


        if (!vals[position].isEmpty()) {
            //SimpleDateFormat form = new SimpleDateFormat("yyyy-MM-dd");

            SimpleDateFormat form;
            if(vals[position].contains("/"))
                form = new SimpleDateFormat("dd/MM/yyyy");
            else
                form = new SimpleDateFormat("dd-MM-yyyy");
            java.util.Date date;
            String newFormat = "";
            try {
                date = form.parse(vals[position]);
                //SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

                //newFormat = formatter.format(date);
                // vh5.dPickers.setText(newFormat);
                Calendar local = Calendar.getInstance();
                local.setTime(date);
                updateLabel(local.get(Calendar.YEAR), local.get(Calendar.MONTH)+1, local.get(Calendar.DAY_OF_MONTH));
                //mPrefs.save(details.get(position).id+"_"+details.get(position).field__system_name, vh4.dPickers.getText().toString());
            } catch (Exception e) {

                e.printStackTrace();
            }
        }



        vh5.dPickers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                /*new DatePickerDialog(context,R.style.CustomDatePickerDialogTheme,date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();*/

               /* if(!vals[position].isEmpty())
                {
                    Toast.makeText(context, context.getResources().getString(R.string.dataaltered), Toast.LENGTH_SHORT).show();
                }
                else */{


                    if(     mPrefs.getString("sgm_new_id","00").equals("7462")  ||
                            mPrefs.getString("sgm_new_id","00").equals("7463")  ||
                            mPrefs.getString("sgm_new_id","00").equals("7464")  ||
                            mPrefs.getString("sgm_new_id","00").equals("7465")  ||
                            mPrefs.getString("sgm_new_id","00").equals("7466")  ||
                            mPrefs.getString("sgm_new_id","00").equals("7467")  ||
                            mPrefs.getString("sgm_new_id","00").equals("7468")  ||
                            mPrefs.getString("sgm_new_id","00").equals("7469")  ||
                            mPrefs.getString("sgm_new_id","00").equals("7470")  ||
                            mPrefs.getString("sgm_new_id","00").equals("7471")  ||
                            mPrefs.getString("sgm_new_id","00").equals("7472")

                            )

                    new SpinnerDatePickerDialogBuilder()
                            .context(context)
                            .callback(dates)
                            .build()
                            .show();

                    else

                        new SpinnerDatePickerDialogBuilder()
                                .context(context)
                                .callback(dates)
                                .build()
                                .show();
                }
            }
        });


        /*dPickers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment newFragment = new SurveyDateFragment();
              idForDate=  entList.data.field_data.get(position).field_id;
                newFragment.show(((FirstSurveyActivity)context).getSupportFragmentManager(), "DatePicker");
            }
        });*/
    }


    private void configureViewHolder4(ViewHolder4 vh4, final int position) {
        String title=entList.data.field_data.get(position).display_name;

        if(title.contains("hectares"))
        {
            vh4.tveditTitle.setText(title.replace("hectares","acres"));
        }
        else if(title.contains("हेक्टेयर"))
        {vh4.tveditTitle.setText(title.replace("hectares","एकड़"));}
        else
            vh4.tveditTitle.setText(entList.data.field_data.get(position).display_name);

        //fragment.setNextVisible();


        if(!vals[position].isEmpty())
        {
            vh4.etText.setText(vals[position]);
            setEditTextValues(vals[position],position);
            //mPrefs.save(details.get(position).id+"_"+details.get(position).field__system_name,vals[position]);
            // vals[position]=vh2.etEnter.getText().toString();
        }
        else {
            vh4.etText.setText("");
        }
      /*  vh4.etText.setInputType(InputType.TYPE_CLASS_TEXT |
                InputType.TYPE_NUMBER_FLAG_DECIMAL);*/
        vh4.etText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if(entList.data.field_data.get(position).display_name.contains("hectares")||entList.data.field_data.get(position).display_name.contains("हेक्टेयर"))
                {
                    try {
                        Double val;
                        if(s.toString().length()>0) {
                            s="0"+s;
                            val = Double.parseDouble(s.toString());
                        }
                        else val=0.0;
                        Double acre = val*0.404686;
                        setEditTextValues(String.valueOf(acre),position);
                    }
                    catch (Exception e)
                    {
                        Toast.makeText(context, "Enter hectare as 0.", Toast.LENGTH_SHORT).show();
                    }

                }

                else
                    setEditTextValues(s.toString(),position);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });



    }



    private void setEditTextValues(String s, int position)
    {
        int chk2=0;
        object=new Value();

        obj = new FieldListClass();
        object.value=s;
        object.display_value=s;
        obj.fieldId=String.valueOf(entList.data.field_data.get(position).field_id);



        obj.value =object;

        if(s!=null&&s.length()>0) {
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i).fieldId.equals(entList.data.field_data.get(position).field_id.toString())) {
                    list.set(i, obj);

                    chk2++;
                }
            }
            if (chk2 == 0)
                list.add(obj);
        }
        else {
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i).fieldId.equals(entList.data.field_data.get(position).field_id.toString())) {
                    list.remove(i);


                }
            }
        }

        //setValues();
    }

    private void configureViewHolder3(final ViewHolder3 vh3, final int position) {
        vh3.tvBoolTitle.setText(entList.data.field_data.get(position).display_name);

        vh3.tvTrue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vh3.tvTrue.setBackgroundResource(R.drawable.green_curved);
                vh3.tvFalse.setBackgroundResource(R.drawable.white_curved);

                vh3.tvTrue.setTextColor(ContextCompat.getColor(context,R.color.white));
                vh3.tvFalse.setTextColor(ContextCompat.getColor(context,R.color.black));
                nonString=String.valueOf("true");
                setTextValues(nonString,position,"yes");
            }
        });

        vh3.tvFalse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vh3.tvTrue.setBackgroundResource(R.drawable.white_curved);
                vh3.tvFalse.setBackgroundResource(R.drawable.green_curved);

                vh3.tvTrue.setTextColor(ContextCompat.getColor(context,R.color.black));
                vh3.tvFalse.setTextColor(ContextCompat.getColor(context,R.color.white));
                nonString=String.valueOf("false");
                setTextValues(nonString,position,"no");
            }
        });
        if(!vals[position].isEmpty()) {
            if (vals[position].trim().equalsIgnoreCase("0")) {
                vh3.tvTrue.setBackgroundResource(R.drawable.white_curved);
                vh3.tvFalse.setBackgroundResource(R.drawable.green_curved);

                vh3.tvTrue.setTextColor(ContextCompat.getColor(context,R.color.black));
                vh3.tvFalse.setTextColor(ContextCompat.getColor(context,R.color.white));
                nonString=String.valueOf("false");
                setTextValues(nonString,position,"no");
            }
            else  {
                vh3.tvTrue.setBackgroundResource(R.drawable.green_curved);
                vh3.tvFalse.setBackgroundResource(R.drawable.white_curved);

                vh3.tvTrue.setTextColor(ContextCompat.getColor(context,R.color.white));
                vh3.tvFalse.setTextColor(ContextCompat.getColor(context,R.color.black));
                nonString=String.valueOf("true");
                setTextValues(nonString,position,"yes");
            }
        }



    }








 /*   private void cconfigueAutoViewHolder2(final AutoViewHolder vh2, final int position12) {

        final List<String> stat = new ArrayList<>();

        values = entList.data.field_data.get(position12).options;
        //stat.add(context.getResources().getString(R.string.select));
        for (int i = 0; i < values.size(); i++) {
            stat.add(values.get(i).text);
        }


          adapter = new ArrayAdapter<String>
                (context, R.layout.sample, stat);
        //Getting the instance of AutoCompleteTextView
        // AutoCompleteTextView actv= (AutoCompleteTextView)findViewById(R.id.autoCompleteTextView1);
        vh2.actv.setThreshold(1);//will start working from first character
        vh2.actv.setAdapter(adapter);//setting the adapter data into the AutoCompleteTextView
        vh2.actv.setTextColor(Color.BLACK);
        vh2.tvSpinnerTitle.setText(entList.data.field_data.get(position12).display_name);

        vh2.actv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selected = (String) parent.getItemAtPosition(position);
                int pos = stat.indexOf(selected);
                // ((TextView) vh2.actv.getSelectedView()).setTextColor(ContextCompat.getColor(context, R.color.black));
                int chk = 0;
                object = new Value();
                obj = new FieldListClass();

                object.display_value = entList.data.field_data.get(position12).options.get(pos).text;
                object.value = String.valueOf(entList.data.field_data.get(position12).options.get(pos).value);
                obj.fieldId = String.valueOf(entList.data.field_data.get(position12).field_id);
                obj.value = object;


                for (int i = 0; i < list.size(); i++) {
                    if (list.get(i).fieldId.equals(entList.data.field_data.get(position12).field_id.toString())) {
                        list.set(i, obj);
                        chk++;
                    }
                }


                if (chk == 0) list.add(obj);
                GeneralFunctions.hideSoftKeyboard((FirstSurveyActivity)context);

            }
        });

        if (!vals[position12].isEmpty()) {
            if (vals[position12].length() > 1) {

                for (int i = 0; i < values.size(); i++) {
                    if (values.get(i).text.equalsIgnoreCase(vals[position12])) {
                        vh2.actv.setText(values.get(i).text);
                        int chk = 0;
                        object = new Value();
                        obj = new FieldListClass();

                        object.display_value = entList.data.field_data.get(position12).options.get(i).text;
                        object.value = String.valueOf(entList.data.field_data.get(position12).options.get(i).value);
                        obj.fieldId = String.valueOf(entList.data.field_data.get(position12).field_id);
                        obj.value = object;


                        for (int i1 = 0; i1 < list.size(); i1++) {
                            if (list.get(i1).fieldId.equals(entList.data.field_data.get(position12).field_id.toString())) {
                                list.set(i1, obj);
                                chk++;
                            }
                        }


                        if (chk == 0) list.add(obj);
                    }
                }

            }
        }

        vh2.actv.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.length()==0||s.toString().equals(""))
                {
                    adapter = new ArrayAdapter<String>
                            (context, R.layout.sample, stat);
                    adapter.notifyDataSetChanged();
                    vh2.actv.setAdapter(adapter);
                    new Handler().postDelayed(new Runnable() {

                        @Override
                        public void run() {
                            vh2.actv.showDropDown();
                        }
                    }, 500);

                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        vh2.actv.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(vh2.actv.hasFocus())
                    vh2.actv.showDropDown();
                return false;
            }
        });

        vh2.actv.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus)
                    vh2.actv.showDropDown();


            }
        });
    }

 */


    /*private void configureViewHolder2(final ViewHolder2 vh2, final int position12) {
        final List<String> stat = new ArrayList<>();

        values = entList.data.field_data.get(position12).options;
        stat.add(context.getResources().getString(R.string.select));
        for (int i = 0; i < values.size(); i++) {
            stat.add(values.get(i).text);
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, R.layout.spinner_title_text, stat) {
            @Override
            public boolean isEnabled(int position) {
                if (position == 0) {
                    // Disable the second item from Spinner
                    return false;
                } else {
                    return true;
                }
            }

            @Override
            public View getDropDownView(int position, View convertView,
                                        ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView tv = (TextView) view;
                if (position == 0) {
                    // Set the disable item text color
                    tv.setTextColor(Color.GRAY);
                } else {
                    tv.setTextColor(Color.BLACK);
                }
                return view;
            }
        };
        adapter.setDropDownViewResource(R.layout.spinner_adapter_value);
        vh2.spSpin.setAdapter(adapter);

        vh2.tvSpinnerTitle.setText(entList.data.field_data.get(position12).display_name);

        vh2.spSpin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position != 0) {
                    String selected = (String) parent.getItemAtPosition(position);
                    int pos = stat.indexOf(selected) - 1;
                    ((TextView) vh2.spSpin.getSelectedView()).setTextColor(ContextCompat.getColor(context, R.color.black));
                    int chk = 0;
                    object = new Value();
                    obj = new FieldListClass();
                    object.value = String.valueOf(entList.data.field_data.get(position12).options.get(pos).value);
                    object.display_value=entList.data.field_data.get(position12).options.get(pos).text;


                    obj.fieldId = String.valueOf(entList.data.field_data.get(position12).field_id);
                    obj.value = object;



                    for (int i = 0; i < list.size(); i++) {
                        if (list.get(i).fieldId.equals(entList.data.field_data.get(position12).field_id.toString())) {
                            list.set(i, obj);
                            chk++;
                        }
                    }


                    if (chk == 0) list.add(obj);


                    // setValues();
                } else {
                    ((TextView) vh2.spSpin.getSelectedView()).setTextColor(ContextCompat.getColor(context, R.color.darkgrey));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        if (!vals[position12].isEmpty()) {
            if (vals[position12].length()>1) {

                for (int i = 0; i < values.size(); i++) {
                    if (values.get(i).value.equals(vals[position12])) {
                        vh2.spSpin.setSelection(i+1);
                    }
                }

            }


        }
    }*/

    private void configureViewHolder1(final ViewHolder1 vh1, final int position) {

        vh1.tvNonListTitle.setText(entList.data.field_data.get(position).display_name);



        if(entList.data.field_data.get(position).options.size()==1)
        {
            vh1.tvSecond.setVisibility(View.GONE);
            vh1.tvThird.setVisibility(View.GONE);

            vh1.tvFirst.setText(entList.data.field_data.get(position).options.get(0).text);

            vh1.tvFirst.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    vh1.tvFirst.setBackgroundResource(R.drawable.green_curved);
                    vh1.tvFirst.setTextColor(ContextCompat.getColor(context,R.color.white));
                    nonString=String.valueOf(entList.data.field_data.get(position).options.get(0).value);
                    setTextValues(nonString,position,entList.data.field_data.get(position).options.get(0).text);
                }
            });
        }

        if(entList.data.field_data.get(position).options.size()==2)
        {
            vh1.tvThird.setVisibility(View.GONE);
            vh1.tvFirst.setText(entList.data.field_data.get(position).options.get(0).text);
            vh1.tvSecond.setText(entList.data.field_data.get(position).options.get(1).text);

            vh1.tvFirst.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    vh1.tvFirst.setBackgroundResource(R.drawable.green_curved);
                    vh1.tvFirst.setTextColor(ContextCompat.getColor(context,R.color.white));

                    vh1.tvSecond.setBackgroundResource(R.drawable.white_curved);
                    vh1.tvSecond.setTextColor(ContextCompat.getColor(context,R.color.black));
                    nonString=String.valueOf(entList.data.field_data.get(position).options.get(0).value);

                    setTextValues(nonString,position,entList.data.field_data.get(position).options.get(0).text);
                }
            });

            vh1.tvSecond.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    vh1.tvFirst.setBackgroundResource(R.drawable.white_curved);
                    vh1.tvFirst.setTextColor(ContextCompat.getColor(context,R.color.black));

                    vh1.tvSecond.setBackgroundResource(R.drawable.green_curved);
                    vh1.tvSecond.setTextColor(ContextCompat.getColor(context,R.color.white));
                    nonString=String.valueOf(entList.data.field_data.get(position).options.get(1).value);
                    setTextValues(nonString,position,entList.data.field_data.get(position).options.get(1).text);
                }
            });

        }

        if(entList.data.field_data.get(position).options.size()==3)
        {
            vh1.tvFirst.setText(entList.data.field_data.get(position).options.get(0).text);
            vh1.tvSecond.setText(entList.data.field_data.get(position).options.get(1).text);
            /*if(entList.data.field_data.get(position).options.get(2).text.equals("Transgender"))
                vh1.tvThird.setText("Trans");

            else*/
            vh1.tvThird.setText(entList.data.field_data.get(position).options.get(2).text);


            vh1.tvFirst.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                       /* if(entList.data.field_data.get(position).field_id.equals(153))
                            Toast.makeText(context, context.getResources().getString(R.string.dataaltered), Toast.LENGTH_SHORT).show();

                        else*/ {
                        vh1.tvFirst.setBackgroundResource(R.drawable.green_curved);
                        vh1.tvFirst.setTextColor(ContextCompat.getColor(context, R.color.white));
                        vh1.tvSecond.setTextColor(ContextCompat.getColor(context, R.color.black));
                        vh1.tvThird.setTextColor(ContextCompat.getColor(context, R.color.black));

                        vh1.tvSecond.setBackgroundResource(R.drawable.white_curved);
                        vh1.tvThird.setBackgroundResource(R.drawable.white_curved);
                        nonString = String.valueOf(entList.data.field_data.get(position).options.get(0).value);
                        setTextValues(nonString, position, entList.data.field_data.get(position).options.get(0).text);
                    }
                }
            });

            vh1.tvSecond.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                      /*  if(entList.data.field_data.get(position).field_id.equals(153))
                            Toast.makeText(context, context.getResources().getString(R.string.dataaltered), Toast.LENGTH_SHORT).show();

                        else */{
                        vh1.tvFirst.setBackgroundResource(R.drawable.white_curved);
                        vh1.tvSecond.setBackgroundResource(R.drawable.green_curved);
                        vh1.tvThird.setBackgroundResource(R.drawable.white_curved);

                        vh1.tvFirst.setTextColor(ContextCompat.getColor(context, R.color.black));
                        vh1.tvSecond.setTextColor(ContextCompat.getColor(context, R.color.white));
                        vh1.tvThird.setTextColor(ContextCompat.getColor(context, R.color.black));
                        nonString = String.valueOf(entList.data.field_data.get(position).options.get(1).value);
                        setTextValues(nonString, position, entList.data.field_data.get(position).options.get(1).text);
                    }
                }
            });

            vh1.tvThird.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                        /*if(entList.data.field_data.get(position).field_id.equals(153))
                            Toast.makeText(context, context.getResources().getString(R.string.dataaltered), Toast.LENGTH_SHORT).show();

                        else */{
                        vh1.tvFirst.setBackgroundResource(R.drawable.white_curved);
                        vh1.tvSecond.setBackgroundResource(R.drawable.white_curved);
                        vh1.tvThird.setBackgroundResource(R.drawable.green_curved);

                        vh1.tvFirst.setTextColor(ContextCompat.getColor(context, R.color.black));
                        vh1.tvSecond.setTextColor(ContextCompat.getColor(context, R.color.black));
                        vh1.tvThird.setTextColor(ContextCompat.getColor(context, R.color.white));
                        nonString = String.valueOf(entList.data.field_data.get(position).options.get(2).value);
                        setTextValues(nonString, position, entList.data.field_data.get(position).options.get(2).text);
                    }
                }
            });



        }

        if(!vals[position].isEmpty()) {
            if (entList.data.field_data.get(position).options.size() == 3) {

                if (vals[position].equals(entList.data.field_data.get(position).options.get(0).value.toString())) {
                    vh1.tvFirst.setBackgroundResource(R.drawable.green_curved);
                    vh1.tvFirst.setTextColor(ContextCompat.getColor(context, R.color.white));
                    vh1.tvSecond.setTextColor(ContextCompat.getColor(context, R.color.black));
                    vh1.tvThird.setTextColor(ContextCompat.getColor(context, R.color.black));

                    vh1.tvSecond.setBackgroundResource(R.drawable.white_curved);
                    vh1.tvThird.setBackgroundResource(R.drawable.white_curved);
                    nonString = String.valueOf(entList.data.field_data.get(position).options.get(0).value);
                    setTextValues(nonString, position, entList.data.field_data.get(position).options.get(0).text);
                }
                if (vals[position].equals(entList.data.field_data.get(position).options.get(1).value.toString())) {
                    vh1.tvFirst.setBackgroundResource(R.drawable.white_curved);
                    vh1.tvSecond.setBackgroundResource(R.drawable.green_curved);
                    vh1.tvThird.setBackgroundResource(R.drawable.white_curved);

                    vh1.tvFirst.setTextColor(ContextCompat.getColor(context, R.color.black));
                    vh1.tvSecond.setTextColor(ContextCompat.getColor(context, R.color.white));
                    vh1.tvThird.setTextColor(ContextCompat.getColor(context, R.color.black));
                    nonString = String.valueOf(entList.data.field_data.get(position).options.get(1).value);
                    setTextValues(nonString, position, entList.data.field_data.get(position).options.get(1).text);
                }
                if (vals[position].equals(entList.data.field_data.get(position).options.get(2).value.toString())) {
                    vh1.tvFirst.setBackgroundResource(R.drawable.white_curved);
                    vh1.tvSecond.setBackgroundResource(R.drawable.white_curved);
                    vh1.tvThird.setBackgroundResource(R.drawable.green_curved);

                    vh1.tvFirst.setTextColor(ContextCompat.getColor(context, R.color.black));
                    vh1.tvSecond.setTextColor(ContextCompat.getColor(context, R.color.black));
                    vh1.tvThird.setTextColor(ContextCompat.getColor(context, R.color.white));
                    nonString = String.valueOf(entList.data.field_data.get(position).options.get(2).value);
                    setTextValues(nonString, position, entList.data.field_data.get(position).options.get(2).text);
                }
            }
            else {
                if (vals[position].equals(entList.data.field_data.get(position).options.get(0).value.toString())) {
                    vh1.tvFirst.setBackgroundResource(R.drawable.green_curved);
                    vh1.tvFirst.setTextColor(ContextCompat.getColor(context, R.color.white));
                    vh1.tvSecond.setTextColor(ContextCompat.getColor(context, R.color.black));
                    //vh1.tvThird.setTextColor(ContextCompat.getColor(context, R.color.black));

                    vh1.tvSecond.setBackgroundResource(R.drawable.white_curved);
                    // vh1.tvThird.setBackgroundResource(R.drawable.white_curved);
                    nonString = String.valueOf(entList.data.field_data.get(position).options.get(0).value);
                    setTextValues(nonString, position, entList.data.field_data.get(position).options.get(0).text);
                }
                if (vals[position].equals(entList.data.field_data.get(position).options.get(1).value.toString())) {
                    vh1.tvFirst.setBackgroundResource(R.drawable.white_curved);
                    vh1.tvSecond.setBackgroundResource(R.drawable.green_curved);
                    //vh1.tvThird.setBackgroundResource(R.drawable.white_curved);

                    vh1.tvFirst.setTextColor(ContextCompat.getColor(context, R.color.black));
                    vh1.tvSecond.setTextColor(ContextCompat.getColor(context, R.color.white));
                    //vh1.tvThird.setTextColor(ContextCompat.getColor(context, R.color.black));
                    nonString = String.valueOf(entList.data.field_data.get(position).options.get(1).value);
                    setTextValues(nonString, position, entList.data.field_data.get(position).options.get(1).text);
                }
            }


        }
    }

    private void setTextValues(String nonString, int position,String display) {
        object=new Value();
        obj = new FieldListClass();
        object.value=nonString;


        object.display_value=display;



        obj.fieldId=String.valueOf(entList.data.field_data.get(position).field_id);
        obj.value =object;
        int chk1=0;
        for(int i=0;i<list.size();i++)
        {
            if(list.get(i).fieldId.equals(entList.data.field_data.get(position).field_id.toString()))
            {
                list.set(i,obj);
                chk1++;
            }
        }
        if(chk1==0)
            list.add(obj);

        // setValues();
    }




    private class ViewHolder1 extends RecyclerView.ViewHolder {
        private TextView tvFirst,tvSecond,tvThird,tvNonListTitle;
        public ViewHolder1(View v) {
            super(v);
            tvFirst= v.findViewById(R.id.tvFirst);
            tvSecond= v.findViewById(R.id.tvSecond);
            tvThird= v.findViewById(R.id.tvThird);
            tvNonListTitle= v.findViewById(R.id.tvNonListTitle);
        }
    }

    private class ViewHolder2 extends RecyclerView.ViewHolder {
        private Spinner spSpin;
        private TextView tvSpinnerTitle;
        public ViewHolder2(View v) {
            super(v);
            spSpin= v.findViewById(R.id.spSpin);
            tvSpinnerTitle= v.findViewById(R.id.tvSpinnerTitle);
        }
    }

    private class ViewHolder3 extends RecyclerView.ViewHolder {
        private TextView tvTrue,tvFalse,tvBoolTitle;
        public ViewHolder3(View v) {
            super(v);
            tvTrue= v.findViewById(R.id.tvTrue);
            tvFalse= v.findViewById(R.id.tvFalse);
            tvBoolTitle= v.findViewById(R.id.tvBoolTitle);
        }
    }

    private class ViewHolder4 extends RecyclerView.ViewHolder {
        private EditText etText;
        private TextView tveditTitle;
        public ViewHolder4(View v) {
            super(v);
            etText= v.findViewById(R.id.etTexts);
            tveditTitle= v.findViewById(R.id.tveditTitle);
        }
    }

    private class AutoViewHolder extends RecyclerView.ViewHolder {
        // private AutoCompleteTextView actv;
        private TextView tvSpinnerTitle,tvSearch;
        public AutoViewHolder(View v) {
            super(v);
            // actv= (AutoCompleteTextView) v.findViewById(R.id.actvAuto);
            tvSpinnerTitle= v.findViewById(R.id.tvSpinnerTitle);
            tvSearch= v.findViewById(R.id.tvSearch);
        }
    }

    @Override
    public int getItemCount() {
        return  entList.data.field_data.size();
    }


    @Override
    public int getItemViewType(int position) {
        int ret =-1;
        String val=entList.data.field_data.get(position).field_type;

        switch (val)
        {
            case "options":

                if (entList.data.field_data.get(position).options.size() > 3)
                    ret = 1;
                else if (entList.data.field_data.get(position).options.size() == 3)
                    ret = 5;
                else
                    ret = 0;
                break;

            case "boolean":
                ret =2;
                break;

            case "day_difference":
                ret=4;
                break;

            case "month_difference":
                ret=4;
                break;

            case "year_difference":
                ret=4;
                break;

            case "bounds":
                ret=3;
                break;

            case "timestamp":
                ret=4;
                break;
        }
        return ret;
    }

    public void setValues()
    {
       /* if(editText.size()>0)
        {
            list.addAll(editText);

          //  editText.clear();
        }*/
        map.put("field_list",new Gson().toJson(list));
        map.put("survey_id",mPrefs.getString(Constants.SurveyId,""));
        map.put("current_scheme_list",new Gson().toJson(entList.data.current_scheme_list));
        map.put("existing_field_list",new Gson().toJson(entList.data.existing_field_list));
        map.put("rule_type",entList.data.rule_type);
        map.put("size","1000");

        map.put(Constants.PARAM_BENEFICIARY_TYPE_ID,mPrefs.getString(Constants.BeneficiaryID,""));
        object=new Value();

        if(list.size()==entList.data.field_data.size())
            hitNext();

        else {
            Toast.makeText(context, context.getResources().getString(R.string.allentries), Toast.LENGTH_SHORT).show();
        }
    }

    public void hitNext()
    {
        //fragment.setnextInvisible();
        GeneralFunctions.showDialog(context);
        map.put("geography_id",mPrefs.getString(Constants.GeographyId,""));
        Call<StartSurveyPaging> call= RestClient.get().setEnt(map);
        call.enqueue(new Callback<StartSurveyPaging>() {
            @Override
            public void onResponse(Call<StartSurveyPaging> call, Response<StartSurveyPaging> response) {
                GeneralFunctions.dismissDialog();
                if(response.isSuccessful())
                {
                    if(response.body().code==401)
                    {
                        GeneralFunctions.tokenExpireAction(context);
                    }
                    else if(response.body().success==1)
                    {
                        mPrefs.save(Constants.GenerateSurveyId,"yes");
                        StartSurveyPaging object =response.body();
                        list=new ArrayList<FieldListClass>();
                        if (response.body().data.rule_type.equals("q")) {
                            List<StartSurveyPaging> StartSurveys = new ArrayList<StartSurveyPaging>();
                            //StartSurveys=new Gson().fromJson(mPrefs.getString(Constants.savedResponses,""),new TypeToken<List<StartSurvey>>(){}.getType());
                            StartSurveys.add(object);

                            count++;
                            String s=Constants.savedResponses+""+count;
                            mPrefs.save(s, new Gson().toJson(StartSurveys));
                            list.clear();
                            list=new ArrayList<FieldListClass>();
                            ((FirstSurveyActivity)context).setPagerPage(count);
                            ((FirstSurveyActivity)context).setProgress();


                        }

                        else if(response.body().data.rule_type.equals("r"))
                        {
                            Intent intent = new Intent(context, SecondSchemesActivity.class);
                            Bundle bundle = new Bundle();
                            //bundle.putString();
                            //bundle.putString("completeList",);
                            // bundle.putString("qualifiedList", new Gson().toJson(response.body()));
                            //intent.putExtras(bundle);
                       /* list.clear();
                        list=new ArrayList<FieldListClass>();*/
                            mPrefs.save("qualifiedList" ,new Gson().toJson(response.body()));
                            context.startActivity(intent);
                            count=0;
                            ((FirstSurveyActivity)context).finish();
                        }
                    /*else if(response.body().data.rule_type.equals("r"))
                    {
                        Intent intent = new Intent(context, EligibilityActivity.class);
                        Bundle bundle = new Bundle();
                        //  bundle.putString();
                        // bundle.putString("completeList",);
                        bundle.putString("qualifiedList", new Gson().toJson(response.body()));
                        intent.putExtras(bundle);
                        mPrefs.save(Constants.FirstSurvey,true);
                        context.startActivity(intent);
                    }*/


                        else {
                            ((FirstSurveyActivity)context).noSchemeAvailable();
                            list.clear();
                            //list=new ArrayList<FieldListClass>()
                            //  ((FirstSurveyActivity)context).finish();

                        }
                    }
                    else {
                        Toast.makeText(context, ""+response.body().message, Toast.LENGTH_SHORT).show();
                    }
                }
                else  Toast.makeText(context, context.getResources().getString(R.string.serverIssue), Toast.LENGTH_SHORT).show();


            }

            @Override
            public void onFailure(Call<StartSurveyPaging> call, Throwable t) {
                GeneralFunctions.dismissDialog();
                Toast.makeText(context, context.getResources().getString(R.string.netIssue), Toast.LENGTH_SHORT).show();
            }
        });
    }


    //public  TextView dPickers;
    private class ViewHolder5 extends RecyclerView.ViewHolder {
        private TextView tvDobQues,dPickers;
        public ViewHolder5(View v) {
            super(v);
            dPickers= v.findViewById(R.id.dPicker);
            tvDobQues= v.findViewById(R.id.tvDobQues);
        }
    }

    private Calendar myCalendar;
    DatePickerDialog.OnDateSetListener date;



    com.tsongkha.spinnerdatepicker.DatePickerDialog.OnDateSetListener dates;

    private void setCustomDate()
    {
        myCalendar = Calendar.getInstance();
        dates= new com.tsongkha.spinnerdatepicker.DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(com.tsongkha.spinnerdatepicker.DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                String s=year+"/"+monthOfYear+"/"+dayOfMonth;
                if(MyApplication.compareDate(Calendar.getInstance().getTime(),myCalendar.getTime())) {
                    mPrefs.save("date_of_survey_birth", s);
                    updateLabel(year, monthOfYear + 1, dayOfMonth);
                }


                if(     mPrefs.getString("sgm_new_id","00").equals("7462")  ||
                        mPrefs.getString("sgm_new_id","00").equals("7463")  ||
                        mPrefs.getString("sgm_new_id","00").equals("7464")  ||
                        mPrefs.getString("sgm_new_id","00").equals("7465")  ||
                        mPrefs.getString("sgm_new_id","00").equals("7466")  ||
                        mPrefs.getString("sgm_new_id","00").equals("7467")  ||
                        mPrefs.getString("sgm_new_id","00").equals("7468")  ||
                        mPrefs.getString("sgm_new_id","00").equals("7469")  ||
                        mPrefs.getString("sgm_new_id","00").equals("7470")  ||
                        mPrefs.getString("sgm_new_id","00").equals("7471")  ||
                        mPrefs.getString("sgm_new_id","00").equals("7472")

                        )
                {



                    long msDiff = myCalendar.getTimeInMillis()- Calendar.getInstance().getTimeInMillis() ;
                    long daysDiff = TimeUnit.MILLISECONDS.toDays(msDiff);

                    if(  Calendar.getInstance().getTimeInMillis()< myCalendar.getTimeInMillis()  && daysDiff <30  )
                    {
                        mPrefs.save("date_of_survey_birth", s);
                        updateLabel(year, monthOfYear + 1, dayOfMonth);
                    }
                    else
                    if(Calendar.getInstance().getTimeInMillis()> myCalendar.getTimeInMillis() )
                    {
                        mPrefs.save("date_of_survey_birth", s);
                        updateLabel(year, monthOfYear + 1, dayOfMonth);
                    }
                    else Toast.makeText(context, context.getResources().getString(R.string.validDate), Toast.LENGTH_SHORT).show();


            }

            }
        };



    }

    private void updateLabel(int year, int month, int day) {
        Calendar c = Calendar.getInstance();


        dateObj.dPickers.setText(day+"/"+month+"/"+year);
        Calendar calendar = Calendar.getInstance();
        int currentYear=calendar.get(Calendar.YEAR);
        int currentMonth =calendar.get(Calendar.MONTH)+1;
        int currentDay =calendar.get(Calendar.DAY_OF_MONTH);






        SimpleDateFormat myFormat = new SimpleDateFormat("dd MM yyyy");

        String firstDate=currentDay+" "+currentMonth+" "+currentYear;

        String secondDate=day+" "+month+" "+year;
        Date startDate = null;   // initialize start date
        try {
            startDate = myFormat.parse(firstDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Date endDate   = null;
        try {
            endDate = myFormat.parse(secondDate);
            c.setTime(endDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        long duration  = startDate.getTime() - endDate.getTime();

        long dayDiff = TimeUnit.MILLISECONDS.toDays(duration);


        //int monthdiff = TimeUnit.DAYS.toDays(duration);


        int yearDiff=  calendar.get(Calendar.YEAR) - c.get(Calendar.YEAR);

        int monthdiff = (yearDiff *12) +(calendar.get(Calendar.MONTH) - c.get(Calendar.MONTH));


        if((calendar.get(Calendar.MONTH)==c.get(Calendar.MONTH)&& (calendar.get(Calendar.DAY_OF_MONTH)<=c.get(Calendar.DAY_OF_MONTH)))|| (calendar.get(Calendar.MONTH)<c.get(Calendar.MONTH)))
        {
            yearDiff--;
        }
        long timeStamp =endDate.getTime();
        long epchTime=timeStamp/1000;

        //int diffYear = endCalendar.get(Calendar.YEAR) - startCalendar.get(Calendar.YEAR);
        //diffYear * 12 + endCalendar.get(Calendar.MONTH) - startCalendar.get(Calendar.MONTH);
        /* */

        int chks=0;
        object=new Value();
        obj = new FieldListClass();
        object.value=year+"/"+day+"/"+month;
        object.date=day+"/"+month+"/"+year;

        object.year=String.valueOf(year);
        object.day=String.valueOf(day);
        object.month=String.valueOf(month);
        object.timestamp=timeStamp;
        object.unixtimestamp=epchTime;
        object.daydifference=(int)dayDiff;
        object.monthdifference=monthdiff;
        object.yeardifference=yearDiff;


        object.display_value=day+"/"+month+"/"+year;



        obj.fieldId=String.valueOf(entList.data.field_data.get(datePosition).field_id);
        obj.value =object;
        for(int i = 0; i< list.size(); i++)
        {
            if(list.get(i).fieldId.equals(entList.data.field_data.get(datePosition).field_id.toString()))
            {
                list.set(i,obj);
                chks++;
            }
        }
        if(chks==0) {
            list.add(obj);
        }

        //  setValues();
    }


   /* private void  sfd(Date currentdate)
    {



        Calendar selectedcal = Calendar.getInstance();
        Calendar current =Calendar.getInstance();

        selectedcal.setTime(currentdate);



       int months = (current.get(Calendar.YEAR) - selectedcal.get(Calendar.YEAR)) * 12;
        months -= selectedcal.get(Calendar.MONTH) + 1;
        months += currentdate.getMonth();
        m_diff =  months <= 0 ? 0 : months

        var y_diff;
        var age_diff_m = Date.now() - selected.getTime();
        var age_date = new Date(age_diff_m); // miliseconds from epoch
        y_diff = Math.abs(age_date.getUTCFullYear() - 1970);

        var d_diff = Math.ceil(age_diff_m / (1000 * 3600 * 24));

        dateobj = {
                value:fd,
            date:selected.toLocaleDateString(),
            year:selected.getFullYear().toString(),
            day:selected.getDay().toString(),
            month:(selected.getMonth() + 1).toString(),
            timestamp:selected.getTime()|0,
            unixtimestamp:selected.getTime()/1000|0,
            yeardifference:y_diff,
            daydifference:d_diff,
            monthdifference:m_diff,
         };
    }*/


}
