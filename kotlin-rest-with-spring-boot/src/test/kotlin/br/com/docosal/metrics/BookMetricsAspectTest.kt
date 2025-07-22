package br.com.docosal.metrics

import br.com.docosal.data.vo.v1.BookDTO
import br.com.docosal.unittests.mockito.metrics.BookMetricsAspectTest
import br.com.docosal.util.Constantes.Companion.KOTLIN_REST_SPRING_BOOT
import io.micrometer.core.instrument.simple.SimpleMeterRegistry
import org.aspectj.lang.JoinPoint
import org.aspectj.lang.Signature
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import org.springframework.http.ResponseEntity
import java.lang.reflect.Field

// 1. A anotação @ExtendWith agora aponta para a extensão do Mockito
@ExtendWith(MockitoExtension::class)
class BookMetricsAspectTest {

    // Defina a classe de exemplo aqui, dentro da classe de teste
    private class DummyBookService {}

    private lateinit var meterRegistry: SimpleMeterRegistry
    private lateinit var bookMetricsAspect: BookMetricsAspect

    // 2. A anotação @Mock do Mockito é usada para criar os mocks
    @Mock
    private lateinit var joinPoint: JoinPoint

    @Mock
    private lateinit var signature: Signature

    @BeforeEach
    fun setUp() {
        meterRegistry = SimpleMeterRegistry()
        bookMetricsAspect = BookMetricsAspect()

        val meterRegistryField: Field = BookMetricsAspect::class.java.getDeclaredField("meterRegistry")
        meterRegistryField.isAccessible = true
        meterRegistryField.set(bookMetricsAspect, meterRegistry)

        // 3. A configuração dos mocks (stubbing) usa a sintaxe do Mockito
        // `whenever` é da biblioteca mockito-kotlin para evitar conflito com a palavra-chave `when` do Kotlin
        whenever(joinPoint.signature).thenReturn(signature)
    }

    // ---------------------------------------------------------------------------------
    // Testes para o método `countMethodCall`
    // ---------------------------------------------------------------------------------

    // SUBSTITUA SEU TESTE POR ESTE CÓDIGO
    @Test
    fun `DADO uma chamada de método QUANDO countMethodCall é invocado DEVE criar e incrementar o contador`() {
        // --- ARRANGE (Preparação) ---

        // 1. O nome do método é uma String.
        val methodName = "findAll"

        // 2. O nome da classe também é uma String, obtido a partir do objeto Class.
        val className = DummyBookService::class.java.simpleName

        // 3. Configure o mock para retornar o OBJETO 'Class', não a String 'className'.
        whenever(signature.declaringType).thenReturn(DummyBookService::class.java)

        // 4. Configure os outros mocks necessários.
        whenever(signature.name).thenReturn(methodName)

        // --- ACT (Ação) ---
        bookMetricsAspect.countMethodCall(joinPoint)
        bookMetricsAspect.countMethodCall(joinPoint)

        // --- ASSERT (Verificação) ---
        val counter = meterRegistry.find(KOTLIN_REST_SPRING_BOOT).counter()

        assertNotNull(counter, "O contador não deveria ser nulo")
        assertEquals(2.0, counter?.count())

        // A asserção usa a variável String 'className'
        assertEquals(className, counter?.id?.getTag("className"))
        assertEquals(methodName, counter?.id?.getTag("methodName"))
    }

    // ---------------------------------------------------------------------------------
    // Testes para o método `recordBookPrice`
    // ---------------------------------------------------------------------------------

