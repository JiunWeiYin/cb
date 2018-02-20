/**
 * (C) Copyright Chun-Pei Cheng All Rights Reserved
 * NOTICE:  All information contained herein is, and remains the
 * property of Chun-Pei Cheng. The intellectual and technical
 * concepts contained herein are proprietary to Chun-Pei Cheng
 * and are protected by trade secret, patent law or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from Chun-Pei Cheng.
 *
 * Author: Chun-Pei Cheng
 * Contact: ccp0625@gmail.com
 *
 **/


package org.misc.model;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.misc.util.Apps;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import static org.misc.constant.ConstVar.DAYS_YEAR;
import static org.misc.constant.ConstVar.MY_FORMATTER;

public class Bond {
    private static final Logger LOGGER = LogManager.getLogger(Bond.class);

    // daily info
    String bondId;
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
    Date presentDate;

    // published info
    Date dueDate;
    Date issuedDate;
    //    long amount;
//    long balance;
//    float couponRate;
    Date putRightDate;
    float putRightPrice;

    // calculated values
    float roi = Float.MIN_VALUE;
    float annualizedReturn = Float.MIN_VALUE;

    public String getBondId() {
        return bondId;
    }

    public void setBondId(String bondId) {
        this.bondId = bondId;
    }

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

    public Date getPresentDate() {
        return presentDate;
    }

    public void setPresentDate(Date presentDate) {
        this.presentDate = presentDate;
    }

    public Date getDueDate() {
        return dueDate;
    }

    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }

    public Date getIssuedDate() {
        return issuedDate;
    }

    public void setIssuedDate(Date issuedDate) {
        this.issuedDate = issuedDate;
    }

//    public long getAmount() {
//        return amount;
//    }

//    public void setAmount(long amount) {
//        this.amount = amount;
//    }

//    public long getBalance() {
//        return balance;
//    }

//    public void setBalance(long balance) {
//        this.balance = balance;
//    }

//    public float getCouponRate() {
//        return couponRate;
//    }

//    public void setCouponRate(float couponRate) {
//        this.couponRate = couponRate;
//    }

    public Date getPutRightDate() {
        return putRightDate;
    }

    public void setPutRightDate(Date putRightDate) {
        this.putRightDate = putRightDate;
    }

    public float getPutRightPrice() {
        return putRightPrice;
    }

    public void setPutRightPrice(float putRightPrice) {
        this.putRightPrice = putRightPrice;
    }

    /**
     * Get ROI (Return Of Investment).
     *
     * Example:  (100 + (101.0025 - 100) * 0.9) / (90 * 1.001425);
     */
    public void setRoi(float putRightPrice, float closingPrice) {
        roi = (100f + (putRightPrice - 100f) * 0.9f) / (closingPrice * 1.001425f);
    }

    public float getRoi() {
        return roi;
    }

    /**
     * Get annual return.
     * <p>
     * Example: ROI / (2017/03/14 - 2017/02/28) * 365;
     */
    public void setAnnualizedReturn(float roi, Date presentDate, Date putRightDate) {
        int days = Apps.getDays(presentDate, putRightDate);
        if (days > 0) {
            annualizedReturn = roi / (float) days * (float) DAYS_YEAR;
        } else {
            LOGGER.warn(String.format("# days '%s' between present date '%s' and due date '%s' is negative.", days, presentDate, putRightDate));
        }
    }

    public float getAnnualizedReturn() {
        return annualizedReturn;
    }

    /**
     * Output as a line.
     * <p>
     * Example: 36626   78.25   2017/02/28  2019/03/02  0.27795526  0.13878752
     */
    public String toLine() {
        DateFormat dateFormat = new SimpleDateFormat(MY_FORMATTER);
        return String.format("%s\t%s\t%s\t%s",
                bondName,
                closingPrice,
                dateFormat.format(presentDate),
                dateFormat.format(dueDate));
    }

//    public String toString() {
//        return "bondName: " + bondName +
//                "; time: " + time +
//                "; closingPrice: " + closingPrice +
//                "; bidPrice: " + bidPrice +
//                "; offerPrice: " + offerPrice +
//                "; dailyPricing: " + dailyPricing +
//                "; boardLot: " + boardLot +
//                "; ydayClosingPrice: " + ydayClosingPrice +
//                "; openingPrice: " + openingPrice +
//                "; dayHigh: " + dayHigh +
//                "; dayLow: " + dayLow +
//                "; presentDate: " + presentDate +
//                "; putRightPrice: " + putRightPrice
//                ;
//    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SIMPLE_STYLE);
    }

    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }
}
