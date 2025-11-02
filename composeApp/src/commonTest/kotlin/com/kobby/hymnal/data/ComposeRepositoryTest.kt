package com.kobby.hymnal.data

import kotlin.test.Test
import kotlin.test.assertEquals

class ComposeRepositoryTest {

    @Test
    fun `welcome returns expected message`() {
        // Given
        val repository = ComposeRepositoryImpl()

        // When
        val result = repository.welcome()

        // Then
        assertEquals("THIS IS COMPOSE MULTIPLATFORM.", result)
    }
}
