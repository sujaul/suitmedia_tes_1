package com.test.test_karim.data.remote

data class ErrorMessage(
    var status: Boolean? = null,
    var statusCode: Int? = null,
    var message: String? = null
)