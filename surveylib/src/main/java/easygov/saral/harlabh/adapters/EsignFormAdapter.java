package easygov.saral.harlabh.adapters;

import android.app.DatePickerDialog;
import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.tsongkha.spinnerdatepicker.SpinnerDatePickerDialogBuilder;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;


import easygov.saral.harlabh.R;
import easygov.saral.harlabh.activity.EsignFormActivity;

import easygov.saral.harlabh.models.responsemodels.esignformmodel.EsignDatum;
import easygov.saral.harlabh.models.responsemodels.esignformmodel.EsignOptions;
import easygov.saral.harlabh.utils.MyApplication;
import easygov.saral.harlabh.utils.Prefs;

/**
 * Created by apoorv on 13/09/17.
 */

public class EsignFormAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<EsignDatum> details;
    private Context context;
    private Prefs mPrefs;
    private String vals[];
    private Calendar myCalendar;
    DatePickerDialog.OnDateSetListener date;
    public EsignFormAdapter(EsignFormActivity esignFormActivity, List<EsignDatum> fieldsData) {
        context=esignFormActivity;
        details=fieldsData;
        mPrefs=Prefs.with(context);
        vals= new String[details.size()];
        for(int i=0;i <details.size();i++)
        {
            if(details.get(i).val!=null)
                vals[i]=details.get(i).val;
            else
                vals[i]="";
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        View v=null;
        switch (viewType) {
            case 0:
                v = inflater.inflate(R.layout.textview_user, parent, false);
                viewHolder = new ViewHolder1(v);
                break;
            case 1:
                v = inflater.inflate(R.layout.edittext, parent, false);
                viewHolder = new ViewHolder2(v);

                break;

            case 2:
                v = inflater.inflate(R.layout.select, parent, false);
                viewHolder = new ViewHolder3(v);
                break;
            case 3:
                v = inflater.inflate(R.layout.datetime_user, parent, false);
                viewHolder = new ViewHolder4(v);
                break;

            default:
                v = inflater.inflate(R.layout.textview_user, parent, false);
                viewHolder = new ViewHolder1(v);
                break;
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        switch (holder.getItemViewType()) {
            case 0:
                ViewHolder1 vh1 = (ViewHolder1) holder;
                //holder.itemView.setBackgroundColor(Color.CYAN);
                configureViewHolder1(vh1, position);
                break;
            case 1:
                ViewHolder2 vh2 = (ViewHolder2) holder;
               // holder.itemView.setBackgroundColor(Color.TRANSPARENT);
                configureViewHolder2(vh2, position);
                break;
            case 2:
                ViewHolder3 vh3 = (ViewHolder3) holder;

               // holder.itemView.setBackgroundColor(Color.TRANSPARENT);
                configureViewHolder3(vh3, position);
                break;

            case 3:
                ViewHolder4 vh4 = (ViewHolder4) holder;
                //holder.itemView.setBackgroundColor(Color.TRANSPARENT);
                configureViewHolder4(vh4, position);
                break;

            default:
                ViewHolder1 vh11 = (ViewHolder1) holder;
                holder.itemView.setBackgroundColor(Color.CYAN);
                configureViewHolder1(vh11, position);
                break;

        }

    }

    private void configureViewHolder1(ViewHolder1 vh1, int position) {
        vh1.tvHeader.setText(details.get(position).display_name);
        mPrefs.save(details.get(position).id+"_"+details.get(position).field__system_name,vh1.tvHeader.getText().toString());



    }
    private void configureViewHolder2(final ViewHolder2 vh2, final int position) {
        vh2.tveditTitle.setText(String.valueOf(details.get(position).field__display_name).toUpperCase());
        vh2.etEnter.setHint(details.get(position).display_name);

        if(!vals[position].isEmpty())
        {
            vh2.etEnter.setText(vals[position]);
            mPrefs.save(details.get(position).id+"_"+details.get(position).field__system_name,vals[position]);
           // vals[position]=vh2.etEnter.getText().toString();
        }
        else {
            vh2.etEnter.setText("");
        }
        String rule=details.get(position).rule_type;

        if(rule!=null)
        {
            switch (rule)
            {
                case "contact":
                    vh2.etEnter.setInputType(InputType.TYPE_CLASS_NUMBER);
                    InputFilter[] FilterArray = new InputFilter[1];
                    FilterArray[0] = new InputFilter.LengthFilter(10);
                    vh2.etEnter.setFilters(FilterArray);
                    break;

                case "pincode":
                    vh2.etEnter.setInputType(InputType.TYPE_CLASS_NUMBER);
                    InputFilter[] FilterArray1 = new InputFilter[1];
                    FilterArray1[0] = new InputFilter.LengthFilter(6);
                    vh2.etEnter.setFilters(FilterArray1);
                    break;

                case "decimal":
                    vh2.etEnter.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL);
                    break;
                case "height":
                    InputFilter[] FilterArray2 = new InputFilter[1];
                    FilterArray2[0] = new InputFilter.LengthFilter(3);
                    vh2.etEnter.setFilters(FilterArray2);
                    vh2.etEnter.setInputType(InputType.TYPE_CLASS_NUMBER);
                    break;

                case "age":
                    InputFilter[] FilterArray3 = new InputFilter[1];
                    FilterArray3[0] = new InputFilter.LengthFilter(2);
                    vh2.etEnter.setFilters(FilterArray3);
                    vh2.etEnter.setInputType(InputType.TYPE_CLASS_NUMBER);
                    break;

                case "amount":
                    vh2.etEnter.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL);
                    break;
            }}
        else {vh2.etEnter.setInputType(InputType.TYPE_CLASS_TEXT);}

        vh2.etEnter.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus)
                    vh2.etEnter.setBackgroundResource(R.drawable.edittectfocused);

                else
                    vh2.etEnter.setBackgroundResource(R.drawable.edittextunfocused);
            }
        });
        vh2.etEnter.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                mPrefs.save(details.get(position).id+"_"+details.get(position).field__system_name,vh2.etEnter.getText().toString());
                //vals[position]=vh2.etEnter.getText().toString();


            }
        });


    }

    private void configureViewHolder3(final ViewHolder3 vh3, final int position) {
        List<String> stat=new ArrayList<>();
        List<EsignOptions> states =new ArrayList();
        vh3.tvSpinnerTitle.setText(details.get(position).display_name);
        states=details.get(position).options;
        for(int i=0;i<states.size();i++)
        {
            stat.add(states.get(i).value);
        }
        ArrayAdapter<String> adapter=new ArrayAdapter<String>(context, R.layout.spinner_title_text,stat);
        adapter.setDropDownViewResource(R.layout.spinner_adapter_value);
        vh3.spState.setAdapter(adapter);

        vh3.spState.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                // mPrefs.save(details.get(position).field__system_name,vh3.spState.getItemAtPosition(i));
                mPrefs.save(details.get(position).id+"_"+details.get(position).field__system_name,details.get(position).options.get(i).id);

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        if(!vals[position].isEmpty())
        {
            if(!details.get(position).field__is_master_table)
            {

                for (int i=0;i<states.size();i++)
                {
                    if(states.get(i).value.equalsIgnoreCase(vals[position]))
                    {
                        vh3.spState.setSelection(i);
                    }
                }

            }
            else {
                for (int i=0;i<states.size();i++)
                {
                    if(states.get(i).id.equals(vals[position]))
                    {
                        vh3.spState.setSelection(i);
                    }
                }
            }
            /*mPrefs.save(details.get(position).id+"_"+details.get(position).field__system_name,vals[position]);
            vals[position]=vh2.etEnter.getText().toString();*/
        }

    }

    int year,month,day,positions;
    ViewHolder4 obj;
    //public static String esignDate="";
    private void configureViewHolder4(final ViewHolder4 vh4, final int position) {

        vh4.tvDobQues.setText(details.get(position).field__display_name);
       // setDate();
        obj=vh4;
        positions=position;
        setCustomDate();
        if(!vals[position].isEmpty())
        {
            SimpleDateFormat form = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
            java.util.Date date ;
            String newFormat="";
            try
            {
                date = form.parse(vals[position]);
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

               newFormat = formatter.format(date);
                vh4.dPickers.setText(newFormat);
                mPrefs.save(details.get(position).id+"_"+details.get(position).field__system_name, vh4.dPickers.getText().toString());
            }
            catch (Exception e)
            {

                e.printStackTrace();
            }


        }

        vh4.dPickers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

               /* new DatePickerDialog(context, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();*/


                new SpinnerDatePickerDialogBuilder()
                        .context(context)
                        .callback(dates)
                        .build()
                        .show();
                //  DialogFragment newFragment = new SelectDateFragment();
                //newFragment.show(((FourthApplicationActivity)context).getSupportFragmentManager(), "DatePicker");


            }
        });

        /*dPickers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment newFragment = new SelectEsignDateFragment();
              esignDate=  details.get(position).id+"_"+details.get(position).field__system_name;
                newFragment.show(((EsignFormActivity)context).getSupportFragmentManager(), "DatePicker");
            }
        });*/
    }

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
                String s=year+"-"+monthOfYear+"-"+dayOfMonth;
                if(MyApplication.compareDate(Calendar.getInstance().getTime(),myCalendar.getTime())) {
                    mPrefs.save("date_of_survey_birth", s);
                    updateLabel(year, monthOfYear + 1, dayOfMonth);
                }
                else Toast.makeText(context, context.getResources().getString(R.string.validDate), Toast.LENGTH_SHORT).show();
            }
        };



    }

    private void setDate()
    {
        myCalendar = Calendar.getInstance();

        // EditText edittext= (EditText) findViewById(R.id.Birthday);
        date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel(year, monthOfYear + 1, dayOfMonth);
            }

        };
    }

    private void updateLabel(int year, int i, int dayOfMonth) {
        String myFormat = "yyyy-MM-dd"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        obj.dPickers.setText(dayOfMonth+"/"+i+"/"+year);
            mPrefs.save(details.get(positions).id + "_" + details.get(positions).field__system_name, obj.dPickers.getText().toString());


    }

    @Override
    public int getItemCount() {
        return details.size();
    }

    @Override
    public int getItemViewType(int position) {
        String val=details.get(position).field__field_type;
        if(val==null)
        {
            val="head";
        }
        int ret=-1;
        switch (val)
        {
            case "head":
                ret=0;
                break;
            case "text":
                ret=1;
                break;

            case "number":
                ret=1;
                break;

            case "select":
                ret=2;
                break;

            case "date":
                ret=3;
                break;
        }
        return ret;
    }

    private class ViewHolder1 extends RecyclerView.ViewHolder {
        private TextView tvHeader;
        public ViewHolder1(View v1) {
            super(v1);
            tvHeader= v1.findViewById(R.id.tvHeader);

        }
    }

    private class ViewHolder2 extends RecyclerView.ViewHolder {
        private EditText etEnter;
        private TextView tveditTitle;
        public ViewHolder2(View v2) {
            super(v2);

            etEnter= v2.findViewById(R.id.etText);
            tveditTitle= v2.findViewById(R.id.tveditTitle);
        }
    }

    private class ViewHolder3 extends RecyclerView.ViewHolder {
        private Spinner spState;
        private TextView tvSpinnerTitle;
        public ViewHolder3(View v3) {
            super(v3);

            spState= v3.findViewById(R.id.spSpin);
            tvSpinnerTitle= v3.findViewById(R.id.tvSpinnerTitle);
        }
    }
    //public  TextView dPickers;


    private class ViewHolder4 extends RecyclerView.ViewHolder {

        //private TimePicker tPicker;
        private TextView tvDobQues;
        public  TextView dPickers;
        public ViewHolder4(View v3) {
            super(v3);
            dPickers= v3.findViewById(R.id.dPicker);
            tvDobQues= v3.findViewById(R.id.tvDobQues);
            // tPicker= (TimePicker) v3.findViewById(R.id.tPicker);
        }
    }

}
