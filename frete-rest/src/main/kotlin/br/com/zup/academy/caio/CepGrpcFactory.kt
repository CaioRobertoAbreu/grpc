package br.com.zup.academy.caio

import io.grpc.ManagedChannel
import io.micronaut.context.annotation.Factory
import io.micronaut.grpc.annotation.GrpcChannel
import javax.inject.Singleton

@Factory
class CepGrpcFactory {

    @Singleton
    fun fretesClientStub(@GrpcChannel("fretes") channel: ManagedChannel):
            FretesServiceGrpc.FretesServiceBlockingStub? {

        return FretesServiceGrpc.newBlockingStub(channel)
    }

}

