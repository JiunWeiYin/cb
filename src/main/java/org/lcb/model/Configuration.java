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

package org.lcb.model;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class Configuration {


    private String urlBondDaily;
    private String urlBondPublished;
    private String outputFilePath;
    private float fee;
    private float tax;
    private float thresholdClosingPrice;

    // profile
    private String urlCash;

    public String getUrlBondDaily() {
        return urlBondDaily;
    }

    public String getUrlBondPublished() {
        return urlBondPublished;
    }

    public String getOutputFilePath() {
        return outputFilePath;
    }

    public float getFee() {
        return fee;
    }

    public float getTax() {
        return tax;
    }

    public float getThresholdClosingPrice() {
        return thresholdClosingPrice;
    }

    public String getUrlCash() {
        return urlCash;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }

    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }
}
