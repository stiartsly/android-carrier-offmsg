package org.elastos.carrier.chatdemo;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

class DateUtils {
    static String getTimestampString(Date var0) {
        String var1;
        long var2 = var0.getTime();
        if (isSameDay(var2)) {
            Calendar var4 = GregorianCalendar.getInstance();
            var4.setTime(var0);
            int var5 = var4.get(11);
            if (var5 > 17) {
                var1 = "晚上 hh:mm";
            } else if (var5 >= 0 && var5 <= 6) {
                var1 = "凌晨 hh:mm";
            } else if (var5 > 11 && var5 <= 17) {
                var1 = "下午 hh:mm";
            } else {
                var1 = "上午 hh:mm";
            }
        } else if (isYesterday(var2)) {
            var1 = "昨天 HH:mm";
        } else {
            var1 = "M月d日 HH:mm";
        }

        return (new SimpleDateFormat(var1, Locale.CHINA)).format(var0);
    }

    static boolean isCloseEnough(long var0, long var2) {
        long var4 = var0 - var2;
        if (var4 < 0L) {
            var4 = -var4;
        }

        return var4 < 30000L;
    }

    private static boolean isSameDay(long var0) {
        TimeInfo var2 = getTodayStartAndEndTime();
        return var0 > var2.getStartTime() && var0 < var2.getEndTime();
    }

    private static boolean isYesterday(long var0) {
        TimeInfo var2 = getYesterdayStartAndEndTime();
        return var0 > var2.getStartTime() && var0 < var2.getEndTime();
    }

    static TimeInfo getYesterdayStartAndEndTime() {
        Calendar var0 = Calendar.getInstance();
        var0.add(5, -1);
        var0.set(11, 0);
        var0.set(12, 0);
        var0.set(13, 0);
        var0.set(14, 0);
        Date var1 = var0.getTime();
        long var2 = var1.getTime();
        Calendar var4 = Calendar.getInstance();
        var4.add(5, -1);
        var4.set(11, 23);
        var4.set(12, 59);
        var4.set(13, 59);
        var4.set(14, 999);
        Date var5 = var4.getTime();
        long var6 = var5.getTime();
        TimeInfo var8 = new TimeInfo();
        var8.setStartTime(var2);
        var8.setEndTime(var6);
        return var8;
    }

    private static TimeInfo getTodayStartAndEndTime() {
        Calendar var0 = Calendar.getInstance();
        var0.set(11, 0);
        var0.set(12, 0);
        var0.set(13, 0);
        var0.set(14, 0);
        Date var1 = var0.getTime();
        long var2 = var1.getTime();
        new SimpleDateFormat("yyyy-MM-dd HH:mm:ss S");
        Calendar var5 = Calendar.getInstance();
        var5.set(11, 23);
        var5.set(12, 59);
        var5.set(13, 59);
        var5.set(14, 999);
        Date var6 = var5.getTime();
        long var7 = var6.getTime();
        TimeInfo var9 = new TimeInfo();
        var9.setStartTime(var2);
        var9.setEndTime(var7);
        return var9;
    }
}
