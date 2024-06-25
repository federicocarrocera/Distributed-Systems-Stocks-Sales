package service.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Autowired;
import com.netflix.discovery.EurekaClient;

@RestController
public class AdvertisementController {
    @Autowired
    private EurekaClient eurekaClient;


    @GetMapping("/advertisement")
    public ResponseEntity<String> getAdvertisement() {
        return new ResponseEntity<>(randomAd(), HttpStatus.OK);
    }

    private String randomAd(){
        String[] ads = {"Trade smarter, not harder! Join our market for expert insights and low fees.",
        "Unlock your financial potential with our intuitive trading platform. Start investing today!",
        "From novice to pro, our market caters to all levels of investors. Trade confidently with us.",
        "Make your money work for you! Join our market for personalized investment strategies.",
        "Seize every opportunity in the market with our real-time trading tools and analysis.",
        "Trade with confidence knowing our market puts your interests first. Invest with integrity.",
        "Maximize your returns and minimize your stress with our user-friendly trading platform.",
        "Experience the difference with our award-winning customer service and competitive rates. Start trading today!"};
        int random = (int) (Math.random() * ads.length);
        return ads[random];
    }
}
