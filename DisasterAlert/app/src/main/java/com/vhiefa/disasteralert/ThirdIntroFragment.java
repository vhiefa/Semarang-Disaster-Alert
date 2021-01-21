package com.vhiefa.disasteralert;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.TextView;

import com.vhiefa.disasteralert.pref.IntroPreference;

/**
 * Created by Afifatul on 4/14/2017.
 */

public class ThirdIntroFragment  extends Fragment {

    IntroPreference introPreference;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.thirdintro_fragment, container, false);

        final TextView tv = (TextView) v.findViewById(R.id.tvFragThird);
        tv.setText("Lihat laporan bencana atau kedaruratan pengguna lain dalam tampilan Peta maupun tampilan Daftar");


    /*    final Animation in = new AlphaAnimation(0.0f, 1.0f);
        in.setDuration(3000);

        Animation out = new AlphaAnimation(1.0f, 0.0f);
        out.setDuration(3000);

        out.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                tv.setText("Lihat laporan bencana atau kedaruratan pengguna lain dalam tampilan Peta maupun tampilan Daftar");
                tv.startAnimation(in);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        tv.startAnimation(out);*/











        Button startBtn= (Button)v.findViewById(R.id.startBtn);
        TextView startTxtView = (TextView) v.findViewById(R.id.startTxtView);

        String styleText ="<u><font color='blue'>GET START</font></u>";
        startTxtView.setText(Html.fromHtml(styleText), TextView.BufferType.SPANNABLE);

        introPreference = new IntroPreference(getActivity());

        startTxtView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                introPreference.setIntroRead();
                Intent in = new Intent(getActivity(), LoginActivity.class);
                startActivity(in);
                getActivity().finish();
            }
        });

        return v;
    }

    public static ThirdIntroFragment newInstance(String text) {

        ThirdIntroFragment f = new ThirdIntroFragment();
        Bundle b = new Bundle();
        b.putString("msg", text);

        f.setArguments(b);

        return f;
    }
}