package hu.bugz.vlcfxclone;

public class Subtitle {
    private final int id;
    private final int startHour, startMinute, endHour, endMinute;
    private final double startSecond, endSecond;

    public int getId() {
        return id;
    }

    public int getStartHour() {
        return startHour;
    }

    public int getStartMinute() {
        return startMinute;
    }

    public int getEndHour() {
        return endHour;
    }

    public int getEndMinute() {
        return endMinute;
    }

    public double getStartSecond() {
        return startSecond;
    }

    public double getEndSecond() {
        return endSecond;
    }

    public String getSubtitle() {
        return id + ",," + subtitle;
    }

    public int getStartMillis() {
        return (int)(startHour * 3600000 + startMinute * 60000 + startSecond * 1000);
    }

    private final String subtitle;

    public Subtitle(int id, int startHour, int startMinute, double startSecond, int endHour, int endMinute, double endSecond, String sub) {
        this.id = id;
        this.startHour = startHour;
        this.startMinute = startMinute;
        this.startSecond = startSecond;
        this.endHour = endHour;
        this.endMinute = endMinute;
        this.endSecond = endSecond;
        this.subtitle = sub;
    }
}
