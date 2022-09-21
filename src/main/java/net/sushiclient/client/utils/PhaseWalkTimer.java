package net.sushiclient.client.utils;

public class PhaseWalkTimer {
    private long startTime;
    private long endTime;
    public void start() {
        startTime = System.currentTimeMillis();
        this.endTime = 4100L;
    }

    @Override
    public String toString() {
        long nowTime = System.currentTimeMillis() - startTime;
        long rTime = endTime - nowTime;
        if (Math.abs(rTime) != rTime)
            return "0.0";
        else {
            String[] str = String.valueOf(rTime).split("");
            if (str.length == 4)
                return str[0] + "." + str[1];
            else if (str.length == 3)
                return "0." + str[0];
            else
                return "0.0";
        }
    }
}
