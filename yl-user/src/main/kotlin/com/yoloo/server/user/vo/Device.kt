package com.yoloo.server.user.vo

import com.yoloo.server.common.util.NoArg

@NoArg
data class Device(
    var brand: String,
    var model: String,
    var screen: Screen,
    var os: Os,
    var localIp: IP
)