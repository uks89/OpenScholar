package org.openscholar.app.agent

interface Agent<in T, out R> {
    val name: String
    val description: String
    suspend fun process(request: T): R
    suspend fun initialize()
    suspend fun shutdown()
}
