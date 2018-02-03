/**
 * Author: Chun-Pei Cheng
 * Contact: ccp0625@gmail.com
 */

package org.misc.model;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class Configuration {
    private String urlBondDaily;
    private String urlBondPublish;

    public String getUrlBondDaily() {
        return urlBondDaily;
    }

    public String geturlBondPublish() {
        return urlBondPublish;
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
