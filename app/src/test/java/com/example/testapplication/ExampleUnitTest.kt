package com.example.testapplication

import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.core.Is.`is`
import org.junit.Assert.assertThrows
import org.junit.Test
import kotlin.jvm.Throws

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    var calculator: SimpleCalculator = SimpleCalculator()

    @Test
    fun addition_returnCorrect(){
        assertThat(calculator.add(2,2),`is`(4))
    }

    @Test
    fun getGrade_95_91_90_99_returnA(){
        assertThat(calculator.getGrade(95), `is`('A'))
        assertThat(calculator.getGrade(91), `is`('A'))
        assertThat(calculator.getGrade(90), `is`('A'))
        assertThat(calculator.getGrade(99), `is`('A'))
    }

    @Test
    fun `getGrade null return nullpointerexception`(){
        assertThrows(NullPointerException::class.java){
            calculator.getGrade()
        }
    }

    @Test
    fun `getGrade big number throw IllegalArgumentException`(){
        val errorCause = assertThrows(IllegalStateException::class.java) {
            calculator.getGrade(120)
        }
        assertThat(errorCause.message, `is`("Unknown number"))
    }
}

class SimpleCalculator{
    fun add(first: Int, second: Int): Int{
        return first + second
    }

    @Throws(IllegalArgumentException::class)
    fun getGrade(first: Int? = null) : Char {
        first?: throw NullPointerException("The Number Does Not Know")
        return if(first <= 50) 'E'
        else if(first < 70) 'D'
        else if(first < 80) 'C'
        else if(first < 90) 'B'
        else if(first < 100) 'A'
        else throw IllegalStateException("Unknown number")
    }
}