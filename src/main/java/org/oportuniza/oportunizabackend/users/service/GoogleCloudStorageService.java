package org.oportuniza.oportunizabackend.users.service;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.*;
import org.javatuples.Pair;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.UUID;

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

    public void makeFilePublic(String fileName) {
        BlobId blobId = BlobId.of(bucketName, fileName);
        storage.createAcl(blobId, Acl.of(Acl.User.ofAllUsers(), Acl.Role.READER));
    }

    public Pair<String, URL> uploadFile(MultipartFile file) throws IOException, URISyntaxException {
        String fileName = UUID.randomUUID() + "-" + file.getOriginalFilename();
        BlobId blobId = BlobId.of(bucketName, fileName);
        BlobInfo blobInfo = BlobInfo.newBuilder(blobId).setContentType(file.getContentType()).build();
        storage.create(blobInfo, file.getBytes());
        makeFilePublic(fileName);
        return new Pair<>(fileName, new URI("https://storage.googleapis.com/" + bucketName + "/" + fileName).toURL());
    }

    public void deleteFile(String fileName) {
        storage.delete(BlobId.of(bucketName, fileName));
    }
}
