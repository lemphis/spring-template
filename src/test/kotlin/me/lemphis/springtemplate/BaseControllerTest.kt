package me.lemphis.springtemplate

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs
import org.springframework.restdocs.RestDocumentationContextProvider
import org.springframework.restdocs.RestDocumentationExtension
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration
import org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.setup.DefaultMockMvcBuilder
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.context.WebApplicationContext

@AutoConfigureRestDocs
@ExtendWith(RestDocumentationExtension::class)
@Disabled
abstract class BaseControllerTest {

	protected lateinit var mockMvc: MockMvc

	@BeforeEach
	fun setUp(webApplicationContext: WebApplicationContext, restDocumentation: RestDocumentationContextProvider) {
		mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
			.apply<DefaultMockMvcBuilder>(
				documentationConfiguration(restDocumentation).operationPreprocessors()
					.withRequestDefaults(prettyPrint())
					.withResponseDefaults(prettyPrint()),
			)
			.build()
	}

}
