package com.server.handsock.properties

import lombok.Getter
import lombok.Setter
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

@Setter @Getter @Component
@ConfigurationProperties(prefix = "handsock")
open class HandProp {
    var port: Int = 5120
    var host: String? = null
    var origin: String? = null
    var pingTimeout: Int = 3000
    var pingInterval: Int = 5000
    var upgradeTimeout: Int = 10000
    var appVersion: String = "2.2.0-B2503107"
}
