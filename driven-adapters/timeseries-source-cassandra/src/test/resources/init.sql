CREATE KEYSPACE IF NOT EXISTS my_keyspace WITH replication = {'class': 'SimpleStrategy', 'replication_factor': 1};

USE my_keyspace;

CREATE TABLE timeseries (
    isin TEXT,
    date DATE,
    price DOUBLE,
    PRIMARY KEY (isin, date)
);

INSERT INTO timeseries (isin, date, price) VALUES ('US0378331005', '2023-01-01', 150.0);
INSERT INTO timeseries (isin, date, price) VALUES ('US0378331005', '2023-02-01', 155.0);
INSERT INTO timeseries (isin, date, price) VALUES ('US0378331005', '2023-03-01', 160.0);
INSERT INTO timeseries (isin, date, price) VALUES ('US0378331005', '2023-04-01', 165.0);
INSERT INTO timeseries (isin, date, price) VALUES ('US0378331005', '2023-05-01', 170.0);
INSERT INTO timeseries (isin, date, price) VALUES ('US0378331005', '2023-06-01', 175.0);
INSERT INTO timeseries (isin, date, price) VALUES ('US0378331005', '2023-07-01', 154.0);