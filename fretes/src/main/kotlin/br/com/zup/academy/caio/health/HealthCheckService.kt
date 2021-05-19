package br.com.zup.academy.caio.health

import grpc.health.v1.HealthGrpc
import grpc.health.v1.HealthOuterClass
import io.grpc.stub.StreamObserver
import javax.inject.Singleton

@Singleton
class HealthCheckService: HealthGrpc.HealthImplBase() {

    override fun check(request: HealthOuterClass.HealthCheckRequest?,
        responseObserver: StreamObserver<HealthOuterClass.HealthCheckResponse>?)  {

        val healthCheck = HealthOuterClass.HealthCheckResponse.newBuilder()
            .setStatus(HealthOuterClass.HealthCheckResponse.ServingStatus.SERVING)
            .build()

        responseObserver?.onNext(healthCheck)
        responseObserver?.onCompleted()

    }

    override fun watch(request: HealthOuterClass.HealthCheckRequest?,
        responseObserver: StreamObserver<HealthOuterClass.HealthCheckResponse>?) {

        val healthCheck = HealthOuterClass.HealthCheckResponse.newBuilder()
            .setStatus(HealthOuterClass.HealthCheckResponse.ServingStatus.SERVING)
            .build()

        responseObserver?.onNext(healthCheck)
    }
}