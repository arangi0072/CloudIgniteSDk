package com.cloudignite.cloudignitesdk;

import java.io.File;
import java.io.IOException;
import okhttp3.*;

public class CloudIgniteSDK {
    private final String baseUrl;
    private final String token;
    private final OkHttpClient client;

    public CloudIgniteSDK(String baseUrl, String token) {
        this.baseUrl = baseUrl;
        this.token = token;
        this.client = new OkHttpClient();
    }

    // Upload a file
    public String uploadFile(String bucket, File file) throws IOException {
        String url = baseUrl + "/s3/object-storage/api/v1/upload";

        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("bucketID", bucket)
                .addFormDataPart("file", file.getName(),
                        RequestBody.create(file, MediaType.parse("application/octet-stream")))
                .build();

        Request request = new Request.Builder()
                .url(url)
                .addHeader("Authorization", "Bearer " + token)
                .post(requestBody)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Failed to upload file: " + response.message());
            }
            return response.body().string();
        }
    }

    // List files in a bucket
    public String listFiles(String bucket) throws IOException {
        String url = baseUrl + "/s3/object-storage/api/v1/files";

        RequestBody requestBody = RequestBody.create(
                "{\"bucketId\":\"" + bucket + "\"}",
                MediaType.parse("application/json")
        );

        Request request = new Request.Builder()
                .url(url)
                .addHeader("Authorization", "Bearer " + token)
                .post(requestBody)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Failed to list files: " + response.message());
            }
            return response.body().string();
        }
    }

    // Get a file's download URL
    public String getFile(String bucket, String filename) throws IOException {
        String url = baseUrl + "/s3/object-storage/" + bucket + "/" + filename;

        Request request = new Request.Builder()
                .url(url)
                .addHeader("Authorization", "Bearer " + token)
                .get()
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Failed to fetch file: " + response.message());
            }
            return response.body().string();
        }
    }

    // Delete a file
    public String deleteFile(String bucket, String filename) throws IOException {
        String url = baseUrl + "/s3/object-storage/api/v1/delete";

        RequestBody requestBody = RequestBody.create(
                "{\"file_id\":\"" + filename + "\",\"bucketId\":\"" + bucket + "\"}",
                MediaType.parse("application/json")
        );

        Request request = new Request.Builder()
                .url(url)
                .addHeader("Authorization", "Bearer " + token)
                .post(requestBody)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Failed to delete file: " + response.message());
            }
            return response.body().string();
        }
    }
}
