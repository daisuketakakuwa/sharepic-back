package jp.sharepic.sharepicback.domains.card;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import jp.sharepic.sharepicback.infra.s3.S3Service;

@Component
public class CardService {

    @Autowired
    CardRepository cardRepository;

    @Autowired
    S3Service s3Service;

    public void uploadCard(String src, String extension, String[] tags, String description, String postUser) {

        // TODO 一時ファイル生成場所はapplication.propertiesで管理する
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddhhmmss");
        String fileName = "C:\\Users\\daisu\\Desktop\\workspace\\imageworkspace\\image_" + sdf.format(new Date()) + "."
                + extension;
        try {
            Files.write(Paths.get(fileName), Base64.getDecoder().decode(src.getBytes()));
        } catch (IOException ie) {
            throw new RuntimeException(ie);
        }

        // S3画像アップロード
        s3Service.putObject(fileName);

        // Cardテーブル登録
        CardEntity entity = new CardEntity();
        entity.setId(UUID.randomUUID().toString());
        // TODO S3の画像ファイルURLを登録する
        entity.setSrc(src);
        entity.setTags(String.join(",", tags));
        // TODO 先頭についている「#」を除外する
        entity.setDescription(description);
        entity.setPostUser(postUser);
        cardRepository.save(entity);
    }

}
