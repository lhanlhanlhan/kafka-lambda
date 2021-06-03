// Create keyspace
CREATE KEYSPACE IF NOT EXISTS TheBoardKeySpace WITH replication = { 'class':'SimpleStrategy', 'replication_factor':1 };

// Create table

-- city_total_trade
CREATE TABLE TheBoardKeySpace.city_total_trade (
    city text,
    number bigint,
    updatetime timestamp,
    PRIMARY KEY ( city ));

-- city_total_amount
CREATE TABLE TheBoardKeySpace.city_total_amount (
    city text,
    amount bigint,
    updatetime timestamp,
    PRIMARY KEY ( city ));
