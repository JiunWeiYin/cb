package org.misc.util;

public class Bond {
    String bondName;
    String time;
    float closingPrice;
    float bidPrice;
    float offerPrice;
    String dailyPricing;
    int boardLot;
    float ydayClosingPrice;
    float openingPrice;
    float dayHigh;
    float dayLow;
    float refund;



    public String getBondName() {
        return bondName;
    }

    public void setBondName(String bondName) {
        this.bondName = bondName;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public float getClosingPrice() {
        return closingPrice;
    }

    public void setClosingPrice(float closingPrice) {
        this.closingPrice = closingPrice;
    }

    public float getBidPrice() {
        return bidPrice;
    }

    public void setBidPrice(float bidPrice) {
        this.bidPrice = bidPrice;
    }

    public float getOfferPrice() {
        return offerPrice;
    }

    public void setOfferPrice(float offerPrice) {
        this.offerPrice = offerPrice;
    }

    public String getDailyPricing() {
        return dailyPricing;
    }

    public void setDailyPricing(String dailyPricing) {
        this.dailyPricing = dailyPricing;
    }

    public int getBoardLot() {
        return boardLot;
    }

    public void setBoardLot(int boardLot) {
        this.boardLot = boardLot;
    }

    public float getYdayClosingPrice() {
        return ydayClosingPrice;
    }

    public void setYdayClosingPrice(float ydayClosingPrice) {
        this.ydayClosingPrice = ydayClosingPrice;
    }

    public float getOpeningPrice() {
        return openingPrice;
    }

    public void setOpeningPrice(float openingPrice) {
        this.openingPrice = openingPrice;
    }

    public float getDayHigh() {
        return dayHigh;
    }

    public void setDayHigh(float dayHigh) {
        this.dayHigh = dayHigh;
    }

    public float getDayLow() {
        return dayLow;
    }

    public void setDayLow(float dayLow) {
        this.dayLow = dayLow;
    }

    public float getRefund() {
        return refund;
    }

    public void setRefund(float refund) {
        this.refund = refund;
    }

    /**
     * get ROI (Return Of Investment)
     */
    public float getROI() {
        return (refund - closingPrice) / closingPrice;
    }

    public String toString() {
        return "bondName: " + bondName +
                "; time: " + time +
                "; closingPrice: " + closingPrice +
                "; bidPrice: " + bidPrice +
                "; offerPrice: " + offerPrice +
                "; dailyPricing: " + dailyPricing +
                "; boardLot: " + boardLot +
                "; ydayClosingPrice: " + ydayClosingPrice +
                "; openingPrice: " + openingPrice +
                "; dayHigh: " + dayHigh +
                "; dayLow: " + dayLow +
                "; refund: " + refund
                ;
    }
}
