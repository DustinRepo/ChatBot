package me.dustin.chatbot.helper;

import java.util.ArrayList;
import java.util.List;

public class TPSHelper {

    private final List<Long> reports = new ArrayList<>();

    public double getTPS(int averageOfSeconds) {
        if (reports.size() < 2) {
            return 20.0; // we can't compare yet
        }

        long currentTimeMS = reports.get(reports.size() - 1);
        long previousTimeMS = reports.get(reports.size() - averageOfSeconds);

        // on average, how long did it take for 20 ticks to execute? (ideal value: 1 second)
        double longTickTime = Math.max((currentTimeMS - previousTimeMS) / (1000.0 * (averageOfSeconds - 1)), 1.0);
        return 20 / longTickTime;
    }

    public double getAverageTPS() {
       return getTPS(reports.size());
    }

    public void clear() {
        reports.clear();
    }

    public void worldTime() {
        reports.add(System.currentTimeMillis());
        while (reports.size() > 15) {
            reports.remove(0);
        }
    }
}
