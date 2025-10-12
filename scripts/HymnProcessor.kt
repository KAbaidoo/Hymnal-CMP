#!/usr/bin/env kotlin

@file:Repository("https://repo1.maven.org/maven2/")
@file:DependsOn("org.xerial:sqlite-jdbc:3.44.1.0")

import java.io.File
import java.sql.Connection
import java.sql.DriverManager

/**
 * Script to process Anglican hymn text files and create a SQLite database
 * Run with: kotlin HymnProcessor.kt
 */

data class Hymn(
    val number: Int,
    val title: String,
    val category: String,
    val content: String
)

fun main() {
    val anglicansDir = File("/Users/kobby/Desktop/Anglican")
    val outputDir = File("../composeApp/src/commonMain/composeResources/files")
    
    if (!anglicansDir.exists()) {
        println("Anglican directory not found: ${anglicansDir.absolutePath}")
        return
    }
    
    // Ensure output directory exists
    outputDir.mkdirs()
    
    val dbFile = File(outputDir, "hymns.db")
    println("Creating database: ${dbFile.absolutePath}")
    
    // Delete existing database
    if (dbFile.exists()) {
        dbFile.delete()
    }
    
    val connection = DriverManager.getConnection("jdbc:sqlite:${dbFile.absolutePath}")
    
    try {
        createTables(connection)
        val hymns = parseHymnFiles(anglicansDir)
        insertHymns(connection, hymns)
        
        println("Successfully processed ${hymns.size} hymns")
        println("Database created at: ${dbFile.absolutePath}")
        println("Database size: ${dbFile.length() / 1024}KB")
        
    } finally {
        connection.close()
    }
}

fun createTables(connection: Connection) {
    val schema = """
        -- SQLDelight schema for Anglican Hymnal app

        CREATE TABLE hymn (
            id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
            number INTEGER NOT NULL,
            title TEXT,
            category TEXT NOT NULL,
            content TEXT NOT NULL,
            created_at INTEGER NOT NULL DEFAULT (strftime('%s', 'now'))
        );

        CREATE TABLE favorite (
            id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
            hymn_id INTEGER NOT NULL,
            created_at INTEGER NOT NULL DEFAULT (strftime('%s', 'now')),
            FOREIGN KEY (hymn_id) REFERENCES hymn(id),
            UNIQUE(hymn_id)
        );

        CREATE TABLE history (
            id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
            hymn_id INTEGER NOT NULL,
            accessed_at INTEGER NOT NULL DEFAULT (strftime('%s', 'now')),
            FOREIGN KEY (hymn_id) REFERENCES hymn(id)
        );

        CREATE TABLE highlight (
            id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
            hymn_id INTEGER NOT NULL,
            start_index INTEGER NOT NULL,
            end_index INTEGER NOT NULL,
            created_at INTEGER NOT NULL DEFAULT (strftime('%s', 'now')),
            FOREIGN KEY (hymn_id) REFERENCES hymn(id)
        );

        -- Indices for performance
        CREATE INDEX idx_hymn_category ON hymn(category);
        CREATE INDEX idx_hymn_number ON hymn(number);
        CREATE INDEX idx_favorite_hymn_id ON favorite(hymn_id);
        CREATE INDEX idx_history_hymn_id ON history(hymn_id);
        CREATE INDEX idx_history_accessed_at ON history(accessed_at DESC);
        CREATE INDEX idx_highlight_hymn_id ON highlight(hymn_id);

        -- Virtual table for full-text search
        CREATE VIRTUAL TABLE hymn_fts USING fts4(
            content='hymn',
            number,
            title,
            category,
            content
        );

        -- Triggers to keep FTS table in sync
        CREATE TRIGGER hymn_fts_insert AFTER INSERT ON hymn BEGIN
            INSERT INTO hymn_fts(docid, number, title, category, content) 
            VALUES (new.id, new.number, new.title, new.category, new.content);
        END;

        CREATE TRIGGER hymn_fts_delete AFTER DELETE ON hymn BEGIN
            DELETE FROM hymn_fts WHERE docid = old.id;
        END;

        CREATE TRIGGER hymn_fts_update AFTER UPDATE ON hymn BEGIN
            DELETE FROM hymn_fts WHERE docid = old.id;
            INSERT INTO hymn_fts(docid, number, title, category, content) 
            VALUES (new.id, new.number, new.title, new.category, new.content);
        END;
    """.trimIndent()
    
    connection.createStatement().use { statement ->
        schema.split(";").forEach { sql ->
            val trimmed = sql.trim()
            if (trimmed.isNotEmpty()) {
                statement.execute(trimmed)
            }
        }
    }
    
    println("Database schema created successfully")
}

