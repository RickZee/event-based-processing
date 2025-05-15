package com.example;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.avro.Conversions;
import org.apache.avro.Schema;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericDatumReader;
import org.apache.avro.generic.GenericDatumWriter;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.io.*;
import org.bson.Document;
import org.bson.types.Decimal128;
import org.junit.jupiter.api.*;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class BigDecimalPrecisionTest {

    private static MongoClient mongoClient;
    private static MongoCollection<Document> collection;
    private static Schema avroSchema;

    @BeforeAll
    public static void setUp() throws Exception {
        // Connect to local MongoDB instance running in Docker
        mongoClient = MongoClients.create("mongodb://localhost:27017");
        MongoDatabase database = mongoClient.getDatabase("test");
        collection = database.getCollection("testCollection");

        // Define Avro schema with decimal logical type (precision 38, scale 10)
        String schemaString = "{\"type\":\"record\",\"name\":\"BigDecimalRecord\",\"fields\":[{\"name\":\"value\",\"type\":{\"type\":\"bytes\",\"logicalType\":\"decimal\",\"precision\":38,\"scale\":10}}]}";
        avroSchema = new Schema.Parser().parse(schemaString);
    }

    @AfterAll
    public static void tearDown() {
        if (mongoClient != null) {
            mongoClient.close();
        }
    }

    // MongoDB Tests
    @Test
    public void testMongoDbStorageTypical() {
        testMongoDbStorage(new BigDecimal("123.4567890123")); // Scale 10
    }

    @Test
    public void testMongoDbStorageLarge() {
        testMongoDbStorage(new BigDecimal("12345678901234567890.1234567890")); // 30 digits
    }

    @Test
    public void testMongoDbStorageSmall() {
        testMongoDbStorage(new BigDecimal("0.0000000001")); // Scale 10
    }

    @Test
    public void testMongoDbStorageNegative() {
        testMongoDbStorage(new BigDecimal("-123.4567890123")); // Scale 10
    }

    @Test
    public void testMongoDbStorageZero() {
        testMongoDbStorage(BigDecimal.ZERO);
    }

    private void testMongoDbStorage(BigDecimal original) {
        // Generate a unique ID for the document
        String uniqueId = UUID.randomUUID().toString();
        Document doc = new Document("_id", uniqueId)
                .append("value", original);
        collection.insertOne(doc);

        // Retrieve the document by unique ID
        Document retrieved = collection.find(Filters.eq("_id", uniqueId)).first();
        Decimal128 decimal128Value = retrieved.get("value", Decimal128.class);
        BigDecimal retrievedValue = decimal128Value.bigDecimalValue();
        assertEquals(original, retrievedValue, "BigDecimal precision lost in MongoDB");

        // Clean up by deleting the document with the unique ID
        collection.deleteOne(Filters.eq("_id", uniqueId));
    }

    private void testMongoDbStorageOriginal(BigDecimal original) {
        Document doc = new Document("value", original);
        collection.insertOne(doc);
        Document retrieved = collection.find().first();
        BigDecimal retrievedValue = retrieved.get("value", BigDecimal.class);
        assertEquals(original, retrievedValue, "BigDecimal precision lost in MongoDB");
        collection.deleteOne(doc); // Clean up
    }

    // JSON Tests
    @Test
    public void testJsonSerializationTypical() throws Exception {
        testJsonSerialization(new BigDecimal("123.4567890123")); // Scale 10
    }

    @Test
    public void testJsonSerializationLarge() throws Exception {
        testJsonSerialization(new BigDecimal("12345678901234567890.1234567890")); // 30 digits
    }

    private void testJsonSerialization(BigDecimal original) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> map = new HashMap<>();
        map.put("value", original.toString()); // Serialize as string to preserve precision
        String json = mapper.writeValueAsString(map);
        Map<String, Object> deserialized = mapper.readValue(json, Map.class);
        String valueStr = (String) deserialized.get("value");
        BigDecimal retrieved = new BigDecimal(valueStr);
        assertEquals(original, retrieved, "BigDecimal precision lost in JSON serialization");
    }

    // Avro Tests
    @Test
    public void testAvroSerializationTypical() throws Exception {
        testAvroSerialization(new BigDecimal("123.4567890123")); // Scale 10
    }

    @Test
    public void testAvroSerializationLarge() throws Exception {
        testAvroSerialization(new BigDecimal("12345678901234567890.1234567890")); // 30 digits
    }

    private void testAvroSerialization(BigDecimal original) throws Exception {
        GenericRecord record = new GenericData.Record(avroSchema);
        Conversions.DecimalConversion decimalConversion = new Conversions.DecimalConversion();
        Schema valueSchema = avroSchema.getField("value").schema();
        record.put("value", decimalConversion.toBytes(original, valueSchema, valueSchema.getLogicalType()));

        DatumWriter<GenericRecord> writer = new GenericDatumWriter<>(avroSchema);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        BinaryEncoder encoder = EncoderFactory.get().binaryEncoder(out, null);
        writer.write(record, encoder);
        encoder.flush();
        byte[] serialized = out.toByteArray();

        DatumReader<GenericRecord> reader = new GenericDatumReader<>(avroSchema);
        BinaryDecoder decoder = DecoderFactory.get().binaryDecoder(serialized, null);
        GenericRecord deserialized = reader.read(null, decoder);

        BigDecimal retrieved = decimalConversion.fromBytes(
                (java.nio.ByteBuffer) deserialized.get("value"),
                valueSchema,
                valueSchema.getLogicalType());
        assertEquals(original, retrieved, "BigDecimal precision lost in Avro serialization");
    }
}