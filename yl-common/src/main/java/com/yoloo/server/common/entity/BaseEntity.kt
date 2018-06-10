package com.yoloo.server.common.entity

import com.googlecode.objectify.annotation.OnLoad
import com.googlecode.objectify.annotation.OnSave
import com.yoloo.server.common.vo.AuditData
import com.yoloo.server.common.vo.Keyable

abstract class BaseEntity<E>(var auditData: AuditData = AuditData()) : Keyable<E> {

    @OnSave
    protected open fun onSave() {
    }

    @OnLoad
    protected open fun onLoad() {
    }
}
