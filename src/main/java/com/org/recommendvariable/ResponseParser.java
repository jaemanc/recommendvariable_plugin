package com.org.recommendvariable;

public class ResponseParser {

    public String stringTrimmer(String variableName) {

        if (variableName.startsWith("a ")) {
            variableName = variableName.substring(1,variableName.length());
        }

        if (variableName.startsWith("an ")) {
            variableName = variableName.substring(3,variableName.length());
        }

        if (variableName.startsWith("the ")) {
            variableName = variableName.substring(4,variableName.length());
        }

        if (variableName.startsWith("in ")) {
            variableName = variableName.substring(3,variableName.length());
        }

        if (variableName.startsWith("it's")) {
            variableName = variableName.replace("it's","");
        }
        if (variableName.startsWith("It's")) {
            variableName = variableName.replace("It's","");
        }
        variableName = variableName.replace(",", "");
        variableName = variableName.replace(".", "");


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

}



