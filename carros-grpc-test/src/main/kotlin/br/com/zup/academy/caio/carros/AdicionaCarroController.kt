package br.com.zup.academy.caio.carros

import br.com.zup.academy.caio.CarrosGrpcServiceGrpc
import br.com.zup.academy.caio.CarrosRequest
import br.com.zup.academy.caio.CarrosResponse
import io.grpc.Status
import io.grpc.stub.StreamObserver
import javax.inject.Inject
import javax.inject.Singleton
import javax.validation.ConstraintViolationException

@Singleton
class AdicionaCarroController(
    @Inject val repository: CarroRepository
): CarrosGrpcServiceGrpc.CarrosGrpcServiceImplBase() {

    override fun adicionar(request: CarrosRequest, responseObserver: StreamObserver<CarrosResponse>) {

        if(repository.existsByPlaca(request.placa)){
            responseObserver.onError(Status.ALREADY_EXISTS
                                        .withDescription("Carro já cadastrado")
                                        .asRuntimeException())

            return
        }

        val carro = Carro(request.modelo, request.placa)

        try{
            repository.save(carro)

        }catch (e: ConstraintViolationException){
            responseObserver.onError(Status.INVALID_ARGUMENT
                                        .withDescription("Dados inválidos")
                                        .asRuntimeException())

            return
        }

        val carroResponse = with(carro){
            CarrosResponse.newBuilder()
                            .setId(carro.id!!)
                            .build()
        }

        responseObserver.onNext(carroResponse)
        responseObserver.onCompleted()

    }
}