package eu.codearte.swagger.model

import com.wordnik.swagger.annotations.ApiOperation
import org.springframework.web.bind.annotation.RequestMapping

import javax.lang.model.element.Element
import javax.lang.model.element.ExecutableElement
import javax.lang.model.type.DeclaredType
import javax.lang.model.type.NoType

/**
 * Created by Jakub Kubrynski / 2014-01-25
 */
class Controller {
	def path
	def apis = []

	Controller(path) {
		this.path = path
	}

	def processMethods(Collection<? extends Element> methods) {
		methods.each { ExecutableElement method ->
			RequestMapping requestMapping = method.getAnnotation(RequestMapping)
			if (!requestMapping) return

			def operation = addOperation(requestMapping.value()[0])
			requestMapping.method()?.each {
				def operationDesc = operation.addOperation()
				operationDesc.method = it
				operationDesc.nickname = method.getSimpleName()
				def type = method.getReturnType()
				if (!(type instanceof NoType)) {
					operationDesc.type = ((DeclaredType) type).asElement().getSimpleName()
				}

				operationDesc.summary = method.getAnnotation(ApiOperation)?.value()

			}

		}
	}

	Operation addOperation(String mappings) {
		def operation = new Operation(mappings)
		apis.add(operation)
		return operation
	}
}
