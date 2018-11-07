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
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.Date;
import java.util.List;

public class Firm {
    private String stat;
    private Date date;
    private String title;
    private List<String> fields;
    private List<List<String>> data;
    private List<String> notes;

    public String getStat() {
        return stat;
    }

    public Date getDate() {
        return date;
    }

    public String getTitle() {
        return title;
    }

    public List<String> getFields() {
        return fields;
    }

    public List<List<String>> getData() {
        return data;
    }

    public List<String> getNotes() {
        return notes;
    }

    @Override
    public String toString () {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }

    @Override
    public boolean equals (Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }
}
