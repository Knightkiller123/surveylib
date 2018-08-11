package easygov.saral.harlabh.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import easygov.saral.harlabh.activity.FirstSurveyActivity;
import easygov.saral.harlabh.models.HeaderList;
import easygov.saral.harlabh.models.ServicesDetails;
import easygov.saral.harlabh.R;
import easygov.saral.harlabh.utils.Constants;
import easygov.saral.harlabh.utils.Prefs;
import static easygov.saral.harlabh.fragments.storyboardfrags.YourStoryDetailsFragment.count;
/**
 * Created by apoorv on 22/08/17.
 */

public class ExpandableListAdapter extends BaseExpandableListAdapter {

    private List <HeaderList> headerListl;
    HashMap<String, List<ServicesDetails>> childMap;
    private Context context;
    private Prefs mPrefs;
    private List<ServicesDetails> exlList;
    public ExpandableListAdapter(Context context, List<HeaderList> headers,
                                 HashMap<String, List<ServicesDetails>> listChildData)
    {
        this.context=context;
        headerListl=headers;
        childMap=listChildData;
        mPrefs=Prefs.with(context);
        exlList=new ArrayList<>();
    }

    @Override
    public int getGroupCount() {
        return this.headerListl.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        int a=this.childMap.get(this.headerListl.get(groupPosition).name)
                .size();
        return a;
    }

