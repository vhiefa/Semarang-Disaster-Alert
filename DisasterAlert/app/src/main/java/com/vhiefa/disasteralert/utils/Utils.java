package com.vhiefa.disasteralert.utils;

import android.net.Uri;

import java.io.InputStream;
import java.io.OutputStream;

public class Utils {

    Uri imageUri;

    public static int angkabuatnotifikasi =0;


    public static String getShorterText(String text) {
        String[] titleChar = text.split("");
        int maxTitleChar = 150;
        if (titleChar.length > maxTitleChar) {
            int i;
            text = titleChar[1];

            for (i=2;i<=maxTitleChar-3;i++){
                text = text+titleChar[i];
            }

            text = text + "...";
        }
        return text;
    }

    public static void CopyStream(InputStream is, OutputStream os)
    {
        final int buffer_size=1024;
        try
        {
            byte[] bytes=new byte[buffer_size];
            for(;;)
            {
              int count=is.read(bytes, 0, buffer_size);
              if(count==-1)
                  break;
              os.write(bytes, 0, count);
            }
        }
        catch(Exception ex){}
    }


}
