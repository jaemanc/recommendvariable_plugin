package com.org.recommendvariable;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.SelectionModel;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Translator {

    public List<String> variableChanger(@NotNull AnActionEvent e) {
        List<String> changed_variable = new ArrayList<>();
        try {
            Project currentProject = e.getProject();

            DataContext dataContext = e.getDataContext();
            Editor editor = PlatformDataKeys.EDITOR.getData(dataContext);
            if (editor != null) {
                SelectionModel selectionModel = editor.getSelectionModel();
                if (StringUtils.isEmpty(selectionModel.getSelectedText()) ) {
                    Messages.showMessageDialog(currentProject, "Please block the WORD to Make Varaiable Name!", "Not Verifiable Statement", Messages.getInformationIcon());
                    return changed_variable;
                }
                if (selectionModel.getSelectedText().contains(" ")) {
                    Messages.showMessageDialog(currentProject, "Only words are needed", "Not Verifiable Statement", Messages.getInformationIcon());
                    return changed_variable;
                }

                Config config = new Config();
                String clId = config.getClientId();
                String clSe = config.getClientSecret();

                VariableMaker variableMaker = new VariableMaker();
                String requestVariableName = variableMaker.requestStringValidator(currentProject, selectionModel.getSelectedText());

                if (requestVariableName.equals("")) {
                    return changed_variable;
                }

                requestVariableName = URLEncoder.encode(requestVariableName, "UTF-8");

                String responseStr = post(config.getUrls(), clId, clSe, requestVariableName);

                int start = responseStr.indexOf("translatedText")+17;
                int end = responseStr.indexOf("engineType")-3;
                String responseVariable = responseStr.substring(start,end);

                ResponseParser responseParser = new ResponseParser();
                responseVariable = responseParser.stringTrimmer(responseVariable);

                String snakeVer = responseParser.snakeBuilder(responseVariable);
                String camelVer = responseParser.camelBuilder(responseVariable);
                String pascalVer = responseParser.pascalBuilder(responseVariable);

                changed_variable.add(snakeVer);
                changed_variable.add(camelVer);
                changed_variable.add(pascalVer);

            }
        } catch (Exception t) {
            t.printStackTrace();
            return changed_variable;
        }
        return changed_variable;
    }

    private static String post(String apiUrl, String clId, String clSe, String text){

        Map<String, String> requestHeaders = new HashMap<>();
        requestHeaders.put("X-Naver-Client-Id", clId);
        requestHeaders.put("X-Naver-Client-Secret", clSe);


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
