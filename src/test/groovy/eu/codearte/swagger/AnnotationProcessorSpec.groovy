package eu.codearte.swagger

import spock.lang.Specification

import javax.tools.ToolProvider

/**
 * Created by Jakub Kubrynski / 2014-01-25
 */
class AnnotationProcessorSpec extends Specification {
	def "should run annotation processor"() {
		given:
			def javaCompiler = ToolProvider.getSystemJavaCompiler()
			def standardFileManager = javaCompiler.getStandardFileManager(null, null, null)

			def objects = standardFileManager.getJavaFileObjects(
					"src/test/groovy/eu/codearte/swagger/sample/FirstController.java",
					"src/test/groovy/eu/codearte/swagger/sample/SecondController.java")
			def task = javaCompiler.getTask(null, standardFileManager, null, ['-AbasePath=/front'], null,
					objects)
		when:
			task.setProcessors([new SpringMvcAnnotationProcessor()])
			def call = task.call()
		then:
			call
	}
}
