package applications.search.configuration;

public class SearchConstants {

    //Port
    public static int PORT = 8080;

    //Path
    public static String REVIEW_SEARCH_URI = "/reviewsearch";
    public static String FIND_URI = "/find";

    public enum Type {
        REVIEW,
        QA
    }

    //XHTML pages
    public static final String REVIEW_SEARCH_FORM = """
            <!DOCTYPE html>
            <html xmlns="http://www.w3.org/1999/xhtml">
            <head>
              <title>Review Search</title>
            </head>
            <body>
            <form class="form" action="/reviewsearch" method="post">
            <input type="text" name="term" placeholder="Term"></input><br />
            <button  type"submit">Search</button>
            </form>
            </body>
            </html>""";

    public static final String FIND_FORM = """
            <!DOCTYPE html>
            <html xmlns="http://www.w3.org/1999/xhtml">
            <head>
              <title>Find</title>
            </head>
            <body>
            <form action="/find" method="post">
            <input type="text" name="asin" placeholder="ASIN Number"></input><br />
            <button  type"submit">Search</button>
            </form>
            </body>
            </html>""";

    public static final String REVIEW_SEARCH_RESPONSE = """
            <!DOCTYPE html>
            <html xmlns="http://www.w3.org/1999/xhtml">
            <head>
              <title>Review Search</title>
            </head>
            <body>
            <form class="form" action="/reviewsearch" method="post">
            <input type="text" name="term" placeholder="Term"></input><br />
            <button  type"submit">Search</button>
            </form><br /><br /><br />
            %s\s
            </body>
            </html>""";

    public static final String FIND_ASIN_RESPONSE = """
            <!DOCTYPE html>
            <html xmlns="http://www.w3.org/1999/xhtml">
            <head>
              <title>Find</title>
            </head>
            <body>
            <form class="form" action="/find" method="post">
            <input type="text" name="asin" placeholder="ASIN Number"></input><br />
            <button  type"submit">Search</button>
            </form><br /><br /><br />
            %s\s
            </body>
            </html>""";
}
