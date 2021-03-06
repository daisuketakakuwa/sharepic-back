package jp.sharepic.sharepicback.domains.card;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import jp.sharepic.sharepicback.domains.card.response.CardForAccountResponse;
import jp.sharepic.sharepicback.domains.card.response.CardForHomeResponse;
import jp.sharepic.sharepicback.domains.card.response.CardForSearchResponse;
import jp.sharepic.sharepicback.domains.relation.CardTagRelEntity;
import jp.sharepic.sharepicback.domains.relation.CardTagRelRepository;
import jp.sharepic.sharepicback.domains.tag.TagEntity;
import jp.sharepic.sharepicback.domains.tag.TagRepository;
import jp.sharepic.sharepicback.domains.user.UserEntity;
import jp.sharepic.sharepicback.domains.user.UserRepository;
import jp.sharepic.sharepicback.infra.s3.S3Service;

@Component
public class CardService {

    @Value("${img_tmp_dir}")
    String imageTmpDir;

    @Autowired
    CardRepository cardRepository;
    @Autowired
    UserRepository userRepository;
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

    public List<String> getTags() {
        return getTagEntities().stream().map(TagEntity::getName).collect(Collectors.toList());
    }

    public List<CardForHomeResponse> home() {
        Set<TagEntity> tagEntities = getTagEntities();
        List<CardForHomeResponse> responses = new ArrayList<>();
        for (TagEntity tagEntity : tagEntities) {
            CardForHomeResponse response = new CardForHomeResponse();
            response.setTag(tagEntity.getName());
            // ???????????????????????????????????????
            int index = tagEntity.getCardTagRelations().size() - 1;
            response.setSrc(tagEntity.getCardTagRelations().get(index).getCard().getSrc());
            responses.add(response);
        }
        return responses;
    }

    private Set<TagEntity> getTagEntities() {
        // ?????????????????????
        List<TagEntity> tagEntities = tagRepository.findAll();
        // ?????????????????????????????????
        Set<TagEntity> sortedEntities = new TreeSet<>(Comparator.comparing(TagEntity::getUpdatedDate).reversed());
        sortedEntities.addAll(tagEntities);

        return sortedEntities;
    }

    public CardForAccountResponse account(String username) {

        CardForAccountResponse response = new CardForAccountResponse();

        // ??????????????????
        List<CardEntity> cardsFindByPoster = cardRepository.findByPostUser(username);
        response.setYourCards(cardFactory.createCardResponses(cardsFindByPoster));

        // ???????????????????????????
        // TODO ??????????????????????????? ?????????????????????????????????

        return response;
    }

    public CardForSearchResponse getTagsAndNames() {
        List<String> names = userRepository.findAll().stream().map(UserEntity::getName).collect(Collectors.toList());
        return new CardForSearchResponse(getTags(), names);
    }

    public List<CardEntity> search(String tag, String name) {

        // ????????????????????????
        List<CardEntity> resultList1 = tagRepository.findByName(tag).stream().map(TagEntity::getCardTagRelations)
                .flatMap(Collection::stream).map(CardTagRelEntity::getCard).collect(Collectors.toList());

        // ??????????????????????????????
        List<CardEntity> resultList2 = cardRepository.findAll(Specification.where(specs.containsPoster(name)));

        // ?????????????????????????????????????????????
        List<CardEntity> combinedResult = new ArrayList<>();
        if (tag.isEmpty() && name.isEmpty()) {
            combinedResult.addAll(resultList1);
            for (CardEntity card : resultList2) {
                if (!combinedResult.contains(card)) {
                    combinedResult.add(card);
                }
            }
        } else if (tag.isEmpty() && !name.isEmpty()) {
            combinedResult.addAll(resultList2);
        } else if (!tag.isEmpty() && name.isEmpty()) {
            combinedResult.addAll(resultList1);
        } else if (!tag.isEmpty() && !name.isEmpty()) {
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

        // S3????????????????????????
        String objectUrl = s3Service.putObject(sb.toString());

        // ??????????????????(Card, Tag)
        saveEntities(objectUrl, tags, description, postUser);

        // ????????????????????????????????????
        saveRelations(objectUrl, tags);
    }

    @Transactional
    private void saveEntities(String objectUrl, String[] tags, String description, String postUser) {

        // Card??????????????????
        CardEntity cardEntity = new CardEntity();
        cardEntity.setId(UUID.randomUUID().toString());
        cardEntity.setSrc(objectUrl);
        cardEntity.setDescription(description);
        cardEntity.setPostDate(LocalDate.now());
        cardEntity.setPostUser(postUser);
        cardRepository.save(cardEntity);
        System.out.println("CARD??????????????????????????????" + "???" + objectUrl + "???");

        // Tag???????????????????????????????????????????????????????????????
        for (String tagName : tags) {
            // ?????????????????????????????????????????????
            TagEntity entity = tagRepository.findByName(tagName).orElse(null);
            if (entity == null) {
                entity = new TagEntity();
                entity.setId(UUID.randomUUID().toString());
                entity.setName(tagName);
                System.out.println("TAG??????????????????????????????" + "???" + tagName + "???");
            } else {
                entity.setUpdatedDate(LocalDateTime.now());
                System.out.println("TAG??????????????????????????????" + "???" + tagName + "???");
            }
            tagRepository.save(entity);
        }
    }

    @Transactional
    private void saveRelations(String objectUrl, String[] tags) {
        // CardTagRel??????????????????
        // Card????????????????????????
        CardEntity card = cardRepository.findBySrc(objectUrl).orElseThrow(RuntimeException::new);
        for (String tagName : tags) {
            // Tag????????????????????????
            TagEntity tag = tagRepository.findByName(tagName).orElseThrow(RuntimeException::new);
            // ??????
            CardTagRelEntity cardTagRelEntity = new CardTagRelEntity();
            cardTagRelEntity.setId(UUID.randomUUID().toString());
            cardTagRelEntity.setCard(card);
            cardTagRelEntity.setTag(tag);
            cardTagRelRepository.save(cardTagRelEntity);
            System.out.println("CardTagRelation??????????????????????????????");
        }
    }

}
