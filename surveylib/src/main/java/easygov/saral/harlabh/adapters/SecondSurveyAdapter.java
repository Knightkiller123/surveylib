package easygov.saral.harlabh.adapters;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
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

import easygov.saral.harlabh.activity.FirstSurveyActivity;
import easygov.saral.harlabh.activity.FourthApplicationActivity;
import easygov.saral.harlabh.activity.ThirdEligibilityActivity;
import easygov.saral.harlabh.fragments.survey.SecondSurveyFragment;
import easygov.saral.harlabh.models.header.FieldListClass;
import easygov.saral.harlabh.models.header.Value;
import easygov.saral.harlabh.models.surveypaging.StartSurveyPaging;
import easygov.saral.harlabh.models.surveypaging.ValuesPaging;
import easygov.saral.harlabh.utils.GeneralFunctions;
import easygov.saral.harlabh.utils.MyApplication;
import easygov.saral.harlabh.utils.Prefs;
import easygov.saral.harlabh.utils.RestClient;
import easygov.saral.harlabh.R;
import easygov.saral.harlabh.utils.Constants;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by apoorv on 31/08/17.
 */

public class SecondSurveyAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private StartSurveyPaging entList=new StartSurveyPaging();
    Map<String ,String> map;
    Value object ;
    public   static List<FieldListClass> lists=new ArrayList<>();
    // List<FieldListClass> editText=new ArrayList<>();
    FieldListClass obj=new FieldListClass();
    private Prefs mPrefs;
    private View popViews;
    PopupWindow pw;
    private String vals[];
    private String nonString="";
    private SecondSurveyFragment secondSurveyFragment;
    int PERMISSION_ALL = 1;
    String[] PERMISSIONS = { Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA};

    public SecondSurveyAdapter(Context context, StartSurveyPaging list, View popView, SecondSurveyFragment secondSurveyFragment)
    {
        this.secondSurveyFragment=secondSurveyFragment;
        this.context=context;
        mPrefs=Prefs.with(context);
        entList=list;
        vals= new String[entList.data.field_data.size()];
        for(int i=0;i <entList.data.field_data.size();i++)
        {
            if(entList.data.field_data.get(i).value!=null)
                vals[i]=entList.data.field_data.get(i).value;
            else
                vals[i]="";
        }

        popViews=popView;
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
           /* case 1:
                ViewHolder2 vh2 = (ViewHolder2) holder;
                configureViewHolder2(vh2, position);
                break;*/

            case 1:
                AutoViewHolder vh2 = (AutoViewHolder) holder;
                newConfigureViewHolder(vh2,position);

                // cconfigueAutoViewHolder2(vh2, position);
                break;

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
                    setTextValues(nonString,pos);

                }

            });
            vh6.flLayout.addView(textView);
        }
    }


    private void newConfigureViewHolder(final AutoViewHolder vh2, final int position) {
        vh2.tvSpinnerTitle.setText(entList.data.field_data.get(position).display_name);

        if (entList.data.field_data.get(position).value != null && entList.data.field_data.get(position).value.isEmpty())
            vh2.tvSearch.setText(R.string.select);
        else {


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
                if(selected_id!=0) {
                    object = new Value();
                    object.display_value = txt;
                    object.value = selected_id.toString();

                    obj = new FieldListClass();
                    obj.fieldId = String.valueOf(entList.data.field_data.get(position).field_id);
                    obj.value = object;

                    int chk=0;
                    for (int i = 0; i < lists.size(); i++) {
                        if (lists.get(i).fieldId.equals(entList.data.field_data.get(position).field_id.toString())) {
                            lists.set(i, obj);
                            chk++;
                        }
                    }

                    if (chk == 0)
                        lists.add(obj);
                    vh2.tvSearch.setText(entList.data.field_data.get(position).value);
                }
            }
            else
                vh2.tvSearch.setText("");

        }

        vh2.tvSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog(vh2, position);
            }
        });

    }

    private void showDialog(final AutoViewHolder vh2, final int position) {

        LayoutInflater factory = LayoutInflater.from(context);
        final View deleteDialogView = factory.inflate(R.layout.dialog_select_option, null);
        final AlertDialog deleteDialog = new AlertDialog.Builder
                (context,android.R.style.Theme_DeviceDefault_Light_NoActionBar_Fullscreen).create();
        deleteDialog.setView(deleteDialogView);
        EditText etSearch=deleteDialogView.findViewById(R.id.etSearchKey);
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

                for (int i = 0; i < lists.size(); i++) {
                    if (lists.get(i).fieldId.equals(entList.data.field_data.get(position).field_id.toString())) {
                        lists.set(i, obj);
                        chk++;
                    }
                }

                if (chk == 0) lists.add(obj);deleteDialog.dismiss();
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
                    if (values.get(i).text.equalsIgnoreCase(vals[position])) {
                        vh2.tvSearch.setText(values.get(i).text);
                        int chk = 0;
                        object = new Value();
                        obj = new FieldListClass();

                        object.display_value = entList.data.field_data.get(position).options.get(i).text;
                        object.value = String.valueOf(entList.data.field_data.get(position).options.get(i).value);
                        obj.fieldId = String.valueOf(entList.data.field_data.get(position).field_id);
                        obj.value = object;


                        for (int i1 = 0; i1 < lists.size(); i1++) {
                            if (lists.get(i1).fieldId.equals(entList.data.field_data.get(position).field_id.toString())) {
                                lists.set(i1, obj);
                                chk++;
                            }
                        }


                        if (chk == 0) lists.add(obj);
                    }
                }

            }
        }





        //Getting the instance of AutoCompleteTextView
        // AutoCompleteTextView actv= (AutoCompleteTextView)findViewById(R.id.autoCompleteTextView1);











        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapterData.clear();

                if(s.length()!=0)
                {
                    for( int i =0;i< stat.size();i++)
                    {

                        if(stat.get(i).toLowerCase().contains(s.toString().toLowerCase()))
                            adapterData.add(stat.get(i));

                    }


                    //   rvData.setAdapter(searchAdapter);
                }

                else
                {
                    adapterData.addAll(stat);
                }
                searchAdapter.notifyDataSetChanged();

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });










        deleteDialog.show();
    }




    ArrayAdapter<String> adapter;
    /*
    private void cconfigueAutoViewHolder2(final AutoViewHolder vh2,final int position12) {
        final List<String> stat=new ArrayList<>();

        values=entList.data.field_data.get(position12).options;

        for (int i=0;i<values.size();i++)
        {
            stat.add(values.get(i).text);
        }

         adapter = new ArrayAdapter<String>
                (context,R.layout.sample,stat);
        //Getting the instance of AutoCompleteTextView
        // AutoCompleteTextView actv= (AutoCompleteTextView)findViewById(R.id.autoCompleteTextView1);
        vh2.actv.setThreshold(1);//will start working from first character
        vh2.actv.setAdapter(adapter);//setting the adapter data into the AutoCompleteTextView
        vh2.actv.setTextColor(Color.BLACK);
        vh2.tvSpinnerTitle.setText(entList.data.field_data.get(position12).display_name);

        vh2.actv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int chk = 0;
                object = new Value();
                obj = new FieldListClass();

                object.value=String.valueOf(entList.data.field_data.get(position12).options.get(position).value);
                obj.fieldId=String.valueOf(entList.data.field_data.get(position12).field_id);
                object.display_value=entList.data.field_data.get(position12).options.get(position).text;
                obj.value =object;


                for(int i=0;i<lists.size();i++)
                {
                    if(lists.get(i).fieldId.equals(entList.data.field_data.get(position12).field_id.toString()))
                    {
                        lists.set(i,obj);
                        chk++;
                    }
                }



                if(chk==0)
                    lists.add(obj);


                GeneralFunctions.hideSoftKeyboard((ThirdEligibilityActivity)context);

                secondSurveyFragment.hideKeyBoard();


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


                        for (int i1 = 0; i1 < lists.size(); i1++) {
                            if (lists.get(i1).fieldId.equals(entList.data.field_data.get(position12).field_id.toString())) {
                                lists.set(i1, obj);
                                chk++;
                            }
                        }


                        if (chk == 0) lists.add(obj);
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
    private int datePosition;
    ViewHolder5 dateObj;
    private void configureViewHolder5(final ViewHolder5 vh5, final int position) {
        dateObj = vh5;
        datePosition = position;
        setCustomDate();
        vh5.tvDobQues.setText(entList.data.field_data.get(position).display_name);


        if (!vals[position].isEmpty()) {
            SimpleDateFormat form;
            if(vals[position].contains("/"))
                form = new SimpleDateFormat("dd/MM/yyyy");
            else
                form = new SimpleDateFormat("dd-MM-yyyy");
            java.util.Date date;
            String newFormat = "";
            try {
                date = form.parse(vals[position]);

                //newFormat = formatter.format(date);
                //vh5.dPickers.setText(newFormat);
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



                new SpinnerDatePickerDialogBuilder()
                        .context(context)
                        .callback(dates)
                        .build()
                        .show();
            }
        });
    }

    private void configureViewHolder4(ViewHolder4 vh4, final int position) {
        vh4.tveditTitle.setText(entList.data.field_data.get(position).display_name);

        vh4.etText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                setEditTextValues(s.toString(),position);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
       /* object.value=vh4.etText.getText().toString();
        obj.fieldId=String.valueOf(entList.data.field_data.get(position).field_id);
        obj.value =object;
        editText.add(obj);*/

    }

    private Calendar myCalendar;
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

        int monthdiff = (yearDiff *12) +( calendar.get(Calendar.MONTH) - c.get(Calendar.MONTH));


        if((calendar.get(Calendar.MONTH)==c.get(Calendar.MONTH)&& (calendar.get(Calendar.DAY_OF_MONTH)<=c.get(Calendar.DAY_OF_MONTH)))|| (calendar.get(Calendar.MONTH)<c.get(Calendar.MONTH)))
        {
            yearDiff--;
        }
        long timeStamp =endDate.getTime();
        long epchTime=timeStamp/1000;



        int chk=0;
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
        for(int i = 0; i< lists.size(); i++)
        {
            if(lists.get(i).fieldId.equals(entList.data.field_data.get(datePosition).field_id.toString()))
            {
                lists.set(i,obj);
                chk++;
            }
        }
        if(chk==0)
            lists.add(obj);


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
            for (int i = 0; i < lists.size(); i++) {
                if (lists.get(i).fieldId.equals(entList.data.field_data.get(position).field_id.toString())) {
                    lists.set(i, obj);

                    chk2++;
                }
            }
            if (chk2 == 0)
                lists.add(obj);
        }

        else {
            for (int i = 0; i < lists.size(); i++) {
                if (lists.get(i).fieldId.equals(entList.data.field_data.get(position).field_id.toString())) {
                    lists.remove(i);


                }
            }
        }
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
                setTextValues(nonString,position);
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
                setTextValues(nonString,position);
            }
        });

    }
    List<ValuesPaging> values =new ArrayList();

    private void configureViewHolder2(ViewHolder2 vh2, final int position12) {
        final List<String> stat=new ArrayList<>();

        values=entList.data.field_data.get(position12).options;

        for (int i=0;i<values.size();i++)
        {
            stat.add(values.get(i).text);
        }
        ArrayAdapter<String> adapter=new ArrayAdapter<String>(context, R.layout.spinner_title_text,stat);
        adapter.setDropDownViewResource(R.layout.spinner_adapter_value);
        vh2.spSpin.setAdapter(adapter);

        vh2.tvSpinnerTitle.setText(entList.data.field_data.get(position12).display_name);

        vh2.spSpin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selected = (String) parent.getItemAtPosition(position);
                int pos = stat.indexOf(selected);
                int chk=0;
                object=new Value();
                obj = new FieldListClass();
                object.value=String.valueOf(entList.data.field_data.get(position12).options.get(pos).value);
                obj.fieldId=String.valueOf(entList.data.field_data.get(position12).field_id);
                object.display_value=entList.data.field_data.get(position12).options.get(pos).text;
                obj.value =object;


                for(int i=0;i<lists.size();i++)
                {
                    if(lists.get(i).fieldId.equals(entList.data.field_data.get(position12).field_id.toString()))
                    {
                        lists.set(i,obj);
                        chk++;
                    }
                }



                if(chk==0) {
                    lists.add(obj);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });



    }

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
                    nonString=String.valueOf(entList.data.field_data.get(position).options.get(0).value);
                    setTextValues(nonString,position);
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
                    setTextValues(nonString,position);
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
                    setTextValues(nonString,position);
                }
            });

        }

        if(entList.data.field_data.get(position).options.size()==3)
        {
            vh1.tvFirst.setText(entList.data.field_data.get(position).options.get(0).text);
            vh1.tvSecond.setText(entList.data.field_data.get(position).options.get(1).text);
            vh1.tvThird.setText(entList.data.field_data.get(position).options.get(2).text);

            vh1.tvFirst.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    vh1.tvFirst.setBackgroundResource(R.drawable.green_curved);
                    vh1.tvFirst.setTextColor(ContextCompat.getColor(context,R.color.white));
                    vh1.tvSecond.setTextColor(ContextCompat.getColor(context,R.color.black));
                    vh1.tvThird.setTextColor(ContextCompat.getColor(context,R.color.black));

                    vh1.tvSecond.setBackgroundResource(R.drawable.white_curved);
                    vh1.tvThird.setBackgroundResource(R.drawable.white_curved);
                    nonString=String.valueOf(entList.data.field_data.get(position).options.get(0).value);
                    setTextValues(nonString,position);
                }
            });

            vh1.tvSecond.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    vh1.tvFirst.setBackgroundResource(R.drawable.white_curved);
                    vh1.tvSecond.setBackgroundResource(R.drawable.green_curved);
                    vh1.tvThird.setBackgroundResource(R.drawable.white_curved);

                    vh1.tvFirst.setTextColor(ContextCompat.getColor(context,R.color.black));
                    vh1.tvSecond.setTextColor(ContextCompat.getColor(context,R.color.white));
                    vh1.tvThird.setTextColor(ContextCompat.getColor(context,R.color.black));
                    nonString=String.valueOf(entList.data.field_data.get(position).options.get(1).value);
                    setTextValues(nonString,position);
                }
            });

            vh1.tvThird.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    vh1.tvFirst.setBackgroundResource(R.drawable.white_curved);
                    vh1.tvSecond.setBackgroundResource(R.drawable.white_curved);
                    vh1.tvThird.setBackgroundResource(R.drawable.green_curved);

                    vh1.tvFirst.setTextColor(ContextCompat.getColor(context,R.color.black));
                    vh1.tvSecond.setTextColor(ContextCompat.getColor(context,R.color.black));
                    vh1.tvThird.setTextColor(ContextCompat.getColor(context,R.color.white));
                    nonString=String.valueOf(entList.data.field_data.get(position).options.get(2).value);
                    setTextValues(nonString,position);
                }
            });
        }
    }

    private void setTextValues(String nonString, int position) {

        object=new Value();
        obj = new FieldListClass();
        object.value=nonString;
        obj.fieldId=String.valueOf(entList.data.field_data.get(position).field_id);


        object.display_value=nonString;



        obj.value =object;
        int chk1=0;
        for(int i=0;i<lists.size();i++)
        {
            if(lists.get(i).fieldId.equals(entList.data.field_data.get(position).field_id.toString()))
            {
                lists.set(i,obj);
                chk1++;
            }
        }
        if(chk1==0)
            lists.add(obj);
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

    private class ViewHolder5 extends RecyclerView.ViewHolder {
        private TextView tvDobQues,dPickers;
        public ViewHolder5(View v) {
            super(v);
            dPickers= v.findViewById(R.id.dPicker);
            tvDobQues= v.findViewById(R.id.tvDobQues);
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
                else ret = 0;

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

        /*if(editText.size()>0)
        {
            lists.addAll(editText);
        }*/
        map.put("field_list",new Gson().toJson(lists));
        map.put("survey_id",mPrefs.getString(Constants.SurveyId,""));
        map.put("current_scheme_list",new Gson().toJson(entList.data.current_scheme_list));
        map.put("existing_field_list",new Gson().toJson(entList.data.existing_field_list));
        map.put("rule_type",entList.data.rule_type);
        map.put(Constants.PARAM_BENEFICIARY_TYPE_ID,mPrefs.getString(Constants.BeneficiaryID,""));

        object=new Value();
        if(lists.size()==entList.data.field_data.size())
            hitNext(map);

        else Toast.makeText(context, context.getResources().getString(R.string.allentries), Toast.LENGTH_SHORT).show();
        //hitNext(map);
    }

    public int hitNext(final Map<String, String> map)
    {
        GeneralFunctions.showDialog(context);
        map.put("geography_id",mPrefs.getString(Constants.GeographyId,""));
        Call<StartSurveyPaging> call= RestClient.get().setEnt(map);
        call.enqueue(new Callback<StartSurveyPaging>() {
            @Override
            public void onResponse(Call<StartSurveyPaging> call, Response<StartSurveyPaging> response) {
                GeneralFunctions.dismissDialog();

                if(response.isSuccessful())
                {
                    if(response.body().success==1)
                    {
                        mPrefs.save(Constants.GenerateSurveyId,"yes");
                        lists = new ArrayList<>();

                        try {
                            Boolean check;
                            try {
                                check = response.body().data.qualified_schemes.objects.get(0).status;

                            } catch (Exception e) {
                                check = true;
                            }
                            if (check == null) {
                                check = true;
                            }
                            if (!check) {
                                /*((ThirdEligibilityActivity) context).finish();
                                 */
                                ((ThirdEligibilityActivity) context).notEligible();

                            } else {
                                StartSurveyPaging object = response.body();
                                if (response.body().data.field_data != null && response.body().data.field_data.size() > 0) {
                                    ThirdEligibilityActivity.counts++;
                                    lists.clear();
                                    lists=new ArrayList<FieldListClass>();
                                    List<StartSurveyPaging> entitlements = new ArrayList<StartSurveyPaging>();
                                    entitlements.add(object);
                                    mPrefs.save(Constants.savedResponses, new Gson().toJson(entitlements));
                                    ((ThirdEligibilityActivity) context).setPagerPage(ThirdEligibilityActivity.counts);
                                    return;
                                } /*else if (response.body().data.qualified_schemes.get(0).benefits != null && response.body().data.qualified_schemes.get(0).benefits.size() > 0) {
                        Bundle bundle = new Bundle();
                        bundle.putString("benefits", new Gson().toJson(response.body()));
                        //  Intent intent=new Intent(context, BenifitsActivity.class);
                        //intent.putExtras(bundle);
                        //context.startActivity(intent);

                    }*/ else if (response.body().data.rule_type.length() == 0) {

                                    if (response.body().data.qualified_schemes.objects.size() > 0) {
                                        if (check) {

                                            //Todo: uncomment code and resume flow to
                                            if(mPrefs.getString(Constants.SchemeActive,"").equals("1"))
                                            {
                                        /*if(!hasPermissions(context, PERMISSIONS)){
                                            ActivityCompat.requestPermissions(((ThirdEligibilityActivity)context), PERMISSIONS, PERMISSION_ALL);
                                        }

                                        else {*/
                                                List<String> benefits =new ArrayList<String>();
                                                String analysis ="",servBenefits="";
                                                servBenefits=response.body().data.qualified_schemes.objects.get(0).service__benefits;
                                                analysis=response.body().data.qualified_schemes.objects.get(0).service__analysis;
                                                benefits=response.body().data.qualified_schemes.objects.get(0).benefits;
                                                mPrefs.save(Constants.IsSchemeOpen,response.body().data.qualified_schemes.objects.get(0).enable_apply);

                                                // benefits.add(servBenefits);
                                                ((ThirdEligibilityActivity) context).eligibleWithBenefits(analysis,servBenefits,benefits,response.body().data.qualified_schemes.objects.get(0).benefits_list);
                                                // }

                                      /*  else {
                                        Intent intent = new Intent(context, FourthApplicationActivity.class);
                                        context.startActivity(intent);
                                        ThirdEligibilityActivity.counts = 0;
                                        ((ThirdEligibilityActivity) context).finish();
                                        }*/
                                            }

                                            else {

                                                //noSchemeFound();
                                                List<String> benefits =new ArrayList<String>();
                                                String analysis ="",servBenefits="";
                                                servBenefits=response.body().data.qualified_schemes.objects.get(0).service__benefits;
                                                analysis=response.body().data.qualified_schemes.objects.get(0).service__analysis;
                                                benefits=response.body().data.qualified_schemes.objects.get(0).benefits;
                                                // benefits.add(servBenefits);
                                                mPrefs.save(Constants.IsSchemeOpen,response.body().data.qualified_schemes.objects.get(0).enable_apply);

                                                ((ThirdEligibilityActivity) context).eligibleWithBenefits(analysis,servBenefits,benefits,response.body().data.qualified_schemes.objects.get(0).benefits_list);
                                            }
                                        } else {
                                            /* ((ThirdEligibilityActivity) context).finish();
                                             */
                                            ((ThirdEligibilityActivity) context).notEligible();
                                            return;
                                        }

                                        // mPrefs.save(Constants.NoAttachments,"YES");
                                    }
                                } else {
                                    Map<String, String> map1 = new HashMap<String, String>();

                                    map1.put("field_list", new Gson().toJson(lists));
                                    map1.put("survey_id", String.valueOf(response.body().data.survey_id));
                                    map1.put("current_scheme_list", new Gson().toJson(response.body().data.current_scheme_list));
                                    map1.put("existing_field_list", new Gson().toJson(response.body().data.existing_field_list));
                                    map1.put("rule_type", entList.data.rule_type);

                                    map.put(Constants.PARAM_BENEFICIARY_TYPE_ID,mPrefs.getString(Constants.BeneficiaryID,""));
                                    ThirdEligibilityActivity.counts++;
                                    lists.clear();
                                    lists=new ArrayList<FieldListClass>();

                                    hitNext(map1);
                                }
                            }
                        }
                        catch (Exception e)
                        {
                    /*Intent intent =new Intent(context, FourthApplicationActivity.class);
                    context.startActivity(intent);*/
                        }

                    }
                    else Toast.makeText(context, ""+response.body().message, Toast.LENGTH_SHORT).show();
                }

                else Toast.makeText(context, context.getResources().getString(R.string.serverIssue), Toast.LENGTH_SHORT).show();




            }

            @Override
            public void onFailure(Call<StartSurveyPaging> call, Throwable t) {
                GeneralFunctions.dismissDialog();
                Toast.makeText(context, context.getResources().getString(R.string.netIssue), Toast.LENGTH_SHORT).show();

            }
        });
        return 1;
    }


    public static boolean hasPermissions(Context context, String... permissions) {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }
    public void noSchemeFound() {
        // final PopupWindow pw;
        //  getActivity().getSupportFragmentManager().beginTransaction().add(R.id.rlAttachment,new DocPopUpFragment()).addToBackStack(null).commit();

        try {
            //We need to get the instance of the LayoutInflater, use the context of this activity
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            //Inflate the view from a predefined XML layout
            View layout = inflater.inflate(R.layout.eligible_dialog,
                    (ViewGroup)popViews.findViewById(R.id.rlnotEligible));
            // create a 300px width and 470px height PopupWindow


            pw = new PopupWindow(layout, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, true);
            // display the popup in the center
            pw.showAtLocation(layout, Gravity.CENTER, 0, 0);






            TextView tvNotEligible= layout.findViewById(R.id.tvNotEligible);

            tvNotEligible.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((ThirdEligibilityActivity)context).finish();
                }
            });



           /* pw.setOnDismissListener(new PopupWindow.OnDismissListener() {
                @Override
                public void onDismiss() {
                    pw.dismiss();

                }
            });*/



        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}

