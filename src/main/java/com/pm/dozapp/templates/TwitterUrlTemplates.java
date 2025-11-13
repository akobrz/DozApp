package com.pm.dozapp.templates;

import static java.lang.Math.max;

public interface TwitterUrlTemplates {

    public static String getTweetsUrl(String userId, int count) {
        return "https://api.twitter.com/2/users/" + userId + "/tweets" +
                "?max_results=" + max(count, 5) + "&tweet.fields=created_at,author_id,text";
    }

    public static String getUserUrl(String username) {
        return "https://api.twitter.com/2/users/by/username/" + username + "?user.fields=username,name";
    }

}
