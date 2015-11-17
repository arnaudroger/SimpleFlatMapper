/*******************************************************************************
 * COPYRIGHT (C) 2015, Rapid7 LLC, Boston, MA, USA.
 * All rights reserved. This material contains unpublished, copyrighted
 * work including confidential and proprietary information of Rapid7.
 ******************************************************************************/

package org.sfm.jdbc.spring;

import java.time.LocalDateTime;

public class Asset {

    private Number id;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private boolean obsolete;

    public Asset(Number id, LocalDateTime createTime, LocalDateTime updateTime, boolean obsolete) {
        this.id = id;
        this.createTime = createTime;
        this.updateTime = updateTime;
        this.obsolete = obsolete;
    }

    public Asset() {}

    public Number getId() {
        return id;
    }

    public void setId(Number id) {
        this.id = id;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public LocalDateTime getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
    }

    public boolean isObsolete() {
        return obsolete;
    }

    public void setObsolete(boolean obsolete) {
        this.obsolete = obsolete;
    }
}
