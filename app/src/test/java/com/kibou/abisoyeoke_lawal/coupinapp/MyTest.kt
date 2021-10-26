package com.kibou.abisoyeoke_lawal.coupinapp

import junit.framework.Assert.assertEquals
import junit.framework.Assert.assertSame
import org.junit.Test
import java.util.*

class MyTest {

    @Test
    fun duplicateIds() {

        val testMap = mapOf("xyz" to 2, "abc" to 3, "dkd" to 1)
        val resultList = mutableListOf<String>()
        testMap.forEach{
            for (item in 1..it.value){
                resultList.add(it.key)
            }
        }
        assertEquals(resultList, mutableListOf("xyz", "xyz", "abc", "abc", "abc", "dkd"))
    }
}