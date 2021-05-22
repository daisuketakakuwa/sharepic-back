package jp.sharepic.sharepicback.domains.card;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jp.sharepic.sharepicback.domains.card.request.CardUploadRequest;
import jp.sharepic.sharepicback.domains.card.response.CardForAccountResponse;
import jp.sharepic.sharepicback.domains.card.response.CardForHomeResponse;
import jp.sharepic.sharepicback.domains.card.response.CardResponse;
import jp.sharepic.sharepicback.domains.user.UserInfo;

@RestController
@RequestMapping("/card")
public class CardController {

    @Autowired
    CardService cardService;

    @Autowired
    CardFactory cardFactory;

    @PutMapping("/upload")
    public void uploadCard(@RequestBody CardUploadRequest req, @AuthenticationPrincipal UserInfo loginUser) {
        cardService.uploadCard(req.getSrc(), req.getExtension(), req.getTags(), req.getDescription(),
                loginUser.getName());
    }

    @GetMapping("/tags")
    public List<String> getTags() {
        return cardService.getTags();
    }

    @GetMapping("/home")
    public List<CardForHomeResponse> home() {
        return cardService.home();
    }

    @GetMapping("/account")
    public CardForAccountResponse account(@AuthenticationPrincipal UserInfo loginUser) {
        System.out.println(loginUser.getName());
        return cardService.account(loginUser.getName());
    }

    @GetMapping("/search")
    public List<CardResponse> search(@RequestParam String tag, @RequestParam String freeword) {
        return cardFactory.createCardResponses(cardService.search(tag, freeword));
    }

}
