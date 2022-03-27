package com.galee.core.data.model

data class ValidationModel(
    var nameOfField: String,
    var message: String?,
    var formErrors: FormErrors?
) {

    constructor(nameOfField: String, message: String?) : this(
        nameOfField,
        message,
        FormErrors.EMPTY
    )

    //    constructor() : this("", false,null, FormErrors.EMPTY)
    constructor(nameOfField: String) : this(nameOfField, null, FormErrors.VALID)
}

data class Validation(var isValid: Boolean, var validationFields: MutableList<ValidationModel>)

enum class FormErrors {
    EMPTY, INVALID_FORMAT, PASSWORD_NOT_MATCHING, CUSTOM_ERROR, VALID
}