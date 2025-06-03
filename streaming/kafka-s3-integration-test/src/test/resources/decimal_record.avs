{
  "type": "record",
  "name": "DecimalRecord",
  "namespace": "com.example",
  "fields": [
    {
      "name": "value",
      "type": {
        "type": "bytes",
        "logicalType": "decimal",
        "precision": 38,
        "scale": 10
      }
    }
  ]
}