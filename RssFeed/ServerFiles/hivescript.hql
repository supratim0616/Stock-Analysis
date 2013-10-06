add jar /home/hadoop/Hive-JSON-Serde-master/target/json-serde-1.1.7.jar;

CREATE TABLE IF NOT EXISTS QUOTES_DATA (
          stock_date string, Open double, high double, close double,volume double,adj_Close double,high52week double,low52week double,market string,ticker string,today_date string, time string
       );

select count(*) from quotes_data;

CREATE EXTERNAL TABLE IF NOT EXISTS tmp (
          stock_date string, Open double, high double, close double,volume double,adj_Close double,high52week double,low52week double,market string,ticker string,today_date string, time string
       )
       ROW FORMAT SERDE 'org.openx.data.jsonserde.JsonSerDe'
    LOCATION 's3://stock-news-feed/10042013/QUOTE/';

insert into table QUOTES_DATA select * from tmp;

select count(*) from quotes_data;

INSERT OVERWRITE DIRECTORY 's3://hive-quote-result/output/' SELECT * FROM QUOTES_DATA;

drop table tmp;
