# Example: AWS Lambda + GraalVM native-image with Scala

An example of running a GraalVM native-image with Scala on AWS Lambda.

## Technology stack (library)

- [sbt-native-image: Generate native-image binaries](https://github.com/scalameta/sbt-native-image)
- [ZIO: Type-safe, composable library for async, concurrent programming](https://github.com/zio/zio)
- [sttp: Http client](https://github.com/softwaremill/sttp)
- [circe: JSON library](https://github.com/circe/circe)
- [Logback: Logging](https://github.com/qos-ch/logback)
- [Serverless Framework: Deployment](https://github.com/serverless/serverless)

## Test this example on your AWS

### Requirements

- AWS Account
- JDK `8` or `later`
- Node.js `12` or `later`

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

## References

Super thanks!

- [akka-graal-native](https://github.com/vmencik/akka-graal-native)
- [scala-native-cli.g8](https://github.com/takezoe/scala-native-cli.g8)

## LICENSE

MIT
