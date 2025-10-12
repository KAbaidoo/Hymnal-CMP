package com.kobby.hymnal.database

/**
 * Factory for creating test data
 */
object TestDataFactory {
    
    data class TestHymn(
        val number: Long,
        val title: String,
        val category: String,
        val content: String
    )
    
    /**
     * Creates a set of realistic test hymns
     */
    fun createTestHymns(): List<TestHymn> = listOf(
        TestHymn(
            number = 1,
            title = "Now that the daylight fills the sky",
            category = "ancient_modern",
            content = """
                Now that the daylight fills the sky,
                We lift our hearts to God on high,
                That He, in all we do or say,
                Would keep us free from harm today.
                
                May He restrain our tongues from strife,
                And shield from anger's in our life,
                And guard with watchful care our eyes
                From earth's absorbing vanities.
            """.trimIndent()
        ),
        
        TestHymn(
            number = 2,
            title = "Jesus, my Saviour",
            category = "supplementary",
            content = """
                JESUS, my SAVIOUR, Brother, Friend,
                On whom I cast my every care,
                On whom for all things I depend,
                Inspire, and then accept, my prayer.
                
                If I have tasted of Thy grace,
                The grace that sure salvation brings,
                If with me now Thy SPIRIT stays,
                And hovering hides me in His wings.
            """.trimIndent()
        ),
        
        TestHymn(
            number = 100,
            title = "Passion",
            category = "ancient_modern",
            content = """
                Zion's Daughter, weep no more,
                Though Thy troubled heart be sore;
                He of whom the Psalmist sung,
                He who woke the Prophet's tongue,
                CHRIST, the Mediator Blest,
                Brings Thee everlasting rest.
            """.trimIndent()
        ),
        
        TestHymn(
            number = 3,
            title = "Amazing Grace",
            category = "ancient_modern",
            content = """
                Amazing grace how sweet the sound
                That saved a wretch like me
                I once was lost but now am found
                Was blind but now I see.
                
                'Twas grace that taught my heart to fear
                And grace my fears relieved
                How precious did that grace appear
                The hour I first believed.
            """.trimIndent()
        ),
        
        TestHymn(
            number = 4,
            title = "Holy, Holy, Holy",
            category = "ancient_modern",
            content = """
                Holy, Holy, Holy! Lord God Almighty!
                Early in the morning our song shall rise to Thee;
                Holy, Holy, Holy! Merciful and Mighty!
                God in three Persons, blessed Trinity!
            """.trimIndent()
        ),
        
        TestHymn(
            number = 5,
            title = "When I Survey",
            category = "supplementary",
            content = """
                When I survey the wondrous cross
                On which the Prince of glory died,
                My richest gain I count but loss,
                And pour contempt on all my pride.
            """.trimIndent()
        )
    )
    
    /**
     * Creates test highlights data
     */
    fun createTestHighlights(hymnId: Long): List<TestHighlight> = listOf(
        TestHighlight(hymnId, 0, 10),
        TestHighlight(hymnId, 50, 75),
        TestHighlight(hymnId, 100, 120)
    )
    
    data class TestHighlight(
        val hymnId: Long,
        val startIndex: Long,
        val endIndex: Long
    )
    
    /**
     * Creates test search queries with expected results
     */
    fun createSearchTestCases(): List<SearchTestCase> = listOf(
        SearchTestCase("grace", listOf("Amazing Grace"), listOf("grace", "Grace")),
        SearchTestCase("holy", listOf("Holy, Holy, Holy"), listOf("Holy")),
        SearchTestCase("jesus", listOf("Jesus, my Saviour"), listOf("JESUS")),
        SearchTestCase("cross", listOf("When I Survey"), listOf("cross")),
        SearchTestCase("daylight", listOf("Now that the daylight fills the sky"), listOf("daylight")),
        SearchTestCase("nonexistent", emptyList(), emptyList())
    )
    
    data class SearchTestCase(
        val query: String,
        val expectedTitles: List<String>,
        val expectedContentWords: List<String>
    )
}