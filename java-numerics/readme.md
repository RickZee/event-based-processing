# Testing Mongo + Kafka + Avro + Json serialization

## Before you run the tests

Start the MongoDB Docker Container:
Ensure Docker is installed and running (docker --version).
Start a MongoDB container:

```terminal
docker run -d -p 27017:27017 --name mongodb mongo:latest
```

Verify the container is running:

```terminal
docker ps
```

If port 27017 is in use, stop any existing MongoDB instances or use a different port (e.g., -p 27018:27017) and update the connection string in BigDecimalPrecisionTest to mongodb://localhost:27018.

## Run the Permutation Generator

To generate NumberPOJO permutations, run:

```terminal
mvn exec:java -Dexec.mainClass="com.example.NumberPOJOPermutations"
```

This will print up to 10,000 NumberPOJO instances with their field values, including BigDecimal.

## Verify Test Results

Check the mvn clean install output to confirm all tests in BigDecimalPrecisionTest pass.
If any tests fail, note the specific error messages and share them for further debugging.
