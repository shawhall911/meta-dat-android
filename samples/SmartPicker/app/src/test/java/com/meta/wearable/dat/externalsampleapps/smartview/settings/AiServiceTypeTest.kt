package com.meta.wearable.dat.externalsampleapps.smartview.settings

import org.junit.Assert.assertEquals
import org.junit.Test

class AiServiceTypeTest {

    @Test
    fun `AiServiceType has correct values`() {
        assertEquals(2, AiServiceType.values().size)
        assertEquals(AiServiceType.HUGGING_FACE, AiServiceType.valueOf("HUGGING_FACE"))
        assertEquals(AiServiceType.MOCK, AiServiceType.valueOf("MOCK"))
    }

    @Test
    fun `AiServiceType name is correct`() {
        assertEquals("HUGGING_FACE", AiServiceType.HUGGING_FACE.name)
        assertEquals("MOCK", AiServiceType.MOCK.name)
    }

    @Test
    fun `AiServiceType ordinal is correct`() {
        assertEquals(0, AiServiceType.HUGGING_FACE.ordinal)
        assertEquals(1, AiServiceType.MOCK.ordinal)
    }
}
