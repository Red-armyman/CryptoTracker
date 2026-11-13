package com.cryptotracker.backend

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class CryptotrackerBackendApplication

fun main(args: Array<String>) {
	runApplication<CryptotrackerBackendApplication>(*args)
}
