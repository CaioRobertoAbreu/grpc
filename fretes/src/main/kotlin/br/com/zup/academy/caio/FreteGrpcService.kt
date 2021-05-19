package br.com.zup.academy.caio

import com.google.protobuf.Any
import com.google.rpc.Code
import io.grpc.Status
import io.grpc.protobuf.StatusProto
import io.grpc.stub.StreamObserver
import io.micronaut.grpc.annotation.GrpcService
import io.micronaut.http.HttpResponse
import io.micronaut.http.client.exceptions.HttpClientResponseException
import org.slf4j.LoggerFactory
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.random.Random

@Singleton
@GrpcService
class FreteGrpcService(
    @Inject
    val cepClient: CepClient
) : FretesServiceGrpc.FretesServiceImplBase() {

    private val logger = LoggerFactory.getLogger(FreteGrpcService::class.java)

    override fun calculaFrete(request: CalculaFreteRequest?, responseObserver: StreamObserver<CalculaFreteResponse>?) {

        val cep = request?.cep
        if (cep == null || cep.isBlank()) {
            val e = Status.INVALID_ARGUMENT
                .withDescription("CEP não informado")
                .asRuntimeException()

            return responseObserver!!.onError(e)
        }

        if(!cep.matches("[0-9]{5}-[0-9]{3}".toRegex())){

            val e = Status.INVALID_ARGUMENT
                .withDescription("CEP inválido")
                .augmentDescription("Formato esperado: 00000-000")
                .asRuntimeException()

            return responseObserver!!.onError(e)
        }

        logger.info("Consultando cep ${request.cep}")
        lateinit var consultar: HttpResponse<CepClientResponse>

        //Simulação erro
        if(cep.endsWith("333")){
            val status = com.google.rpc.Status
                            .newBuilder()
                            .setCode(Code.INTERNAL.number)
                            .setMessage("Ocorreu um erro")
                            .addDetails(Any.pack(ErroDetails.newBuilder()
                                                    .setCode(400)
                                                    .setMensagem("Algo de errado com o final do CEP")
                                                    .build()))
                            .build()

            val e = StatusProto.toStatusRuntimeException(status)

            return responseObserver!!.onError(e)
        }

        try{
            consultar = cepClient.consultar(request.cep)
        }catch (e: HttpClientResponseException){

            val exception = Status.NOT_FOUND
                .withDescription("CEP não encontrado")
                .asRuntimeException()

            return responseObserver!!.onError(exception)
        }

        val response = CalculaFreteResponse.newBuilder()
            .setCep(consultar.body()!!.cep)
            .setLogradouro(consultar.body()!!.logradouro)
            .setCidade(consultar.body()!!.localidade)
            .setEstado(consultar.body()!!.uf)
            .setValor(Random.nextDouble(0.0, 143.24))
            .build()


        responseObserver!!.onNext(response)
        responseObserver.onCompleted()
    }


}