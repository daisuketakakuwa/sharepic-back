package jp.sharepic.sharepicback.domains.card;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Date;
import java.util.UUID;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import jp.sharepic.sharepicback.domains.relation.CardTagRelEntity;
import jp.sharepic.sharepicback.domains.relation.CardTagRelRepository;
import jp.sharepic.sharepicback.domains.tag.TagEntity;
import jp.sharepic.sharepicback.domains.tag.TagRepository;
import jp.sharepic.sharepicback.infra.s3.S3Service;

@Component
public class CardService {

    @Value("${img_tmp_dir}")
    String imageTmpDir;

    @Autowired
    CardRepository cardRepository;
    @Autowired
    TagRepository tagRepository;
    @Autowired
    CardTagRelRepository cardTagRelRepository;

    @Autowired
    S3Service s3Service;

    public void uploadCard(String src, String extension, String[] tags, String description, String postUser) {

        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddhhmmss");
        StringBuilder sb = new StringBuilder();
        sb.append(imageTmpDir);
        sb.append("image_");
        sb.append(postUser).append("_");
        sb.append(sdf.format(new Date()));
        sb.append(".").append(extension);

        try {
            Files.write(Paths.get(sb.toString()), Base64.getDecoder().decode(src.getBytes()));
        } catch (IOException ie) {
            throw new RuntimeException(ie);
        }

        // S3画像アップロード
        String objectUrl = s3Service.putObject(sb.toString());

        // テーブル登録(Card, Tag)
        saveEntities(objectUrl, tags, description, postUser);

        // リレーションテーブル登録
        saveRelations(objectUrl, tags);
    }

    @Transactional
    private void saveEntities(String objectUrl, String[] tags, String description, String postUser) {

        // Cardテーブル登録
        CardEntity cardEntity = new CardEntity();
        cardEntity.setId(UUID.randomUUID().toString());
        cardEntity.setSrc(objectUrl);
        cardEntity.setDescription(description);
        cardEntity.setPostDate(LocalDateTime.now());
        cardEntity.setPostUser(postUser);
        cardRepository.save(cardEntity);
        System.out.println("CARDテーブル登録処理完了" + "【" + objectUrl + "】");

        // Tagテーブル登録（新規タグの場合のみ登録する）
        for (String tagName : tags) {
            if (!tagRepository.findByName(tagName).isPresent()) {
                TagEntity tagEntity = new TagEntity();
                tagEntity.setId(UUID.randomUUID().toString());
                tagEntity.setName(tagName);
                tagRepository.save(tagEntity);
                System.out.println("TAGテーブル登録処理完了" + "【" + tagName + "】");

            }
        }
    }

    @Transactional
    private void saveRelations(String objectUrl, String[] tags) {
        // CardTagRelテーブル登録
        // Cardエンティティ取得
        CardEntity card = cardRepository.findBySrc(objectUrl).orElseThrow(RuntimeException::new);
        for (String tagName : tags) {
            // Tagエンティティ取得
            TagEntity tag = tagRepository.findByName(tagName).orElseThrow(RuntimeException::new);
            // 登録
            CardTagRelEntity cardTagRelEntity = new CardTagRelEntity();
            cardTagRelEntity.setId(UUID.randomUUID().toString());
            cardTagRelEntity.setCard(card);
            cardTagRelEntity.setTag(tag);
            cardTagRelRepository.save(cardTagRelEntity);
            System.out.println("CardTagRelationテーブル登録処理完了");
        }
    }

}
