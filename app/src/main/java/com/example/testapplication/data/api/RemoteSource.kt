package com.example.testapplication.data.api

import com.example.testapplication.data.source.DataSource
import kotlinx.coroutines.flow.flow

class RemoteSource(private val remoteApi: IRemoteSource) : DataSource {
    override fun callRandomAPI() = flow {
        emit(remoteApi.getRandomDish())
    }

    override fun callDetailApi(id: Int) = flow {
        emit(remoteApi.getDetail(id))
    }
}