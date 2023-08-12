package com.example.testapplication

import com.google.common.truth.Truth.assertThat
import org.junit.Assert.assertThrows
import org.junit.Test

import java.lang.IllegalArgumentException

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    var calculator: SimpleCalculator = SimpleCalculator()

    @Test
    fun addition_returnCorrect(){
        assertThat(calculator.add(2,2)).isEqualTo(4)
    }

    @Test
    fun getGrade_95_91_90_99_returnA(){
        assertThat(calculator.getGrade(95)).isEqualTo('A')
        assertThat(calculator.getGrade(91)).isEqualTo('A')
        assertThat(calculator.getGrade(90)).isEqualTo('A')
        assertThat(calculator.getGrade(99)).isEqualTo('A')
    }

    @Test
    fun `getGrade null return nullpointerexception`(){
        assertThat(calculator.getGrade()).isNotInstanceOf(NullPointerException::class.java)
    }

    @Test
    fun `getGrade big number throw IllegalArgumentException`(){
        val errorCause = assertThrows(IllegalArgumentException::class.java) {
            calculator.getGrade(120)
        }
        assertThat(errorCause).hasMessageThat().isEqualTo("The Number Does Not Know")
    }
}

class SimpleCalculator{
    fun add(first: Int, second: Int): Int{
        return first + second
    }

    fun getGrade(first: Int = 0) : Char {
        return if(first <= 50) 'E'
        else if(first < 70) 'D'
        else if(first < 80) 'C'
        else if(first < 90) 'B'
        else if(first < 100) 'A'
        else throw IllegalArgumentException("The Number Does Not Know")
    }
}