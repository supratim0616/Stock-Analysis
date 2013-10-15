add jar /home/hadoop/Hive-JSON-Serde-master/target/json-serde-1.1.7.jar;

CREATE TABLE IF NOT EXISTS StockAnalysis.QUOTES_DATA (
          stock_date string, Open double, high double, close double,volume double,adj_Close double,high52week double,low52week double,market string,ticker string,todaydate string, time string
       ) PARTITIONED BY(today_date STRING)
        CLUSTERED BY(ticker) INTO 26 BUCKETS;

CREATE EXTERNAL TABLE IF NOT EXISTS StockAnalysis.tmp (
          stock_date string, Open double, high double, close double,volume double,adj_Close double,high52week double,low52week double,market string,ticker string,today_date string, time string
       )
       ROW FORMAT SERDE 'org.openx.data.jsonserde.JsonSerDe'
    LOCATION '${hiveconf:S3_PATH}';

insert into table StockAnalysis.QUOTES_DATA PARTITION (today_date = '${hiveconf:TODAY_DATE}') select * from StockAnalysis.tmp;

INSERT OVERWRITE DIRECTORY 's3://hive-quote-result/output/' SELECT * FROM StockAnalysis.QUOTES_DATA;

drop table StockAnalysis.tmp;