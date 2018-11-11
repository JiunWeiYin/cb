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

import java.text.DecimalFormat;
import java.util.Date;

import static org.cb.constant.ConstVar.DAYS_YEAR;

public class Bond {
    private static final Logger LOGGER = LogManager.getLogger(Bond.class);
    private static final DecimalFormat DF2 = new DecimalFormat(".##");

    // daily info
    String id;
    String name;
    float closingPrice;
    float ydayClosingPrice;
    Date presentDate;

    // published info
    Date putRightDate;
    Date issuedDate;
    Date dueDate;
    int amount; // in 10^6
    int balance; // in 10^6
    float balanceRatio;
    float putRightPrice;
    int daysToPutRightDate;
    int daysToDueDate;

    // calculated values
    float roi;
    float annualizedReturn;
    float earlyOutPrice;

    // profile
    int accountsReceivable; // in 10^6
    int cash; // in 10^6

    // stock
    float priceIssuedDate;
    float priceYesterday;
    float netAssetValue;

    public String getBondId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBondName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public Date getIssuedDate() {
        return issuedDate;
    }

    public void setIssuedDate(Date issuedDate) {
        this.issuedDate = issuedDate;
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

    public int getDaysToPutRightDate() {
        return daysToPutRightDate;
    }

    public void setDaysToPutRightDate(int daysToPutRightDate) {
        this.daysToPutRightDate = daysToPutRightDate;
    }

    public int getDaysToDueDate() {
        return daysToDueDate;
    }

    public void setDaysToDueDate(int daysToDueDate) {
        this.daysToDueDate = daysToDueDate;
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

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public long getBalance() {
        return balance;
    }

    public void setBalance(int balance) {
        this.balance = balance;
    }

    public int getAccountsReceivable() {
        return accountsReceivable;
    }

    public void setAccountsReceivable(int accountsReceivable) {
        this.accountsReceivable = accountsReceivable;
    }

    public long getCash() {
        return cash;
    }

    public void setCash(int cash) {
        this.cash = cash;
    }

    public float getBalanceRatio() {
        return balanceRatio;
    }

    public void setBalanceRatio(float balanceRatio) {
        this.balanceRatio = balanceRatio;
    }

    public float getPriceIssuedDate() {
        return priceIssuedDate;
    }

    public void setPriceIssuedDate(float priceIssuedDate) {
        this.priceIssuedDate = priceIssuedDate;
    }

    public float getPriceYesterday() {
        return priceYesterday;
    }

    public void setPriceYesterday(float priceYesterday) {
        this.priceYesterday = priceYesterday;
    }

    public float getNetAssetValue() {
        return netAssetValue;
    }

    public void setNetAssetValue(float netAssetValue) {
        this.netAssetValue = netAssetValue;
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
                "成交價\t" +
                "昨日股票收盤價\t" +
                "每股淨值(元)\t" +
                "今天日期\t" +
                "賣回權日期\t" +
                "執行賣回權剩餘天數\t" +
                "發行日期\t" +
                "到期日期\t" +
                "到期日剩餘天數\t" +
                "發行總額 (M)\t" +
                "目前餘額 (M)\t" +
                "在外流通餘額比例 (%)\t" +
                "賣回權價格\t" +
                "報酬率 (%)\t" +
                "年化報酬率 (%)\t" +
                "提早獲利價格\t" +
                "應收帳款 (M)\t" +
                "公司現金 (M)\n";
    }

    @Override
    public String toString() {
        return String.format("%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s\n",
                id,
                name,
                closingPrice,
                priceYesterday,
                netAssetValue,
                Apps.printDate(presentDate),
                Apps.printDate(putRightDate),
                daysToPutRightDate,
                Apps.printDate(issuedDate),
                Apps.printDate(dueDate),
                daysToDueDate,
                amount,
                balance,
                DF2.format(balanceRatio),
                putRightPrice,
                DF2.format(roi),
                DF2.format(annualizedReturn),
                earlyOutPrice,
                accountsReceivable,
                cash
        );
    }

    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }
}
