# Binary Serializer / Deserializer

A binary serializer/ deserializer implementation in Java. The project aims to provide the following features:
* compact, fast, binary data format
* transport or store data between processes

The project does not intend to replace Java serialization or provide ways to intercept, create arbitrary classes or 
execute code during serialization/deserialization. The main goal is to provide a way to convert easy POJO / DTO 
(plain old java object / data transfer objects) objects to streams and back.

## Data Types

The following data types are supported:
* [primitive data types](https://docs.oracle.com/javase/tutorial/java/nutsandbolts/datatypes.html) and their coresponding object
* [date time types](https://www.oracle.com/technical-resources/articles/java/jf14-date-time.html)
* [collections](https://docs.oracle.com/javase/tutorial/collections/interfaces/index.html)

## How to build

Using a JDK 11 or newer, with Apahce Maven:

```bash
mvn clean install
```