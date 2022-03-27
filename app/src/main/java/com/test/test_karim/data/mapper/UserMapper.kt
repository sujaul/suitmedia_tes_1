package com.test.test_karim.data.mapper

import com.galee.core.data.BaseMapperResponseToEntity
import com.test.test_karim.data.model.Guests

open class UserMapper : BaseMapperResponseToEntity<Guests, Guests>{
    override fun mapResponseToEntity(response: Guests): Guests {
        return response
    }
}