package com.example.hobbytracker.models;


public class AchievementsData {
    private final String title, bronzeDesc, silverDesc, goldDesc;
    int currProgress, bronzeThreshold, silverThreshold, goldThreshold, currLevel;
    private boolean hasGotBronze, hasGotSilver, hasGotGold;

    public AchievementsData(String title, String bronzeDesc, String silverDesc, String goldDesc, int currProgress, int bronzeThreshold, int silverThreshold, int goldThreshold, int currLevel) {
        this.title = title;
        this.bronzeDesc = bronzeDesc;
        this.silverDesc = silverDesc;
        this.goldDesc = goldDesc;
        this.currProgress = currProgress;
        this.bronzeThreshold = bronzeThreshold;
        this.silverThreshold = silverThreshold;
        this.goldThreshold = goldThreshold;
        this.currLevel = currLevel;
        hasGotBronze = false;
        hasGotSilver = false;
        hasGotGold = false;
    }

    public boolean isHasGotBronze() {
        return hasGotBronze;
    }

    public void setHasGotBronze(boolean hasGotBronze) {
        this.hasGotBronze = hasGotBronze;
    }

    public boolean isHasGotGold() {
        return hasGotGold;
    }

    public void setHasGotGold(boolean hasGotGold) {
        this.hasGotGold = hasGotGold;
    }

    public boolean isHasGotSilver() {
        return hasGotSilver;
    }

    public void setHasGotSilver(boolean hasGotSilver) {
        this.hasGotSilver = hasGotSilver;
    }

    public int getNextThreshold() {
        switch (currLevel) {
            case 0:
                return bronzeThreshold;
            case 1:
                return silverThreshold;
            case 2:
                return goldThreshold;
            default:
                return goldThreshold;
        }
    }

    public int getProgressPercentage() {
        int nextThreshold = getNextThreshold();
        if (currLevel >= 3) return 100;
        return Math.min(100, (currProgress * 100) / nextThreshold);
    }

    public String getBronzeDesc() {
        return bronzeDesc;
    }


    public String getTitle() {
        return title;
    }

    public int getSilverThreshold() {
        return silverThreshold;
    }


    public String getSilverDesc() {
        return silverDesc;
    }


    public int getGoldThreshold() {
        return goldThreshold;
    }


    public String getGoldDesc() {
        return goldDesc;
    }


    public int getBronzeThreshold() {
        return bronzeThreshold;
    }

    public int getCurrProgress() {
        return currProgress;
    }

    public void setCurrProgress(int currProgress) {
        this.currProgress = currProgress;
    }

    public int getCurrLevel() {
        return currLevel;
    }

    public void setCurrLevel(int currLevel) {
        this.currLevel = currLevel;
    }
}
