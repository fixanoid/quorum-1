package org.web3j.quorum.enclave

import org.web3j.protocol.core.methods.response.EthSendTransaction
import org.web3j.quorum.Quorum
import org.web3j.quorum.enclave.protocol.EnclaveService

/**
 * Constellation implements the "privacy engine" of Quorum, a fork of Ethereum with support for private
 * transactions.
 *
 * <p> In this library it is used to encrypt the payload of a private transaction.
 */
class Constellation(private val ipcService: EnclaveService, private val web3: Quorum) : Enclave {

    override fun sendRawRequest(payload: String, privateFor: List<String>): EthSendTransaction {
        return web3.ethSendRawTransaction(payload).send()
    }

    override fun storeRawRequest(payload: String, from: String, to: List<String>): SendResponse {
        val sendRequest = SendRequest(payload, from, to)
        return ipcService.send(sendRequest, "send", SendResponse::class.java)
    }

    override fun receiveRequest(key: String, to: String): ReceiveResponse {
        val receiveRequest = ReceiveRequest(key, to)
        return ipcService.send(receiveRequest, "receive", ReceiveResponse::class.java)
    }

    override fun upCheck(): Boolean {
        val test = ipcService.send( "upcheck")
        return test  == "OK"
    }

    override fun deleteRequest(key: String): Boolean {
        throw NotImplementedError("Constellation delete not implemented")
    }
}