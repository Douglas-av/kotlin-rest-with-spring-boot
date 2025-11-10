package br.com.docosal

import br.com.docosal.config.DogStatsdConfig
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication
import org.springframework.security.crypto.password.DelegatingPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.crypto.password.Pbkdf2PasswordEncoder
import org.springframework.security.crypto.password.Pbkdf2PasswordEncoder.SecretKeyFactoryAlgorithm

@SpringBootApplication
@EnableConfigurationProperties(DogStatsdConfig::class)
class Startup
fun main(args: Array<String>) {
	runApplication<Startup>(*args)

	val pbkdf2PasswordEncoder = Pbkdf2PasswordEncoder("", 8, 185000, SecretKeyFactoryAlgorithm.PBKDF2WithHmacSHA256)
	val encoders: MutableMap<String, PasswordEncoder> = HashMap()
	encoders["pbkdf2"] = pbkdf2PasswordEncoder
	val passwordEncoder = DelegatingPasswordEncoder("pbkdf2", encoders)
	passwordEncoder.setDefaultPasswordEncoderForMatches(pbkdf2PasswordEncoder)

	val result = passwordEncoder.encode("admin123")
	println("My hash $result")
}
