package com.udacity.maluleque.meutako;

import org.junit.Assert;
import org.junit.Test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class DatesUnitTest {
    @Test
    public void addition_isCorrect() {
        String fromdate = "Jun 2014";
        SimpleDateFormat format = new SimpleDateFormat("MMM yyyy");
        Date date = null;
        try {
            date = format.parse(fromdate);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        Assert.assertEquals(fromdate, format.format(date));
    }
}