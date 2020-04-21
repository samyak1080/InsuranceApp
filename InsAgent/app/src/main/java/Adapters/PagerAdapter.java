package Adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.Log;

import com.infosys.touchless.claims.ClaimsDecisioning;
import com.infosys.touchless.claims.MySpace;
import com.infosys.touchless.claims.ShowClaims;


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
                MySpace tab0=new MySpace();
                Log.e("SLIDER","TAB 0");
                return  tab0;
            case 1:
                ShowClaims tab1=new ShowClaims();
                Log.e("SLIDER","TAB 1");
                return  tab1;
            case 2:
                ClaimsDecisioning tab2=new ClaimsDecisioning();
                Log.e("SLIDER","TAB 2");
                return  tab2;

            default:
                return null;
        }
    }

    @Override
    public int getCount()
    {
        return NumberOfTabs;
    }
}