    @Test
    fun `DADO um BookDTO QUANDO recordBookPrice é invocado DEVE registrar métricas de DistributionSummary e Gauge`() {
        // Arrange
        val book = BookDTO(key = 1, author = "Machado de Assis", title = "Dom Casmurro", price = 45.50)

        whenever(signature.declaringType).thenReturn(DummyBookService::class.java)
        whenever(signature.name).thenReturn("findById")

        // Act
        bookMetricsAspect.recordBookPrice(joinPoint, book)

        // Assert (Nenhuma alteração aqui)
        val summary = meterRegistry.find("$KOTLIN_REST_SPRING_BOOT.distribution.summary").summary()
        assertNotNull(summary, "A métrica DistributionSummary não deveria ser nula")
        assertEquals(1, summary?.count(), "A contagem do summary deveria ser 1")
        assertEquals(45.50, summary?.totalAmount(), "O total do summary deveria ser o preço do livro")
        assertEquals("Machado de Assis", summary?.id?.getTag("BookAuthor"), "A tag 'BookAuthor' está incorreta")

        val gauge = meterRegistry.find("$KOTLIN_REST_SPRING_BOOT.gauge").gauge()
        assertNotNull(gauge, "A métrica Gauge não deveria ser nula")
        assertEquals(45.50, gauge?.value(), "O valor do gauge deveria ser o preço do livro")
        assertEquals("Machado de Assis", gauge?.id?.getTag("BookAuthor"), "A tag 'BookAuthor' do gauge está incorreta")
    }

    @Test
    fun `DADO uma ResponseEntity com BookDTO QUANDO recordBookPrice é invocado DEVE extrair o body e registrar métricas`() {
        // Arrange
        val book = BookDTO(key = 2, author = "Clarice Lispector", title = "A Hora da Estrela", price = 32.90)
        val response = ResponseEntity.ok(book)
        whenever(signature.declaringType).thenReturn(DummyBookService::class.java)
        whenever(signature.name).thenReturn("findById")

        // Act
        bookMetricsAspect.recordBookPrice(joinPoint, response)

        // Assert (Nenhuma alteração aqui)
        val summary = meterRegistry.find("$KOTLIN_REST_SPRING_BOOT.distribution.summary").summary()
        assertNotNull(summary)
        assertEquals(32.90, summary?.totalAmount())
        assertEquals("Clarice Lispector", summary?.id?.getTag("BookAuthor"))
    }

    @Test
    fun `DADO um resultado nulo QUANDO recordBookPrice é invocado NÃO DEVE registrar nenhuma métrica`() {
        // Arrange
        whenever(signature.declaringType).thenReturn(DummyBookService::class.java)
        whenever(signature.name).thenReturn("findById")

        // Act
        bookMetricsAspect.recordBookPrice(joinPoint, null)

        // Assert (Nenhuma alteração aqui)
        assertTrue(meterRegistry.meters.isEmpty(), "Nenhuma métrica deveria ter sido registrada para um resultado nulo")
    }

//    @Test
//    fun `DADO um BookDTO com preço nulo QUANDO recordBookPrice é invocado NÃO DEVE registrar nenhuma métrica`() {
//        // Arrange
//        val bookWithNullPrice = BookDTO(key = 3, author = "Graciliano Ramos", title = "Vidas Secas", price = null)
//        whenever(signature.declaringType).thenReturn(DummyBookService::class.java)
//        whenever(signature.name).thenReturn("findById")
//
//        // Act
//        bookMetricsAspect.recordBookPrice(joinPoint, bookWithNullPrice)
//
//        // Assert (Nenhuma alteração aqui)
//        assertTrue(meterRegistry.meters.isEmpty(), "Nenhuma métrica deveria ter sido registrada para um preço nulo")
//    }

    @Test
    fun `DADO um tipo de payload não suportado QUANDO recordBookPrice é invocado NÃO DEVE registrar nenhuma métrica`() {
        // Arrange
        val unsupportedPayload = "Isto não é um BookDTO"
        whenever(signature.declaringType).thenReturn(DummyBookService::class.java)
        whenever(signature.name).thenReturn("findById")

        // Act
        bookMetricsAspect.recordBookPrice(joinPoint, unsupportedPayload)

        // Assert (Nenhuma alteração aqui)
        assertTrue(
            meterRegistry.meters.isEmpty(),
            "Nenhuma métrica deveria ser registrada para um payload não suportado"
        )
    }
}