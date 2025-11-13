package com.pm.dozapp.templates;

public interface ConsoleTemplate {

    static final String HTML_TEMPLATE = """
        <!DOCTYPE html>
        <html>
        <head>
            <title>Doz App</title>
        </head>
        <body>
            <h1>Enter Twitter user (like Nasa, SpaceX, BoeingSpace):</h1>
            <form method="post" action="/">
                <input type="text" name="text" placeholder="Enter user and press Enter" autofocus />
                <br><br>
                <button type="submit">Get tweets</button>
            </form>
            %s
        </body>
        </html>
        """;

}
