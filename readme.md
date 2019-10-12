# File watcher that sends messages to Amazon SQS

## Functionality
This application watches a directory for changes.
Newly added files will be read and send to an SQS-queue.
After successful sending the file will be deleted. 

## Getting the configuration for supporting the necessary reflection 
on [Graal VM](https://www.graalvm.org/)

As described [here](https://github.com/oracle/graal/blob/master/substratevm/CONFIGURE.md), 
use the following to capture all the reflection magic that is being used in the application:

`java -agentlib:native-image-agent=config-output-dir=native-config -jar build/libs/filewatch-2-sqs-1.0-SNAPSHOT-all.jar`

This will capture all configuration detected in the directory native-config.
This configuration (jni-config.json, proxy-config.json, reflect-config.json and resource-config.json) is already stored in `src/main/resources/` under `META-INF/native-image`.

## Configuration and creation of the Graal native image with Gradle
Execute `./gradlew nativeImage`.

## Configuration and creation of the Graal native image with scripts
Next to the configuration mentioned above, 
the`native-image.properties` configures how the native-image will be compiled.

Make sure that the `native-image` of the Graal VM is on the Path.
```shell script
export JAVA_HOME=$(/usr/libexec/java_home -v 1.8)
export PATH=$JAVA_HOME/bin:$PATH
```

The `build.sh` contains building the jar file (with gradle) and the creation of the native image

## Running the sample

### Environment variables
The application uses two environment variables to transfer newly created files.

These are:

- SQS_QUEUE
- SQS_INPUT_DIRECTORY

### Amazon SQS
Make sure that the correct [credentials](https://docs.aws.amazon.com/cli/latest/userguide/cli-configure-files.html) for AWS are available.
Make sure that the SQS queue is defined in the [Amazon Console](https://console.aws.amazon.com/console).

### Running the application
Next to the environment variables, you also need to provide a trust store, 
as the default trust store of Graal VM does not contain the correct certificates. 

So the following 
```shell script
export SQS_QUEUE=https://sqs.***.amazonaws.com/****/*** && \
  export SQS_INPUT_DIRECTORY=/watch/directory && \
   ./filewatch-2-sqs -Djavax.net.ssl.trustStore=./cacerts
```

