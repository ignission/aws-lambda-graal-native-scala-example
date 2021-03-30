# Example: AWS Lambda + GraalVM native-image with Scala

An example of running a GraalVM native-image with Scala on AWS Lambda.

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
