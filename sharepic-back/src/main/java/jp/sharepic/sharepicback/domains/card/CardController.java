package jp.sharepic.sharepicback.domains.card;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jp.sharepic.sharepicback.domains.card.request.CardUploadRequest;

@RestController
@RequestMapping("/card")
public class CardController {

    @Autowired
    CardService cardService;

    @PutMapping("/upload")
    public void uploadCard(@RequestBody CardUploadRequest req) {
        cardService.uploadCard(req.getSrc(), req.getExtension(), req.getTags(), req.getDescription(),
                req.getPostUser());
    }

}
