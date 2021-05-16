package jp.sharepic.sharepicback.domains.card;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import jp.sharepic.sharepicback.domains.card.response.CardForAccountResponse;
import jp.sharepic.sharepicback.domains.card.response.CardForHomeResponse;
import jp.sharepic.sharepicback.domains.relation.CardTagRelEntity;
import jp.sharepic.sharepicback.domains.relation.CardTagRelRepository;
import jp.sharepic.sharepicback.domains.tag.TagEntity;
import jp.sharepic.sharepicback.domains.tag.TagRepository;
import jp.sharepic.sharepicback.infra.s3.S3Service;

@Component
public class CardService {

    @Value("${img_tmp_dir}")
    String imageTmpDir;

    private static String NO_TAG = "※未指定";

    @Autowired
    CardRepository cardRepository;
    @Autowired
    TagRepository tagRepository;
    @Autowired
    CardTagRelRepository cardTagRelRepository;

    @Autowired
    CardSpecifications specs;

    @Autowired
    CardFactory cardFactory;

    @Autowired
    S3Service s3Service;

    public List<CardForHomeResponse> home() {
        // タグ一覧を取得
        List<TagEntity> tagEntities = tagRepository.findAll();

        List<CardForHomeResponse> responses = new ArrayList<>();
        for (TagEntity tagEntity : tagEntities) {
            CardForHomeResponse response = new CardForHomeResponse();
            response.setTag(tagEntity.getName());
            // タグに紐づく写真を１枚選択
            int index = tagEntity.getCardTagRelations().size() - 1;
            response.setSrc(tagEntity.getCardTagRelations().get(index).getCard().getSrc());
            responses.add(response);
        }
        return responses;
    }

    public CardForAccountResponse account(String username) {

        CardForAccountResponse response = new CardForAccountResponse();

        // 投稿写真取得
        List<CardEntity> cardsFindByPoster = cardRepository.findByPostUser(username);
        response.setYourCards(cardFactory.createCardResponses(cardsFindByPoster));

        // お気に入り写真取得
        // TODO お気に入り機能追加 お気に入りテーブル追加

        return response;
    }

    public List<String> getTags() {
        return tagRepository.findAll().stream().map(TagEntity::getName).collect(Collectors.toList());
    }

    public List<CardEntity> search(String tag, String freeword) {

        // 検索対象「タグ」
        List<CardEntity> resultList1 = tagRepository.findByName(tag).stream().map(TagEntity::getCardTagRelations)
                .flatMap(Collection::stream).map(CardTagRelEntity::getCard).collect(Collectors.toList());

        // 検索対象「投稿者名・説明」
        List<CardEntity> resultList2 = cardRepository
                .findAll(Specification.where(specs.containsPoster(freeword)).or(specs.containsDescription(freeword)));

        // result1とresult2の両方に含まれるもの
        List<CardEntity> combinedResult = new ArrayList<>();
        if (tag.isEmpty() || NO_TAG.equals(tag)) {
            combinedResult.addAll(resultList2);
        } else {
            for (CardEntity card : resultList1) {
                if (resultList2.contains(card)) {
                    combinedResult.add(card);
                }
            }
        }

        return combinedResult;

    }

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
        cardEntity.setPostDate(LocalDate.now());
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
