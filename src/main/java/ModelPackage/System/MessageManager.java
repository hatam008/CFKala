package ModelPackage.System;

import ModelPackage.System.database.DBManager;
import ModelPackage.Users.Message;
import ModelPackage.Users.User;

import java.util.Date;

public class MessageManager {
    private static MessageManager messageManager;

    public static MessageManager getInstance() {
        return messageManager;
    }

    public void sendMessage(User user,String subject,String text){
        Message message = new Message();
        message.setDate(new Date());
        message.setSubject(subject);
        message.setRead(false);
        message.setMessage(text);
        DBManager.save(message);
        user.getMessages().add(message);
        DBManager.save(user);
    }
}
