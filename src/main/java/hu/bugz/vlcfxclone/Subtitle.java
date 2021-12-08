package hu.bugz.vlcfxclone;

import java.util.Timer;

public class Subtitle {
    int id;
    int hour,minute, endHour, endMinute;
    double second, endSecond;
    String subtitle;

    Subtitle(int id ,int hour,int minute, double second, int endHour,int endMinute, double endSecond, String sub ){
        id = id;
        hour = hour;
        minute = minute;
        second = second;
        endHour = endHour;
        endMinute = endMinute;
        endSecond = endSecond;
        subtitle  = sub;
    }
}
