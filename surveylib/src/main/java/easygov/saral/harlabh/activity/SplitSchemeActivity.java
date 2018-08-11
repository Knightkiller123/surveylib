package easygov.saral.harlabh.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import easygov.saral.harlabh.R;

public class SplitSchemeActivity extends AppCompatActivity {

    private RadioGroup rgInBpl,rgIsSportsPlayer,rgMaritalStatus,rgMaritalStatusMother,rgGender,
            rgCategory,rgIsSelfOrphan,rgAnnualIncome;
   /* private RadioButton  rbInBplYes,rbInBplNo,rbIsSportsPlayerYes,rbIsSportsPlayerNo,rbUnmarried,rbWidow,
            rbDivorce,rbMarried,rbMotherWidow,rbMotherDivorce,rbMotherMarried,rbFemale,rbMale,rbSC,rbDNTs,
            rbTapriwas,rbOBC,rbGeneral,rbNomadicStates,rbOrphanYes,rbOrphanNo,rbLessThanLakh,rbGreatThanLakh;*/
    private TextView tvNext;
    private Bundle bundle = new Bundle();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_split_scheme);
        initialize();
    }

    private void initialize() {
        tvNext=findViewById(R.id.tvNext);

        rgInBpl=findViewById(R.id.rgInBpl);
        rgIsSportsPlayer=findViewById(R.id.rgIsSportsPlayer);
        rgMaritalStatus=findViewById(R.id.rgMaritalStatus);
        rgMaritalStatusMother=findViewById(R.id.rgMaritalStatusMother);
        rgGender=findViewById(R.id.rgGender);
        rgCategory=findViewById(R.id.rgCategory);
        rgIsSelfOrphan=findViewById(R.id.rgIsSelfOrphan);
        rgAnnualIncome=findViewById(R.id.rgAnnualIncome);


       /* rbInBplYes=findViewById(R.id.rbInBplYes);
        rbInBplNo=findViewById(R.id.rbInBplNo);
        rbIsSportsPlayerYes=findViewById(R.id.rbIsSportsPlayerYes);
        rbIsSportsPlayerNo=findViewById(R.id.rbIsSportsPlayerNo);
        rbUnmarried=findViewById(R.id.rbUnmarried);
        rbWidow=findViewById(R.id.rbWidow);
        rbDivorce=findViewById(R.id.rbDivorce);
        rbMarried=findViewById(R.id.rbMarried);
        rbMotherWidow=findViewById(R.id.rbMotherWidow);
        rbMotherDivorce=findViewById(R.id.rbMotherDivorce);
        rbMotherMarried=findViewById(R.id.rbMotherMarried);
        rbFemale=findViewById(R.id.rbFemale);
        rbMale=findViewById(R.id.rbMale);
        rbSC=findViewById(R.id.rbSC);
        rbDNTs=findViewById(R.id.rbDNTs);
        rbGreatThanLakh=findViewById(R.id.rbGreatThanLakh);
        rbLessThanLakh=findViewById(R.id.rbLessThanLakh);
        rbOrphanNo=findViewById(R.id.rbOrphanNo);
        rbOrphanYes=findViewById(R.id.rbOrphanYes);
        rbNomadicStates=findViewById(R.id.rbNomadicStates);
        rbOBC=findViewById(R.id.rbOBC);
        rbGeneral=findViewById(R.id.rbGeneral);
        rbTapriwas=findViewById(R.id.rbTapriwas);

*/


        tvNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(rgInBpl.getCheckedRadioButtonId()!=-1 && rgIsSportsPlayer.getCheckedRadioButtonId()!=-1 && rgMaritalStatus.getCheckedRadioButtonId()!=-1
                        && rgMaritalStatusMother.getCheckedRadioButtonId()!=-1 &&
                        rgGender.getCheckedRadioButtonId()!=-1 && rgIsSelfOrphan.getCheckedRadioButtonId()!=-1 &&  rgAnnualIncome.getCheckedRadioButtonId()!=-1 )
                {
                    if( rgGender.getCheckedRadioButtonId()==R.id.rbMale ||
                            rgAnnualIncome.getCheckedRadioButtonId()== R.id.rbGreatThanLakh)
                    {
                        Toast.makeText(SplitSchemeActivity.this,"Not elegible",Toast.LENGTH_SHORT).show();
                    }
                    else if(rgMaritalStatus.getCheckedRadioButtonId()== R.id.rbUnmarried  )
                    {
                        if(rgMaritalStatusMother.getCheckedRadioButtonId()==R.id.rbMotherDivorce ||
                                rgMaritalStatusMother.getCheckedRadioButtonId()==R.id.rbMotherWidow)
                        {
                            updateIntent("7449");
                        }
                        else if(rgInBpl.getCheckedRadioButtonId()==R.id.rbInBplYes)
                        {
                            updateIntent("7445");
                        }
                        else
                        {
                            updateIntent("7447");
                        }

                    }
                    else if(rgMaritalStatus.getCheckedRadioButtonId()== R.id.rbMarried  )
                    {
                        if(rgMaritalStatusMother.getCheckedRadioButtonId()==R.id.rbMotherDivorce ||
                                rgMaritalStatusMother.getCheckedRadioButtonId()==R.id.rbMotherWidow)
                        {
                            updateIntent("7450");
                        }
                        else if(rgInBpl.getCheckedRadioButtonId()==R.id.rbInBplYes)
                        {
                            updateIntent("7446");
                        }
                        else
                        {
                            updateIntent("7448");
                        }
                    }
                    else if(rgMaritalStatus.getCheckedRadioButtonId()== R.id.rbWidow ||
                            rgMaritalStatus.getCheckedRadioButtonId()== R.id.rbDivorce)
                    {
                        updateIntent("7451");
                    }

                    else  if(rgIsSelfOrphan.getCheckedRadioButtonId()== R.id.rbOrphanYes)

                    {
                        updateIntent("7453");
                    }

                    else if(rgIsSportsPlayer.getCheckedRadioButtonId()== R.id.rbIsSportsPlayerYes)
                    {
                        updateIntent("7455");
                    }
                    else
                    {
                        Toast.makeText(SplitSchemeActivity.this,"Not elegible",Toast.LENGTH_SHORT).show();
                    }
                }
                else
                {
                    Toast.makeText(SplitSchemeActivity.this,"enter data",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void  updateIntent(String id)
    {
        bundle=getIntent().getExtras();
        if(bundle!=null)
            bundle.putString("selected_id",id);
        Intent intent = new Intent(this, FirstSurveyActivity.class);
        intent.putExtras(bundle);
        startActivity(intent);

    }
}
