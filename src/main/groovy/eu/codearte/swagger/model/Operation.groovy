package eu.codearte.swagger.model

import com.google.common.collect.Lists

/**
 * @author jakub.kubrynski
 */
class Operation {
	String path
	String description = ''
	List<OperationDesc> operations = Lists.newArrayList()

	Operation(String path) {
		this.path = path
	}

	OperationDesc addOperation() {
		def operationDesc = new OperationDesc()
		operations.add(operationDesc)
		operationDesc
	}
}
