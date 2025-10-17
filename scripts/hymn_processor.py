#!/usr/bin/env python3

import os
import sqlite3
import re
from pathlib import Path

def create_database_schema(cursor):
    """Create the database schema with all tables and indices"""
    
    # Create tables one by one
    schemas = [
        """CREATE TABLE hymn (
            id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
            number INTEGER NOT NULL,
            title TEXT,
            category TEXT NOT NULL,
            content TEXT NOT NULL,
            created_at INTEGER NOT NULL DEFAULT (strftime('%s', 'now'))
        )""",
        
        """CREATE TABLE favorite (
            id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
            hymn_id INTEGER NOT NULL,
            created_at INTEGER NOT NULL DEFAULT (strftime('%s', 'now')),
            FOREIGN KEY (hymn_id) REFERENCES hymn(id),
            UNIQUE(hymn_id)
        )""",
        
        """CREATE TABLE history (
            id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
            hymn_id INTEGER NOT NULL,
            accessed_at INTEGER NOT NULL DEFAULT (strftime('%s', 'now')),
            FOREIGN KEY (hymn_id) REFERENCES hymn(id)
        )""",
        
        """CREATE TABLE highlight (
            id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
            hymn_id INTEGER NOT NULL,
            start_index INTEGER NOT NULL,
            end_index INTEGER NOT NULL,
            created_at INTEGER NOT NULL DEFAULT (strftime('%s', 'now')),
            FOREIGN KEY (hymn_id) REFERENCES hymn(id)
        )""",
        
        "CREATE INDEX idx_hymn_category ON hymn(category)",
        "CREATE INDEX idx_hymn_number ON hymn(number)",
        "CREATE INDEX idx_favorite_hymn_id ON favorite(hymn_id)",
        "CREATE INDEX idx_history_hymn_id ON history(hymn_id)",
        "CREATE INDEX idx_history_accessed_at ON history(accessed_at DESC)",
        "CREATE INDEX idx_highlight_hymn_id ON highlight(hymn_id)",
        
        """CREATE VIRTUAL TABLE hymn_fts USING fts4(
            content='hymn',
            number,
            title,
            category,
            content
        )""",
        
        """CREATE TRIGGER hymn_fts_insert AFTER INSERT ON hymn BEGIN
            INSERT INTO hymn_fts(docid, number, title, category, content) 
            VALUES (new.id, new.number, new.title, new.category, new.content);
        END""",
        
        """CREATE TRIGGER hymn_fts_delete AFTER DELETE ON hymn BEGIN
            DELETE FROM hymn_fts WHERE docid = old.id;
        END""",
        
        """CREATE TRIGGER hymn_fts_update AFTER UPDATE ON hymn BEGIN
            DELETE FROM hymn_fts WHERE docid = old.id;
            INSERT INTO hymn_fts(docid, number, title, category, content) 
            VALUES (new.id, new.number, new.title, new.category, new.content);
        END"""
    ]
    
    for schema in schemas:
        cursor.execute(schema)
    
    print("Database schema created successfully")

def parse_hymn_file(file_path, category):
    """Parse a single hymn file and extract hymn data"""
    try:
        # Try different encodings to handle various text files
        encodings = ['utf-8', 'latin-1', 'cp1252']
        content = None
        
        for encoding in encodings:
            try:
                with open(file_path, 'r', encoding=encoding) as f:
                    content = f.read().strip()
                break
            except UnicodeDecodeError:
                continue
        
        if content is None:
            print(f"Could not decode file {file_path} with any encoding")
            return None
        
        if not content:
            return None
        
        lines = content.split('\n')
        if not lines:
            return None
        
        # Extract hymn number from filename
        filename = os.path.basename(file_path)
        if category == "ancient_modern":
            # Extract from "Hymn 123.txt"
            match = re.search(r'Hymn (\d+)\.txt', filename)
            number = int(match.group(1)) if match else 0
        elif category == "supplementary":
            # Extract from "supp 12.txt"
            match = re.search(r'supp (\d+)\.txt', filename)
            number = int(match.group(1)) if match else 0
        else:
            number = 0
        
        # Skip the first line (HYMN X) and any empty lines to get to actual content
        content_start_index = 1
        while content_start_index < len(lines) and not lines[content_start_index].strip():
            content_start_index += 1
        
        # Extract hymn content (everything after the header)
        hymn_content = '\n'.join(lines[content_start_index:]).strip()
        
        # Extract title from first line of content
        title = ""
        if hymn_content:
            first_line = hymn_content.split('\n')[0].strip()
            # Remove trailing punctuation and use as title
            title = first_line.rstrip('.,;:!?').strip()
        
        if not hymn_content:
            return None
        
        return {
            'number': number,
            'title': title,
            'category': category,
            'content': hymn_content
        }
        
    except Exception as e:
        print(f"Error parsing file {file_path}: {e}")
        return None

