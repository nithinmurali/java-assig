## Requirements

java 1.8+, maven, redis

## Install
Clone the repository

```
git clone git@github.com:nithinmurali/java-assig.git
cd java-assig
```
Install all the dependencies listed

Then create the configuration file (config.properties) and fill values.

```
cp config.properties.sample config.properties
```

Run the code
```
mvn exec:java -Dexec.mainClass="com.stakx.BinanceRunner"
```