fun parseHymnFiles(anglicansDir: File): List<Hymn> {
    val hymns = mutableListOf<Hymn>()
    
    // Process Ancient and Modern hymns
    val ancientModernDir = File(anglicansDir, "Ancient and Modern")
    if (ancientModernDir.exists()) {
        ancientModernDir.listFiles { file -> 
            file.name.endsWith(".txt") && file.name.startsWith("Hymn ")
        }?.forEach { file ->
            parseHymnFile(file, "ancient_modern")?.let { hymns.add(it) }
        }
    }
    
    // Process Supplementary hymns
    val supplementaryDir = File(anglicansDir, "Supplementary Clean")
    if (supplementaryDir.exists()) {
        supplementaryDir.listFiles { file -> 
            file.name.endsWith(".txt") && file.name.startsWith("supp ")
        }?.forEach { file ->
            parseHymnFile(file, "supplementary")?.let { hymns.add(it) }
        }
    }
    
    return hymns.sortedWith(compareBy({ it.category }, { it.number }))
}

fun parseHymnFile(file: File, category: String): Hymn? {
    try {
        val content = file.readText().trim()
        if (content.isEmpty()) return null
        
        val lines = content.lines()
        if (lines.isEmpty()) return null
        
        // Extract hymn number from filename or first line
        val number = when (category) {
            "ancient_modern" -> {
                // Extract from "Hymn 123.txt"
                file.nameWithoutExtension.removePrefix("Hymn ").toIntOrNull() ?: 0
            }
            "supplementary" -> {
                // Extract from "supp 12.txt"
                file.nameWithoutExtension.removePrefix("supp ").toIntOrNull() ?: 0
            }
            else -> 0
        }
        
        // Extract title (second line if it exists and is not empty)
        var title = ""
        var contentStartIndex = 1
        
        if (lines.size > 1 && lines[1].trim().isNotEmpty() && !lines[1].trim().startsWith("HYMN")) {
            title = lines[1].trim()
            contentStartIndex = 2
        } else if (lines.size > 2 && lines[2].trim().isNotEmpty()) {
            title = lines[2].trim()
            contentStartIndex = 3
        }
        
        // Extract hymn content (skip first line which contains hymn number/title)
        val hymnContent = lines.drop(contentStartIndex)
            .joinToString("\n")
            .trim()
        
        if (hymnContent.isEmpty()) return null
        
        return Hymn(
            number = number,
            title = title,
            category = category,
            content = hymnContent
        )
        
    } catch (e: Exception) {
        println("Error parsing file ${file.name}: ${e.message}")
        return null
    }
}

fun insertHymns(connection: Connection, hymns: List<Hymn>) {
    val sql = "INSERT INTO hymn (number, title, category, content) VALUES (?, ?, ?, ?)"
    
    connection.prepareStatement(sql).use { statement ->
        hymns.forEach { hymn ->
            statement.setInt(1, hymn.number)
            statement.setString(2, hymn.title)
            statement.setString(3, hymn.category)
            statement.setString(4, hymn.content)
            statement.addBatch()
        }
        
        val results = statement.executeBatch()
        println("Inserted ${results.size} hymns successfully")
    }
}