package com.ksra_handygo.model

import com.ksra_handygo.model.User
import com.ksra_handygo.network.RetrofitInstance

class UserRepository {

    suspend fun getUsers(): List<User> {
        return RetrofitInstance.api.getUsers()
    }

    suspend fun createUser(user: User): User {
        return RetrofitInstance.api.createUser(user)
    }
}
