package com.vhiefa.disasteralert;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;

import com.vhiefa.disasteralert.pref.IntroPreference;

import java.util.HashMap;

/**
 * Created by Afifatul on 4/14/2017.
 */

public class IntroActivity extends FragmentActivity {

    IntroPreference introPreference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);

       introPreference = new IntroPreference(IntroActivity.this);
        if (introPreference.isIntroRead() == true){
            Intent i = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(i);
            finish();
        }

        ViewPager pager = (ViewPager) findViewById(R.id.viewPager);
        pager.setAdapter(new MyPagerAdapter(getSupportFragmentManager()));
    }

    private class MyPagerAdapter extends FragmentPagerAdapter {

        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int pos) {
            switch(pos) {

                case 0: return PreIntroFragment.newInstance("FirstFragment, Instance 1");
                case 1: return FirstIntroFragment.newInstance("FirstFragment, Instance 1");
                case 2: return SecondIntroFragment.newInstance("SecondFragment, Instance 1");
                case 3: return ThirdIntroFragment.newInstance("ThirdFragment, Instance 1");
          //      case 3: return ThirdIntroFragment.newInstance("ThirdFragment, Instance 2");
          //      case 4: return ThirdIntroFragment.newInstance("ThirdFragment, Instance 3");
                default: return ThirdIntroFragment.newInstance("ThirdFragment, Default");
            }
        }

        @Override
        public int getCount() {
            return 4;
        }
    }
}
