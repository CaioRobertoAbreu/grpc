package br.com.zup.academy.caio

import io.micronaut.http.HttpResponse
import io.micronaut.http.MediaType
import io.micronaut.http.annotation.Consumes
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.QueryValue
import io.micronaut.http.client.annotation.Client

@Client("https://viacep.com.br/ws")
interface CepClient {

    @Get("/{cep}/json")
    @Consumes(MediaType.APPLICATION_JSON)
    fun consultar(@QueryValue cep: String) : HttpResponse<CepClientResponse>
}

data class CepClientResponse(
    val cep: String,
    val logradouro: String,
    val localidade: String,
    val uf: String
) {

}
