# BEHOLDER-MS

Pet-project about simple microservice for extraction, transforming and loading information (ETL) collected from web. 
Currently project limited to URL achievable sources and includes components and demo application for gathering adverts information from [AUTO.RIA](https://auto.ria.com) through REST API and storing it to [Elasticsearch](https://www.elastic.co) instance.

## Getting started

### If you just want to see results of demo application (CURRENTLY UNAVAILABLE)

You can visit Kibana plugin based on [AWS Elasticsearch Service](https://aws.amazon.com/en/elasticsearch-service) and see impersonal data from auto.ria.com about cars, motorbikes and cargo vehicles adverts for sale (information updates each hour). 

Link: [https://search-beholder-demo-mwabk5i2wofjt6cglzmgyftyxi.eu-central-1.es.amazonaws.com/_plugin/kibana/](https://search-beholder-demo-mwabk5i2wofjt6cglzmgyftyxi.eu-central-1.es.amazonaws.com/_plugin/kibana/)

*Warning!* Access is public and anonymous, but mostly in read-only mode, so you can sink in access restriction messages if try to change something important. 

### If you want to build the same demo application, but connect it to your personal Elasticsearch instance
You should do next few steps: 
1. Fork project’s repository
2. Configure access to your Elasticsearch by changing org.apache.http.HttpHost class based bean in src/main/resources/beans.xml to something like:
```
<bean id="myHost" class="org.apache.http.HttpHost">
        <constructor-arg name="hostname"
                         value="myHostName"/>
        <constructor-arg name="port" value="xxx"/>
        <constructor-arg name="scheme" value="https"/>
    </bean>
```
3. Build application with Maven using projects pom-file
4. Register at [developers.ria.com](https://developers.ria.com) and get user API KEY (unfortunately, I can’t share my one due to security and API restrictions reasons)
5. Run application with parameters:
- JVM parameters: ```-Dfile.encoding=utf-8```
- Application parameter (it is only one and not optional): your API KEY

### If you want to build your own microservice for gathering information from another sources and/or to use storage other than Elasticsearch instance:
1. **To gather information from custom source:**
Write your own subclass of UrlDataExtractor from home.javaphite.beholder.extraction package and override only one method – extract().
2. **To store information to custom storage: **
 	Write your own implementation of Accessor interface from home.javaphite.beholder.storage.accessors package and implement its push method.
3. Build your own application using new components.

## Build on:
[Maven](https://maven.apache.org)
## See also:
[RIA.COM dev portal](https://developers.ria.com),
[Elastic stack](https://www.elastic.co),
[Amazon Web Services (AWS)](https://aws.amazon.com) 

## Announcements
If you will find weaknesses, bugs, weird code lines (and you will!) – please contact me on javaphite@gmail.com. I will be glad to learn something from you and make my code better! 

