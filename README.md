# LDA-LncRNADiseaseAssociation

## Build
There are two ways to build this project:
- manually build using Maven
- automated build using docker-compose

### Maven Build
The Maven build will create the jar file into `target/` folder.
```
mvn clean install
```
### Docker Build
The Docker build will create the jar file into `jars/` folder.
```
docker-compose up
```

## Execution
The execution of the jar via terminal is done with this command:
```
java -jar lda.jar <arguments>
```
There are several arguments that can be used. For more information use the argument `-h`.