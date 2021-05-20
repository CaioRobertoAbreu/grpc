package br.com.zup.academy.caio.carros

import br.com.zup.academy.caio.CarrosGrpcServiceGrpc
import br.com.zup.academy.caio.CarrosRequest
import br.com.zup.academy.caio.CarrosResponse
import io.grpc.ManagedChannel
import io.grpc.Status
import io.grpc.StatusRuntimeException
import io.micronaut.context.annotation.Factory
import io.micronaut.grpc.annotation.GrpcChannel
import io.micronaut.grpc.server.GrpcServerChannel
import io.micronaut.test.annotation.TransactionMode
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import javax.inject.Inject
import javax.inject.Singleton

@MicronautTest(transactionMode = TransactionMode.SINGLE_TRANSACTION,
transactional = false)
internal class AdicionaCarroControllerTest(
    @Inject val repository: CarroRepository,
    @Inject val client: CarrosGrpcServiceGrpc.CarrosGrpcServiceBlockingStub){

    /*
        1. Caminho feliz - OK
        2. Placa já existente - OK
        3. Dados inválidos
     */

    @BeforeEach
    fun setup(){
        repository.deleteAll()
        println("Dados apagados")
    }

    @Test
    fun `deve adicionar um novo carro`() {
        //Cenario
        val request = CarrosRequest.newBuilder()
            .setModelo("Logan")
            .setPlaca("FBB-3012")
            .build()

        //Acao
        val response: CarrosResponse = client.adicionar(request)

        //Verificacao
        with(response){
            Assertions.assertNotNull(this.id)
            Assertions.assertTrue(repository.existsById(this.id))

        }
    }

    @Test
    fun `deve retornar excecao ao adicionar carro com placa ja existente`(){
        //Cenario
        val existente = repository.save(Carro("Logan", "RCB-1619"))

        //Acao

        val request = CarrosRequest.newBuilder()
                        .setModelo("Ferrati")
                        .setPlaca(existente.placa)
                        .build()

        val erro = assertThrows<StatusRuntimeException> {
            client.adicionar(request)
        }

        //Verificacao
        with(erro) {
            assertEquals(Status.ALREADY_EXISTS.code, this.status.code)
            assertEquals("Carro já cadastrado", this.status.description)
        }

    }

    @Test
    fun `Deve retornar excecao ao tentar cadastrar carro com dados invalidos`() {
        //Cenario
        val request = CarrosRequest.newBuilder()
                        .setPlaca("")
                        .setModelo("")
                        .build()
        //Acao
        val erro = assertThrows<StatusRuntimeException> {
            client.adicionar(request)
        }

        //Verificacao
        with(erro) {
            assertEquals(status.code, this.status.code)
            assertEquals("Dados inválidos", this.status.description)
        }
    }

}

@Factory
class GrpcClient{

    @Singleton
    fun blockingStub(@GrpcChannel(GrpcServerChannel.NAME) channel: ManagedChannel): CarrosGrpcServiceGrpc.CarrosGrpcServiceBlockingStub? {

        return CarrosGrpcServiceGrpc.newBlockingStub(channel)
    }

}