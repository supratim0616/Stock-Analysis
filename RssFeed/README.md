---Package Structure

--RssFeed

----src

------com
--------bean
----------Item.java : Bean class corresponding to item in News feed
----------Stock.java : Bean class corresponding to price quote

------com
--------feed
----------FeedInitiator.java : Main class which is the driver class for getting news feed and uploading on s3.
----------QuoteInitiator.java : Main class which is the driver class for getting news feed and uploading on s3.
----------ElasticMapReduceJobFlow.java : Main class which is the driver class for running map reduce program.
----------RSSFeed.java : Contains the logic for fetching News feed from URL, creating json files and Uploading on S3
----------S3FileUpload.java : For uploading files on S3
----------StockData.java : Contains the logic for fetching Price feed from URL, creating json files and Uploading on S3

------AwsCredentials.properties : Contains secret and access key for AWS
------build.xml : for including AwsCredentials.properties in classpath

------config
--------DOW.tx : all the <ticker><company name> for DOW
--------NASDAQ.tx : all the <ticker><company name> for NASDAQ
--------S&P.tx : all the <ticker><company name> for S&P

----dependencies - contains all required jar files for ruuning the application
------aws-java-sdk-1.5.4.jar
------commons-codec-1.3.jar
------commmons-io-1.3.2.jar
------commons-logging-1.1.1.jar
------gson-1.7.1.jar
------httpclient-4.2.jar
------httpcore-4.2.jar
------jackson-core-asl-1.8.9.jar
------json-serde-1.1.7.jar
------log4j-1.2.16.jar
------opencsv-2.0.jar

----log
------ Contains the log files

----ServerFiles
------assignTagsmapper.py : python script to assign the symbol among (PP,TP,AP,PN,TN,AN) based on criteria met
------assignWordsWeightagemapper.py : python script which is assigning creating a key based on time and assigning weightage to it (using file weightsAndKeywords.txt)
------hivescript.hql : hive script for inserting new records into table "StockAnalysis.QUOTES_DATA" after reading records from S3.
------callhiveScript.sh : shell script for calling hivescript.hql along with parameters (S3_PATH, TODAY_DATE)
------reductionOutputConfig.txt : File with permarket and aftermarket time. After file has been uploaded make sure the link is public.
------w thi : Filw contains weights and corresponding keywords. After file has been uploaded make sure the link is public.

----pom.xml : containing all maven dependencies
----README.md : containing the class structure