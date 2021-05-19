package br.com.zup.academy.caio

import io.grpc.Status
import io.grpc.StatusRuntimeException
import io.grpc.protobuf.StatusProto
import io.micronaut.http.HttpStatus
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.QueryValue
import io.micronaut.http.client.HttpClient
import io.micronaut.http.client.exceptions.HttpClientResponseException
import io.micronaut.http.exceptions.HttpStatusException
import javax.inject.Inject

@Controller
class CalculaFreteController(
    @Inject
    val grpcClient: FretesServiceGrpc.FretesServiceBlockingStub
) {

    @Get("/fretes")
    fun calcular(@QueryValue cep: String): FreteResponse {

        val request = CalculaFreteRequest.newBuilder()
            .setCep(cep)
            .build()

        try {
            val responseGrpc = grpcClient.calculaFrete(request)
            return FreteResponse(responseGrpc.cep, responseGrpc.valor)

        } catch (e: StatusRuntimeException) {

            val statusCode = e.status.code
            val description = e.status.description

            if (statusCode == Status.Code.INVALID_ARGUMENT) {

                throw HttpStatusException(HttpStatus.BAD_REQUEST, description)
            }

            if (statusCode == Status.Code.INTERNAL) {

                val statusProto = StatusProto.fromThrowable(e) ?: throw HttpStatusException(HttpStatus.NOT_FOUND, description)
                val details = statusProto.detailsList[0].unpack(ErroDetails::class.java)
                throw HttpStatusException(HttpStatus.BAD_REQUEST, "${details.code}: ${details.mensagem}")
            }
        }

        throw HttpStatusException(HttpStatus.NOT_FOUND, "CEP não encontrado")

    }

}