package jp.sharepic.sharepicback.infra.s3;

import java.io.File;
import java.io.FileInputStream;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import jp.sharepic.sharepicback.domains.info.InfoEntity;
import jp.sharepic.sharepicback.domains.info.InfoRepository;

@Component
public class S3Service {

    @Autowired
    private InfoRepository infoRepository;

    @Value("${bucket_name}")
    private String bucketName;

    @Value("${access_key}")
    private String accessKey;

    /**
     * 引数に指定されたファイルをS3にアップロードします。
     * 
     * @param objectUrl 画像ファイル
     * @return
     * @throws Exception
     */
    @Async
    public boolean putObject(String objectUrl) {

        System.out.println("【開始】S3アップロード処理");

        boolean uploadResult = false;
        File targetFile = new File(objectUrl);

        // ファイルが読み込めること
        if (!targetFile.canRead()) {
            System.out.println("ファイルが読み込めません。");
            return uploadResult;
        }

        try (FileInputStream fis = new FileInputStream(targetFile);) {
            AmazonS3 s3Client = buildS3Client();
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(targetFile.length());
            PutObjectRequest putRequest = new PutObjectRequest(bucketName, targetFile.getName(), fis, metadata);
            // オブジェクトのACLを設定
            putRequest.setCannedAcl(CannedAccessControlList.PublicRead);
            // アップロード
            s3Client.putObject(putRequest);
            uploadResult = true;
            System.out.println("S3アップロード処理完了；" + objectUrl);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("S3アップロード処理に失敗しました。");
        }

        System.out.println("【終了】S3アップロード処理");

        return uploadResult;
    }

    private AmazonS3 buildS3Client() {
        // データベースより「AccessKeyId」と「SecretKey」を取得する
        InfoEntity info = infoRepository.findById("IAM").orElseThrow();
        // 認証オブジェクトを作成
        BasicAWSCredentials awsCreds = new BasicAWSCredentials(info.getKey(), info.getValue());
        return AmazonS3ClientBuilder.standard().withCredentials(new AWSStaticCredentialsProvider(awsCreds))
                .withRegion(Regions.AP_NORTHEAST_1).build();
    }

}
