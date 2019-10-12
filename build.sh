#!/bin/bash
# Be sure that the 'native-image' is in scope (see readme.md)
./gradlew clean shadowJar && \
  native-image --no-server \
    --no-fallback \
    --initialize-at-build-time=org.apache.http,org.apache.commons.logging,org.apache.commons.codec \
    --enable-https --enable-url-protocols=https --enable-all-security-services \
     --report-unsupported-elements-at-runtime \
         --rerun-class-initialization-at-runtime=org.bouncycastle.crypto.prng.SP800SecureRandom \
      --rerun-class-initialization-at-runtime=org.bouncycastle.jcajce.provider.drbg.DRBG$Default \
      --rerun-class-initialization-at-runtime=org.bouncycastle.jcajce.provider.drbg.DRBG$NonceAndIV \
      -J-Djava.security.properties=java.security.overrides \
    -cp build/libs/filewatch-2-sqs-1.0-SNAPSHOT-all.jar
