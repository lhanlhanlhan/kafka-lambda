// Create keyspace
CREATE KEYSPACE IF NOT EXISTS TheBoardKeySpace WITH replication = { 'class':'SimpleStrategy', 'replication_factor':1 };

// Create table
