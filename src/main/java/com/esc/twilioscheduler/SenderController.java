package com.esc.twilioscheduler;

import com.esc.message.TextMessage;
import com.esc.message.TextMessageRepository;
import com.esc.user.ApplicationUserRepository;
import com.twilio.Twilio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.view.RedirectView;
import java.text.ParseException;
import java.util.Date;
import java.util.List;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;

@Controller
public class SenderController {

    @Autowired
    private TextMessageRepository textMessageRepo;
    @Autowired
    private ApplicationUserRepository appUserRepo;

    // Sends scheduled messages from database
    @PostMapping(value="/scheduler")
    public RedirectView getMessages(@RequestParam String key) throws ParseException {

        // Checks to make sure only an "authorized user" can run this code
        // As we discussed, this is quite the hack, but I'm glad you got it implemented.
        if (key.equals(System.getenv("HEROKU_KEY"))) {

            // Get current Date
            Date now = new Date();

            // Get all messages
            // Oof. I'd much prefer to do this with a query specified in your repo.
            // Perhaps finding all that fall within a certain timestamp range for the requested send?
            List<TextMessage> messages = (List<TextMessage>) textMessageRepo.findAll();

            for (TextMessage m : messages) {

                if ((m.sendTimestamp.before(now) || m.sendTimestamp.equals(now)) && !m.wasSent) {

                    // Twilio auth
                    Twilio.init(System.getenv("ACCOUNT_SID"), System.getenv("AUTH_TOKEN"));
                    // Twilio send text message
                    Message message = Message.creator(new PhoneNumber(m.applicationUser.phoneNumber),
                            new PhoneNumber("+12062028535"),
                            m.message).create();

                    m.wasSent = true;
                    textMessageRepo.save(m);
                }
            }
        }
        System.out.println("END");
        return new RedirectView("/profile");
    }
}
