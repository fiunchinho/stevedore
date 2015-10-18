package stevedore.messagebus;

import java.util.Date;

public interface Message {
    String getId();

    Date getCreatedAt();
}
