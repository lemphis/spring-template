package me.lemphis.springtemplate.global

import jakarta.validation.ConstraintViolationException
import me.lemphis.springtemplate.global.dto.ErrorResponse
import org.springframework.http.HttpStatus
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException

@RestControllerAdvice
class GlobalExceptionHandler {

	// path parameter, query parameter type mismatch
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ExceptionHandler(MethodArgumentTypeMismatchException::class)
	fun handle400(e: MethodArgumentTypeMismatchException): ErrorResponse {
		val errorField = e.name
		val receivedValue = e.value
		val requiredType = e.requiredType
		val errorMessage = "type mismatch. required type: $requiredType"
		return ErrorResponse(
			errorField,
			receivedValue,
			errorMessage,
		)
	}

	// query parameter(dto) type mismatch, validation error
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ExceptionHandler(MethodArgumentNotValidException::class)
	fun handle400(e: MethodArgumentNotValidException): ErrorResponse {
		val firstFieldError = e.bindingResult.fieldErrors.first()
		val errorField = firstFieldError.field
		val receivedValue = firstFieldError.rejectedValue
		val errorMessage = firstFieldError.defaultMessage
		return ErrorResponse(
			errorField,
			receivedValue,
			errorMessage,
		)
	}

	// validation error when use @Validated (but it produces NullPointerException, IllegalStateException)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ExceptionHandler(ConstraintViolationException::class)
	fun handle400(e: ConstraintViolationException): ErrorResponse {
		val firstFieldError = e.constraintViolations.first()
		val errorField = firstFieldError.propertyPath.toString()
		val receivedValue = firstFieldError.invalidValue
		val errorMessage = firstFieldError.message
		return ErrorResponse(
			errorField,
			receivedValue,
			errorMessage,
		)
	}

}
