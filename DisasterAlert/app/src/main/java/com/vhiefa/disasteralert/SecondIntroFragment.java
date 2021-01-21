package com.vhiefa.disasteralert;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.TextView;

/**
 * Created by Afifatul on 4/14/2017.
 */

public class SecondIntroFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.secondintro_fragment, container, false);

        final TextView tv = (TextView) v.findViewById(R.id.tvFragSecond);
        tv.setText("Aktifkan GPS kamu! Atur lokasi rumahmu! Dan dapatkan notifikasi alert setiap bencana/ kedaruratan terjadi di sekitarmu.");

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
                tv.setText("Aktifkan GPS kamu! Atur lokasi rumahmu! Dan dapatkan notifikasi alert setiap bencana/ kedaruratan terjadi di sekitarmu.");
                tv.startAnimation(in);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        tv.startAnimation(out);*/

        return v;
    }

    public static SecondIntroFragment newInstance(String text) {

        SecondIntroFragment f = new SecondIntroFragment();
        Bundle b = new Bundle();
        b.putString("msg", text);

        f.setArguments(b);

        return f;
    }
}
