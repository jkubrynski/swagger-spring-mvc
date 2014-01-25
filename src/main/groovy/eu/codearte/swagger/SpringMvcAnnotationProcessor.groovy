package eu.codearte.swagger

import com.wordnik.swagger.annotations.Api
import eu.codearte.swagger.model.ApiDocument
import groovy.json.JsonOutput
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping

import javax.annotation.processing.AbstractProcessor
import javax.annotation.processing.Messager
import javax.annotation.processing.ProcessingEnvironment
import javax.annotation.processing.RoundEnvironment
import javax.annotation.processing.SupportedAnnotationTypes
import javax.annotation.processing.SupportedOptions
import javax.annotation.processing.SupportedSourceVersion
import javax.lang.model.SourceVersion
import javax.lang.model.element.TypeElement
import javax.tools.Diagnostic

/**
 * @author jakub.kubrynski
 */
@SupportedAnnotationTypes('org.springframework.stereotype.Controller')
@SupportedSourceVersion(SourceVersion.RELEASE_7)
@SupportedOptions([Consts.BASE_PATH_PARAMETER, Consts.DESTINATION_PARAMETER, Consts.API_VERSION_PARAMETER, Consts.SWAGGER_VERSION_PARAMETER])
class SpringMvcAnnotationProcessor extends AbstractProcessor {

	private Messager messager

	private ApiDocument indexDocument

	Map<String, String> processorParams = [:]

	Map<String, eu.codearte.swagger.model.Controller> controllerMap = [:]

	@Override
	synchronized void init(ProcessingEnvironment processingEnv) {
		super.init(processingEnv)
		messager = processingEnv.getMessager()

		processParameter(processingEnv, Consts.BASE_PATH_PARAMETER, "/")
		processParameter(processingEnv, Consts.API_VERSION_PARAMETER, "1.0")
		processParameter(processingEnv, Consts.SWAGGER_VERSION_PARAMETER, "1.2")
		processParameter(processingEnv, Consts.DESTINATION_PARAMETER, "")
	}

	private void processParameter(ProcessingEnvironment processingEnv, String parameterName, String defaultValue) {
		def paramValue = processingEnv.getOptions().get(parameterName)
		if (!paramValue) {
			paramValue = defaultValue
		}
		processorParams.put(parameterName, paramValue)
	}

	@Override
	boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
		if (annotations.isEmpty()) return false

		messager.printMessage(Diagnostic.Kind.NOTE, "Running Swagger JSON generation...")

		indexDocument = buildApiDocument()

		roundEnv.getElementsAnnotatedWith(Controller.class).each {
			def requestMapping = it.getAnnotation(RequestMapping)
			def api = it.getAnnotation(Api)
			def map = [:]
			def controllerUrl
			if (requestMapping && requestMapping.value()?.length == 1) {
				controllerUrl = requestMapping.value()[0]
				map << [path: controllerUrl]
			} else {
				messager.printMessage(Diagnostic.Kind.WARNING, "No @RequestMapping found on " + it.simpleName)
				return
			}
			if (api && api.value()) {
				map << [description: api.value()]
			} else {
				messager.printMessage(Diagnostic.Kind.WARNING, "No @Api found on " + it.simpleName)
			}
			indexDocument.addApi(map)
			def controller = new eu.codearte.swagger.model.Controller(controllerUrl)
			controller.processMethods(it.getEnclosedElements())
			controllerMap.put(controllerUrl, controller)
		}


		def jsonOutput = new JsonOutput()
		def jsonPayload = jsonOutput.prettyPrint(jsonOutput.toJson(indexDocument))
		println jsonPayload
		new File(ensureSlashTrailing(processorParams.get(Consts.DESTINATION_PARAMETER)) + "api-docs.json").write(jsonPayload)

		controllerMap.each {
			def controllerJsonOutput = new JsonOutput()
			def controllerDocument = buildApiDocument()
			controllerDocument.basePath = it.value.path
			controllerDocument.apis = it.value.apis
			jsonPayload = controllerJsonOutput.prettyPrint(controllerJsonOutput.toJson(controllerDocument))
			println jsonPayload
			new File(ensureSlashTrailing(processorParams.get(Consts.DESTINATION_PARAMETER)) + stripSlashes(it.key) + ".json").write(jsonPayload)
		}
		true
	}

	private ApiDocument buildApiDocument() {
		indexDocument = new ApiDocument()
		indexDocument.basePath = processorParams.get(Consts.BASE_PATH_PARAMETER)
		indexDocument.apiVersion = processorParams.get(Consts.API_VERSION_PARAMETER)
		indexDocument.swaggerVersion = processorParams.get(Consts.SWAGGER_VERSION_PARAMETER)
		indexDocument.resourcePath = "/apis"
		indexDocument
	}

	private String ensureSlashTrailing(String directory) {
		if (directory.length() > 1 && directory.charAt(directory.length() - 1) != File.separatorChar) {
			return directory + File.separator;
		}
		directory
	}

	private String stripSlashes(String directory) {
		directory.replaceAll("/", "")
	}

}