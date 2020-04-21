package Adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.Log;

import com.notez.com.myapplication.ClaimEstimation;
import com.notez.com.myapplication.MySpace;
import com.notez.com.myapplication.ShowClaims;
import com.notez.com.myapplication.RaiseClaim;

public class PagerAdapter extends FragmentStatePagerAdapter {
    int NumberOfTabs;
    public PagerAdapter(FragmentManager fm, int NumberofTabs) {
        super(fm);
        this.NumberOfTabs=NumberofTabs;
    }

    @Override
    public Fragment getItem(int i) {
        switch ( i){
            case 0:
                RaiseClaim tab1 =new RaiseClaim();
                Log.e("SLIDER","TAB 1");
                return  tab1;
            case 1:
                ShowClaims tab2=new ShowClaims();
                Log.e("SLIDER","TAB 2");
                return  tab2;

            case 2:
                ClaimEstimation tab3=new ClaimEstimation();
                Log.e("SLIDER","TAB 3");
                return  tab3;
            case 3:
                MySpace tab4=new MySpace();
                Log.e("SLIDER","TAB 4");
                return  tab4;


            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return NumberOfTabs;
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }
}
