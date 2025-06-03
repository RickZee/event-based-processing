from minio import Minio
from minio.error import S3Error

def main():
    # Create a client with the MinIO server, access key, and secret key.
    client = Minio(
        "localhost:9000",
        access_key="minioadmin",
        secret_key="minioadmin",
        secure=False  # Set to True for HTTPS
    )

    # Make a new bucket.
    try:
        client.make_bucket("event-based-processing")
        print("Bucket 'event-based-processing' created successfully.")
    except S3Error as err:
        print("Error occurred:", err)

if __name__ == "__main__":
    main()
