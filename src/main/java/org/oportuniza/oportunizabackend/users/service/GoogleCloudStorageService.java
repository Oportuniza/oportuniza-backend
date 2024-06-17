package org.oportuniza.oportunizabackend.users.service;

import com.google.auth.oauth2.GoogleCredentials;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URL;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class GoogleCloudStorageService {
    private final Storage storage;
    private final String bucketName;

    public GoogleCloudStorageService(@Value("${spring.cloud.gcp.storage.bucket}") String bucketName, @Value("${spring.cloud.gcp.credentials.location}") Resource credentialsResource) throws IOException {
        this.storage = StorageOptions.newBuilder()
                .setCredentials(GoogleCredentials.fromStream(credentialsResource.getInputStream()))
                .build()
                .getService();
        this.bucketName = bucketName;
    }

    public String uploadFile(MultipartFile file) throws IOException {
        String fileName = UUID.randomUUID() + "-" + file.getOriginalFilename();
        BlobId blobId = BlobId.of(bucketName, fileName);
        BlobInfo blobInfo = BlobInfo.newBuilder(blobId).setContentType(file.getContentType()).build();
        storage.create(blobInfo, file.getBytes());
        return fileName;
    }

    public URL generateV4GetObjectSignedUrl(String fileName) {
        BlobId blobId = BlobId.of(bucketName, fileName);
        BlobInfo blobInfo = BlobInfo.newBuilder(blobId).build();
        return storage.signUrl(blobInfo, 15, TimeUnit.MINUTES, Storage.SignUrlOption.withV4Signature());
    }

    public void deleteFile(String fileName) {
        storage.delete(BlobId.of(bucketName, fileName));
    }
}
