package eu.codearte.swagger.model

/**
 * @author jakub.kubrynski
 */
class ApiDocument {

	def basePath
	def apiVersion
	def swaggerVersion
	def resourcePath
	def apis = []
	def model = []

	def addApi(def api) {
		apis << api
	}
}
