package easygov.saral.harlabh.adapters;

import android.app.DatePickerDialog;
import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
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
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.tsongkha.spinnerdatepicker.SpinnerDatePickerDialogBuilder;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import easygov.saral.harlabh.models.responsemodels.mandatoryfields.MandatoryDetails;
import easygov.saral.harlabh.utils.MyApplication;
import easygov.saral.harlabh.utils.Prefs;
import easygov.saral.harlabh.R;
import easygov.saral.harlabh.models.responsemodels.mandatoryfields.FieldsOptions;


/**
 * Created by apoorv on 04/09/17.
 */

public class ApplicationDetailAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private List<MandatoryDetails> details=new ArrayList<>();
    private Prefs mPrefs;
    private String vals[];
    private LinearLayoutManager applicationManager;
    ViewHolder2 editViewHolder;
    private Calendar myCalendar;

    DatePickerDialog.OnDateSetListener date;
   // private EditText etText;


    public ApplicationDetailAdapter(Context context, List<MandatoryDetails> details, LinearLayoutManager manager) {
        this.context=context;
        this.details=details;
        vals= new String[details.size()];
       // applicationManager=manager;
        for(int i=0;i <details.size();i++)
        {
            if(details.get(i).val!=null)
                vals[i]=details.get(i).val;
            else
                vals[i]="";
        }
        mPrefs=Prefs.with(context);
    }
    int s=1;
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
                v = inflater.inflate(R.layout.spinner, parent, false);
                viewHolder = new ViewHolder3(v);
                break;
            case 3:
                v = inflater.inflate(R.layout.datetime_user, parent, false);
                viewHolder = new ViewHolder4(v);
                break;

            case 4:
                v = inflater.inflate(R.layout.secondtext, parent, false);
                viewHolder = new ViewHolder5(v);
                break;

            default:
                v = inflater.inflate(R.layout.textview_user, parent, false);
                viewHolder = new ViewHolder1(v);
                break;
        }
        return viewHolder;
    }

    private class ViewHolder1 extends RecyclerView.ViewHolder {
        private TextView tvHeader;
        public ViewHolder1(View v1) {
            super(v1);
            tvHeader= v1.findViewById(R.id.tvHeader);

        }
    }

    private class ViewHolder2 extends RecyclerView.ViewHolder {
        private EditText etText;
        private TextView tveditTitle;
        private ImageView ivAppDetailErase;
        public ViewHolder2(View v2) {
            super(v2);

            etText= v2.findViewById(R.id.etText);
            tveditTitle= v2.findViewById(R.id.tveditTitle);
            ivAppDetailErase= v2.findViewById(R.id.ivAppDetailErase);
        }
    }

    private class ViewHolder3 extends RecyclerView.ViewHolder {
        private Spinner spSpin;
        private TextView tvSpinnerTitle;
        public ViewHolder3(View v3) {
            super(v3);

            spSpin= v3.findViewById(R.id.spSpin);
            tvSpinnerTitle= v3.findViewById(R.id.tvSpinnerTitle);
        }
    }



    private class ViewHolder4 extends RecyclerView.ViewHolder {

        //private TimePicker tPicker;
        private TextView tvDobQues;
        public  TextView dPicker;
        public ViewHolder4(View v3) {
            super(v3);
            dPicker= v3.findViewById(R.id.dPicker);
            tvDobQues= v3.findViewById(R.id.tvDobQues);
        }
    }

    private class ViewHolder5 extends RecyclerView.ViewHolder {

        private TextView tvSecondHeaders;
        public ViewHolder5(View v5) {
            super(v5);
            tvSecondHeaders= v5.findViewById(R.id.tvSecondHeaders);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        switch (holder.getItemViewType()) {
            case 0:
                ViewHolder1 vh1 = (ViewHolder1) holder;
              //  holder.itemView.setBackgroundColor(Color.CYAN);
                configureViewHolder1(vh1, position);
                break;
            case 1:
                ViewHolder2 vh2 = (ViewHolder2) holder;
                editViewHolder=vh2;

                configureViewHolder2(vh2, position);
                break;
            case 2:
                ViewHolder3 vh3 = (ViewHolder3) holder;

               // holder.itemView.setBackgroundColor(Color.TRANSPARENT);
                configureViewHolder3(vh3, position);
                break;

            case 3:
                ViewHolder4 vh4 = (ViewHolder4) holder;
               // holder.itemView.setBackgroundColor(Color.TRANSPARENT);
                configureViewHolder4(vh4, position);
                break;
            case 4:
                ViewHolder5 vh5 = (ViewHolder5) holder;
                // holder.itemView.setBackgroundColor(Color.TRANSPARENT);
                configureViewHolder5(vh5, position);
                break;

            default:
                ViewHolder1 vh11 = (ViewHolder1) holder;
                //holder.itemView.setBackgroundColor(Color.CYAN);
                configureViewHolder1(vh11, position);
                break;

        }

    }

    private void configureViewHolder5(ViewHolder5 vh5, int position) {

        vh5.tvSecondHeaders.setText(details.get(position).display_name);
    }

    private void configureViewHolder1(ViewHolder1 vh1, int position) {

        vh1.tvHeader.setText(details.get(position).display_name);
        mPrefs.save(details.get(position).id + "_" + details.get(position).field__system_name, vh1.tvHeader.getText().toString());

    }


    private void configureViewHolder2(final ViewHolder2 vh2, final int position) {
        vh2.tveditTitle.setText(details.get(position).field__display_name);
        vh2.etText.setHint(details.get(position).display_name);
        editViewHolder.etText.setTag(position);


        if(!vals[position].isEmpty())
        {
            vh2.etText.setText(vals[position]);
        }
        else {
            if(!mPrefs.getString(details.get(position).id+"_"+details.get(position).field__system_name,"").isEmpty())
            {
                vh2.etText.setText(mPrefs.getString(details.get(position).id+"_"+details.get(position).field__system_name,""));
            }
           // vh2.etText.setText("");
        }


        //Todo: enable after api change
        if(details.get(position).is_survey_field)
        {
            vh2.etText.setEnabled(false);
            vh2.etText.setClickable(false);
            mPrefs.save(details.get(position).id+"_"+details.get(position).field__system_name,vh2.etText.getText().toString());
        }
        else {
            vh2.etText.setEnabled(true);
            vh2.etText.setClickable(true);
        }


        String rule=details.get(position).rule_type;

        if(rule!=null)
        {
            switch (rule)
            {
                case "contact":
                    vh2.etText.setInputType(InputType.TYPE_CLASS_NUMBER);
                    InputFilter[] FilterArray = new InputFilter[1];
                    FilterArray[0] = new InputFilter.LengthFilter(10);
                    vh2.etText.setFilters(FilterArray);
                    break;

                case "pincode":
                    vh2.etText.setInputType(InputType.TYPE_CLASS_NUMBER);
                    InputFilter[] FilterArray1 = new InputFilter[1];
                    FilterArray1[0] = new InputFilter.LengthFilter(6);
                    vh2.etText.setFilters(FilterArray1);
                    break;

                case "decimal":
                    vh2.etText.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL);
                    break;

                case "height":
                    InputFilter[] FilterArray2 = new InputFilter[1];
                    FilterArray2[0] = new InputFilter.LengthFilter(2);
                    vh2.etText.setFilters(FilterArray2);
                    vh2.etText.setInputType(InputType.TYPE_CLASS_NUMBER);
                    break;

                case "age":
                    InputFilter[] FilterArray3 = new InputFilter[1];
                    FilterArray3[0] = new InputFilter.LengthFilter(3);
                    vh2.etText.setFilters(FilterArray3);
                    vh2.etText.setInputType(InputType.TYPE_CLASS_NUMBER);
                    break;

                case "amount":
                    vh2.etText.setInputType(InputType.TYPE_CLASS_NUMBER);
                    break;
              case "bankaccount":

                  if(details.get(position).display_name.equalsIgnoreCase("bank account number"))
                      vh2.etText.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_PASSWORD);
                  else
                    vh2.etText.setInputType(InputType.TYPE_CLASS_NUMBER);
                    break;
            }
        }
        else {vh2.etText.setInputType(InputType.TYPE_CLASS_TEXT);}


        vh2.etText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus) {
                    vh2.etText.setBackgroundResource(R.drawable.edittectfocused);
                    if(vh2.etText.getText().toString().trim().length()>0)
                    {
                        vh2.ivAppDetailErase.setVisibility(View.VISIBLE);
                    }
                    else {
                        vh2.ivAppDetailErase.setVisibility(View.GONE);
                    }
                }

                else {

                        vh2.etText.setBackgroundResource(R.drawable.edittextunfocused);
                        vh2.ivAppDetailErase.setVisibility(View.GONE);

                }
            }
        });


        vh2.ivAppDetailErase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vh2.etText.setText("");
            }
        });

        vh2.etText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(!vh2.etText.getText().toString().trim().isEmpty()) {
                    mPrefs.save(details.get(position).id + "_" + details.get(position).field__system_name, vh2.etText.getText().toString());
                    vals[position] = vh2.etText.getText().toString();
                    vh2.ivAppDetailErase.setVisibility(View.VISIBLE);
                }
                else {
                    mPrefs.save(details.get(position).id + "_" + details.get(position).field__system_name, "");
                    vh2.ivAppDetailErase.setVisibility(View.GONE);
                }
               /* if(vh2.etText.getText().toString().isEmpty())
                {
                    //details.get(position).isEntered=false;

                }*/

            }
        });


    }

    public void setErrorBox(int i)
    {
      // editViewHolder.etText.getTag(i);
       // EditText tp=editViewHolder.etText.getTag(i);
        ((EditText)editViewHolder.etText.getTag(i)).setBackgroundResource(R.drawable.edittexterror);
    }


    private void configureViewHolder3(final ViewHolder3 vh3, final int position) {
        vh3.tvSpinnerTitle.setText(details.get(position).display_name);



        List<FieldsOptions> states = new ArrayList();
        states = details.get(position).options;

        List<String> stat = new ArrayList<>();
        for (int i = 0; i < states.size(); i++)
        {
            stat.add(states.get(i).value);
        }

        //Collections.sort(stat);





        ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, R.layout.spinner_title_text, stat);
        adapter.setDropDownViewResource(R.layout.spinner_adapter_value);
        vh3.spSpin.setAdapter(adapter);

        vh3.spSpin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                // mPrefs.save(details.get(position).field__system_name,vh3.spState.getItemAtPosition(i));
                mPrefs.save(details.get(position).id+"_"+details.get(position).field__system_name, details.get(position).options.get(i).id);

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        if(details.get(position).is_survey_field)
        {
           // ((Spinner)  vh3.spSpin).getSelectedView().setEnabled(false);
            vh3.spSpin.setPrompt(details.get(position).val);
            vh3.spSpin.setEnabled(false);
            mPrefs.save(details.get(position).id+"_"+details.get(position).field__system_name, details.get(position).val);
        }
        else {
          //  ((Spinner)  vh3.spSpin).getSelectedView().setEnabled(true);
            vh3.spSpin.setEnabled(true);
        }

    }

    int year,month,day,positions;
    ViewHolder4 obj;
    private void configureViewHolder4(final ViewHolder4 vh4, final int position) {
        vh4.tvDobQues.setText(details.get(position).field__display_name);
        //setDate();

        if(!mPrefs.getString(details.get(position).id+"_"+details.get(position).field__system_name,"").isEmpty())
        {
            vh4.dPicker.setText(mPrefs.getString(details.get(position).id+"_"+details.get(position).field__system_name,""));
        }

        setCustomDate();
        obj=vh4;
        positions=position;
        //if(details.get(position).enabled==1)

       /* if(!mPrefs.getString(details.get(position).id+"_"+details.get(position).field__system_name,"").isEmpty())
        {
            vh4.dPicker.setText(mPrefs.getString(details.get(position).id+"_"+details.get(position).field__system_name,""));

        }*/

        vh4.dPicker.setOnClickListener(new View.OnClickListener() {
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
               // updateLabel();
            }

        };
    }

    private void updateLabel(int year, int i, int dayOfMonth) {
        String myFormat = "yyyy-MM-dd"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

            obj.dPicker.setText(sdf.format(myCalendar.getTime()));
            mPrefs.save(details.get(positions).id + "_" + details.get(positions).field__system_name, obj.dPicker.getText().toString());


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

            case "secondhead":
                ret=4;
                break;
        }
        return ret;
    }

    @Override
    public int getItemCount() {
        return details.size();
    }





}
