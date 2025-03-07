package com.server.handsock.properties

import lombok.Getter
import lombok.Setter
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

@Setter @Getter @Component
@ConfigurationProperties(prefix = "ai")
open class AiProp {
    var url: String? = null
    var path: String? = null
    var model: String? = null
    var token: String? = null
}
