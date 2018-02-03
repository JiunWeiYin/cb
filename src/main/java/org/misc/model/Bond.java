/**
 * Author: Chun-Pei Cheng
 * Contact: ccp0625@gmail.com
 */

package org.misc.model;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.misc.util.Apps;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import static org.misc.constant.ConstVar.*;

public class Bond {
    private static final Logger LOGGER = LogManager.getLogger(Bond.class);

    // daily info
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
    Date present;

    // publish info
    float refund = REFUND;

    Date due;
    Date issued;





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

    public Date getPresent() {
        return present;
    }

    public void setPresent(Date present) {
        this.present = present;
    }

    public float getRefund() {
        return refund;
    }

    public void setRefund(float refund) {
        this.refund = refund;
    }

    public Date getDue() {
        return due;
    }

    public void setDue(Date due) {
        this.due = due;
    }

    public Date getIssued() {
        return issued;
    }

    public void setIssued(Date issued) {
        this.issued = issued;
    }





    /**
     * Get ROI (Return Of Investment).
     *
     * Example:  (100.0 - 90.0) / 90.0;
     */
    public float getRoi() {
        return (refund - closingPrice) / closingPrice;
    }

    /**
     * Get ROI (Return Of Investment) over one year.
     * <p>
     * Example: (100.0 - 90.0) / 90.0 / (2017/03/14 - 2017/02/28) * 365;
     */
    public float getRoiOverYear() {
        int days = Apps.getDays(present, due);
        LOGGER.debug(String.format("%s days in between.", days));
        return (refund - closingPrice) / closingPrice / (float) days * (float) DAYS_YEAR;
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
                "; present: " + present +
                "; refund (%): " + refund / 100
                ;
    }

    /**
     * Output as a line.
     * <p>
     * Example: 36626   78.25   2017/02/28  2019/03/02  0.27795526  0.13878752
     */
    public String toLine() {
        DateFormat dateFormat = new SimpleDateFormat(MY_FORMATTER);
        return String.format("%s\t%s\t%s\t%s",
                bondName, closingPrice, dateFormat.format(present), dateFormat.format(due));
    }

}
