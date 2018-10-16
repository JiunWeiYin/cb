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

package org.cb.model;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cb.util.Apps;

import java.util.Date;

import static org.cb.constant.ConstVar.DAYS_YEAR;

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
    float earlyOutPrice;

    // profile
    long cash;

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

    public long getCash() {
        return cash;
    }

    public void setCash(long cash) {
        this.cash = cash;
    }

    /**
     * Get ROI (Return Of Investment).
     *
     * Example: in:90,out:101.0025,fee:0.001425,ROI:(((100 + (101.0025 - 100) * (1 - 10%)) / (90 * 1.001425)) - 1) * 100
     */
    public void setRoi(float putRightPrice, float closingPrice, float fee) {
        this.roi = ((100.0f + (putRightPrice - 100.0f) * 0.9f) / (closingPrice * (1.0f + fee)) - 1.0f) * 100;
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
            this.annualizedReturn = computeAnnualizedReturn(roi, minDays);
        } else if (maxDays > 0) {
            this.annualizedReturn = computeAnnualizedReturn(roi, maxDays);
        } else {
            LOGGER.warn(String.format(" None of days (present '%s' to put right '%s') and (present '%s' to due '%s') are positive.",
                    presentDate, putRightDate, presentDate, dueDate));
        }
    }

    public float getEarlyOutPrice() {
        return earlyOutPrice;
    }

    /**
     * Compute price for early out. Only supports Example 1.
     * <p>
     * Example 1 (# bonds  < 20,000): tax:10%,   EOP:((101 - 100) * (1 -    10%) + 100) * 1.001425
     * Example 2 (# bonds >= 20,000): tax:11.91%,EOP:((101 - 100) * (1 - 11.91%) + 100) * 1.001425
     */
    public void setEarlyOutPrice(float putRightPrice, float tax) {
        this.earlyOutPrice = ((putRightPrice - 100f) * (1f - tax) + 100f) * 1.001425f;
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

    public String printHeader() {
        return  "代碼\t" +
                "名稱\t" +
                "收盤價\t" +
                "今日日期\t" +
                "賣回權日期\t" +
                "到期日期\t" +
                "發行總額\t" +
                "目前餘額\t" +
                "賣回權價格\t" +
                "報酬率\t" +
                "年化報酬率\t" +
                "提早獲利價格\t" +
                "公司現金\n";
    }

    @Override
    public String toString() {
        return String.format("%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s\n",
                bondId,
                bondName,
                closingPrice,
                Apps.printDate(presentDate),
                Apps.printDate(putRightDate),
                Apps.printDate(dueDate),
                amount,
                balance,
                putRightPrice,
                roi,
                annualizedReturn,
                earlyOutPrice,
                cash
        );
    }

    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }
}
