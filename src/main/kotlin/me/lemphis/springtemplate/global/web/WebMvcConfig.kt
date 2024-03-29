package me.lemphis.springtemplate.global.web

import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer


@Configuration
class WebMvcConfig : WebMvcConfigurer {

	override fun addResourceHandlers(registry: ResourceHandlerRegistry) {
		registry
			.addResourceHandler("/docs/**")
			.addResourceLocations("classpath:/static/docs/")
	}

}
