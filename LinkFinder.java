package linkfinder;

import java.net.*;
import java.io.*;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author Samuel Machat
 */
public class LinkFinder {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        makej();
    }

    private static int forum_n = 0;
    private static int topic_n = 0;
    private static int topic_max = 50000;
    private static int start_n = 0;
    public static String address;
    private static String html1;
    private static String html2;

    public static ArrayList<Page> pages = new ArrayList();

    private static void makej() {
        ArrayList pages = new ArrayList();
        Page page = new Page(address);
        pages.add(page);
        File file = new File("forum_pirati_links.csv");
        while (true) {
            forum_n++;
            topic_n = 0;

            page = new Page(address);

            while (topic_n < topic_max) {
                start_n = 0;
                topic_n++;
                address = "https://forum.pirati.cz/viewtopic.php?f=" + forum_n + "&t=" + topic_n;
                System.out.println(address);
                try {
                    usePage(page, file);
                } catch (Exception e) {

                }
                start_n = 10;
                for (start_n = 10; start_n < 10000; start_n = start_n + 10) {
                    address = "https://forum.pirati.cz/viewtopic.php?f=" + forum_n + "&t=" + topic_n + "&start=" + start_n;
                    try {
                        usePage(page, file);
                        // ovìøí konec diskuzního vlákna
                        if ((html1.substring(html1.indexOf("<time datetime"), html1.indexOf("</time>"))).equals(html1.substring(html2.indexOf("<time datetime"), html2.indexOf("</time>")))) {
                            break;
                        }
                    } catch (Exception e) {
                        break;
                    }
                }
            }
        }
    }

    private static void usePage(Page page, File file) throws IOException {
        page.findAllLinksIn();
        ArrayList<String> links = page.getLinks();

        writeLinksIntofile(page.getLink(), links, file);
    }

    private static boolean pagesContain(String string) {
        boolean itIsIn = false;
        for (Page page : pages) {
            if (string.equals(page.getLink())) {
                itIsIn = true;
                break;
            }
        }
        return itIsIn;
    }

    private static void writeLinksIntofile(String linkPage, ArrayList<String> links, File file) {
        FileWriter fr = null;
        BufferedWriter br = null;
        try {
            fr = new FileWriter(file, true);
            br = new BufferedWriter(fr);
            for (String link : links) {
                br.write(address);
                br.write("\t");
                br.write(link);
                br.write("\n");

            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                br.close();
                fr.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static class Page {

        String link;
        ArrayList<String> links;

        public Page(String linkIn) {
            link = linkIn;
            links = new ArrayList();
        }

        public ArrayList getLinks() {
            return links;
        }

        private void findAllLinksIn() throws IOException {
            links = findAllLinks();
        }

        private ArrayList<String> findAllLinks() throws IOException {
            html2 = "" + html1;
            String html = getHtmlFromLink(address);
            html1 = html;
            ArrayList<String> links2 = new ArrayList<>();
            links2 = extractUrls(html);
            return links2;
        }

        private String getLink() {
            return link;
        }

        /**
         * Returns a list with all links contained in the input
         */
        public static ArrayList<String> extractUrls(String text) {
            ArrayList<String> containedUrls = new ArrayList<String>();
            String urlRegex = "((https?|ftp|gopher|telnet|file):((//)|(\\\\\\\\))+([^\\s||'||\"]*))";
            Pattern pattern = Pattern.compile(urlRegex, Pattern.CASE_INSENSITIVE);
            Matcher urlMatcher = pattern.matcher(text);
            while (urlMatcher.find()) {
                String urlice = text.substring(urlMatcher.start(0), urlMatcher.end(0));
                if (urlice.contains("google")) {
                    containedUrls.add(urlice);
                }
            }

            return containedUrls;
        }

        private String getHtmlFromLink(String link) throws IOException {
            String html = "";
            URL url = new URL(link);
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(url.openStream()));
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                html += inputLine;
            }
            in.close();

            return html;
        }

    }
}
