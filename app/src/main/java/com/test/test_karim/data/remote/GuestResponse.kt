package com.test.test_karim.data.remote

import com.test.test_karim.data.model.Guests

data class GuestResponse(
    var page: Int? = null,
    var per_page: Int? = null,
    var total: Int? = null,
    var total_pages: Int? = null,
    var data: List<Guests>? = null
)