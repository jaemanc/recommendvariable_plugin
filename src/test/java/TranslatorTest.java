import com.org.recommendvariable.Config;
import org.junit.Test;

import javax.naming.directory.InvalidAttributesException;
import java.io.*;
import java.net.*;
import java.util.*;
import java.net.URLEncoder;
import java.util.Map;
import java.util.regex.Pattern;

public class TranslatorTest {

    @Test
    public void requestPapago() {

        try {

            String resource = "local.properties";
            Properties properties = new Properties();

            InputStream reader = Config.class.getClassLoader().getResource(resource).openStream();
            properties.load(reader);

            String apiURL = "https://openapi.naver.com/v1/papago/n2mt";
            String tempText = "이런것도되나";
            String requestVariable = requestStringValidator(tempText);
            try {
                requestVariable = URLEncoder.encode(requestVariable, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException("인코딩 실패", e);
            }

            Map<String, String> requestHeaders = new HashMap<>();
            requestHeaders.put("X-Naver-Client-Id", properties.getProperty("clientId"));
            requestHeaders.put("X-Naver-Client-Secret", properties.getProperty("ClientSecret"));

            String responseBody = post(apiURL, requestHeaders, requestVariable);

            // {"message":{"result":{"srcLangType":"ko","tarLangType":"en","translatedText":"Hello. How are you today?","engineType":"N2MT"},"@type":"response","@service":"naverservice.nmt.proxy","@version":"1.0.0"}}
            // 파싱보다는 속도 빠름.
            int start = responseBody.indexOf("translatedText")+17;
            int end = responseBody.indexOf("engineType")-3;
            String responseVariable = responseBody.substring(start,end);

            responseVariable = stringTrimmer(responseVariable);

            String snakeVer = snakeBuilder(responseVariable);
            String camelVer = camelBuilder(responseVariable);
            String pascalVer = pascalBuilder(responseVariable);

            System.out.println(" Orgin : " + tempText + " / S : " + snakeVer + " / C : " + camelVer + " / P : " + pascalVer);

        } catch ( Exception e) {
            e.printStackTrace();
        }

    }
    public String requestStringValidator(String requestVariableName) throws Exception {

        requestVariableName = requestVariableName.trim();

        if (requestVariableName.contains(" ")) {
            throw new InvalidAttributesException();
        }

        String pattern = "^[가-힣]*$";
        if (!Pattern.matches(pattern, requestVariableName))  {
            throw new InvalidAttributesException();
        }

        return requestVariableName;
    }

    public String stringTrimmer(String variableName) {
        variableName = variableName.replace("a ", "");
        variableName = variableName.replace("the ", "");
        variableName = variableName.replace(",", "");
        variableName = variableName.trim();
        return variableName;
    }

    public String camelBuilder(String variableName) {
        // camelCaseOfString
        boolean nextUpper = false;
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < variableName.length(); i++) {
            char currentChar = variableName.charAt(i);
            if (currentChar == ' ') {
                nextUpper = true;
            } else {
                if (nextUpper) {
                    sb.append(Character.toUpperCase(currentChar));
                    nextUpper = false;
                } else {
                    sb.append(Character.toLowerCase(currentChar));
                }
            }
        }

        return sb.toString();
    }

    public String pascalBuilder(String variableName) {
        //PascalCaseOfString
        boolean nextUpper = false;
        StringBuffer sb = new StringBuffer();

        sb.append(Character.toUpperCase(variableName.charAt(0)));

        for (int i = 1; i < variableName.length(); i++) {
            char currentChar = variableName.charAt(i);
            if (currentChar == ' ') {
                nextUpper = true;
            } else {
                if (nextUpper) {
                    sb.append(Character.toUpperCase(currentChar));
                    nextUpper = false;
                } else {
                    sb.append(Character.toLowerCase(currentChar));
                }
            }
        }

        return sb.toString();
    }

    public String snakeBuilder(String variableName) {
        //snake_case_of_string
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < variableName.length(); i++) {
            char currentChar = variableName.charAt(i);
            if (currentChar == ' ') {
                sb.append('_');
            } else {
                sb.append(Character.toLowerCase(currentChar));
            }
        }

        return sb.toString();
    }

    private static String post(String apiUrl, Map<String, String> requestHeaders, String text){
        HttpURLConnection con = connect(apiUrl);
        String postParams = "source=ko&target=en&text=" + text; //원본언어: 한국어 (ko) -> 목적언어: 영어 (en)
        try {
            con.setRequestMethod("POST");
            for(Map.Entry<String, String> header :requestHeaders.entrySet()) {
                con.setRequestProperty(header.getKey(), header.getValue());
            }

            con.setDoOutput(true);
            try (DataOutputStream wr = new DataOutputStream(con.getOutputStream())) {
                wr.write(postParams.getBytes());
                wr.flush();
            }

            int responseCode = con.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) { // 정상 응답
                return readBody(con.getInputStream());
            } else {  // 에러 응답
                return readBody(con.getErrorStream());
            }
        } catch (IOException e) {
            throw new RuntimeException("API 요청과 응답 실패", e);
        } finally {
            con.disconnect();
        }
    }

    private static HttpURLConnection connect(String apiUrl){
        try {
            URL url = new URL(apiUrl);
            return (HttpURLConnection)url.openConnection();
        } catch (MalformedURLException e) {
            throw new RuntimeException("API URL이 잘못되었습니다. : " + apiUrl, e);
        } catch (IOException e) {
            throw new RuntimeException("연결이 실패했습니다. : " + apiUrl, e);
        }
    }

    private static String readBody(InputStream body){
        InputStreamReader streamReader = new InputStreamReader(body);

        try (BufferedReader lineReader = new BufferedReader(streamReader)) {
            StringBuilder responseBody = new StringBuilder();

            String line;
            while ((line = lineReader.readLine()) != null) {
                responseBody.append(line);
            }

            return responseBody.toString();
        } catch (IOException e) {
            throw new RuntimeException("API 응답을 읽는데 실패했습니다.", e);
        }
    }

}