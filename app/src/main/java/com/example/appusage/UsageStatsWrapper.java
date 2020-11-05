package com.example.appusage;

import android.app.usage.UsageStats;

import androidx.annotation.NonNull;

public class UsageStatsWrapper implements Comparable<UsageStatsWrapper>{


    private final UsageStats usageStats;
    private final String appName;

    public UsageStatsWrapper(UsageStats usageStats, String appName) {
        this.usageStats = usageStats;
        this.appName = appName;
    }

    public UsageStats getUsageStats() {
        return usageStats;
    }

    public String getAppName() {
        return appName;
    }

    @Override
    public int compareTo(@NonNull UsageStatsWrapper usageStatsWrapper) {
        if (usageStats == null && usageStatsWrapper.getUsageStats() != null) {
            return 1;
        } else if (usageStatsWrapper.getUsageStats() == null && usageStats != null) {
            return -1;
        } else if (usageStatsWrapper.getUsageStats() == null && usageStats == null) {
            return 0;
        } else {
            assert usageStats != null;
            return Long.compare(usageStatsWrapper.getUsageStats().getLastTimeUsed(),
                    usageStats.getLastTimeUsed());
        }
    }
}

