# Example: AWS Lambda + GraalVM native-image with Scala

An example of running a GraalVM native-image with Scala on AWS Lambda.

## Technology stack (library)

- [sbt-native-image: Generate native-image binaries](https://github.com/scalameta/sbt-native-image)
- [ZIO: Type-safe, composable library for async, concurrent programming](https://github.com/zio/zio)
- [circe: JSON library](https://github.com/circe/circe)
- [Serverless Framework: Deployment](https://github.com/serverless/serverless)
- [Logback: Logging](https://github.com/qos-ch/logback)
- java.net.http: Http client (available from JDK 11)

## Requirements

- JDK `11`
- Node.js `12` or `later`

## Test this example on your AWS

### Build

    make dist

### Set up Serverless

    cp serverless/.env.example serverless/

Edit `serverless/.env`

```
AWS_PROFILE=YOUR_AWS_PROFILE_NAME
```

### Deploy

    make deploy