    @Override
    public Object getGroup(int groupPosition) {
        return this.headerListl.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return this.childMap.get(this.headerListl.get(groupPosition).name)
                .get(childPosition).serviceName;
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        HeaderList obj =new HeaderList();
        obj=(HeaderList) getGroup(groupPosition);
        String headerTitle = obj.name;

        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.list_group, null);
        }

        TextView tvCategoryHead = convertView.findViewById(R.id.tvCategoryHead);
        ImageView ivCatImg= convertView.findViewById(R.id.ivCatImg);

       // setImageView(ivCatImg,Integer.parseInt(mPrefs.getString(Constants.StateId,"")));

        setImageRound(ivCatImg,obj.img);
        ImageView  ivExArrow= convertView.findViewById(R.id.ivExArrow);
       // tvCategoryHead.setTypeface(null, Typeface.BOLD);
        tvCategoryHead.setText(headerTitle);
        if(isExpanded)
        {
            ivExArrow.setImageDrawable(context.getDrawable(R.drawable.uparrw));
        }
        else
        {
            ivExArrow.setImageDrawable(context.getDrawable(R.drawable.downarrow));
        }


        return convertView;
    }

    @Override
    public View getChildView(final int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        final String childText =  String.valueOf(getChild(groupPosition, childPosition));

        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this.context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.list_item, null);
        }

        TextView tvListItem = convertView
                .findViewById(R.id.tvListItem);

        tvListItem.setText(childText);

        tvListItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle=new Bundle();
                Intent intent =new Intent(context,FirstSurveyActivity.class);
                exlList=childMap.get(headerListl.get(groupPosition).name);

                bundle.putString("is_category","0");
                if(exlList.get(childPosition).scheme==1) {
                    bundle.putString("is_scheme", "1");
                    mPrefs.save(Constants.IsScheme, "YES");
                    mPrefs.save(Constants.SecontActivityText, context.getResources().getString(R.string.schemes));

                }
                else {
                    bundle.putString("is_scheme","0");
                    mPrefs.save(Constants.IsScheme, "NO");
                    mPrefs.save(Constants.SecontActivityText, context.getResources().getString(R.string.Services));

                    if(SurveyAdapter.list!=null)
                    {
                        SurveyAdapter.list.clear();
                        SurveyAdapter.list=new ArrayList<>();
                        count=0;
                    }
                }
                bundle.putString("selected_id",exlList.get(childPosition).sgmid.toString());
                mPrefs.save("sgm_new_id",exlList.get(childPosition).sgmid.toString());

                bundle.putString("geography_id",exlList.get(childPosition).geographyId.toString());
                intent.putExtras(bundle);
                mPrefs.save(Constants.FromServices,"yes");
                context.startActivity(intent);
            }
        });
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    private void setImageView(ImageView img ,int pos)
    {
        switch (pos) {
            case 1:
                img.setBackgroundResource(R.drawable.delhi_01);
                break;

            case 2:
                img.setBackgroundResource(R.drawable.up_01);
                break;

            case 3:
                img.setBackgroundResource(R.drawable.punjab_01);
                break;

            case 4:
                img.setBackgroundResource(R.drawable.maharastra_01);
                break;

            case 5:
                img.setBackgroundResource(R.drawable.west_bengal_01);
                break;

            case 6:
                img.setBackgroundResource(R.drawable.karnataka_01);
                break;

            case 7:
                img.setBackgroundResource(R.drawable.gujrat_01);
                break;

            case 8:
                img.setBackgroundResource(R.drawable.rajasthan_01);
                break;

            case 9:
                img.setBackgroundResource(R.drawable.bihar_01);
                break;

            case 10:
                img.setBackgroundResource(R.drawable.telangana_01);

                break;

            case 11:
                img.setBackgroundResource(R.drawable.ap_01);

                break;

            case 12:
                img.setBackgroundResource(R.drawable.tamilnadu_01);

                break;

            case 13:
                img.setBackgroundResource(R.drawable.kerala_01);

                break;

            case 14:
                img.setBackgroundResource(R.drawable.chhattisgarh_01);
                break;

            case 15:
                break;

            case 16:
                img.setBackgroundResource(R.drawable.odisa_01);

                break;

            case 17:
                img.setBackgroundResource(R.drawable.mp_01);

                break;

            case 18:
                img.setBackgroundResource(R.drawable.assam_01);

                break;

            case 19:
                img.setBackgroundResource(R.drawable.uttrakhand_01);

                break;

            case 20:
                img.setBackgroundResource(R.drawable.haryana_01);

                break;


            case 21:
                img.setBackgroundResource(R.drawable.kashmir_01);

                break;

            case 22:
                img.setBackgroundResource(R.drawable.hp_01);

                break;

            case 23:
                img.setBackgroundResource(R.drawable.chandigrah_01);

                break;

            case 24:
                img.setBackgroundResource(R.drawable.puducherry_01);

                break;

            case 25:
                img.setBackgroundResource(R.drawable.tripura_01);

                break;

            case 26:
                img.setBackgroundResource(R.drawable.meghalaya_01);

                break;

            case 27:
                img.setBackgroundResource(R.drawable.goa_01);

                break;

            case 28:
                img.setBackgroundResource(R.drawable.manipur_01);

                break;

            case 29:
                img.setBackgroundResource(R.drawable.mizoram_01);

                break;

            case 30:
                img.setBackgroundResource(R.drawable.dadra_01);

                break;

            case 31:
                img.setBackgroundResource(R.drawable.sikkim_01);

                break;

            case 32:
                img.setBackgroundResource(R.drawable.andaman_01);

                break;

            case 33:
                img.setBackgroundResource(R.drawable.daman_01);

                break;

            case 34:
                img.setBackgroundResource(R.drawable.arunachal_01);

                break;

            case 35:
                img.setBackgroundResource(R.drawable.lakshadeew_01);

                break;

            case 36:
                img.setBackgroundResource(R.drawable.jharkhand_01);

                break;

            case 37:
                img.setBackgroundResource(R.drawable.nagaland_01);

                break;


        }





    }


    private void setImageRound(final ImageView ivCarImage, String image) {
        Glide.with(context).load(mPrefs.getString(Constants.IMAGESTR,"")+image).asBitmap().centerCrop().into(new BitmapImageViewTarget(ivCarImage) {
            @Override
            protected void setResource(Bitmap resource) {
                RoundedBitmapDrawable circularBitmapDrawable =
                        RoundedBitmapDrawableFactory.create(context.getResources(), resource);
                circularBitmapDrawable.setCornerRadius(2f);
                ivCarImage.setImageDrawable(circularBitmapDrawable);

            }
        });
    }
}
