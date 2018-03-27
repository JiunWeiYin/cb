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
    //    String time;
    float closingPrice;
    //    float bidPrice;
//    float offerPrice;
//    String dailyPricing;
//    int boardLot;
    float ydayClosingPrice;
//    float openingPrice;
//    float dayHigh;
//    float dayLow;
    Date presentDate;

    // published info
    Date putRightDate;
    Date dueDate;
    //    Date issuedDate;
    long amount;
    long balance;
//    float couponRate;
    float putRightPrice;

    // calculated values
    float roi;
    float annualizedReturn;

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

    public float getClosingPrice() {
        return closingPrice;
    }

    public void setClosingPrice(float closingPrice) {
        this.closingPrice = closingPrice;
    }

    public float getYdayClosingPrice() {
        return ydayClosingPrice;
    }

    public void setYdayClosingPrice(float ydayClosingPrice) {
        this.ydayClosingPrice = ydayClosingPrice;
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

    public long getAmount() {
        return amount;
    }

    public void setAmount(long amount) {
        this.amount = amount;
    }

    public long getBalance() {
        return balance;
    }

    public void setBalance(long balance) {
        this.balance = balance;
    }

    /**
     * Get ROI (Return Of Investment).
     *
     * Example:  (100 + (101.0025 - 100) * 0.9) / (90 * 1.001425);
     */
    public void setRoi(float putRightPrice, float closingPrice, float fee) {
        roi = ((100.0f + (putRightPrice - 100.0f) * 0.9f) / (closingPrice * (1.0f + fee)) - 1.0f) * 100;
    }

    public float getRoi() {
        return roi;
    }

    public void setAnnualizedReturn(float roi, Date presentDate, Date putRightDate, Date dueDate) {
        int p2pr = Apps.getDays(presentDate, putRightDate);
        int p2du = Apps.getDays(presentDate, dueDate);
        int minDays = Math.min(p2pr, p2du);
        int maxDays = Math.max(p2pr, p2du);

        if (minDays > 0) {
            annualizedReturn = computeAnnualizedReturn(roi, minDays);
        } else if (maxDays > 0) {
            annualizedReturn = computeAnnualizedReturn(roi, maxDays);
        } else {
            LOGGER.warn(String.format(" None of days (present '%s' to put right '%s') and (present '%s' to due '%s') are positive.",
                    presentDate, putRightDate, presentDate, dueDate));
        }
    }

    /**
     * Compute annualized return.
     *
     * @return float annualized return
     */
    private float computeAnnualizedReturn(double roi, double duration) {
        return (float) ((Math.pow(1.0 + roi / 100.0, 1.0 / (duration / (double) DAYS_YEAR)) - 1.0) * 100.0);
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
