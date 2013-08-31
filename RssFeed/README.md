---Package Structure

--RssFeed

----src

------com
--------bean
----------Item.java : Bean class corresponding to item in News feed
----------Stock.java : Bean class corresponding to price quote

------com
--------feed
----------Initiator.java : Main class which is the driver class for application
----------RSSFeed.java : Contains the logic for fetching News feed from URL, creating json files and Uploading on S3
----------S3FileUpload.java : For uploading files on S3
----------StockData.java : Contains the logic for fetching Price feed from URL, creating json files and Uploading on S3

------AwsCredentials.properties : Contains secret and access key for AWS
------build.xml : for including AwsCredentials.properties in classpath

------config
--------DOW.tx : all the <ticker><company name> for DOW
--------NASDAQ.tx : all the <ticker><company name> for NASDAQ
--------S&P.tx : all the <ticker><company name> for S&P

----log
------ Contains the log files

----pom.xml : containing all maven dependencies
----README.md : containing the class structure