def process_hymn_directory(anglican_dir):
    """Process all hymn files in the Anglican directory"""
    hymns = []
    
    # Process Ancient and Modern hymns
    ancient_modern_dir = os.path.join(anglican_dir, "Ancient and Modern")
    if os.path.exists(ancient_modern_dir):
        for filename in os.listdir(ancient_modern_dir):
            if filename.endswith('.txt') and filename.startswith('Hymn '):
                file_path = os.path.join(ancient_modern_dir, filename)
                hymn = parse_hymn_file(file_path, "ancient_modern")
                if hymn:
                    hymns.append(hymn)
    
    # Process Supplementary hymns
    supplementary_dir = os.path.join(anglican_dir, "Supplementary Clean")
    if os.path.exists(supplementary_dir):
        for filename in os.listdir(supplementary_dir):
            if filename.endswith('.txt') and filename.startswith('supp '):
                file_path = os.path.join(supplementary_dir, filename)
                hymn = parse_hymn_file(file_path, "supplementary")
                if hymn:
                    hymns.append(hymn)
    
    # Sort hymns by category and number
    hymns.sort(key=lambda x: (x['category'], x['number']))
    return hymns

def insert_hymns(cursor, hymns):
    """Insert hymns into the database"""
    insert_sql = "INSERT INTO hymn (number, title, category, content) VALUES (?, ?, ?, ?)"
    
    for hymn in hymns:
        cursor.execute(insert_sql, (
            hymn['number'],
            hymn['title'],
            hymn['category'],
            hymn['content']
        ))
    
    print(f"Inserted {len(hymns)} hymns successfully")

def main():
    # Paths
    anglican_dir = "/Users/kobby/Desktop/Anglican"
    output_dir = "../composeApp/src/commonMain/composeResources/files"
    
    if not os.path.exists(anglican_dir):
        print(f"Anglican directory not found: {anglican_dir}")
        return
    
    # Create output directory
    Path(output_dir).mkdir(parents=True, exist_ok=True)
    
    db_path = os.path.join(output_dir, "hymns.db")
    print(f"Creating database: {db_path}")
    
    # Remove existing database
    if os.path.exists(db_path):
        os.remove(db_path)
    
    # Create database and process hymns
    conn = sqlite3.connect(db_path)
    try:
        cursor = conn.cursor()
        
        # Create schema
        create_database_schema(cursor)
        
        # Parse hymn files
        print("Parsing hymn files...")
        hymns = process_hymn_directory(anglican_dir)
        
        # Insert hymns
        print("Inserting hymns into database...")
        insert_hymns(cursor, hymns)
        
        # Commit changes
        conn.commit()
        
        # Print statistics
        print(f"\nSuccess! Processed {len(hymns)} hymns")
        print(f"Database created at: {db_path}")
        
        # Get file size
        file_size = os.path.getsize(db_path)
        print(f"Database size: {file_size / 1024:.1f}KB")
        
        # Print category breakdown
        ancient_modern_count = sum(1 for h in hymns if h['category'] == 'ancient_modern')
        supplementary_count = sum(1 for h in hymns if h['category'] == 'supplementary')
        
        print(f"\nCategory breakdown:")
        print(f"- Ancient & Modern: {ancient_modern_count} hymns")
        print(f"- Supplementary: {supplementary_count} hymns")
        
    finally:
        conn.close()

if __name__ == "__main__":
    main()