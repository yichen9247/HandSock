package com.server.handsock.configs

import com.server.handsock.props.HandProp
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.UrlBasedCorsConfigurationSource
import org.springframework.web.filter.CorsFilter
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
open class WebCrossConfig @Autowired constructor(private val handProp: HandProp) : WebMvcConfigurer {
    private val corsConfiguration = CorsConfiguration()
    @Bean
    open fun corsFilter(): CorsFilter {
        corsConfiguration.allowCredentials = true
        corsConfiguration.allowedHeaders = listOf("*")
        corsConfiguration.allowedOrigins = listOf(handProp.origin)
        corsConfiguration.allowedMethods = mutableListOf("GET", "POST", "PUT", "DELETE", "OPTIONS")

        val source = UrlBasedCorsConfigurationSource()
        source.registerCorsConfiguration("/**", corsConfiguration)
        return CorsFilter(source)
    }
}